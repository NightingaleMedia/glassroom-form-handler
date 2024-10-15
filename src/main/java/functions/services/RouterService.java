package functions.services;

import com.google.cloud.functions.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import functions.api.FormLabelValue;
import functions.api.mailchimp.response.MailChimpListStats;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Arrays;
import java.util.List;

public class RouterService {
    private static final Dotenv dotenv =
            Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
    GSheetService sht = new GSheetService();

    EmailService mailer = new EmailService();

    MailchimpService mailchimp = new MailchimpService();

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

    public void handleOrderPaidEmail(HttpRequest request) throws Exception {

        JsonObject parsedRequest = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject formRequest = parsedRequest.getAsJsonObject("data");
        String emailAddress = formRequest.get("contact_email").getAsString();

        String orderNumber = formRequest.get("order_number").getAsString();

        sht.addEmailRow(emailAddress, "ORDER PAID", orderNumber);

        mailchimp.subscribeUser(emailAddress);
    }

    public MailChimpListStats handleEmailSignup(HttpRequest request) throws Exception {

        JsonObject parsedRequest = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject formRequest = parsedRequest.getAsJsonObject("data");
        String emailAddress = formRequest.get("email").getAsString();
        boolean shouldSub = formRequest.getAsJsonPrimitive("subscribe").getAsBoolean();

        if (shouldSub) {
            sht.addEmailRow(emailAddress, "FORM SIGNUP", "");
            mailchimp.subscribeUser(emailAddress);
        } else {
            mailchimp.unSubscribeUser(emailAddress);
        }
        MailChimpListStats stats = mailchimp.getGlassroomListDetails();

        return stats;
    }
}
