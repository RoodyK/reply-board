# Reply Board

게시판을 만들며 학습하는 토이 프로젝트

```txt
backend  
- gradle 8.8
- spring-boot 3
- spring-data-jpa
- querydsl
- spring-security 6
- junit5
- mysql 8.0.28 (개발 및 배포)
- h2db (테스트)

frontend
- vue.js 3
- typescript
- vite 5
- axios 1.7.4
- bootstrap 5.3
- tsyringe
- class-transformer
```
<br/>
<br/>

## 프로젝트 frontend

reply-board-front 리포지토리의 vue 프로젝트를 빌드한 뒤 main/resources/static 하위에 옮겨서 html 구성  

- [reply-board-front](https://github.com/RoodyK/reply-board-front)

<br/>
<br/>

## mysql DB

### 스키마 구조

![db-schema](/src/main/resources/static/images/db-schema.png)

<br/>

### 프로젝트 local db 설정
프로젝트 루트 경로의 docker-compose.yml 파일을 실행해서 도커 컨테이너 구성  
`docker compose up -d`

<br/>
<br/>

## 주요 기능 정리

#### 스프링 시큐리티 설정
Spring Security 6 버전에서는 [Security 설정 클래스](/src/main/java/com/replyboard/config/SecurityConfig.java)에서 HttpSecurity 객체의 메서드 체이닝에서 `securityMatcher()` 메서드를 통해서 특정 경로의 요청이 들어왔을 때 우선 처리할 수 있게 설정할 수 있다.  
`http.securityMatcher()`를 설정하고 SecurityFilterChain을 설정한 메서드에 `@Order`를 통해 우선 순위를 설정해서 API 설정 메서드가 먼저 동작하도록 설정했다.
<br/>

#### 스프링 시큐리티 로그인 인증
1. UsernamePasswordAuthenticationFilter를 커스터마이징한 [CustomUsernamePasswordAuthenticationFilter](/src/main/java/com/replyboard/api/security/filter/CustomUsernamePasswordAuthenticationFilter.java) 객체를 구현해서 RestController로 요청이 오는 json 방식의 로그인을 처리한다.
2. UsernamePasswordAuthenticationToken을 전달받은 AuthenticationManager가 커스터마이징한 [CustomAuthenticationProvider](/src/main/java/com/replyboard/api/security/auth/CustomAuthenticationProvider.java)에게 토큰을 전달한다.
3. CustomAuthenticationProvider는 [CustomUserDetailsService](/src/main/java/com/replyboard/api/security/auth/CustomUserDetailsService.java)를 사용해서 [CustomUserDetails](/src/main/java/com/replyboard/api/security/auth/CustomUserDetails.java) 객체를 얻어와 인증을 처리하고 Authentication 객체를 반환한다.  

<br/>

#### 스프링 RestDocs 사용
게시글 API를 문서화 하기 위해서 Spring RestDocs 사용했다. RestDocs 사용 방법은  [블로그](https://dawncode.tistory.com/13)에 정리했다.

<br/>

#### 페이징 응답 객체
전역적으로 사용될 페이징을 공통 처리하기 위한 [PagingResponse](/src/main/java/com/replyboard/api/dto/PagingResponse.java) 객체를 정의했다.  

<br/>

#### 댓글 및 대댓글 기능 N + 1 문제
댓글 및 대댓글을 조회할 때 발생하는 컬렉션 페치 조인 문제를 default_batch_fetch_size를 통해서 최적화

<br/>
<br/>

## 프로젝트를 진행하며 학습한 내용 정리
- [Security.md](/src/main/resources/study/Security.md)
- [RestDocs.md](/src/main/resources/study/RestDocs.md)