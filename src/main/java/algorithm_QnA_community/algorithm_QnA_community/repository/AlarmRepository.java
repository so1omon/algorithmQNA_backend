package algorithm_QnA_community.algorithm_QnA_community.repository;

import algorithm_QnA_community.algorithm_QnA_community.domain.alarm.Alarm;
import algorithm_QnA_community.algorithm_QnA_community.domain.comment.Comment;
import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.repository
 * fileName       : AlarmRepository
 * author         : solmin
 * date           : 2023/05/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/25        solmin       최초 생성
 */

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findTop10ByMemberIdOrderByCreatedDateDesc(Long memberId);

    @Query(value = "select * from alarm" +
        " where alarm_id>:alarm_id" +
        " and member_id=:member_id" +
        " order by created_at asc limit 10", nativeQuery = true)
    List<Alarm> findTop10ByRecentAlarmId(@Param("alarm_id") Long alarmId, @Param("member_id") Long memberId);

    @Query(value = "select * from alarm" +
        " where alarm_id<:alarm_id" +
        " and member_id=:member_id" +
        " order by created_at desc limit 10", nativeQuery = true)
    List<Alarm> findTop10ByOldAlarmId(@Param("alarm_id") Long alarmId, @Param("member_id") Long memberId);

}
