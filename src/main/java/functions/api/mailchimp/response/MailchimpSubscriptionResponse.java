package functions.api.mailchimp.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailchimpSubscriptionResponse {
    @JsonProperty("new_members")
    public McMember newMembers;
}
