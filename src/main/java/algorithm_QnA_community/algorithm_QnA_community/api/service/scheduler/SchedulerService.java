package algorithm_QnA_community.algorithm_QnA_community.api.service.scheduler;

import algorithm_QnA_community.algorithm_QnA_community.api.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.api.service.scheduler
 * fileName       : SchedulerService
 * author         : solmin
 * date           : 2023/06/02
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/06/02        solmin       최초 생성
 */
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final S3Service s3Service;
    private static final int FIXED_DELAY = 1000 * 60 * 60 * 24;
//  TODO cron으로 전환  @Scheduled(cron = "* * * * * *")
    @Scheduled(fixedDelay = FIXED_DELAY , initialDelay = FIXED_DELAY)
    public void removeLogImages(){
        s3Service.removeLogImages();
    }
}
