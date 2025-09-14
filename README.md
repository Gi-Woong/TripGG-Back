# TripGG Backend

여행 계획 및 관리 서비스를 위한 스프링 부트 백엔드 API 서버입니다.

## 🚀 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **JWT (JSON Web Token)**
- **MySQL Database**
- **Maven**
- **Lombok**

## 📁 프로젝트 구조 (Layered Architecture)

```
src/
├── main/
│   ├── java/
│   │   └── com/tripgg/
│   │       ├── common/                    # 공통 컴포넌트
│   │       │   ├── dto/                  # 공통 DTO
│   │       │   ├── exception/            # 예외 처리
│   │       │   └── util/                 # 유틸리티
│   │       ├── config/                   # 설정 클래스
│   │       ├── user/                     # 사용자 도메인
│   │       │   ├── controller/           # Presentation Layer
│   │       │   ├── service/              # Business Logic Layer
│   │       │   ├── repository/           # Data Access Layer
│   │       │   └── entity/               # Domain Model
│   │       ├── schedule/                 # 일정 도메인
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── repository/
│   │       │   └── entity/
│   │       ├── schedulePlaces/                    # 장소 도메인
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── repository/
│   │       │   └── entity/
│   │       ├── chat/                     # 채팅 도메인
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── repository/
│   │       │   └── entity/
│   │       ├── video/                    # 비디오 도메인
│   │       │   ├── controller/
│   │       │   ├── service/
│   │       │   ├── repository/
│   │       │   └── entity/
│   │       └── public/                   # 공개 API
│   │           └── controller/
│   └── resources/
│       └── application.yml               # 애플리케이션 설정
```

## 🏗️ 아키텍처 설명

### 레이어드 아키텍처 (Layered Architecture)
각 도메인은 다음과 같은 계층으로 구성됩니다:

1. **Presentation Layer (Controller)**
   - HTTP 요청/응답 처리
   - 입력값 검증
   - API 엔드포인트 제공

2. **Business Logic Layer (Service)**
   - 비즈니스 로직 처리
   - 트랜잭션 관리
   - 도메인 간 협력 조정

3. **Data Access Layer (Repository)**
   - 데이터베이스 접근
   - CRUD 작업 수행
   - 쿼리 최적화

4. **Domain Model (Entity)**
   - 비즈니스 엔티티 정의
   - 도메인 규칙 표현
   - 데이터 구조 정의

## 🛠️ 개발 환경 설정

### 필수 요구사항
- Java 17 이상
- Maven 3.6 이상
- MySQL 데이터베이스 접근 권한

### 설치 및 실행

1. **프로젝트 클론**
   ```bash
   git clone [repository-url]
   cd TripGG-Back
   ```

2. **의존성 설치 및 컴파일**
   ```bash
   mvn clean install
   ```

3. **애플리케이션 실행**

   **⚠️ 중요: PowerShell에서 실행해야 합니다!**

   ```bash
   # Windows PowerShell에서 실행 (권장)
   $env:DB_HOST=" "; $env:DB_PORT=" "; $env:DB_NAME=" "; $env:DB_USERNAME=" "; $env:DB_PASSWORD=" "; $env:JWT_SECRET=" "; mvn spring-boot:run
   ```

   **또는 단계별로 설정:**
   ```bash
   # 1단계: 환경 변수 설정
   $env:DB_HOST=" "
   $env:DB_PORT=" "
   $env:DB_NAME=" "
   $env:DB_USERNAME=" "
   $env:DB_PASSWORD=" "
   $env:JWT_SECRET=" "
   
   # 2단계: 애플리케이션 실행
   mvn spring-boot:run
   ```

   **🚨 주의사항:**
   - 반드시 **PowerShell**에서 실행해야 합니다
   - CMD나 Git Bash에서는 작동하지 않습니다
   - 환경 변수 설정 후 바로 `mvn spring-boot:run` 실행

4. **애플리케이션 접속**
                - 메인 페이지: http://localhost:8080/
                - 카카오 로그인: http://localhost:8080/login.html
                - API 서버: http://localhost:8080/api
                                       - 상태 확인: `curl http://localhost:8080/api/users`

### 🧪 API 테스트

애플리케이션 실행 후 다음 명령어로 API를 테스트할 수 있습니다:

