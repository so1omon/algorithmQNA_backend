package algorithm_QnA_community.algorithm_QnA_community.utils.listner;

import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.Alarm;
import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.AlarmType;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikeComment;
import algorithm_QnA_community.algorithm_QnA_community.domain.like.LikePost;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.repository.AlarmRepository;
import algorithm_QnA_community.algorithm_QnA_community.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PrePersist;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.utils.listner
 * fileName       : LikeListener
 * author         : solmin
 * date           : 2023/05/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/26        solmin       최초 생성 (추후 리팩토링 예정)
 */

@Transactional
@Slf4j
public class LikeListener {
    @PrePersist
    public void notifyToPoster(Object likeInfo){
        AlarmRepository alarmRepository = BeanUtils.getBean(AlarmRepository.class);
        if (likeInfo instanceof LikeComment){
            LikeComment likeComment = (LikeComment) likeInfo;

            Member likeMember = likeComment.getMember();

            Member writer = likeComment.getComment().getMember();
            if(likeMember.getId()==writer.getId()) return;

            String message = likeMember.getName()+"님이 당신의 댓글에 "+
                (likeComment.isLike()?"좋아요":"싫어요")+"를 남겼습니다.";
            alarmRepository.save(Alarm.createAlarm()
                .member(writer)
                    .type(likeComment.isLike()? AlarmType.COMMENT_LIKE:AlarmType.COMMENT_DISLIKE)
                    .msg(message)
                    .commentId(likeComment.getComment().getId())
                    .subjectMemberName(likeMember.getName())
                    .eventUrl("/post/"+likeComment.getComment().getPost().getId())
                .build());
        }else{
            LikePost likePost = (LikePost) likeInfo;
            Member likeMember = likePost.getMember();
            Member writer = likePost.getPost().getMember();
            if(likeMember.getId()==writer.getId()) return;

            String message = likeMember.getName()+"님이 당신의 게시글에 "+
                (likePost.isLike()?"좋아요":"싫어요")+"를 남겼습니다.";

            alarmRepository.save(Alarm.createAlarm()
                .member(writer)
                .type(likePost.isLike()? AlarmType.POST_LIKE:AlarmType.POST_DISLIKE)
                .msg(message)
                .subjectMemberName(likeMember.getName())
                .eventUrl("/post/"+likePost.getPost().getId())
                .build());
        }
    }
}
