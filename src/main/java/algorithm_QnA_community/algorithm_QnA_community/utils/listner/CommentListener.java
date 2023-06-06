package algorithm_QnA_community.algorithm_QnA_community.utils.listner;

import algorithm_QnA_community.algorithm_QnA_community.api.service.s3.S3Service;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.repository.AlarmRepository;
import algorithm_QnA_community.algorithm_QnA_community.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PreRemove;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.utils.listner
 * fileName       : CommentListener
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
public class CommentListener {

    @PreRemove
    public void deleteImageOnPreRemove(Comment comment){
        S3Service s3Service = BeanUtils.getBean(S3Service.class);

        s3Service.deleteImagesByPrefix("comment/"+comment.getId()+"/");
    }
}
