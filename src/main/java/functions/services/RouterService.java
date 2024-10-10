package functions.services;

import com.google.cloud.functions.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;

public class RouterService {

    GSheetService sht = new GSheetService();

    EmailService mailer = new EmailService();

    Gson gson = new Gson();

    public String handleGlassroomForm(HttpRequest request) throws Exception {


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

        return "Ok";

    }

    public String handleEmailSignup(HttpRequest request, String email) throws Exception {
        sht.addEmailRow("test");

        // check if email exists
        // check if they are in blacklist
        // check which newsletter to add them to
        // add them to the mail list
        return "Ok";
    }
}
