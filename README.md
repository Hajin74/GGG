# GGG
- [프로젝트 구조](#프로젝트-구조)
- [Quick Start](#quick-start)
- [설계](#설계)
  - [ERD](#erd)
  - [아키텍처](#아키텍처)
- [사용 기술](#사용-기술)
- [Swagger](#swagger)
- [트러블슈팅](#트러블슈팅)
- [TIL](#til)

</br>

## 프로젝트 구조
이 프로젝트는 두 개의 모듈로 나뉘어 있습니다.

### ggg-authorization(인증서버)

<details>
<summary>디렉토리 구조</summary>
  
```bash
  src
  ├── main
  │   ├── java
  │   │   └── org
  │   │       └── example
  │   │           └── gggauthorization
  │   │               ├── GggAuthorizationApplication.java
  │   │               ├── aop
  │   │               │   ├── ErrorResponse.java
  │   │               │   └── GlobalExceptionHandler.java
  │   │               ├── auth
  │   │               │   ├── CustomLoginFilter.java
  │   │               │   ├── CustomLogoutFilter.java
  │   │               │   ├── CustomUserDetails.java
  │   │               │   ├── JwtFilter.java
  │   │               │   └── JwtService.java
  │   │               ├── config
  │   │               │   ├── SecurityConfig.java
  │   │               │   └── SwaggerConfig.java
  │   │               ├── controller
  │   │               │   ├── AuthController.java
  │   │               │   └── UserController.java
  │   │               ├── domain
  │   │               │   └── entity
  │   │               │       ├── RefreshToken.java
  │   │               │       └── User.java
  │   │               ├── dto
  │   │               │   ├── UserJoinRequest.java
  │   │               │   └── UserLoginRequest.java
  │   │               ├── exception
  │   │               │   ├── CustomException.java
  │   │               │   └── ErrorCode.java
  │   │               ├── grpc
  │   │               │   └── AuthServiceServer.java
  │   │               ├── repository
  │   │               │   ├── RefreshTokenRepository.java
  │   │               │   └── UserRepository.java
  │   │               └── service
  │   │                   ├── CustomUserDetailsService.java
  │   │                   └── UserService.java
  │   ├── proto
  │   │   └── auth.proto
  │   └── resources
  │       ├── application.properties
  └──     └── application.yml
```
</details>

- 역할 : 인증 및 권한 부여 처리, JWT 발급 및 검증, gRPC 서버 구현

### ggg-resource(자원서버)
<details>
<summary>디렉토리 구조</summary>
  
```bash
src
├── main
│   ├── java
│   │   └── org
│   │       └── example
│   │           └── gggresource
│   │               ├── GggResourceApplication.java
│   │               ├── aop
│   │               │   ├── ErrorResponse.java
│   │               │   └── GlobalExceptionHandler.java
│   │               ├── config
│   │               │   └── SwaggerConfig.java
│   │               ├── controller
│   │               │   ├── AuthController.java
│   │               │   └── OrderController.java
│   │               ├── domain
│   │               │   └── entity
│   │               │       ├── Order.java
│   │               │       └── Product.java
│   │               ├── dto
│   │               │   ├── InvoiceResponse.java
│   │               │   ├── LinkResponse.java
│   │               │   ├── OrderCreateRequest.java
│   │               │   ├── OrderCreateResponse.java
│   │               │   ├── OrderDetailResponse.java
│   │               │   ├── OrderStatusUpdateResponse.java
│   │               │   ├── PaginationResponse.java
│   │               │   └── UserResponse.java
│   │               ├── enums
│   │               │   ├── OrderStatus.java
│   │               │   ├── OrderType.java
│   │               │   ├── ProductType.java
│   │               │   └── PurityType.java
│   │               ├── exception
│   │               │   ├── CustomException.java
│   │               │   └── ErrorCode.java
│   │               ├── grpc
│   │               │   └── AuthServiceClient.java
│   │               ├── repository
│   │               │   ├── OrderRepository.java
│   │               │   └── ProductRepository.java
│   │               └── service
│   │                   └── OrderService.java
│   ├── proto
│   │   └── auth.proto
│   └── resources
│       ├── application.properties
└──     └── application.yml
```
</details>

- 역할 : 자원 관리 및 주문 처리, gRPC 클라이언트 사용


</br>

## Quick Start

- 이 가이드를 따라 `ggg-authorization`과 `ggg-resource` 서버를 빠르게 시작할 수 있습니다.
- Git, Java, MariaDB 는 설치되어 있다고 가정합니다.
</br>

**1. 프로젝트 클론**
  - 원하는 위치에서 프로젝트를 clone 합니다.
    ```sh
    $ git clone https://github.com/Hajin74/GGG.git
    $ cd GGG
    ```

 **2. application.properties 파일 추가**
 - `GGG/ggg-authorization/src/main/resources`와 `GGG/ggg-resource/src/main/resources` 디렉토리에 각각 제공된 `application.properties` 파일을 추가합니다.

 **3. 프로젝트 빌드**
 - 각각의 서버 디렉토리에서 다음 명령어를 실행하여 프로젝트를 빌드합니다
     ```sh
     $ ./gradlew build -x test
     ```

 **4-1. ggg-authorization 서버 실행**
   - `ggg-authorization` 서버 디렉토리로 이동한 후, 다음 명령어로 서버를 실행합니다.
     ```sh
     $ cd ggg-authorization/build/libs
     $ java -jar ggg-authorization-0.0.1-SNAPSHOT.jar
     ```

 **4-2. ggg-resource 서버 실행**
   - `ggg-resource` 서버 디렉토리로 이동한 후, 다음 명령어로 서버를 실행합니다.
     ```sh
     $ cd ggg-resource/build/libs
     $ java -jar ggg-resource-0.0.1-SNAPSHOT.jar
     ```
</br>

</br>

## API 명세서

### Swagger
실행이 되면, Swagger로 API 명세를 확인할 수 있습니다.
- [ggg-authorization API 바로가기](http://localhost:8888/swagger-ui/index.html#/)
- [ggg-resource API 바로가기](http://localhost:9999/swagger-ui/index.html)

### Postman
실행이 되면, Postman으로 API를 호출할 수 있습니다.
- [GGG Postman API 바로가기](https://documenter.getpostman.com/view/34589851/2sAXqmAk5W)

</br>

## 설계

### ERD
![erd](https://github.com/user-attachments/assets/ed4bd025-c178-4c45-b2e8-669dd839bdce)


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

## 트러블슈팅
[✅ 로그인 시도 시에도 JWT 필터를 거치는 문제](https://qwertyv.tistory.com/77)

[✅ 자원서버로부터 받은 토큰을 인증서버에서 어떻게 검증할까?](https://www.notion.so/26ed1962bf31425f9b1fc8c2432d40bd?pvs=4)

</br>

## TIL
[✅ DTO 타입으로 Record 사용해보기](https://complex-raptorex-908.notion.site/DTO-Class-Record-0a96b76c69654e8f8572dbc1e3f8de1a)

[✅ ObjectMapper가 뭘까?](https://complex-raptorex-908.notion.site/ObjectMapper-5552fc0ec1624d05943151045fbc5873)
