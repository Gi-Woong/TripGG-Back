# TripGG Backend

여행 계획 및 관리 서비스를 위한 스프링 부트 백엔드 API 서버입니다.

## 🚀 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
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
│   │       ├── place/                    # 장소 도메인
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
# 사용자 목록 조회
curl http://localhost:8080/api/users

# 일정 목록 조회  
curl http://localhost:8080/api/schedules

# 장소 목록 조회
curl http://localhost:8080/api/places

# 채팅 목록 조회
curl http://localhost:8080/api/chats

# 비디오 목록 조회
curl http://localhost:8080/api/videos
```

### 🚨 중요 사항
- MySQL 데이터베이스가 자동으로 연결됩니다
- 첫 실행 시 테이블이 자동으로 생성됩니다 (`ddl-auto: create`)
- 테스트 데이터가 자동으로 삽입됩니다
- 현재 모든 API가 인증 없이 접근 가능합니다 (개발용 설정)

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



### 사용자 관리 API
- `GET /api/users` - 전체 사용자 목록 조회
- `GET /api/users/{id}` - 특정 사용자 조회
- `POST /api/users` - 사용자 생성
- `PUT /api/users/{id}` - 사용자 정보 수정
- `DELETE /api/users/{id}` - 사용자 삭제
- `GET /api/users/kakao/{kakaoId}` - 카카오 ID로 사용자 조회

### 일정 관리 API
- `POST /api/schedules` - 일정 생성
- `GET /api/schedules/{id}` - 특정 일정 조회
- `GET /api/schedules/user/{userId}` - 사용자별 일정 목록 조회
- `GET /api/schedules/user/{userId}/ai-generated` - AI 생성 일정 조회
- `GET /api/schedules/user/{userId}/search` - 일정 검색
- `PUT /api/schedules/{id}` - 일정 수정
- `DELETE /api/schedules/{id}` - 일정 삭제
- `GET /api/schedules/user/{userId}/count` - 사용자별 일정 개수 조회

### 장소 관리 API
- `POST /api/places` - 장소 생성
- `GET /api/places/{id}` - 특정 장소 조회
- `GET /api/places` - 전체 장소 목록 조회
- `GET /api/places/category/{category}` - 카테고리별 장소 조회
- `GET /api/places/search/name` - 이름으로 장소 검색
- `GET /api/places/search/address` - 주소로 장소 검색
- `GET /api/places/search/coordinates` - 좌표 범위 내 장소 조회
- `PUT /api/places/{id}` - 장소 수정
- `DELETE /api/places/{id}` - 장소 삭제
- `GET /api/places/category/{category}/count` - 카테고리별 장소 개수 조회

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

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.
