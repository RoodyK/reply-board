# Reply Board

게시판을 만들며 학습하는 토이 프로젝트

### mysql DB

docker-compose.yml 파일을 실행해서 컨테이너 구성 `docker compose up -d`

## 스프링 시큐리티 6 버전

스프링 시큐리티 5버전 까지는 WebSecurityConfigurerAdapter 클래스를 상속헤서 설정 파일을 구성했다.  
스프링 시큐리티 6버전 부터는 SecurityFilterChain을 빈으로 직접 구현하면된다.

<br/>

### PasswordEncoder

비밀번호를 암호화할 때 주로 SHA256을 사용했었다. 하지만 스프링 시큐리티에서는 기본적으로 BCryptPasswordEncoder를 사용한 암호화를 권장한다.  

<br/>

### SHA256 (지양하자)

SHA256 암호화는 빠르게 해싱 될 수 있어서, 해커가 빠르게 해시를 생성해서 비밀번호를 추측할 수 있다. 그래서 무차별 대입(부르트 포스) 등의 공격이 쉬워진다.  
그리고 SHA256은 데이터 무결성이나 디지털 서명에 사용하기 위해 설계됐기 때문에 비밀번호 해싱에는 적합하지 않다.  
그리고 Salt 값을 사용하지 않기 때문에 동일한 비밀번호 문자열에 있어서 항상 동일한 해시값이 생성돼, 해커가 비밀번호를 쉽게 추축 가능하다.  

<br/>

### BCrypt, SCrypt (지향하자)

BCrypt, SCrypt는 비밀번호 해싱을 설계된 알고리즘으로 빠른 해싱보다는 보안성을 우선시한다.  
이 두 알고리즘은 해시 계산 속도를 조절할 수 있는 옵션을 제공한다. BCrypt는 CPU 성능에 맞춰 설계됐고, SCrypt는 메모리와 CPU 자원을 모두 고려해 설계됐다.

그리고 이 알고리즘들은 Salt값을 사용한 Salting을 지원한다. **솔트는 각 비밀번호에 고유한 랜덤 값을 추가해서 동일한 비밀번호여도 다른 값을 가지도록 한다.**  

프로젝트에서는 BCrypt 암호화를 사용했다.

<br/>

### 시큐리티 역할(Role)과 권한(Authority)

공부를 하며 알게된 것인데 이전까지는 Role, Authority 에서 Role은  prefix로 "ROLE_" 만 생략하고 "ADMIN" 과 같이만 작성했었고, Authority는 "ROLE_ADMIN" 과 같이 작성했었다.  

그냥 prefix 있고 없고의 차이 같지만 이 두 용어에는 차이가 있다.  

역할(Role)은 직잭을 말한다. 우리가 흔히 사용하는 관리자, 사용자, 중간 관리자, 방문자 등을 나타낸다.  
권한(Authority)은 사용자가 리소스에 접근할 수 있는 권한을 나타낸다. READ, WRITE, DELETE 처럼 세부적인 관리를 위해서 사용된다.

Role은 시큐리티에서 ROLE_ADMIN, ROLE_USER 처럼 패턴이 강제되지만, Authority는 패턴이 강제되지 않기 때문에 WRITE, READ_PRIVILEGE 등과 같이 사용 가능하다. 

<br/>

### 경로(리소스) 별 접근 처리

스프링 시큐리티에서 인증된되지 않은 사용자가 보호된 리소스에 접근할 때 호출되는 `AuthenticationEntryPoint`와 인증된 사용자지만 리소스 권한이 없을 때 호출되는 `AccessDeniedHandler`가 있다.  
이 두 객체의 호출과 `@EnableMethodSecurity` 애노테이션으로 권한을 설정했을 때의 예외 처리를 확인해본다.

```java
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/test").hasRole("ADMIN")
                                .anyRequest().permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper))
                )
        ;

        return http.build();
    }
}
```
<br/>

