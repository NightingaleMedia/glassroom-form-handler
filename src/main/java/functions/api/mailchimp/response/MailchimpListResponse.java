package functions.api.mailchimp.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MailchimpListResponse {
    public String id;
    public MailChimpListStats stats;
}
