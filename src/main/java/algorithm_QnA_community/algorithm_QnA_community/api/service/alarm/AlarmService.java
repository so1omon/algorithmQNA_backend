package algorithm_QnA_community.algorithm_QnA_community.api.service.alarm;

import algorithm_QnA_community.algorithm_QnA_community.api.controller.alarm.AlarmDto;
import algorithm_QnA_community.algorithm_QnA_community.api.controller.alarm.AlarmsRes;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.CustomException;
import algorithm_QnA_community.algorithm_QnA_community.config.exception.ErrorCode;
import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.Alarm;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.alarm
 * fileName       : AlarmService
 * author         : solmin
 * date           : 2023/05/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/26        solmin       최초 생성
 */
@Service
@RequiredArgsConstructor
public class AlarmService {
    private final AlarmRepository alarmRepository;

    @Transactional
    public void checkAlarm(Member member, Long alarmId) {
        Alarm findAlarm = checkAuthoritiesAndGetAlarm(member, alarmId);
        findAlarm.check();
    }

    @Transactional
    public void deleteAlarm(Member member, Long alarmId) {
        Alarm findAlarm = checkAuthoritiesAndGetAlarm(member, alarmId);
        findAlarm.deleteAlarm();
        alarmRepository.delete(findAlarm);
    }

    @Transactional(readOnly = true)
    public AlarmsRes getAlarms(Long recentAlarmId, Long oldAlarmId, Member loginMember) {

        List<AlarmDto> alarms = new ArrayList<>();
        // 1. recentAlarmId, oldAlarmId 둘 다 존재할 경우 오류
        if(recentAlarmId!=null && oldAlarmId!=null){
            throw new CustomException(ErrorCode.INCOMPATIBLE_PARAMETER, "recentAlarmId와 oldAlarmId는 둘 중 하나만 전송해야 합니다.");
        }else if(recentAlarmId!=null){// 2. recentAlarmId
            alarms = alarmRepository.findTop10ByRecentAlarmId(recentAlarmId,loginMember.getId())
                .stream().map(AlarmDto::new).collect(Collectors.toList());
            Collections.reverse(alarms);
        }else if(oldAlarmId!=null){// 3. oldAlarmId
            alarms = alarmRepository.findTop10ByOldAlarmId(oldAlarmId,loginMember.getId())
                .stream().map(AlarmDto::new).collect(Collectors.toList());
        }else{// 4. empty
            alarms = alarmRepository.findTop10ByMemberIdOrderByCreatedDateDesc(loginMember.getId())
                .stream().map(AlarmDto::new).collect(Collectors.toList());
        }

        return new AlarmsRes(alarms);
    }

    private Alarm checkAuthoritiesAndGetAlarm(Member member, Long alarmId) {
        // 1. alarm_id에 해당하는 알람이 존재하지 않는 경우
        Alarm findAlarm = alarmRepository.findById(alarmId)
            .orElseThrow(() -> new EntityNotFoundException("알람이 존재하지 않습니다."));
        // 2. alarm_id에 해당하는 member와 동일한 사람이 아닐 경우
        if(findAlarm.getMember().getId()!= member.getId()){
            throw new CustomException(ErrorCode.UNAUTHORIZED, "알람을 확인할 수 있는 권한이 존재하지 않습니다.");
        }
        return findAlarm;
    }
}
