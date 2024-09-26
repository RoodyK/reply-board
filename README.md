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

## 프로젝트 frontend

reply-board-front 리포지토리의 vue 프로젝트를 빌드한 뒤 main/resources/static 하위에 옮겨서 html 구성  

- [reply-board-front](https://github.com/RoodyK/reply-board-front)

<br/>

## mysql DB

docker-compose.yml 파일을 실행해서 도커 컨테이너 구성 `docker compose up -d`

<br/>

# 스프링 시큐리티 6 버전

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

- 역할(Role)은 직책을 말한다. 우리가 흔히 사용하는 관리자, 사용자, 중간 관리자, 방문자 등을 나타낸다.  
- 권한(Authority)은 사용자가 리소스에 접근할 수 있는 권한을 나타낸다. READ, WRITE, DELETE 처럼 세부적인 관리를 위해서 사용된다.

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

이는 `CustomAuthenticationEntryPoint`가 동작하지 않은 것을 확인할 수 있는데, 우리가 `.requestMatchers(...).hasRole(...)` 을 사용했을 때는 `SecurityFilterChain` 에서 접근을 제어한다. 필터에서는 리소스에 접근했을 때 `AuthenticationEntryPoint`나 `AccessDeniedHandler`를 호출해서 처리하게 된다.

하지만 `@PreAuthorize`와 같은 애노테이션 기반의 접근 제어를 하면 스프링 AOP 기반으로 처리하기 때문에 생성한 `@RestControllerAdvice` 클래스가 있다면 이곳에서 예외를 잡아서 처리하게 된다. 이 때문에 예외가 `@RestControllerAdvice`에서 처리된 것이다.

<br/>

### This object has already been built 에러

스프링 시큐리티에서 HttpSecurity 객체는 여러번 초기화되고 빌드될 수 없다. 여러 번 빌드하려고 할 때 "This object has already been built" 메시지와 함께 에러가 발생하는 것을 볼 수 있다.

<br/>

### @WithMockUser 커스터 마이징

Controller 에서 `@AuthenticationPrincipal`을 사용하는 로직이 있는데 CustomUserDetails에서 참조하는 MemberDto 객체의 id값을 사용한다.  
하지만 스프링 시큐리티가 기본적으로 제공하는 `@WithMockUser`에서 사용가능한 프로퍼티는 다음과 같다.

```java
public @interface WithMockUser {
    String value() default "user";

    String username() default "";

    String[] roles() default {"USER"};

    String[] authorities() default {};

    String password() default "password";

    // ...
}
```

<br/>

하지만 컨트롤러에서는 MemberDto에서 id 값을 꺼내야 하므로 `@WithMockUser`를 커스터마이징해서 사용하기로 했다.
```java
// 커스터마이징한 UserDetails
@Getter
public class CustomUserDetails implements UserDetails {

    private final MemberDto memberDto;
    
    // ...
}
```

<br/>

`@WithMockUser`를 커스터마이징 할 때는 애노테이션을 생성한 뒤 `@WithSecurityContext` 애노테이션을 사용해서 `SecurityContext`를 직접 생성해줘야 한다.  
`@WithSecurityContext`의 `factory` 프로퍼티로 `SecurityContext`를 생성하는 클래스를 명시해준다.

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WithSecurityContext(factory = AdminMockSecurityContext.class) // 시큐리티 테스트용 SecurityContext 생성을 명시
public @interface CustomMockRoleAdmin {

    String email() default "admin3@gmail.com";

    String password() default "1234";

    String name() default "루디";

    Role role() default Role.ROLE_ADMIN;
}
```
<br/>

다음으로 factory로 지정한 `AdminMockSecurityContext`를 구현해준다.  
`WithSecurityContextFactory`를 구현해서 제네릭 타입으로 생성했던 애노테이션을 사용하면 된다.  
`createSecurityContext(CustomMockRoleAdmin annotation)` 메서드를 구현해줘야 하는데 파라미터에 있는 `annotation`은 우리가 생성했던 애노테이션이므로 default로 지정한 값들을 모두 사용할 수 있다.  


```java
@RequiredArgsConstructor
public class AdminMockSecurityContext implements WithSecurityContextFactory<CustomMockRoleAdmin> {

    @Override
    public SecurityContext createSecurityContext(CustomMockRoleAdmin annotation) {
        MemberDto memberDto = MemberDto.builder()
                .id(1L)
                .email(annotation.email())
                .password(annotation.password())
                .name(annotation.name())
                .roles(Set.of(new SimpleGrantedAuthority(annotation.role().name())))
                .build();

        CustomUserDetails details = new CustomUserDetails(memberDto);
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(details, memberDto.getPassword(), memberDto.getRoles());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(token);

        return securityContext;
    }
}
```

MemberDto와 CustomUserDetails를 설정하고 `UsernamePasswordAuthenticationToken`을 만들어 준 뒤 생성한 `SecurityContext`에 토큰을 넣고 리턴해줬다.  
이제 만들어진 `@CustomMockRoleAdmin`을 사용해본다.

```java
@CustomMockRoleAdmin
@DisplayName("카테고리를 등록한다.")
@Test
void addCategoryRoleAdmin() throws Exception {
    CreateCategoryRequest request = createCategoryRequest("기타");

    BDDMockito.given(categoryService.addCategory(anyLong(), any(CreateCategoryServiceRequest.class)))
            .willReturn(1L);

    mockMvc.perform(post("/api/v1/categories")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(objectMapper.writeValueAsString(request))
    )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.result").value(true))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data").value(1L))
            ;

    BDDMockito.then(categoryService).should().addCategory(anyLong(), any(CreateCategoryServiceRequest.class));
}
```

테스트에 만든 권한을 갖는 `UserDetails`가 적용되서 테스트가 통과함을 확인할 수 있다.

<br/>
<br/>

# Spring Rest Docs

토이 프로젝트에서 Rest Docs를 사용하면서 내용을 정리해본다.

<br/>


생성된 스니펫을 사용하기 위해서는 src/docs/asciidoc 디렉토리 경로에 `*.adoc`의 확장자를 갖는 파일을 만들어야 한다.
## API 문서

API 문서란 웹 애플리케이션을 개발하면서 만들어진 HTTP API를 개발자나 사용자 입자에서 쉽게 이해할 수 있도록 문서화한 자료를 말한다.  
API의 사용 방법, 엔드 포인트, 요청 및 응답 구조 등을 담고 있다.

<br/>
<br/>

### API 문서를 생성하는 이유
- API 문서는 팀 내부, 외부 개발자 및 사용자로 하여금 API를 이해할 수 있게 도와준다. API 문서가 없다면 직접 소스를 보고 파악해야 할 수도 있다.
- API 문서를 제대로 작성하면 시스템의 변경이 있을 때 영향이 있는 부분을 파악해서, 유지보수성을 높일 수 있다.
- 백엔드, 프론트엔드 협업 시 프론트드 개발자들이 API 문서를 통해 인터페이스의 이해를 도와준다.

<br/>
<br/>

### API 도구의 필요성
- API 문서를 수동으로 작성할 수도 있지만, 오타나 오류가 발생하기 쉽다. 자동화된 문서화 도구는 코드 변경 시 문서를 자동으로 갱신해 일관성을 유지해준다.
- 많은 문서화 도구가 테스트와 통합해서 API의 실제 동작을 문서로 생성한다. 이로 인해 문서와 실제 API의 불일치를 줄여준다.
- 문서화 도구는 HTML, PDF등 다양한 포맷으로 문서를 생성 가능하다.

<br/>
<br/>

## Swagger vs Rest Docs

스프링 부트에서는 API 문서화를 위해 주로 Swagger와 Rest Docs를 주로 사용하는 것 같다.  
Swagger는 아직 사용해본 적 없지만 Swagger와 Rest Docs의 특징과 Spring Rest Docs를 문서화 도구로 선택한 이유를 정리해본다.

<br/>
<br/>

### Swagger
- Swagger는 `@Api`, `@ApiOperation`, `@ApiParam` 등의 애노테이션을 기반으로 API를 문서화 한다. 개발자가 컨트롤러나 메서드에 애노테이션을 추가해서 API 동작을 설명하고, 이를 바탕으로 Swagger UI를 통해서 문서를 생성한다.
- Rest Docs에 비해 초기 구성이 간단하며, 의존성만 추가하면 애노테이션 기반의  설정이 가능하다.
- JSON, YAML 형식으로 API 정의 파일을 생성하고, Swagger UI를 통해 HTML 형태의 문서를 제공한다. 이 기본적으로 제공되는 UI 기능은 편하지만 커스터마이징에 제한적일 수 있다.
- Production 코드에 Swagger 코드가 포함되기 때문에 코드 가독성이나 유지보수성을 떨어트린다.
- 운영 환경에서 Swagger UI를 활성화하면, API 스펙이 외부에 노출될 수 있다.

<br/>
<br/>

### Rest Docs
- Rest Docs는 테스트를 기반으로 API를 문서화한다. 테스트가 통과한 것만 문서화하므로 실제 API와 문서의 일관성이 높아진다.
- 초기 구성이 Swagger에 비해 어려울 수 있다. build.gradle 설정 및 기본적으로 테스트 코드를 이해해야 하며 시간소모도 높다. 그리고 기본적으로 사용되는 asciidoc의 이해 및 약간의 mustache 문법의 이해도 필요하다.
- 기본적으로 Asciidoctor를 통해서 작성된 문서를 HTML, PDF 등의 형식으로 출력한다. 문서는 정적 페이지로 제공된다.
- Rest Docs는 테스트 코드를 기반으로 문서를 생성하기 때문에 신뢰성 있는 문서를 생성하며 프로덕션 코드에 영향을 주지 않는다.
- 문서 형식을 선택하거나 커스터마이징 할 수 있지만, 추가적인 설정 및 코드 작성이 필요하다.
- 스프링에 특화된 도구로 스프링 프레임워크를 사용하는 프로젝트와 호환성이 높다.

<br/>
<br/>

우선 둘 다 사용해본적 없는 입장에서 Swagger는 애노테이션 기반이기 때문에 프로덕션 코드가 더럽혀진다는 점이 별로 와닿지 않았다.  
Rest Docs는 테스트를 기반으로 동작하고 통과된 것만 문서화되기 때문에 테스트 코드의 중요성이 높아지는 현재 시기에 적합한 도구라는 생각이 들었다. 이런 이유로 Rest Docs를 사용하며 API를 문서화할 것이다.

<br/>
<br/>
<br/>

## Rest Docs 사용해보기

스프링 REST Docs는 공식 문서([Spring Rest Docs](https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/))의 최신 버전인 3.0.1 버전으로 사용할 것이다.

<br/>
<br/>

### build.gradle 설정

```
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.2.8'
	id 'io.spring.dependency-management' version '1.1.6'
	id "org.asciidoctor.jvm.convert" version "3.3.2" // Asciidoctor 플러그인 적용
}

