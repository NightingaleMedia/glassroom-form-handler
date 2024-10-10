package functions.api;

public class ResponseMessage {

    private String message = "Success";

    public ResponseMessage() {

    }

    public ResponseMessage(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
