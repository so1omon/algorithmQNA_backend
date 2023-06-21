package algorithm_QnA_community.algorithm_QnA_community.api.controller.alarm;

import algorithm_QnA_community.algorithm_QnA_community.api.service.alarm.AlarmService;
import algorithm_QnA_community.algorithm_QnA_community.config.auth.PrincipalDetails;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.DefStatus;
import algorithm_QnA_community.algorithm_QnA_community.domain.response.Res;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.alarm
 * fileName       : AlarmApiController
 * author         : solmin
 * date           : 2023/05/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/26        solmin       최초 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
public class AlarmApiController {

    private final AlarmService alarmService;

    @GetMapping
    public Res<AlarmsRes> getAlarms(@RequestParam(required = false, name = "recentAlarmId") Long recentAlarmId,
                         @RequestParam(required = false, name = "oldAlarmId") Long oldAlarmId,
                         Authentication authentication){
        Member loginMember = getLoginMember(authentication);

        AlarmsRes result = alarmService.getAlarms(recentAlarmId, oldAlarmId, loginMember);
        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 알람 내역을 조회했습니다."), result);
    }

    @PatchMapping("/{alarm_id}")
    public Res checkAlarm(@PathVariable("alarm_id") Long alarmId,
                          Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        alarmService.checkAlarm(loginMember,alarmId);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 알람을 확인했습니다."));
    }

    @DeleteMapping("/{alarm_id}")
    public Res deleteAlarm(@PathVariable("alarm_id") Long alarmId,
                          Authentication authentication){
        Member loginMember = getLoginMember(authentication);
        alarmService.deleteAlarm(loginMember,alarmId);

        return Res.res(new DefStatus(HttpStatus.OK.value(), "성공적으로 알람을 삭제했습니다."));
    }

    // TODO 추후 해당 메소드 추출하기
    private static Member getLoginMember(Authentication authentication) {
        Member loginMember = ((PrincipalDetails) authentication.getPrincipal()).getMember();
        return loginMember;
    }
}
