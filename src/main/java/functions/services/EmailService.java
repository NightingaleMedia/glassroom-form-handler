package functions.services;

import com.google.gson.Gson;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import functions.api.FormLabelValue;
import functions.api.dynamicmailer.EventEmailTemplate;
import functions.api.dynamicmailer.MailchimpSubscriberEmail;
import functions.api.mailchimp.response.MailChimpListStats;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.List;

public class EmailService {

    protected MailchimpService mailchimpService = new MailchimpService();
    private static final String ONLINE_FORM_TEMPLATE_ID = "d-55ccf61b94cb4ed081fe567a96a1fddc";
    private static final String MAILCHIMP_ALERT_TEMPLATE_ID = "d-035f5de959d348589fc79f467ee426ba";
    private static final String REPLY_TO_KEY = "email";
    protected static Dotenv dotenv = Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
    private static final String TO_EMAIL = dotenv.get("DEFAULT_TO_EMAIL");
    private static final String FROM_EMAIL = dotenv.get("DEFAULT_FROM_EMAIL");


    public String sendMailchimpAlertEmail(String email) {

        String response;
        try {
            MailChimpListStats stats = mailchimpService.getGlassroomListDetails();

            MailchimpSubscriberEmail data = MailchimpSubscriberEmail.builder()
                    .clickRate(String.valueOf(stats.getClickRate()))
                    .openRate(String.valueOf(stats.getOpenRate()))
                    .memberCount(String.valueOf(stats.getMemberCount()))
                    .memberCountSinceSend(String.valueOf(stats.getMemberCountSinceSend()))
                    .unsubSinceLast(String.valueOf(stats.getUnsubSinceLast()))
                    .unsubscribeCount(String.valueOf(stats.getUnsubscribeCount()))
                    .email(email)
                    .build();


            Mail mail = new Mail();

            Personalization personalization = new Personalization();

            personalization.addDynamicTemplateData("data", data);
            mail.setFrom(new Email(FROM_EMAIL));

            personalization.addTo(new Email(TO_EMAIL));
            personalization.addBcc(new Email("alsigman@gmail.com"));

            mail.setTemplateId(MAILCHIMP_ALERT_TEMPLATE_ID);
            mail.addPersonalization(personalization);
            response = sendAnyEmail(mail);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            response = e.getMessage();
        }

        return response;
    }

    public String sendEmail(String eventDisplayName, List<FormLabelValue> formLabelValues) {

        Mail mail = new Mail();

        EventEmailTemplate t = new EventEmailTemplate();

        t.setEventTypeDisplayName(eventDisplayName);
        t.setFormValues(formLabelValues);

        Personalization personalization = new Personalization();

        personalization.addDynamicTemplateData("data", t);
        personalization.addTo(new Email(TO_EMAIL));

        mail.setTemplateId(ONLINE_FORM_TEMPLATE_ID);

        mail.setFrom(new Email(FROM_EMAIL));

        String emailValue =
                formLabelValues.stream()
                        .filter(e -> e.getLabel().equalsIgnoreCase(REPLY_TO_KEY))
                        .toList()
                        .get(0)
                        .getValue();

        mail.setReplyTo(new Email(emailValue));
        mail.addPersonalization(personalization);
        String response;
        try {
            response = sendAnyEmail(mail);
        } catch (RuntimeException ex) {
            response = ex.getMessage();
        }

        return response;
    }

    String sendAnyEmail(Mail mail) throws RuntimeException {
        SendGrid sg = new SendGrid(dotenv.get("SG_API_KEY"));
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            return response.getBody();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
