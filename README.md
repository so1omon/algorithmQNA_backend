# ALGO싶다 - Algorithm Q&A Community

<div align="center">
<img width="329" alt="image" src="https://user-images.githubusercontent.com/37677461/251651727-4258e35b-d12b-4312-87e7-61a7823da9c9.png">

[//]: # ([![Hits]&#40;https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FVoluntain-SKKU%2FVoluntain-2nd&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false&#41;]&#40;https://hits.seeyoufarm.com&#41;)

</div>


> **알고리즘 문제 풀이 꿀팁 공유 및 질문을 위한 커뮤니티** <br/> **개발기간: 2023.04 ~ 2023.06**

## 배포 주소

> [https://algoqna.ddns.net/](https://algoqna.ddns.net) <br>

## 웹개발팀 소개

|                                       김솔민                                        |                                       장윤희                                        |                                       이진희                                        |                                       전오승                                       |                                                                                     
|:--------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------:|  
| <img width="160px" src="https://avatars.githubusercontent.com/u/37677461?v=4" /> | <img width="160px" src="https://avatars.githubusercontent.com/u/87846081?v=4" /> | <img width="160px" src="https://avatars.githubusercontent.com/u/49019236?v=4" /> |                          <img width="160px" src="https://avatars.githubusercontent.com/u/114666481?v=4" />                           | 
|                      [@so1omon](https://github.com/so1omon)                      |                      [@janguni](https://github.com/janguni)                      |                       [@bebusl](https://github.com/bebusl)                       |                  [@jeonoseung](https://github.com/jeonoseung)                   |  
|                                    Backend 개발                                    |                                    Backend 개발                                    |                                   Frontend 개발                                    |                                   Frontend 개발                                   |                                   Backend 개발                                    | 

## 프로젝트 소개

알고리즘 분야에서의 학습과 문제 해결을 돕기 위해 만들어진 간편한 커뮤니티 사이트를 제작했습니다. <br/><br/>
저희의 목표는 프로그래머스, 백준 저지 등 다양한 코딩 테스트 문제를 풀고 이에 대한 질의 응답과 풀이를 공유하는데 있습니다.
우리는 알고리즘 문제 풀이와 관련된 모든 분야에서 활동하는 개발자들을 위해 특정 온라인 저지 사이트에 관계 없이 유저들의 질문과 고민을 나누고 풀이를 공유할 수 있는 플랫폼을 제공합니다.<br/><br/>
이 커뮤니티에서는 그래프, 탐색, 정렬, 다이나믹 프로그래밍 등 다양한 알고리즘 분류별로 질문과 꿀팁을 누구나 공유할 수 있습니다.
우리의 목표는 상호간에 지식을 나누고 향상시킴으로써 개발자들의 알고리즘 역량을 향상시키는 것입니다.<br/><br/>
위와 같은 커뮤니티 서비스를 구현하기 위해 유저들의 의견 공유를 원할히 수행하기 위한 기능 (알람, 댓글 하이라이팅 등)을 제외한 부가적인 기능들 없이 컴팩트한 서비스만을 제공하고 있습니다.



## 시작 가이드
### Requirements
다음 요구사항은 M1 Mac을 기준으로 작성하였습니다. Docker 기반으로 작동하기 때문에 운영체제에 맞게 아래 Requirements를 충족시키시기 바랍니다.
- [Git](https://git-scm.com/downloads)
- [Docker](https://docs.docker.com/desktop/install/mac-install/)

[//]: # (- [Node.js 14.19.3]&#40;https://nodejs.org/ca/blog/release/v14.19.3/&#41;)
[//]: # (- [Npm 9.2.0]&#40;https://www.npmjs.com/package/npm/v/9.2.0&#41;)
[//]: # (- [Strapi 3.6.6]&#40;https://www.npmjs.com/package/strapi/v/3.6.6&#41;)


### Backend

1. 레포지토리 클론
``` bash
$ git clone https://github.com/algorithmQNA/algorithmQNA_backend.git
```

2. application.yml 생성
```bash
##### 공통 properties를 포함한 local 환경과 production 환경을 분리하여 작성하였습니다. #####
##### 또한 properties 정보를 외부에 노출하지 않기 위해 Git action 동작 시 {$secret.PROPERTIES}에 복사하여 사용하였습니다. #####
##### logging 관련 설정값은 적절히 바꿔서 사용하시기 바랍니다. #####
spring:
  profiles:
    active: local
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: {google oauth client id}
            client-secret: {google oauth client secret}
            redirect-uri: {google callback uri}
            authorization-grant-type: authorization_code
            scope:
              - profile
              - email
            client-name: Google
            client-authentication-method: basic

        provider:
          google:
            authorization-uri: https://accounts.google.com/o/oauth2/v2/auth?prompt=consent&access_type=offline
google:
  uri: "https://accounts.google.com/o/oauth2/v2/auth"

cookie:
  domain: {Your domain}

jwt:
  header: Authorization
  accessSecret: {accessSecret}
  refreshSecret: {refreshSecret}

# local properties
---
spring.config.activate.on-profile: local
spring:
  datasource:
    url: {local datasource url}
    username: {mysql username}
    password: {mysql password}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        default_batch_fetch_size: 1000
        show_sql: true
        format_sql: true
  data:
    redis:
      host: {local redis host}
      port: {local redis port}
      password: {local redis port}

logging:
  level:
    org.hibernate.SQL: debug
    org.hibernate.type: trace
    root: info

cloud:
  aws:
    credentials:
      accessKey: {IAM Access key}
      secretKey: {IAM Secret key}
    s3:
      bucket: {s3 bucket name}
    region:
      static: {s3 bucket region}
    stack:
      auto: false

# production properties
---
spring.config.activate.on-profile: prod
spring:
  datasource:
    url: {local datasource url}
    username: {mysql username}
    password: {mysql password}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        default_batch_fetch_size: 1000
        show_sql: false
        format_sql: false
  data:
    redis:
      host: {local redis host}
      port: {local redis port}
      password: {local redis port}

logging:
  level:
    root: info

cloud:
  aws:
    credentials:
      accessKey: {IAM Access key}
      secretKey: {IAM Secret key}
    s3:
      bucket: {s3 bucket name}
    region:
      static: {s3 bucket region}
    stack:
      auto: false

decorator:
  datasource:
    p6spy:
      enable-logging: false


```

3. docker-compose.yml 파일 수정 : 도커허브 레포지토리에 있는 이미지 대신 Dockerfile을 통한 이미지 빌드로 전환하기
``` bash
1  version: "3"
2
3  services:
4    myapp:
5        # image: janguni/algorithm-project-docker-repo:1.0)
6        build:
7            context: .
8            dockerfile: Dockerfile
```
4. docker-compose 파일을 통한 이미지 필드
```bash
$ docker-compose build
```
5. 빌드한 이미지 파일로부터 컨테이너 실행 (백그라운드 실행 시 -d 옵션 추가)
```bash
$ docker-compose up
```

---

## Stacks 🐈

### Environment

![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-007ACC?style=for-the-badge&logo=Visual%20Studio%20Code&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white)
![Github](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)
![Docker](https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white)
![Ubuntu](https://img.shields.io/badge/ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)

[//]: # (Ubuntu 22.04.2 LTS)

### Config
![npm](https://img.shields.io/badge/npm-CB3837?style=for-the-badge&logo=npm&logoColor=white)
![gradle](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

### Development
![Spring](https://img.shields.io/badge/spring-green?logo=spring&style=for-the-badge&logoColor=white)
![SpringSecurity](https://img.shields.io/badge/spring_sequrity-6DB33F?logo=springboot&style=for-the-badge&logoColor=white)
![Mysql](https://img.shields.io/badge/mysql-4479A1?logo=mysql&style=for-the-badge&logoColor=white)
![React](https://img.shields.io/badge/React-61DAFB?logo=react&style=for-the-badge&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/aws-569A31?logo=amazonec2&style=for-the-badge&logoColor=white)
![S3](https://img.shields.io/badge/s3-569A31?logo=amazons3&style=for-the-badge&logoColor=white)
![intellijidea](https://img.shields.io/badge/intellij-000000?logo=intellijidea&style=for-the-badge&logoColor=white)
![nginx](https://img.shields.io/badge/intellij-000000?logo=nginx&style=for-the-badge&logoColor=white)

### Communication
![Slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=Slack&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)

---
## 화면 구성 📺
|대시보드|게시판|
|:---:|:---:|
|![로그인대시보드](https://github.com/algorithmQNA/algorithmQNA_frontend/assets/49019236/1f2b10e0-51ea-4c7d-a442-3c19764402c0)|![게시판필터링](https://github.com/algorithmQNA/algorithmQNA_frontend/assets/49019236/31ca9139-de0d-455e-a245-61228e988ed7)|
|전체게시판의 글을 간략하게 볼 수 있는 대시보드입니다. 로그인 후 접근 가능합니다.| 게시판에는 Q&A게시판, 꿀팁게시판이 있습니다. 여러가지 필터링과 정렬 기능을 사용할 수 있습니다.|
|**게시글작성**|**게시글보기**|
|![글작성](https://github.com/algorithmQNA/algorithmQNA_frontend/assets/49019236/82f2deef-2945-4ab2-b840-5f44e03b7e08)|![글보기및채택](https://github.com/algorithmQNA/algorithmQNA_frontend/assets/49019236/f99933f1-7c12-4c83-ae38-abb12d07b5c0)|
|게시글을 작성할 때 이미지, 코드블록을 이용해 구체적인 질문 사항이나 꿀팁을 작성할 수 있습니다. 또한 키워드도 등록이 가능합니다.|이용자들이 작성한 글을 읽고 댓글을 달거나 신고, 채택, 좋아요 표시 등의 기능을 수행할 수 있습니다.|
|**하이라이팅**| --|
| ![하이라이팅](https://github.com/algorithmQNA/algorithmQNA_frontend/assets/49019236/55355601-bb64-4a85-8e08-24265e8b7bfa)| . |
|알람을 받은 댓글이나 채택된 댓글로 바로 이동하고 하이라이팅해줍니다| . |
|**마이페이지**|**관리자페이지(신고)**|
|![마이페이지](https://github.com/algorithmQNA/algorithmQNA_frontend/assets/49019236/be8255de-c396-41c7-bc95-b80d43184e8f)|![신고합](https://github.com/algorithmQNA/algorithmQNA_frontend/assets/49019236/129f0282-f423-46e5-953d-fa0854cad808)|
|마이페이지에서 내 정보 수정, 내 뱃지, 내 활동 내역, 받은 알람등을 확인하고 관리가능합니다.| 관리자 페이지에서 글/댓글에 대한 신고를 관리, 공지사항을 관리할 수 있습니다.|

---


## 주요 기능 📦

### ⭐️ 소셜 로그인을 통한 회원가입
- Google Oauth를 통한 유저 로그인, 간편 회원가입 제공

### ⭐️ 알고리즘 카테고리별 Q&A, TIP 게시판 제공
- 게시글 작성 시 이미지 업로드 등 다양한 형태의 게시글을 작성할 수 있도록 하는 Editor 제공
- 제목, 글쓴이, 키워드로 검색과 댓글 유무, 채택 유무를 통하여 게시글을 필터링하는 기능 제공

### ⭐️ 신고된 게시글, 공지사항 등을 관리 가능한 관리자 페이지 기능

### ⭐️ 다양한 상황에서 알람 확인 가능
- 내가 쓴 게시글이나 댓글을 좋아요/싫어요 시 알람
- 다른 사람이 내가 쓴 게시글에 댓글을 작성 시, 내가 쓴 댓글에 대댓글을 작성 시 알람
- 관리자가 내 작성글을 삭제 시
- 댓글 또는 게시글 알람의 경우 해당 게시글 페이지로 이동 가능한 링크를 제공
- 댓글 알람의 경우 해당 게시글 페이지에 댓글이 위치한 페이지로 동적 이동 가능한 하이라이팅 기능 제공

---
## 제작 일정 (백앤드)
<img src="https://user-images.githubusercontent.com/37677461/246733314-58eb11c4-479c-4890-bae8-cff66de192b6.png" />

|           제목           |      기간       | 설명                                                                                                                |
|:----------------------:|:-------------:|:------------------------------------------------------------------------------------------------------------------|
|       프로젝트 초기 설계       | 04.23 ~ 05.07 | - ERD와 API 명세 설계 수행                                                                                               |
|   로그인 기능, Entity 설계    | 04.24 ~ 05.05 | - 초기 설계와 독립적인 Oauth 기능 구현<br> - ERD설계와 병렬적으로 Entity Class 구현                                                      |
|     댓글, 게시물 API 개발     | 05.06 ~ 05.13 | - API 설계에 따른 댓글, 게시물 CRUD 기능 구현<br> - 자동 배포를 위한 사전 조사 및 AWS 인스턴스 세팅                                               |
|     관리자, 멤버 API 개발     | 05.14 ~ 05.29 | - API 설계에 따른 관리자, 멤버 API 기능 구현<br> - 글 작성에 따른 멤버 뱃지 상태 변동 기능 추가                                                   |
|   코드 리팩토링 및 인증 방식 변경   | 05.23 ~ 06.04 | - 기존 controller, service 단 소스코드 리팩토링 및 버그 수정 <br> - Oauth에 의존한 인증 방식에서 인증 후 자체적인 jwt를 생상하는 인증 방식으로 변경 후 구현        |
| 알람 API 개발, 알람 생성 로직 추가 | 05.26 ~ 06.01 | - 특정 event 발생 시 각 유저에 해당하는 알람 데이터 생성하는 기능 및 알람 조회/삭제 API 개발 <br> - S3 이미지 서버 연동으로 멤버 프로필 변경기능 및 게시글 이미지 업로드 기능 구현 |
|        자동 배포 설정        | 06.05 ~ 06.08 | - git action을 통한 자동 배포 pipeline 구성 <br> - 프론트 배포구축을 위한 nginx 세팅<br> - host 임대 후 certbot을 이용한 ssl 적용               |
|         최종 테스트         | 06.09 ~ 06.31 | - 테스트코드 작성 및 오류 수정<br> - REAME.md 작성<br> - React 연동 후 단위기능 테스트                                                    |




## 아키텍쳐

### ERD 설계

<img src="https://user-images.githubusercontent.com/37677461/246724349-6ce065cd-cd40-43ff-a067-4e50dd39b9bf.png" />

### 서버 다이어그램

<img src="https://user-images.githubusercontent.com/37677461/246726962-ac98c418-7b44-4e47-a5e6-08dd13e46048.png" />

- 소셜 로그인 기능을 수행하기 위해서 Spring Security Oauth와 Google Oauth기능을 사용하여 구현했습니다. 로그인 성공 시 스프링에서 자체적으로 JWT 토큰을 provide하여 헤더 정보에 포함시킵니다.</br></br>
- 실제 배포 시 redirectURI에 production 환경 도메인을 설정하기 위해서 no-ip라는 무료 호스팅 사이트로부터 도메인을 발급받고, CA로는 무료 SSL 인증서 발급 기관인 Let's Encrypt를 사용하였습니다.</br></br>
- /api, /oauth 등 API 서버로의 리소스 요청에 해당하는 경우와, 정적 페이지 요청에 해당하는 경우를 분리하여 리버스 프록시를 적용하고자 nginx를 사용했습니다. 이 경우 요청 Origin이 동일하기 때문에 CORS를 적용할 필요가 없습니다.</br></br>
- 프론트단에서 리액트 서버를 따로 구동할 수도 있지만 서버 성능 및 비용을 절감하고자 webpack을 통한 build 파일을 Git action 수행 시 서버에 scp로 업로드하는 방식으로 구현하였습니다. </br></br>
- 메인 branch로 push시 docker를 통해 자동 배포할 수 있게끔 설정하였으며 algorithmQNA_backend/.github/workflows/main.yml을 참고해보세요!


### API Specification

[API 명세 노션 페이지](https://mysterious-drug-b34.notion.site/API-962eb2ef84e44e12ae4be892155f256e)







### 디렉토리 구조
```bash
algorithm_QnA_community
├── Dockerfile
├── build.gradle
├── docker-compose.yml
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── redis
│   └── dump.rdb
├── settings.gradle
├── src
│   ├── main
│   │   ├── generated
│   │   ├── java
│   │   │   └── algorithm_QnA_community
│   │   │       └── algorithm_QnA_community
│   │   │           ├── AlgorithmQnACommunityApplication.java
│   │   │           ├── InitDB.java
│   │   │           ├── api
│   │   │           │   ├── controller
│   │   │           │   │   ├── LikeReq.java
│   │   │           │   │   ├── MemberBriefDto.java
│   │   │           │   │   ├── ReportReq.java
│   │   │           │   │   ├── admin
│   │   │           │   │   │   ├── AdminApiController.java
│   │   │           │   │   │   ├── FlatCommentDto.java
│   │   │           │   │   │   ├── PostPageRes.java
│   │   │           │   │   │   ├── PostWithContentDto.java
│   │   │           │   │   │   ├── ReportCommentDto.java
│   │   │           │   │   │   ├── ReportPostDto.java
│   │   │           │   │   │   ├── ReportedCommentDetailRes.java
│   │   │           │   │   │   ├── ReportedCommentsRes.java
│   │   │           │   │   │   └── ReportedPostDetailRes.java
│   │   │           │   │   ├── alarm
│   │   │           │   │   │   ├── AlarmApiController.java
│   │   │           │   │   │   ├── AlarmDto.java
│   │   │           │   │   │   └── AlarmsRes.java
│   │   │           │   │   ├── comment
│   │   │           │   │   │   ├── CommentApiController.java
│   │   │           │   │   │   ├── CommentCreateReq.java
│   │   │           │   │   │   ├── CommentCreateRes.java
│   │   │           │   │   │   ├── CommentDetailRes.java
│   │   │           │   │   │   ├── CommentRes.java
│   │   │           │   │   │   ├── CommentsRes.java
│   │   │           │   │   │   ├── MoreCommentListRes.java
│   │   │           │   │   │   └── TopCommentRes.java
│   │   │           │   │   ├── member
│   │   │           │   │   │   ├── CommentPageRes.java
│   │   │           │   │   │   ├── MemberApiController.java
│   │   │           │   │   │   ├── MemberDetailDto.java
│   │   │           │   │   │   ├── MemberNameReq.java
│   │   │           │   │   │   └── MemberProfileDto.java
│   │   │           │   │   ├── post
│   │   │           │   │   │   ├── PostApiController.java
│   │   │           │   │   │   ├── PostCreateReq.java
│   │   │           │   │   │   ├── PostDetailRes.java
│   │   │           │   │   │   ├── PostSearchDto.java
│   │   │           │   │   │   ├── PostSimpleDto.java
│   │   │           │   │   │   ├── PostUpdateReq.java
│   │   │           │   │   │   ├── PostWriteRes.java
│   │   │           │   │   │   └── PostsResultRes.java
│   │   │           │   │   └── s3
│   │   │           │   │       ├── ImageUploadRes.java
│   │   │           │   │       └── S3ApiController.java
│   │   │           │   └── service
│   │   │           │       ├── admin
│   │   │           │       │   └── AdminService.java
│   │   │           │       ├── alarm
│   │   │           │       │   └── AlarmService.java
│   │   │           │       ├── comment
│   │   │           │       │   └── CommentService.java
│   │   │           │       ├── member
│   │   │           │       │   └── MemberService.java
│   │   │           │       ├── post
│   │   │           │       │   └── PostService.java
│   │   │           │       ├── s3
│   │   │           │       │   └── S3Service.java
│   │   │           │       └── scheduler
│   │   │           │           └── SchedulerService.java
│   │   │           ├── config
│   │   │           │   ├── RedisAndRestConfig.java
│   │   │           │   ├── S3Config.java
│   │   │           │   ├── auth
│   │   │           │   │   ├── AccessDeniedHandlerImpl.java
│   │   │           │   │   ├── AuthEntryPointJwt.java
│   │   │           │   │   ├── AuthTokenFilter.java
│   │   │           │   │   ├── CustomOAuth2UserService.java
│   │   │           │   │   ├── ExceptionHandlerFilter.java
│   │   │           │   │   ├── JwtTokenProvider.java
│   │   │           │   │   ├── OAuthController.java
│   │   │           │   │   ├── OAuthService.java
│   │   │           │   │   ├── PrincipalDetails.java
│   │   │           │   │   ├── SecurityConfig.java
│   │   │           │   │   └── dto
│   │   │           │   │       ├── AccessTokenAndRefreshUUID.java
│   │   │           │   │       ├── RefreshToken.java
│   │   │           │   │       ├── ResponseTokenAndMember.java
│   │   │           │   │       └── UserDetailsImpl.java
│   │   │           │   └── exception
│   │   │           │       ├── CustomException.java
│   │   │           │       ├── ErrorCode.java
│   │   │           │       ├── ExceptionHandlerAdvice.java
│   │   │           │       └── TokenAuthenticationException.java
│   │   │           ├── domain
│   │   │           │   ├── BaseTimeEntity.java
│   │   │           │   ├── alarm
│   │   │           │   │   ├── Alarm.java
│   │   │           │   │   └── AlarmType.java
│   │   │           │   ├── comment
│   │   │           │   │   └── Comment.java
│   │   │           │   ├── image
│   │   │           │   │   └── Image.java
│   │   │           │   ├── like
│   │   │           │   │   ├── LikeComment.java
│   │   │           │   │   └── LikePost.java
│   │   │           │   ├── member
│   │   │           │   │   ├── Badge.java
│   │   │           │   │   ├── Member.java
│   │   │           │   │   └── Role.java
│   │   │           │   ├── post
│   │   │           │   │   ├── Post.java
│   │   │           │   │   ├── PostCategory.java
│   │   │           │   │   ├── PostSortType.java
│   │   │           │   │   └── PostType.java
│   │   │           │   ├── report
│   │   │           │   │   ├── ReportCategory.java
│   │   │           │   │   ├── ReportComment.java
│   │   │           │   │   └── ReportPost.java
│   │   │           │   └── response
│   │   │           │       ├── CodeAndState.java
│   │   │           │       ├── DefStatus.java
│   │   │           │       ├── DefStatusWithBadRequest.java
│   │   │           │       ├── MemberInfoRes.java
│   │   │           │       ├── Res.java
│   │   │           │       ├── ResponseMessage.java
│   │   │           │       └── StatusCode.java
│   │   │           ├── error
│   │   │           ├── repository
│   │   │           │   ├── AlarmRepository.java
│   │   │           │   ├── CommentRepository.java
│   │   │           │   ├── ImageRepository.java
│   │   │           │   ├── LikeCommentRepository.java
│   │   │           │   ├── LikePostRepository.java
│   │   │           │   ├── MemberRepository.java
│   │   │           │   ├── PostRepository.java
│   │   │           │   ├── PostRepositoryCustom.java
│   │   │           │   ├── PostRepositoryImpl.java
│   │   │           │   ├── ReportCommentRepository.java
│   │   │           │   └── ReportPostRepository.java
│   │   │           └── utils
│   │   │               ├── BeanUtils.java
│   │   │               ├── EnumValidatorConstraint.java
│   │   │               ├── MultipartUtils.java
│   │   │               ├── annotation
│   │   │               │   └── EnumValidator.java
│   │   │               └── listner
│   │   │                   ├── CommentListener.java
│   │   │                   ├── LikeListener.java
│   │   │                   ├── MemberBadgeUpdateListener.java
│   │   │                   └── PostListener.java
│   │   └── resources
│   │       ├── application-test.yml
│   │       ├── application.yml
│   │       └── templates
│   │           ├── hello.html
│   │           ├── loginSuccess.html
│   │           └── ok.html
│   └── test 
│       ├── generated_tests
│       ├── java
│       │   └── algorithm_QnA_community
│       │       └── algorithm_QnA_community
│       │           ├── AlgorithmQnACommunityApplicationTests.java
│       │           ├── api
│       │           │   └── service
│       │           │       ├── admin
│       │           │       │   └── AdminServiceTest.java
│       │           │       ├── comment
│       │           │       │   └── CommentServiceTest.java
│       │           │       └── post
│       │           │           └── PostServiceTest.java
│       │           └── repository
│       │               ├── CommentRepositoryTest.java
│       │               ├── MemberRepositoryTest.java
│       │               └── PostRepositoryTest.java
│       └── resources
│           └── application.yml
└── README.md

```