```bash
# 1. 카카오 로그인 (JWT 토큰 획득)
curl -X POST "http://localhost:8080/auth/kakao/login?authorizationCode=<카카오_인가코드>"

# 2. JWT 토큰을 사용한 API 호출 (Authorization 헤더 필요)
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/users

# 3. 현재 사용자 정보 조회
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/users/my

# 4. 내 일정 목록 조회
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/schedules/my-schedules

# 5. 내 비디오 목록 조회
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/api/videos/my-videos

# 6. 장소 검색 (인증 불필요)
curl "http://localhost:8080/api/schedulePlaces/search?category=FD6&x=127.0276&y=37.4979&radius=5000"
```

### 🚨 중요 사항
- MySQL 데이터베이스가 자동으로 연결됩니다
- 첫 실행 시 테이블이 자동으로 생성됩니다 (`ddl-auto: update`)
- 테스트 데이터가 자동으로 삽입됩니다
- **JWT 인증 시스템**: 대부분의 API는 JWT 토큰 인증이 필요합니다
- **공개 API**: `/auth/**`, `/static/**`, `/api/schedulePlaces/search` 등은 인증 없이 접근 가능

### 🔐 환경 변수 설정

보안을 위해 민감한 정보는 환경 변수로 설정하세요:

**카카오 로그인 설정:**
```bash
# Windows PowerShell
$env:KAKAO_CLIENT_ID="your_kakao_client_id"
$env:KAKAO_CLIENT_SECRET="your_kakao_client_secret"
$env:KAKAO_REDIRECT_URI="http://localhost:8080/auth/kakao/callback"

# Windows CMD
set KAKAO_CLIENT_ID=your_kakao_client_id
set KAKAO_CLIENT_SECRET=your_kakao_client_secret
set KAKAO_REDIRECT_URI=http://localhost:8080/auth/kakao/callback

# Linux/Mac
export KAKAO_CLIENT_ID=your_kakao_client_id
export KAKAO_CLIENT_SECRET=your_kakao_client_secret
export KAKAO_REDIRECT_URI=http://localhost:8080/auth/kakao/callback
```

**카카오맵 API 설정:**
```bash
# Windows PowerShell
$env:KAKAO_MAP_API_KEY="your_kakao_map_rest_api_key"

# Windows CMD
set KAKAO_MAP_API_KEY=your_kakao_map_rest_api_key

# Linux/Mac
export KAKAO_MAP_API_KEY=your_kakao_map_rest_api_key
```

**✅ 카카오맵 API 활성화 완료:** 카카오 개발자 콘솔에서 카카오맵 서비스가 활성화되어 실제 장소 데이터를 제공합니다.

**데이터베이스 설정:**

```bash
# Windows PowerShell
$env:DB_HOST="your_database_host"
$env:DB_PORT="3306"
$env:DB_NAME="your_database_name"
$env:DB_USERNAME="your_database_username"
$env:DB_PASSWORD="your_secure_password"
$env:JWT_SECRET="your_jwt_secret_key"

# Windows CMD
set DB_HOST=your_database_host
set DB_PORT=3306
set DB_NAME=your_database_name
set DB_USERNAME=your_database_username
set DB_PASSWORD=your_secure_password
set JWT_SECRET=your_jwt_secret_key

# Linux/Mac
export DB_HOST=your_database_host
export DB_PORT=3306
export DB_NAME=your_database_name
export DB_USERNAME=your_database_username
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your_jwt_secret_key
```

또는 `.env` 파일을 생성하여 설정할 수 있습니다 (`.env` 파일은 자동으로 `.gitignore`에 포함됩니다).

**예시 .env 파일:**
```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tripgg_db
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key
```

## 🔐 기본 계정 정보

### 테스트 사용자 계정
- **카카오 ID**: test_kakao_id_001
- **닉네임**: 테스트 사용자

## 📚 API 엔드포인트

### 🔐 인증 API (공개)
- `GET /auth/kakao/login` - 카카오 로그인 시작 (리다이렉트)
- `POST /auth/kakao/login` - 카카오 로그인 처리 (JWT 토큰 발급)

### 👥 사용자 관리 API (JWT 인증 필요)
- `GET /users` - 전체 사용자 목록 조회
- `GET /users/{id}` - 특정 사용자 조회
- `GET /users/my` - **현재 로그인한 사용자 정보 조회**
- `POST /users` - 사용자 생성
- `PUT /users/{id}` - 사용자 정보 수정
- `DELETE /users/{id}` - 사용자 삭제
- `GET /users/kakao/{kakaoId}` - 카카오 ID로 사용자 조회

