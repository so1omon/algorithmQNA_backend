package algorithm_QnA_community.algorithm_QnA_community.api.controller.alarm;

import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.Alarm;
import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.alarm
 * fileName       : AlarmDto
 * author         : solmin
 * date           : 2023/05/26
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/26        solmin       최초 생성
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmDto {
    private Long alarmId;
    private String subjectMemberName;
    private String eventURL;
    private boolean checked;
    private AlarmType alarmType;
    private Long commentId;
    private String msg;
    private LocalDateTime createdAt;

    public AlarmDto(Alarm alarm){
        this.alarmId = alarm.getId();
        this.subjectMemberName = alarm.getSubjectMemberName();
        this.eventURL = alarm.getEventUrl();
        this.checked = alarm.isChecked();
        this.alarmType = alarm.getType();
        this.commentId = alarm.getCommentId();
        this.msg = alarm.getMsg();
        this.createdAt = alarm.getCreatedDate();
    }

}
