package algorithm_QnA_community.algorithm_QnA_community.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * packageName      : algorithm_QnA_community.algorithm_QnA_community.response
 * fileNmae         : Res
 * author           : janguni
 * date             : 2023-05-02
 * description      : response 단일화
 *
 * ========================================================
 * DATE             AUTHOR          NOTE
 * 2023/04/20       janguni         최초 생성
 */

@Data
@AllArgsConstructor
@Builder
public class Res<T> {
    private DefStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL) // data가 null일 시 json에 나타나지 않음
    private T data;

    public static<T> Res<T> res(DefStatus status, HttpStatus ok) {
        return res(status, null);
    }

    public static<T> Res<T> res(final DefStatus status, final T t) {
        return Res.<T>builder()
                .status(status)
                .data(t)
                .build();
    }

    public static<T> Res<T> res(final DefStatus status) {
        return Res.<T>builder()
                .status(status)
                .build();
    }
}
