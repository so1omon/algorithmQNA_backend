package algorithm_QnA_community.algorithm_QnA_community.api.controller.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.controller.alarm
 * fileName       : AlarmsRes
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
public class AlarmsRes {
    private List<AlarmDto> alarms;
    private int size;

    public AlarmsRes(List<AlarmDto> alarms){
        this.alarms = alarms;
        this.size = alarms.size();
    }
}