group = 'com'
version = '0.0.1-SNAPSHOT'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
	asciidoctorExt // Asciidoctor 빌드 프로세스에 추가적인 플러그인이나 라이브러리를 포함해준다.
}

repositories {
	mavenCentral()
}

// 스크립트에서 재사용을 위한 변수(프로퍼티) 정의
ext {
	asciidocVersion = "3.0.1"
	snippetsDir = file('build/generated-snippets') // Snippet 파일들이 저장되는 디렉토리 지정
}

dependencies {
	// rest docs
	asciidoctorExt "org.springframework.restdocs:spring-restdocs-asciidoctor:${asciidocVersion}"
	testImplementation "org.springframework.restdocs:spring-restdocs-mockmvc:${asciidocVersion}"
}

tasks.named('test') {
	useJUnitPlatform()
}

jar {
	enabled = false
}

test {
	// 테스트 작업이 실행될 때 snippetsDir에 출력 파일들이 생성되도록 설정
	outputs.dir snippetsDir
}

// Asciidoctor 작업 설정
asciidoctor {
    // adoc 문서 파일의 기본 위치를 설정
    sourceDir = file('src/docs/asciidoc')
    // 생성된 HTML 파일의 출력을 설정
    outputDir = file('src/main/resources/static/docs')

    // gradle이 Asciidoctor 작업을 실행할 때 snippetsDir을 읽어 문서 Snippet을 생성하도록 설정
    inputs.dir snippetsDir
    // Asciidoctor 작업에서 사용할 configuration(외부 구성)을 asciidoctorExt로 설정. 추가 의존설을 지정할 때 사용
    configurations 'asciidoctorExt'
    // Asciidoctor의 작업이 test에 의존하도록 설정. Asciidoctor 작업이 실행되기 전에 test가 먼저 실행된다.
    dependsOn test
}

