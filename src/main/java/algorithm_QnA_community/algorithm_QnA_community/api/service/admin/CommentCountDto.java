package algorithm_QnA_community.algorithm_QnA_community.api.service.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.admin
 * fileName       : CommentCountDto
 * author         : solmin
 * date           : 2023/05/18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/18        solmin       최초 생성
 */
@Data
public class CommentCountDto {
    private Long postId;
    private Long count;

    public CommentCountDto(Long postId, Long count){
        this.postId = postId;
        this.count = count;
    }
}
