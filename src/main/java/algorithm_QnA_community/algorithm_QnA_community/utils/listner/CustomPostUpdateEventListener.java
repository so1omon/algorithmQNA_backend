package algorithm_QnA_community.algorithm_QnA_community.utils.listner;

import algorithm_QnA_community.algorithm_QnA_community.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.utils.listner
 * fileName       : CustomPostUpdateEventListener
 * author         : solmin
 * date           : 2023/05/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/05/25        solmin       최초 생성
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomPostUpdateEventListener implements PostUpdateEventListener {

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if(event.getEntity() instanceof Member){
            EntityPersister entityPersister = event.getPersister();
            String[] propertyNames = entityPersister.getPropertyNames();
//            Object[] state = event.getState();
//            Object[] oldState = event.getOldState();
            for (String propertyName : propertyNames) {
                log.info("updated field = {}", propertyName);
            }
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return PostUpdateEventListener.super.requiresPostCommitHandling(persister);
    }
}
