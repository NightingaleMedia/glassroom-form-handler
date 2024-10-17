package functions.api.mailchimp.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class UnsubResponse {
    List<EmailUnsub> members;
}
