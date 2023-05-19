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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateReq {

    @Size(min=5,max=30, message = "게시물 제목을 5글자 이상 작성해야 합니다.")
    @Nullable
    private String title;

    @Nullable
    private String content;

    @EnumValidator(target = PostCategory.class, message = "올바른 카테고리를 입력하세요.")
    @Nullable
    private String categoryName;

    @EnumValidator(target = PostType.class, message = "올바른 내용타입을 입력하세요.")
    @Nullable
    private String contentType;
}