bootJar {
    // JAR 파일을 빌드할 때, 문서를 포함하도록 설정합니다.
    dependsOn asciidoctor
    // jar 파일에 포함할 파일 및 디렉토리를 지정
    // asciidoctor 출력 디렉토리가 jar 파일 내에 어느 위치에 포함될지 지정
    from("${asciidoctor.outputDir}") {
        into 'static/docs' // 
    }
}
```
<br/>

build.gradle에서 rest docs와 연관된 설정 및 의존성만 남겨두었고 설명은 주석으로 남겨두었다.  
<br/>

Snippet은 테스트 문서화 과정에서 생성되는 문서 조각을 의미한다. API 엔드포인트와 관련된 여러 정보를 포함할 수 있으며, 최종적으로 문서를 구성하는데 사용된다.

<br/>
<br/>

### Junit5 테스트 설정

Junit을 사용할 때 문서 Snippet을 생성하기 위해서는 `RestDocumentationExtension` 을 테스트 클래스를 적용해야 한다.

```java
@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(RestDocumentationExtension.class)
public class DocsPostControllerTest {
}
```
<br/>
<br/>

다음으로 MockMvc 관련 설정을 해야한다.

```java
private MockMvc mockMvc;

// 각 테스트를 수행하기 전 MockMvc를 준비하기 위한 메서드를 설정한다. MockMvc에 rest docs 설정을 적용한다.
@BeforeEach
void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
}
```
<br/>
<br/>

mockMvc 설정을 마치고 reference에 있는 예제 코드를 통해서 테스트 코드를 한번 실행할 것이다.

```java
    @CustomMockRoleAdmin
    @DisplayName("게시글 단건 조회 API 문서화")
    @Test
    void post() throws Exception {
        Member member = createMember();
        memberRepository.save(member);

        Category category = createCategory(member);
        categoryRepository.save(category);

        Post post = createPost(member, category);
        postRepository.save(post);

        this.mockMvc.perform(get("/api/v1/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-inquiry")); // snippet이 생길 디렉토리명
    }
