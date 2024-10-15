package functions.api.mailchimp.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailChimpListStats {

    @SerializedName("member_count")
    public float memberCount;

    @SerializedName("member_count_since_send")
    public float memberCountSinceSend;

    @SerializedName("unsubscribe_count")
    public float unsubscribeCount;

    @SerializedName("unsubscribe_count_since_send")
    public float unsubSinceLast;

    @SerializedName("open_rate")
    public double openRate;

    @SerializedName("click_rate")
    public double clickRate;
}
