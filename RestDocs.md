
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