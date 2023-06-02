package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostSortType;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.utils.annotation.EnumValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostSearchDto
 * author         : janguni
 * date           : 2023/05/31
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/31        janguni           최초 생성
 */
@Data
@AllArgsConstructor
public class PostSearchDto {
    @EnumValidator(target = PostCategory.class, message = "올바른 카테고리를 입력하세요.")
    private PostCategory postCategory;

    @EnumValidator(target = PostType.class, message = "올바른 내용타입을 입력하세요.")
    private PostType postType;

    @EnumValidator(target = PostSortType.class, message = "올바른 정렬타입을 입력하세요.")
    private PostSortType postSort;

    @Nullable
    private int page=0;

    @Nullable
    private Boolean hasCommentCond;

    @Nullable
    private String keyWordsCond;

    @Nullable
    private String titleCond;

    @Nullable
    private String memberNameCond;

    @Nullable
    private Boolean isAcceptedCommentCond;

}
