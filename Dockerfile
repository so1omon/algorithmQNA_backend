FROM adoptopenjdk/openjdk11

COPY build/libs/algorithm_QnA_community-0.0.1-SNAPSHOT.jar algorithm_qna_backend_project.jar

ENTRYPOINT ["java", "-jar", "algorithm_qna_backend_project.jar"]

ENV	USE_PROFILE local