우선 이 두가지 케이스에 대해서 처리는 `AuthenticationEntryPoint`, `AccessDeniedHandler` 객체를 커스터마이징해서 내가 원하는 응답을 출력하게 할 수 있다. `AccessDeniedHandler` 구현 방법도 아래 코드와 다르지 않다.  

```java
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("[인증되지 않은 사용자가 접근함]", authException);

        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(null, ResultCode.UNAUTHORIZED, "로그인 후 이용해주세요");

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        objectMapper.writeValue(response.getWriter(), apiErrorResponse);
    }
}
```
<br/>

테스트할 컨트롤러의 요청 메서드를 생성하고 확인해준다.

```java
@Slf4j
@RestController
@RequestMapping("${api.url-prefix}")
@RequiredArgsConstructor
public class AuthController {
    @PostMapping("/test")
    public String test() {
        log.info("[Test 요청]");

        return "test";
    }
}
```
<br/>


`AuthenticationEntryPoint` 객체를 구현한 응답을 확인하면, 인증되지 않은 사용자가 접근했을 때 로그인 후 이용해달라는 메시지와 함꼐 JSON 응답을 내려주고 있다.
```json
{
    "result": null,
    "code": 1100,
    "message": "로그인 후 이용해주세요",
    "validation": {}
}
```

로그인 하지않고 권한이 필요한 리소스에 접근해서 확인하면 커스터마이징한 json 응답이 정상적으로 출력되는 것을 알 수 있다.  

<br/>

그러면 `@EnableMethodSecurity`를 사용한 방법을 확인해보자.   
`SecurityConfig`에 `@EnableMethodSecurity`를 적용하고 리소스 접근 권한을 설정할 메서드에 적용 후 리소스테 요청을 보내고 응답을 확인해본다.  
`@EnableMethodSecurity`를 설정 클래스에 적용하고 `requestMatchers` 부분을 주석 처리한다.

```java
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/v1/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/api/v1/test").hasRole("ADMIN")
                                .anyRequest().permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))
                        .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper))
                )
        ;

        return http.build();
    }
}
```
<br/>

이제 컨트롤러 내 메서드에 `@PreAuthorize("hasRole('ADMIN')")`를 적용해준다.

```java
@Slf4j
@RestController
@RequestMapping("${api.url-prefix}")
@RequiredArgsConstructor
public class AuthController {
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/test")
    public String test() {
        log.info("[Test 요청]");

        return "test";
    }
}
```
<br/>

애플리케이션을 실행한 뒤 "/api/v1/test" 로 post 요청을 보냈을 때 응답결과와 이전과 다르게 나타남을 확인할 수 있다.  

```json
{
    "result": false,
    "code": 10000,
    "message": "Access Denied",
    "validation": {}
}
```
<br/>

이전 방법과 응답이 달라진 것을 확인할 수 있다.

이는 CustomAuthenticationEntryPoint가 동작하지 않은 것을 확인할 수 있는데, 우리가 `.requestMatchers(...).hasRole(...)` 을 사용했을 때는 `SecurityFilterChain` 에서 접근을 제어한다. 필터에서는 리소스에 접근했을 때 `AuthenticationEntryPoint`나 `AccessDeniedHandler`를 호출해서 처리하게 된다.

하지만 `@PreAuthorize`와 같은 애노테이션 기반의 접근 제어를 하면 스프링 AOP 기반으로 처리하기 때문에 생성한 `@RestControllerAdvice` 클래스가 있다면 이곳에서 예외를 잡아서 처리하게 된다. 이 때문에 예외가 `@RestControllerAdvice`에서 처리된 것이다.

<br/>

### This object has already been built 에러

스프링 시큐리티에서 HttpSecurity 객체는 여러번 초기화되고 빌드될 수 없다. 여러 번 빌드하려고 할 때 "This object has already been built" 메시지와 함께 에러가 발생하는 것을 볼 수 있다. 