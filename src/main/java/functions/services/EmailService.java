package functions.services;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class EmailService {
  private static final String TEMPLATE_ID = "d-55ccf61b94cb4ed081fe567a96a1fddc";
  private static final String REPLY_TO_KEY = "email";
  protected static Dotenv dotenv =  Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();
  private static final String TO_EMAIL = dotenv.get("DEFAULT_TO_EMAIL");
  private static final String FROM_EMAIL = dotenv.get("DEFAULT_FROM_EMAIL");
  public String sendEmail(String eventDisplayName, List<FormLabelValue> formLabelValues) {

    Mail mail = new Mail();

    EmailTemplate t = new EmailTemplate();

    t.setEventTypeDisplayName(eventDisplayName);
    t.setFormValues(formLabelValues);

    Personalization personalization = new Personalization();

    personalization.addDynamicTemplateData("data", t);
    personalization.addTo(new Email(TO_EMAIL));

    mail.setTemplateId(TEMPLATE_ID);

    mail.setFrom(new Email(FROM_EMAIL));

    String emailValue =
        formLabelValues.stream()
            .filter(e -> e.getLabel().toLowerCase().equals(REPLY_TO_KEY))
            .collect(Collectors.toList())
            .get(0)
            .getValue();

    mail.setReplyTo(new Email(emailValue));
    mail.addPersonalization(personalization);
    SendGrid sg = new SendGrid(dotenv.get("SG_API_KEY"));
    Request request = new Request();

    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      //      Response response = sg.makeCall(request);
      Response response = sg.api(request);

      return response.getBody().toString();
    } catch (IOException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }
}