```
<br/>

게시글 조회를 해야하기 때문에 게시글을 우선 저장한 뒤 엔드포인트로 요청을 보내는 mockMvc 테스트 코드를 작성한다. document로 지정한 디렉토리명으로 Snippet이 생기게 된다.

<br/>
<br/>



테스트 코드를 실행하면 build/generated-snippets/get-post 경로에 snippet 파일들이 생긴 것을 확인할 수 있다.
- curl-request.adoc: curl의 명령어를 통해 HTTP 요청을 어떻게 보낼 수 있는지 설명한다.
- http-request.adoc: Http 요청의 내용을 보여준다. 요청 메서드, url, header, body 등 정보가 포함된다.
- http-response.adoc: Http 응답의 내용을 보여준다. 응답 상태코드, header, body 등의 정보가 포함된다.
- httpie-request.adoc: CLI에서 사용되는 Http 클라이언트다. 사용하진 않을 것 같다.
- request-body.adoc: Http 요청 바디를 보여준다. 주로 JSON 형식의 데이터를 포함한다.
- response-body.adoc: Http 응답 바디를 보여준다. 주로 JSON 형식의 데이터를 포함한다.

<br/>
<br/>

### Snippet 사용하기

생성된 Snippet을 사용하기 위해서는 src/docs/asciidoc 디렉토리 경로에 `*.adoc`의 확장자를 갖는 파일을 만들어야 한다.  
<br/>

나는 게시글(Post)에 관한 문서를 생성하기 위해 src/docs/asciidoc/post 디렉토리 경로에 post.adoc 파일을 만들고 빌드 후 생성된 스니펫파일들을 include 했다.  
<br/>

다음은 post.adoc 파일이다.

```
= Reply Board API
:doctype: book
:icons: font
:source-highlighter: highlightjs
:toc: left
:toclevels: 2
:sectlinks:

