package functions.api.mailchimp.request;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class MailchimpSubscribeRequest {
    public List<Member> members;


    @SerializedName("update_existing")
    public boolean updateExisting;
}
