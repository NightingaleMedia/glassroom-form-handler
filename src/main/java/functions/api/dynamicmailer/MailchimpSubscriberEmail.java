package functions.api.dynamicmailer;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MailchimpSubscriberEmail {

    public String email;


    public String memberCount;


    public String memberCountSinceSend;


    public String unsubscribeCount;


    public String unsubSinceLast;


    public String openRate;

    public String clickRate;
}
