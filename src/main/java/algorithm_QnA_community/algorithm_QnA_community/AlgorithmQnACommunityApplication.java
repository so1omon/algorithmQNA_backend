package algorithm_QnA_community.algorithm_QnA_community;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.EntityManager;

@SpringBootApplication
@EnableJpaAuditing
public class AlgorithmQnACommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgorithmQnACommunityApplication.class, args);
	}

	@Bean
	JPAQueryFactory jpaQueryFactory(EntityManager entityManager){
		return new JPAQueryFactory(entityManager);
	}

}
