package algorithm_QnA_community.algorithm_QnA_community.config.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Res<T> {
    private boolean success;
    private T data;

    public static<T> Res<T> res(final boolean success, final T data) {
        return new Res(success, data);
    }
}