== 게시글 단건 조회

=== curl 요청

include::{snippets}/post-inquiry/curl-request.adoc[]

=== 요청

include::{snippets}/post-inquiry/http-request.adoc[]

=== 응답

include::{snippets}/post-inquiry/http-response.adoc[]
```

<br/>

adoc 파일을 작성하고 빌드하면 src/main/resources/static/docs/post/post.html 파일이 생성된 것을 확인할 수 있는데, 애플리케이션을 실행 후 페이지에 가서 결과를 확인할 수 있다.

<br/>

## Asciidoctor 문법

Rest Docs를 학습하며 사용했던 adoc 문법을 정리한다. 지금 사용한것은 기본적인 것들이므로 추가적으로 Asciidoctor Reference를 통해 다양한 문법을 사용하면 된다.

<br/>

### 문서 제목 및 메타데이터

```
= Reply Board API
:doctype: book
:icons: font
:source-highlighter: highlightjs
:toc: left
:toclevels: 2
:sectlinks:
```

-   `:doctype: bock`: 문서의 유형을 지정한다. book은 책과 같은 긴 형식의 문서를 작성할 때 사용하며 article(기본값), manpage, inline 등이 있다.
-   `:icons: font`: 문서에서 아이콘 사용 방식을 지정한다. font는 FontAwesome과 같은 웹 글꼴 기반 아이콘을 사용한다.
-   `:source-highlighter: highlightjs`: 코드 블록에 대한 syntax highlighting을 지정한다. 여기서는 자바스크립트 기반의 highlightjs를 사용했다.
-   `:toc: left`: 목차(Table of Contents, TOC)를 문서 내에서 표시할 위치를 지정한다.
-   `:toclevels: 2`: 목차에 포함될 Section들의 dept를 지정한다. 레벨을 2로 지정한 것은 2 단계 Section의 제목까지 목차에 포함한다. 즉, 1단계(==), 2단계(===) 까지 포함된다.
-   `:sectlinks:`: 문서 내의 Section 제목을 하이퍼 링크로 만들어서 사용자가 문서의 다른 부분으로 쉽게 이동할 수 있다.


<br/>

### 기타 문법

제목 및 부제목은 등호(=)를 사용한다.

```
= API 문서 제목
== 1. 부제목
=== 2. 부제목
```

단락은 빈 줄 하나로 구분한다.

```
첫 번째 단락

두 번째 단락
```

목록은 `*`, `.`, `-` 기호로 작성한다.

```
* 첫 번째 항목
** 첫 번째 항목의 하위 항목
* 두 번째 항목

. 첫 번째 항목
.. 첫 번째 항목의 하위 항목
. 두 번째 항목
```

코드블록은 `----` 또는 `....` 구문을 사용한다. 대괄호 `[]`는 블록의 속성 설정의 링크, 이미지, 인용구 등을 정의한다.

```
[source,java]
----
public class Example {
    public static void main(String[] args) {
        System.out.println("Hello, REST DOCS!");
    }
}
----
```

테이블은 `|`기호를 사용한다.

```
|===
|이름 |설명

|이름1
|설명1

|이름2
|설명2
|===
```

링크는 `link:` 구문을 사용한다.

```
link:https://api.replyboard.com
```

이미지는 `image::` 구문을 사용한다.

```
image::path/image.png
```