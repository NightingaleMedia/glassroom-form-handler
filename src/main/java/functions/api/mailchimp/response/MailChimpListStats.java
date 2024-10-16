package functions.api.mailchimp.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailChimpListStats {

    @SerializedName("member_count")
    public int memberCount;

    @SerializedName("member_count_since_send")
    public int memberCountSinceSend;

    @SerializedName("unsubscribe_count")
    public int unsubscribeCount;

    @SerializedName("unsubscribe_count_since_send")
    public int unsubSinceLast;

    @SerializedName("open_rate")
    public double openRate;

    @SerializedName("click_rate")
    public double clickRate;
}
