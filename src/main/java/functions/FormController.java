package functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import functions.services.EmailService;
import functions.services.FormLabelValue;
import functions.services.GSheetService;
import java.io.BufferedWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class FormController implements HttpFunction {

  EmailService mailer = new EmailService();

  GSheetService sht = new GSheetService();

  // Simple function to return "Hello World"
  @Override
  public void service(HttpRequest request, HttpResponse response)
      throws IOException, GeneralSecurityException {

    response.appendHeader("Access-Control-Allow-Origin", "*");

    if ("OPTIONS".equals(request.getMethod())) {
      response.appendHeader("Access-Control-Allow-Methods", "POST, GET");
      response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
      response.appendHeader("Access-Control-Max-Age", "3600");
      response.setStatusCode(204);
      return;
    }
    if ("GET".equals(request.getMethod())) {
      response.setStatusCode(204);
      return;
    }

    Gson gson = new Gson();

    JsonObject parsedRequest = gson.fromJson(request.getReader(), JsonObject.class);

    JsonObject formRequest = parsedRequest.getAsJsonObject("data");
    List<FormLabelValue> labelArr =
        Arrays.stream(
                new Gson()
                    .fromJson(formRequest.getAsJsonArray("formValues"), FormLabelValue[].class))
            .toList();

    JsonElement shouldSkipEmail = parsedRequest.getAsJsonObject().get("skipEmail");
    if (shouldSkipEmail == null || shouldSkipEmail.isJsonNull()) {

      mailer.sendEmail(formRequest.get("eventTypeDisplayName").getAsString(), labelArr);
    }
    sht.addSheetRow(formRequest.get("eventType").getAsString(), labelArr);

    response.setContentType("application/json");
    response.setStatusCode(200, "OK");

    BufferedWriter writer = response.getWriter();

    writer.write("{\"message\":\"Success\"}");
    writer.close();
  }
}
