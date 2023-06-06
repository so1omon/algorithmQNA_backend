package algorithm_QnA_community.algorithm_QnA_community.utils.listner;

import algorithm_QnA_community.algorithm_QnA_community.api.service.s3.S3Service;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.post.Post;
import algorithm_QnA_community.algorithm_QnA_community.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PreRemove;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.utils.listner
 * fileName       : PostListener
 * author         : solmin
 * date           : 2023/06/01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/06/01        solmin       최초 생성
 */

@Transactional
@Slf4j
public class PostListener {

    @PreRemove
    public void deleteImageOnPreRemove(Post post){
        S3Service s3Service = BeanUtils.getBean(S3Service.class);
        log.info("postListner 시작");
        s3Service.deleteImagesByPrefix("post/"+post.getId()+"/");
    }
}
