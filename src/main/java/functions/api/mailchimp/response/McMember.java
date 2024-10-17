package functions.api.mailchimp.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class McMember {
    public String id;

    @SerializedName("email_address")
    public String emailAddr;

    @SerializedName("unique_email_id")
    public String uniqueEmailId;

    public String status;
}
