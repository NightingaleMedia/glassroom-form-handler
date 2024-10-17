package functions.api;

import lombok.Data;

@Data
public class ResponseMessage<T> {

    private String message = "Success";
    private T data = null;


}
