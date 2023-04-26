package algorithm_QnA_community.algorithm_QnA_community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AlgorithmQnACommunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlgorithmQnACommunityApplication.class, args);
	}

}
