package functions;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.gson.Gson;
import functions.api.ResponseMessage;
import functions.services.GSheetService;
import functions.services.RouterService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FormController implements HttpFunction {

    RouterService routerService = new RouterService();

    GSheetService sht = new GSheetService();
    Gson gson = new Gson();

    @Override
    public void service(HttpRequest request, HttpResponse response)
            throws IOException, GeneralSecurityException {

        Map<String, List<String>> headerMap = request.getHeaders();

        System.out.println(headerMap.toString());

        if (headerMap.get("X-Forwarded-For") == null || !Objects.equals(headerMap.get("X-Forwarded-For").get(0), "162.241.216.221")) {
            response.setStatusCode(401);
            return;
        }

        response.appendHeader("Access-Control-Allow-Origin", "*");

        if ("OPTIONS".equals(request.getMethod())) {
            response.appendHeader("Access-Control-Allow-Methods", "POST, GET");
            response.appendHeader("Access-Control-Allow-Headers", "Content-Type");
            response.appendHeader("Access-Control-Max-Age", "3600");
            response.setStatusCode(204);
            return;
        }

        response.setContentType("application/json");

        BufferedWriter writer = response.getWriter();
        ResponseMessage responseMessage = new ResponseMessage();

        try {
            switch (request.getPath()) {
                case "/send-form":
                    routerService.handleGlassroomForm(request);
                    response.setStatusCode(200, "OK");
                    break;
                case "/email-list":
                    routerService.
            }
        } catch (Exception ex) {
            response.setStatusCode(400, "Bad Request");
            responseMessage.setMessage(ex.getMessage());
        }


        writer.write(gson.toJson(responseMessage));
        writer.close();
    }
}
