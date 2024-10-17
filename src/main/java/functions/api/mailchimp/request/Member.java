package functions.api.mailchimp.request;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Member {

    @SerializedName("email_address")
    public String emailAddress;

    public String status;

}
