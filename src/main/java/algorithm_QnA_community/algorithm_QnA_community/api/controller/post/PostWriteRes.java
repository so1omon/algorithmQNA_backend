package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostWriteRes
 * author         : solmin
 * date           : 2023/05/1\9
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/19        solmin       최초 생성
 */
@Data
@AllArgsConstructor
public class PostWriteRes{
    private Long postId;
    private LocalDateTime createdAt;
}