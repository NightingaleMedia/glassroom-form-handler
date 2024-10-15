package functions.api.mailchimp.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class EmailUnsub {
    @SerializedName("email_address")
    public String email;
    public String status;
}
