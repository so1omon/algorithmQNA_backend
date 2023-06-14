# ALGO싶다 - ALgorithm Q&A Community

<div align="center">
<img width="329" alt="image" src="https://user-images.githubusercontent.com/50205887/207568862-cdc9e2c0-b03c-43ff-bf46-3ba79a110d0c.png">

[//]: # ([![Hits]&#40;https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FVoluntain-SKKU%2FVoluntain-2nd&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false&#41;]&#40;https://hits.seeyoufarm.com&#41;)

</div>


> **알고리즘 문제 풀이 꿀팁 공유 및 질문을 위한 커뮤니티** <br/> **개발기간: 2023.04 ~ 2023.06**

## 배포 주소

> **개발 버전** : [https://algoqna.ddns.net/](https://algoqna.ddns.net/) <br>

## 웹개발팀 소개

|                                       김솔민                                        |                  |               |                                                                                                               
|:--------------------------------------------------------------------------------:| :---------------------------------------------------------------------------------------------------------------------------------------------------: | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: | 
| <img width="160px" src="https://avatars.githubusercontent.com/u/37677461?v=4" /> |                         |                 |
|                      [@so1omon](https://github.com/so1omon)                      |     |   |
|                                    Backend 개발                                    |  | |

## 프로젝트 소개

알고리즘 분야에서의 학습과 문제 해결을 돕기 위해 만들어진 간편한 커뮤니티 사이트를 제작했습니다. 저희의 목표는 프로그래머스, 백준 저지 등 다양한 코딩 테스트 문제를 풀고 이에 대한 질의 응답과 풀이를 공유하는데 있습니다. 우리는 알고리즘 문제 풀이와 관련된 모든 분야에서 활동하는 개발자들을 위해 특정 온라인 저지 사이트에 관계 없이 유저들의 질문과 고민을 나누고 풀이를 공유할 수 있는 플랫폼을 제공합니다. 이 커뮤니티에서는 그래프, 탐색, 정렬, 다이나믹 프로그래밍 등 다양한 알고리즘 분류별로 질문과 꿀팁을 누구나 공유할 수 있습니다. 우리의 목표는 상호간에 지식을 나누고 향상시킴으로써 개발자들의 알고리즘 역량을 향상시키는 것입니다.

[//]: # (#### Voluntain will share the knowledge of programming.)

[//]: # (You can take easy-to-understand lectures created by our team, including Scratch and Python. Also, improve your understanding and application skills of programming with exercise questions in the lecture videos.)

[//]: # ()
[//]: # (#### Voluntain's website will give you a learning-friendly environment.)

[//]: # ()
[//]: # (Our website supports the following component.)

[//]: # (1. Various online lectures with detailed descriptions and exercise questions.)

[//]: # (2. Q&A section to ask questions freely.)

[//]: # (3. Function to check the lecture you watched recently.)

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
2. docker-compose.yml 파일 수정 : 도커허브 레포지토리에 있는 이미지 대신 Dockerfile을 통한 이미지 빌드로 전환하기
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
3. docker-compose 파일을 통한 이미지 필드
```bash
$ docker-compose build
```
4. 빌드한 이미지 파일로부터 컨테이너 실행 (백그라운드 실행 시 -d 옵션 추가)
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
| 메인 페이지  |  소개 페이지   |
| :-------------------------------------------: | :------------: |
|  <img width="329" src="https://user-images.githubusercontent.com/50205887/208036155-a57900f7-c68a-470d-923c-ff3c296ea635.png"/> |  <img width="329" src="https://user-images.githubusercontent.com/50205887/208036645-a76cf400-85bc-4fa2-af72-86d2abf61366.png"/>|  
| 강좌 소개 페이지   |  강의 영상 페이지   |  
| <img width="329" src="https://user-images.githubusercontent.com/50205887/208038737-2b32b7d2-25f4-4949-baf5-83b5c02915a3.png"/>   |  <img width="329" src="https://user-images.githubusercontent.com/50205887/208038965-43a6318a-7b05-44bb-97c8-b08b0495fba7.png"/>     |

---
## 주요 기능 📦

### ⭐️ 강좌 선택 및 강의 영상 시청 기능
- Scratch, Python 2개 강좌 및 각 강좌마다 10개 가량의 강의 영상 제공
- 추후 지속적으로 강좌 추가 및 업로드 예정

### ⭐️ 강의 관련 및 단체에 대한 자유로운 댓글 작성 가능
- Disqus를 이용하여 강의 관련 질문이나 단체에 대한 질문 작성 가능

### ⭐️ 이어 학습하기 기능
- Cookie 기능을 이용하여 이전에 학습했던 내용 이후부터 바로 학습 가능

---
## 아키텍쳐

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

