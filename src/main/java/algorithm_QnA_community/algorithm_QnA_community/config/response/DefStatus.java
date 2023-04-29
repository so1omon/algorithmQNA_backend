package algorithm_QnA_community.algorithm_QnA_community.config.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class DefStatus {
    private int code;
    private String message;

    public DefStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
