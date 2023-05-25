package algorithm_QnA_community.algorithm_QnA_community.utils.listner;

import lombok.RequiredArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

/**
 * packageName    : algorithm_QnA_community.algorithm_QnA_community.utils.listner
 * fileName       : HibernateListener
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
public class HibernateListener {
    private final EntityManagerFactory entityManagerFactory;
    private final CustomPostUpdateEventListener customPostUpdateEventListener;

    @PostConstruct
    private void init() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(customPostUpdateEventListener);
    }
}