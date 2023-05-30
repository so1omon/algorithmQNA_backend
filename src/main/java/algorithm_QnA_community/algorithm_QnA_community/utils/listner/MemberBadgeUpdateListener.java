package algorithm_QnA_community.algorithm_QnA_community.utils.listner;


import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.Alarm;
import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.AlarmType;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Badge;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.repository.AlarmRepository;
import algorithm_QnA_community.algorithm_QnA_community.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.PostLoad;
import javax.persistence.PreUpdate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.utils.listner
 * fileName       : MemberEntityListener
 * author         : solmin
 * date           : 2023/05/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/25        solmin       최초 생성, 멤버 뱃지 변동사항 감지 후 알람생성
 */

@Transactional
@Slf4j
public class MemberBadgeUpdateListener {
    @PostLoad
    public void postLoad(Member member){
        member.savePreBadgeCnt();
    }
    @PreUpdate
    public void preUpdate(Member member) { // 1차적으로 알람을 생성할 만한 데이터 변경점인지 확인
        int[] preCnt = {member.getPrePostBadgeCnt(),
            member.getPreCommentBadgeCnt(),
            member.getPreLikeBadgeCnt()};
        int[] postCnt = {member.getPostBadgeCnt(),
            member.getCommentBadgeCnt(),
            member.getLikeBadgeCnt()};

        preCnt = Arrays.stream(preCnt).map(this::calculate).toArray();
        postCnt = Arrays.stream(postCnt).map(this::calculate).toArray();
        for(Badge badge : Badge.values()){
            if (preCnt[badge.getValue()]!=postCnt[badge.getValue()]) {
                updateBadgeCnt(member, preCnt[badge.getValue()], postCnt[badge.getValue()], badge);
            }
        }
    }

    private void updateBadgeCnt(Member member, int preCnt, int postCnt, Badge badge) {
        AlarmRepository alarmRepository = BeanUtils.getBean(AlarmRepository.class);
        alarmRepository.save(Alarm.createAlarm()
            .member(member)
            .eventUrl("/member")
            .type(preCnt<postCnt?AlarmType.BADGE_UPGRADE:AlarmType.BADGE_DOWNGRADE)
            .msg("멤버의 "+badge.name() +" 뱃지 상태가 "+preCnt+"단계에서 "+postCnt+"단계로 변경되었습니다.")
            .build());
    }

    private int calculate(int value){
        if(value>=150){
            return 5;
        }else if(value>=100){
            return 4;
        }else if(value>=60){
            return 3;
        }else if(value>=30){
            return 2;
        }else if(value>=10){
            return 1;
        }else{
            return 0;
        }
    }
}
