package algorithm_QnA_community.algorithm_QnA_community.config.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@Builder
public class Res<T> {
    private DefStatus status;
    private T data;


    public static<T> Res<T> res(DefStatus status, HttpStatus ok) {
        return res(status, null);
    }

    public static<T> Res<T> res(final DefStatus status, final T t) {
        return Res.<T>builder()
                .data(t)
                .status(status)
                .build();
    }

    public static<T> Res<T> res(final DefStatus status) {
        return Res.<T>builder()
                .status(status)
                .build();
    }
}