### 📅 일정 관리 API (JWT 인증 필요)
- `POST /schedules` - **일정 생성** (현재 사용자 기준)
- `GET /schedules/{id}` - 특정 일정 조회
- `GET /schedules/my-schedules` - **내 일정 목록 조회**
- `GET /schedules/my-ai-schedules` - **내 AI 생성 일정 조회**
- `GET /schedules/search` - **내 일정 검색**
- `PUT /schedules/{id}` - 일정 수정
- `DELETE /schedules/{id}` - 일정 삭제
- `GET /schedules/my-count` - **내 일정 개수 조회**

### 🏛️ 장소 관리 API (JWT 인증 필요)
- `POST /api/schedulePlaces` - 장소 생성
- `GET /api/schedulePlaces/{id}` - 특정 장소 조회
- `GET /api/schedulePlaces` - 전체 장소 목록 조회
- `GET /api/schedulePlaces/category/{category}` - 카테고리별 장소 조회
- `GET /api/schedulePlaces/search/name` - 이름으로 장소 검색
- `GET /api/schedulePlaces/search/address` - 주소로 장소 검색
- `GET /api/schedulePlaces/search/coordinates` - 좌표 범위 내 장소 조회
- `PUT /api/schedulePlaces/{id}` - 장소 수정
- `DELETE /api/schedulePlaces/{id}` - 장소 삭제
- `GET /api/schedulePlaces/category/{category}/count` - 카테고리별 장소 개수 조회

### 🏷️ 카테고리 기반 장소 검색 API (카카오맵 연동, 공개)
- `GET /api/schedulePlaces/search` - **카테고리별 장소 검색** (카카오맵 API 사용, 인증 불필요)
- `GET /api/schedulePlaces/detail/{placeId}` - **장소 상세 조회** (인증 불필요)
- **검색 파라미터**: category, x(경도), y(위도), radius, page, size, sort
- **카테고리 코드**: MT1(대형마트), CS2(편의점), FD6(음식점), CE7(카페), HP8(병원), PM9(약국) 등

### 🎥 비디오 관리 API (JWT 인증 필요)
- `GET /api/videos` - 전체 비디오 목록 조회
- `GET /api/videos/{id}` - 특정 비디오 조회
- `GET /api/videos/my-videos` - **내 비디오 목록 조회**
- `POST /api/videos` - **비디오 생성** (현재 사용자 기준)
- `PUT /api/videos/{id}` - 비디오 수정
- `DELETE /api/videos/{id}` - **비디오 삭제** (현재 사용자 기준)
- `GET /api/videos/search/title` - 제목으로 비디오 검색
- `GET /api/videos/search/tag` - 태그로 비디오 검색
- `GET /api/videos/popular/likes` - 인기 비디오 조회 (좋아요 기준)
- `GET /api/videos/popular/views` - 인기 비디오 조회 (조회수 기준)
- `POST /api/videos/{id}/like` - 좋아요 증가

### 💬 채팅 API (JWT 인증 필요)
- `GET /api/chat` - 채팅방 자동 선택 (위치 기반)
- `GET /api/chat/{roomId}` - 채팅방 조회
- `GET /api/chat/{roomId}/messages` - 채팅방 메시지 조회
- `POST /api/chat/{roomId}/messages/send` - **메시지 전송** (현재 사용자 기준)
- `GET /api/chat/rooms` - 모든 채팅방 목록 조회

## 🗄️ 데이터베이스

- **운영용**: MySQL 데이터베이스
- **DDL**: `validate` (기존 테이블 구조 검증)
- **연결 정보**: 환경 변수로 설정 (DB_HOST, DB_PORT, DB_NAME)

## 🔧 설정

주요 설정은 `src/main/resources/application.yml` 파일에서 관리됩니다:

- 서버 포트: 8080
- API 컨텍스트 경로: /api
- 데이터베이스: MySQL (환경 변수로 설정)
- JWT 시크릿 키 설정
- 로깅 레벨 설정

## 🧪 테스트

```bash
# 단위 테스트 실행
mvn test

# 통합 테스트 실행
mvn verify
```

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.
