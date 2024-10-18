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

### 스키마 구조

![db-schema](/src/main/resources/static/images/db-schema.png)

<br/>
