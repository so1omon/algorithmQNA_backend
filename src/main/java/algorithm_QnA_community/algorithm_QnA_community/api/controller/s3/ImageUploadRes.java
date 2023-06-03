package algorithm_QnA_community.algorithm_QnA_community.api.controller.s3;

import algorithm_QnA_community.algorithm_QnA_community.domain.image.Image;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.s3
 * fileName       : ImageUploadRes
 * author         : solmin
 * date           : 2023/06/01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/06/01        solmin       최초 생성
 */

@Data
public class ImageUploadRes {
    private String url;
    private Long id;
    private LocalDateTime createdAt;

    public ImageUploadRes(Image image){
        this.url = image.getUrl();
        this.id = image.getId();
        this.createdAt = image.getCreatedDate();
    }
}
