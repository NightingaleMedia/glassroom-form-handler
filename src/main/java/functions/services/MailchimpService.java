package functions.services;

import com.google.gson.Gson;
import functions.api.mailchimp.request.MailchimpSubscribeRequest;
import functions.api.mailchimp.request.Member;
import functions.api.mailchimp.response.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

public class MailchimpService {
    Gson gson = new Gson();
    private static final Dotenv dotenv =
            Dotenv.configure().ignoreIfMissing().ignoreIfMalformed().load();

    private static HttpClient client =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    static final String BASE_URL = "https://us11.api.mailchimp.com";

    private String getAuthHeader() {
        String mailchimpToken = "glassroom:" + dotenv.get("MAILCHIMP_API_KEY");

        byte[] encodedBytes = Base64.getEncoder().encode(mailchimpToken.getBytes());
        return "Basic " + new String(encodedBytes);
    }

    private String getGlassroomListUrl() {
        return "/3.0/lists/" + dotenv.get("GLASSROOM_MAILCHIMP_LIST_ID");
    }

    private HttpRequest getSubscribePost(MailchimpSubscribeRequest request) {
        return java.net.http.HttpRequest.newBuilder().uri(URI.create(BASE_URL + getGlassroomListUrl()))
                .header("Authorization", getAuthHeader()).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request, MailchimpSubscribeRequest.class))).build();
    }

    public boolean isUnsubscribed(String email) throws IOException, InterruptedException {

        String appendingForUnSub = "/members?fields=members.status,members.email_address&count=1000&status=unsubscribed";
        var getRequest = java.net.http.HttpRequest.newBuilder().uri(URI.create(BASE_URL + getGlassroomListUrl() + appendingForUnSub))
                .header("Authorization", getAuthHeader()).GET().build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        UnsubResponse listOfUnres = gson.fromJson(getResponse.body(), UnsubResponse.class);

        Optional<EmailUnsub> didUnSub = listOfUnres.getMembers().stream().filter(r -> r.getEmail().equals(email)).findFirst();
        if (didUnSub.isPresent()) {
            System.out.println("Unsubscribed: " + email);
        }
        return didUnSub.isPresent();
    }


    public MailChimpListStats getGlassroomListDetails() throws IOException, InterruptedException {


        var getRequest = java.net.http.HttpRequest.newBuilder().uri(URI.create(BASE_URL + getGlassroomListUrl()))
                .header("Authorization", getAuthHeader()).GET().build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        MailchimpListResponse stats = gson.fromJson(getResponse.body(), MailchimpListResponse.class);

        return stats.getStats();
    }

    public MailchimpSubscriptionResponse subscribeUser(String email) throws IOException, InterruptedException {
        if (isUnsubscribed(email)) {
            return null;
        }
        Member member = new Member();

        member.setEmailAddress(email);
        member.setStatus("subscribed");

        MailchimpSubscribeRequest request = new MailchimpSubscribeRequest();

        request.setUpdateExisting(true);
        request.setMembers(Collections.singletonList(member));


        HttpResponse<String> getResponse = client.send(getSubscribePost(request), HttpResponse.BodyHandlers.ofString());

        return gson.fromJson(getResponse.body(), MailchimpSubscriptionResponse.class);

    }

    public MailchimpSubscriptionResponse unSubscribeUser(String email) throws IOException, InterruptedException {
        Member member = new Member();

        member.setEmailAddress(email);
        member.setStatus("unsubscribed");

        MailchimpSubscribeRequest request = new MailchimpSubscribeRequest();

        request.setUpdateExisting(true);
        request.setMembers(Collections.singletonList(member));

        HttpResponse<String> getResponse = client.send(getSubscribePost(request), HttpResponse.BodyHandlers.ofString());

        return gson.fromJson(getResponse.body(), MailchimpSubscriptionResponse.class);

    }
}
