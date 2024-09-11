# GGG
- [프로젝트 구조](#프로젝트-구조)
- [설계](#설계)
  - [ERD](#erd)
  - [아키텍처](#아키텍처)
- [사용 기술](#사용-기술)
- [Swagger](#swagger)
- [트러블슈팅](#트러블슈팅)
- [TIL](#til)
- [Quick Start](#quick-start)

</br>

## 프로젝트 구조
이 프로젝트는 두 개의 모듈로 나뉘어 있습니다.

### ggg-authorization(인증서버)
- 역할 : 인증 및 권한 부여 처리, JWT 발급 및 검증, gRPC 서버 구현
- 디렉토리 구조 : tree

### ggg-resource(자원서버)
- 역할 : 자원 관리 및 주문 처리, gRPC 클라이언트 사용
- 디렉토리 구조 : tree

</br>

## 설계

### ERD
(이미지 삽입 예정)

### 아키텍처
(이미지 삽입 예정)

</br>

## 사용 기술
- Java 17
- SpringBoot 3.X
- JPA (ORM)
- gRPC
- MariaDB

</br>

## Swagger
- [ggg-authorization API](http://localhost:8888/swagger-ui/index.html#/)
- [ggg-resource API](http://localhost:9999/swagger-ui/index.html)

</br>

## 트러블슈팅
[✅ 로그인 시도 시에도 JWT 필터를 거치는 문제](https://complex-raptorex-908.notion.site/JWT-fbee0507969d4bc89b50a582fc09754b)

</br>

## TIL
[✅ DTO 타입으로 Record 사용해보기](https://complex-raptorex-908.notion.site/DTO-Class-Record-0a96b76c69654e8f8572dbc1e3f8de1a)

[✅ ObjectMapper가 뭘까?](https://complex-raptorex-908.notion.site/ObjectMapper-5552fc0ec1624d05943151045fbc5873?pvs=25)

</br>

## Quick Start
(추가 예정)
