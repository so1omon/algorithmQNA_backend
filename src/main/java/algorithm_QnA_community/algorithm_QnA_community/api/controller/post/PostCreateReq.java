package algorithm_QnA_community.algorithm_QnA_community.api.controller.post;

import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostCategory;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.PostType;
import algorithm_QnA_community.algorithm_QnA_community.utils.annotation.EnumValidator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.post
 * fileName       : PostCreateReq
 * author         : janguni
 * date           : 2023/05/11
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/11        janguni       최초 생성
 * 2023/05/19        solmin        필드명 일부 변경
 * 2023/05/30        janguni       keyWord 필드 추가
 * 2023/06/01        solmin        게시글 작성 시 imageIds 필드 추가
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateReq {

    @Size(min=5,max=30, message = "게시물 제목을 5글자 이상 작성해야 합니다.")
    private String title;

    @NotBlank(message = "게시글 내용을 1글자 이상 작성해야 합니다.")
    private String content;

    @EnumValidator(target = PostCategory.class, message = "올바른 카테고리를 입력하세요.")
    private String postCategory;

    @EnumValidator(target = PostType.class, message = "올바른 내용타입을 입력하세요.")
    private String postType;

    List<Long> imageIds = new ArrayList<>();
  
    @Nullable
    private List<String> keyWords;

}
