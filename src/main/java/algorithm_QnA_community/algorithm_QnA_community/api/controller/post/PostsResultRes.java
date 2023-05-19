package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostsResultRes
 * author         : janguni
 * date           : 2023/05/17
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/17        janguni           최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostsResultRes {
    private int currentPage;
    private int totalPageCount;
    private boolean next;
    private boolean prev;
    private int size;
    private List<PostSimpleDetail> posts;
}
