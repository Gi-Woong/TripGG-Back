# 🚀 TripGG 백엔드 API 엔드포인트 가이드

## 📋 **기본 정보**
- **Base URL**: `http://localhost:8080`
- **응답 형식**: 모든 API는 `ApiResponse<T>` 형태로 응답
- **인증**: JWT 토큰 기반 인증 시스템 (대부분의 API는 인증 필요)
- **공개 API**: `/auth/**`, `/static/**`, `/api/places/search` 등은 인증 없이 접근 가능

## 📊 **ApiResponse 구조**
```json
{
  "success": true,
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": { /* 실제 데이터 */ },
  "timestamp": 1703123456789
}
```

---

## 🔐 **1. 인증 API (공개)**

| 메서드 | 엔드포인트 | 설명 | 실제 응답 데이터 |
|--------|------------|------|------------------|
| `GET` | `/auth/kakao/login` | 카카오 로그인 시작 (리다이렉트) | ```json<br>HTTP 302 Found<br>Location: https://kauth.kakao.com/oauth/authorize?client_id=169cf2b3607149a96a21cb8461486eba&redirect_uri=http://localhost:8080/auth/kakao/callback&response_type=code<br><br>카카오 로그인 페이지로 리다이렉트``` |
| `POST` | `/auth/kakao/login` | 카카오 로그인 처리 (JWT 토큰 발급) | ```json<br>{<br>  "success": true,<br>  "message": "카카오 로그인 성공",<br>  "data": {<br>    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "isNewUser": false<br>  },<br>  "timestamp": 1703123456789<br>}``` |

---

## 👥 **2. 사용자 관리 API (JWT 인증 필요)**

| 메서드 | 엔드포인트 | 설명 | 실제 응답 데이터 |
|--------|------------|------|------------------|
| `GET` | `/users` | 전체 사용자 목록 조회 | ```json<br>{<br>  "success": true,<br>  "message": "사용자 목록을 성공적으로 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/users/{id}` | 특정 사용자 조회 | ```json<br>{<br>  "success": true,<br>  "message": "사용자 정보를 성공적으로 조회했습니다.",<br>  "data": {<br>    "id": 1,<br>    "kakaoId": "test_kakao_id_001",<br>    "nickname": "테스트 사용자",<br>    "profileImageUrl": "https://example.com/profile.jpg",<br>    "createdAt": "2024-01-01T00:00:00",<br>    "updatedAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/users/my` | **현재 로그인한 사용자 정보 조회** | ```json<br>{<br>  "success": true,<br>  "message": "현재 사용자 정보를 성공적으로 조회했습니다.",<br>  "data": {<br>    "id": 1,<br>    "kakaoId": "test_kakao_id_001",<br>    "nickname": "테스트 사용자",<br>    "profileImageUrl": "https://example.com/profile.jpg",<br>    "createdAt": "2024-01-01T00:00:00",<br>    "updatedAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `POST` | `/users` | 사용자 생성 | ```json<br>{<br>  "success": true,<br>  "message": "사용자가 성공적으로 생성되었습니다.",<br>  "data": {<br>    "id": 2,<br>    "kakaoId": "kakao_user_123",<br>    "nickname": "홍길동",<br>    "profileImageUrl": "https://k.kakaocdn.net/dn/profile.jpg",<br>    "createdAt": "2024-01-01T00:00:00",<br>    "updatedAt": null<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `PUT` | `/users/{id}` | 사용자 정보 수정 | ```json<br>{<br>  "success": true,<br>  "message": "사용자 정보가 성공적으로 수정되었습니다.",<br>  "data": {<br>    "id": 1,<br>    "kakaoId": "test_kakao_id_001",<br>    "nickname": "수정된 닉네임",<br>    "profileImageUrl": "https://example.com/new_profile.jpg",<br>    "createdAt": "2024-01-01T00:00:00",<br>    "updatedAt": "2024-01-01T12:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `DELETE` | `/users/{id}` | 사용자 삭제 | ```json<br>{<br>  "success": true,<br>  "message": "사용자가 성공적으로 삭제되었습니다.",<br>  "data": "삭제 완료",<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/users/kakao/{kakaoId}` | 카카오 ID로 사용자 조회 | ```json<br>{<br>  "success": true,<br>  "message": "카카오 ID로 사용자 정보를 성공적으로 조회했습니다.",<br>  "data": {<br>    "id": 1,<br>    "kakaoId": "test_kakao_id_001",<br>    "nickname": "테스트 사용자",<br>    "profileImageUrl": "https://example.com/profile.jpg",<br>    "createdAt": "2024-01-01T00:00:00",<br>    "updatedAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |

---

## 🏛️ **2. 장소 관리 API**

| 메서드 | 엔드포인트 | 설명 | 실제 응답 데이터 |
|--------|------------|------|------------------|
| `GET` | `/api/places` | 전체 장소 목록 조회 | ```json<br>{<br>  "success": true,<br>  "message": "전체 장소 목록을 성공적으로 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "name": "스타벅스 강남점",<br>      "category": "CE7",<br>      "address": "서울시 강남구 테헤란로 123",<br>      "latitude": 37.4979,<br>      "longitude": 127.0276,<br>      "description": "강남역 근처 스타벅스",<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/places/{id}` | 특정 장소 조회 | ```json<br>{<br>  "success": true,<br>  "message": "장소를 성공적으로 조회했습니다.",<br>  "data": {<br>    "id": 1,<br>    "name": "스타벅스 강남점",<br>    "category": "CE7",<br>    "address": "서울시 강남구 테헤란로 123",<br>    "latitude": 37.4979,<br>    "longitude": 127.0276,<br>    "description": "강남역 근처 스타벅스",<br>    "createdAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `POST` | `/api/places` | 장소 생성 | ```json<br>{<br>  "success": true,<br>  "message": "장소가 성공적으로 생성되었습니다.",<br>  "data": {<br>    "id": 2,<br>    "name": "맥도날드 강남점",<br>    "category": "FD6",<br>    "address": "서울시 강남구 테헤란로 456",<br>    "latitude": 37.4980,<br>    "longitude": 127.0277,<br>    "description": "강남역 근처 맥도날드",<br>    "createdAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `PUT` | `/api/places/{id}` | 장소 수정 | ```json<br>{<br>  "success": true,<br>  "message": "장소가 성공적으로 수정되었습니다.",<br>  "data": {<br>    "id": 1,<br>    "name": "스타벅스 강남점 (수정)",<br>    "category": "CE7",<br>    "address": "서울시 강남구 테헤란로 123",<br>    "latitude": 37.4979,<br>    "longitude": 127.0276,<br>    "description": "강남역 근처 스타벅스 (수정됨)",<br>    "createdAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `DELETE` | `/api/places/{id}` | 장소 삭제 | ```json<br>{<br>  "success": true,<br>  "message": "장소가 성공적으로 삭제되었습니다.",<br>  "data": "삭제 완료",<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/places/category/{category}` | 카테고리별 장소 조회 | ```json<br>{<br>  "success": true,<br>  "message": "카테고리별 장소를 성공적으로 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "name": "스타벅스 강남점",<br>      "category": "CE7",<br>      "address": "서울시 강남구 테헤란로 123",<br>      "latitude": 37.4979,<br>      "longitude": 127.0276,<br>      "description": "강남역 근처 스타벅스",<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/places/search/name` | 이름으로 장소 검색 | ```json<br>{<br>  "success": true,<br>  "message": "이름으로 장소 검색이 완료되었습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "name": "스타벅스 강남점",<br>      "category": "CE7",<br>      "address": "서울시 강남구 테헤란로 123",<br>      "latitude": 37.4979,<br>      "longitude": 127.0276,<br>      "description": "강남역 근처 스타벅스",<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/places/search/address` | 주소로 장소 검색 | ```json<br>{<br>  "success": true,<br>  "message": "주소로 장소 검색이 완료되었습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "name": "스타벅스 강남점",<br>      "category": "CE7",<br>      "address": "서울시 강남구 테헤란로 123",<br>      "latitude": 37.4979,<br>      "longitude": 127.0276,<br>      "description": "강남역 근처 스타벅스",<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/places/search/coordinates` | 좌표 범위 내 장소 조회 | ```json<br>{<br>  "success": true,<br>  "message": "좌표 범위 내 장소를 성공적으로 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "name": "스타벅스 강남점",<br>      "category": "CE7",<br>      "address": "서울시 강남구 테헤란로 123",<br>      "latitude": 37.4979,<br>      "longitude": 127.0276,<br>      "description": "강남역 근처 스타벅스",<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/places/category/{category}/count` | 카테고리별 장소 개수 조회 | ```json<br>{<br>  "success": true,<br>  "message": "카테고리별 장소 개수를 성공적으로 조회했습니다.",<br>  "data": 5,<br>  "timestamp": 1703123456789<br>}``` |

### **카카오맵 API 연동 (중요!)**
| 메서드 | 엔드포인트 | 설명 | 실제 응답 데이터 |
|--------|------------|------|------------------|
| `GET` | `/api/places/search` | 카테고리별 장소 검색 (카카오맵 API) | ```json<br>{<br>  "success": true,<br>  "message": "카테고리 기반 장소 검색이 완료되었습니다.",<br>  "data": [<br>    {<br>      "id": "dummy_1",<br>      "placeName": "FD6 더미 장소 1",<br>      "categoryName": "음식점 > 카페",<br>      "categoryGroupCode": "FD6",<br>      "categoryGroupName": "음식점",<br>      "phone": "02-1234-5671",<br>      "addressName": "서울시 강남구 더미동 1번지",<br>      "roadAddressName": "서울시 강남구 더미로 1길",<br>      "longitude": 127.01,<br>      "latitude": 37.51,<br>      "placeUrl": "http://place.map.kakao.com/dummy1",<br>      "distance": "101",<br>      "source": "kakao",<br>      "description": null<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/places/detail/{placeId}` | 장소 상세 조회 | ```json<br>{<br>  "success": true,<br>  "message": "장소 상세 정보를 성공적으로 조회했습니다.",<br>  "data": {<br>    "id": "1",<br>    "placeName": "스타벅스 강남점",<br>    "categoryName": "CE7",<br>    "categoryGroupCode": "CE7",<br>    "categoryGroupName": "카페",<br>    "phone": null,<br>    "addressName": "서울시 강남구 테헤란로 123",<br>    "roadAddressName": null,<br>    "longitude": 127.0276,<br>    "latitude": 37.4979,<br>    "placeUrl": null,<br>    "distance": null,<br>    "source": "database",<br>    "description": "강남역 근처 스타벅스"<br>  },<br>  "timestamp": 1703123456789<br>}``` |

### **카테고리 코드**
- `MT1`: 대형마트
- `CS2`: 편의점  
- `FD6`: 음식점
- `CE7`: 카페
- `HP8`: 병원
- `PM9`: 약국

---

## 📅 **3. 일정 관리 API (JWT 인증 필요)**

| 메서드 | 엔드포인트 | 설명 | 실제 응답 데이터 |
|--------|------------|------|------------------|
| `GET` | `/schedules` | 전체 일정 조회 | ```json<br>{<br>  "success": true,<br>  "message": "전체 일정을 성공적으로 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "서울 여행 일정",<br>      "description": "1박 2일 서울 여행 계획",<br>      "isAiGenerated": false,<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/schedules/{id}` | 특정 일정 조회 | ```json<br>{<br>  "success": true,<br>  "message": "일정을 성공적으로 조회했습니다.",<br>  "data": {<br>    "id": 1,<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "title": "서울 여행 일정",<br>    "description": "1박 2일 서울 여행 계획",<br>    "isAiGenerated": false,<br>    "createdAt": "2024-01-01T00:00:00",<br>    "updatedAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `POST` | `/schedules` | **일정 생성** (현재 사용자 기준) | ```json<br>{<br>  "success": true,<br>  "message": "일정이 성공적으로 생성되었습니다.",<br>  "data": {<br>    "id": 2,<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "title": "부산 여행 일정",<br>    "description": "2박 3일 부산 여행 계획",<br>    "isAiGenerated": true,<br>    "createdAt": "2024-01-01T00:00:00",<br>    "updatedAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `PUT` | `/schedules/{id}` | 일정 수정 | ```json<br>{<br>  "success": true,<br>  "message": "일정이 성공적으로 수정되었습니다.",<br>  "data": {<br>    "id": 1,<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "title": "서울 여행 일정 (수정)",<br>    "description": "1박 2일 서울 여행 계획 (수정됨)",<br>    "isAiGenerated": false,<br>    "createdAt": "2024-01-01T00:00:00",<br>    "updatedAt": "2024-01-01T12:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `DELETE` | `/schedules/{id}` | 일정 삭제 | ```json<br>{<br>  "success": true,<br>  "message": "일정이 성공적으로 삭제되었습니다.",<br>  "data": "삭제 완료",<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/schedules/my-schedules` | **내 일정 목록 조회** | ```json<br>{<br>  "success": true,<br>  "message": "사용자 일정 목록을 성공적으로 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "서울 여행 일정",<br>      "description": "1박 2일 서울 여행 계획",<br>      "isAiGenerated": false,<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/schedules/my-ai-schedules` | **내 AI 생성 일정 조회** | ```json<br>{<br>  "success": true,<br>  "message": "AI 생성 일정을 성공적으로 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 2,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "부산 여행 일정",<br>      "description": "2박 3일 부산 여행 계획",<br>      "isAiGenerated": true,<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/schedules/search` | **내 일정 검색** | ```json<br>{<br>  "success": true,<br>  "message": "일정 검색이 완료되었습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "서울 여행 일정",<br>      "description": "1박 2일 서울 여행 계획",<br>      "isAiGenerated": false,<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/schedules/my-count` | **내 일정 개수 조회** | ```json<br>{<br>  "success": true,<br>  "message": "일정 개수를 성공적으로 조회했습니다.",<br>  "data": 3,<br>  "timestamp": 1703123456789<br>}``` |
| `POST` | `/schedules/ai-generate` | **AI 일정 생성** (GPT API 연동) | ```json<br>{<br>  "success": true,<br>  "message": "AI 일정이 성공적으로 생성되었습니다.",<br>  "data": {<br>    "schedule": {<br>      "id": 456,<br>      "userId": 123,<br>      "title": "서울 전역 여행",<br>      "description": "K-POP과 역사를 테마로 한 서울 여행",<br>      "isAiGenerated": true,<br>      "startDate": "2025-01-15T00:00:00.000Z",<br>      "endDate": "2025-01-17T00:00:00.000Z",<br>      "createdAt": "2025-01-10T10:30:00.000Z"<br>    },<br>    "scheduleItems": [<br>      {<br>        "id": 1001,<br>        "scheduleId": 456,<br>        "placeId": 2001,<br>        "day": 1,<br>        "orderInDay": 1,<br>        "memo": "조선왕조의 대표 궁궐",<br>        "startDate": "2025-01-15T00:00:00.000Z",<br>        "startTime": "09:00:00",<br>        "endTime": "10:00:00",<br>        "place": {<br>          "id": 2001,<br>          "name": "경복궁",<br>          "category": "관광",<br>          "description": "조선왕조의 대표 궁궐",<br>          "address": "서울특별시 종로구 사직로 161",<br>          "latitude": 37.5796,<br>          "longitude": 126.9770<br>        }<br>      }<br>    ]<br>  },<br>  "timestamp": 1757825204322<br>}``` |

---

## 🎥 **4. 비디오 관리 API (JWT 인증 필요)**

| 메서드 | 엔드포인트 | 설명 | 실제 응답 데이터 |
|--------|------------|------|------------------|
| `GET` | `/api/videos` | 전체 비디오 목록 조회 | ```json<br>{<br>  "success": true,<br>  "message": "전체 비디오 목록을 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "서울 여행 영상",<br>      "videoUrl": "https://example.com/video1.mp4",<br>      "description": "서울 여행 영상입니다",<br>      "tags": "서울,여행,관광",<br>      "likes": 10,<br>      "views": 100,<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/videos/{id}` | 특정 비디오 조회 | ```json<br>{<br>  "success": true,<br>  "message": "비디오를 성공적으로 조회했습니다.",<br>  "data": {<br>    "id": 1,<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "title": "서울 여행 영상",<br>    "videoUrl": "https://example.com/video1.mp4",<br>    "description": "서울 여행 영상입니다",<br>    "tags": "서울,여행,관광",<br>    "likes": 10,<br>    "views": 100,<br>    "createdAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/videos/my-videos` | **내 비디오 목록 조회** | ```json<br>{<br>  "success": true,<br>  "message": "사용자별 비디오 목록을 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "서울 여행 영상",<br>      "videoUrl": "https://example.com/video1.mp4",<br>      "description": "서울 여행 영상입니다",<br>      "tags": "서울,여행,관광",<br>      "likes": 10,<br>      "views": 100,<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `POST` | `/api/videos` | **비디오 생성** (현재 사용자 기준) | ```json<br>{<br>  "success": true,<br>  "message": "비디오가 성공적으로 생성되었습니다.",<br>  "data": {<br>    "id": 2,<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "title": "부산 여행 영상",<br>    "videoUrl": "https://example.com/video2.mp4",<br>    "description": "부산 여행 영상입니다",<br>    "tags": "부산,여행,바다",<br>    "likes": 0,<br>    "views": 0,<br>    "createdAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `PUT` | `/api/videos/{id}` | 비디오 수정 | ```json<br>{<br>  "success": true,<br>  "message": "비디오가 성공적으로 수정되었습니다.",<br>  "data": {<br>    "id": 1,<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "title": "서울 여행 영상 (수정)",<br>    "videoUrl": "https://example.com/video1.mp4",<br>    "description": "서울 여행 영상입니다 (수정됨)",<br>    "tags": "서울,여행,관광,수정",<br>    "likes": 10,<br>    "views": 100,<br>    "createdAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `DELETE` | `/api/videos/{id}` | **비디오 삭제** (현재 사용자 기준) | ```json<br>{<br>  "success": true,<br>  "message": "비디오가 성공적으로 삭제되었습니다.",<br>  "data": "삭제 완료",<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/videos/search/title` | 제목으로 비디오 검색 | ```json<br>{<br>  "success": true,<br>  "message": "제목으로 비디오를 검색했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "서울 여행 영상",<br>      "videoUrl": "https://example.com/video1.mp4",<br>      "description": "서울 여행 영상입니다",<br>      "tags": "서울,여행,관광",<br>      "likes": 10,<br>      "views": 100,<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/videos/search/tag` | 태그로 비디오 검색 | ```json<br>{<br>  "success": true,<br>  "message": "태그로 비디오를 검색했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "서울 여행 영상",<br>      "videoUrl": "https://example.com/video1.mp4",<br>      "description": "서울 여행 영상입니다",<br>      "tags": "서울,여행,관광",<br>      "likes": 10,<br>      "views": 100,<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/videos/popular/likes` | 인기 비디오 조회 (좋아요 기준) | ```json<br>{<br>  "success": true,<br>  "message": "인기 비디오를 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "서울 여행 영상",<br>      "videoUrl": "https://example.com/video1.mp4",<br>      "description": "서울 여행 영상입니다",<br>      "tags": "서울,여행,관광",<br>      "likes": 10,<br>      "views": 100,<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/videos/popular/views` | 인기 비디오 조회 (조회수 기준) | ```json<br>{<br>  "success": true,<br>  "message": "인기 비디오를 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "user": {<br>        "id": 1,<br>        "kakaoId": "test_kakao_id_001",<br>        "nickname": "테스트 사용자",<br>        "profileImageUrl": "https://example.com/profile.jpg",<br>        "createdAt": "2024-01-01T00:00:00",<br>        "updatedAt": "2024-01-01T00:00:00"<br>      },<br>      "title": "서울 여행 영상",<br>      "videoUrl": "https://example.com/video1.mp4",<br>      "description": "서울 여행 영상입니다",<br>      "tags": "서울,여행,관광",<br>      "likes": 10,<br>      "views": 100,<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |
| `POST` | `/api/videos/{id}/like` | 좋아요 증가 | ```json<br>{<br>  "success": true,<br>  "message": "좋아요가 증가했습니다.",<br>  "data": {<br>    "id": 1,<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "title": "서울 여행 영상",<br>    "videoUrl": "https://example.com/video1.mp4",<br>    "description": "서울 여행 영상입니다",<br>    "tags": "서울,여행,관광",<br>    "likes": 11,<br>    "views": 100,<br>    "createdAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |

---

## 💬 **5. 채팅 API**

| 메서드 | 엔드포인트 | 설명 | 실제 응답 데이터 |
|--------|------------|------|------------------|
| `GET` | `/api/chat` | 채팅방 자동 선택 (위치 기반) | ```json<br>HTTP 302 Found<br>Location: /api/chat/1<br><br>리다이렉트 응답 (브라우저에서 자동으로 이동)``` |
| `GET` | `/api/chat/{roomId}` | 채팅방 조회 | ```json<br>{<br>  "success": true,<br>  "message": "채팅방에 입장했습니다.",<br>  "data": {<br>    "roomId": 1,<br>    "roomName": "수원시 채팅방",<br>    "message": "채팅방에 입장했습니다."<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/chat/{roomId}/messages` | 채팅방 메시지 조회 | ```json<br>{<br>  "success": true,<br>  "message": "채팅방 메시지를 성공적으로 조회했습니다.",<br>  "data": {<br>    "locationMessage": "수원시 채팅방입니다",<br>    "messages": [<br>      {<br>        "id": 1,<br>        "user": {<br>          "id": 1,<br>          "kakaoId": "test_kakao_id_001",<br>          "nickname": "테스트 사용자",<br>          "profileImageUrl": "https://example.com/profile.jpg",<br>          "createdAt": "2024-01-01T00:00:00",<br>          "updatedAt": "2024-01-01T00:00:00"<br>        },<br>        "chatRoom": {<br>          "id": 1,<br>          "roomName": "수원시 채팅방",<br>          "latitude": 37.2636,<br>          "longitude": 127.0286,<br>          "locationName": "수원시",<br>          "radiusKm": 15.0,<br>          "isActive": true,<br>          "createdAt": "2024-01-01T00:00:00"<br>        },<br>        "message": "안녕하세요!",<br>        "createdAt": "2024-01-01T00:00:00"<br>      }<br>    ],<br>    "roomId": 1<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `POST` | `/api/chat/{roomId}/messages/send` | 메시지 전송 | ```json<br>{<br>  "success": true,<br>  "message": "메시지를 성공적으로 전송했습니다.",<br>  "data": {<br>    "id": 2,<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "chatRoom": {<br>      "id": 1,<br>      "roomName": "수원시 채팅방",<br>      "latitude": 37.2636,<br>      "longitude": 127.0286,<br>      "locationName": "수원시",<br>      "radiusKm": 15.0,<br>      "isActive": true,<br>      "createdAt": "2024-01-01T00:00:00"<br>    },<br>    "message": "안녕하세요! 반갑습니다.",<br>    "createdAt": "2024-01-01T00:00:00"<br>  },<br>  "timestamp": 1703123456789<br>}``` |
| `GET` | `/api/chat/rooms` | 모든 채팅방 목록 조회 | ```json<br>{<br>  "success": true,<br>  "message": "모든 채팅방을 성공적으로 조회했습니다.",<br>  "data": [<br>    {<br>      "id": 1,<br>      "roomName": "수원시 채팅방",<br>      "latitude": 37.2636,<br>      "longitude": 127.0286,<br>      "locationName": "수원시",<br>      "radiusKm": 15.0,<br>      "isActive": true,<br>      "createdAt": "2024-01-01T00:00:00"<br>    },<br>    {<br>      "id": 2,<br>      "roomName": "성남시 채팅방",<br>      "latitude": 37.4201,<br>      "longitude": 127.1268,<br>      "locationName": "성남시",<br>      "radiusKm": 15.0,<br>      "isActive": true,<br>      "createdAt": "2024-01-01T00:00:00"<br>    }<br>  ],<br>  "timestamp": 1703123456789<br>}``` |

### **경기도 31개 지역 매핑**
| roomId | 지역명 | roomId | 지역명 |
|--------|--------|--------|--------|
| 1 | 수원시 | 16 | 포천시 |
| 2 | 성남시 | 17 | 동두천시 |
| 3 | 고양시 | 18 | 광명시 |
| 4 | 용인시 | 19 | 군포시 |
| 5 | 부천시 | 20 | 양평군 |
| 6 | 안산시 | 21 | 양주시 |
| 7 | 안양시 | 22 | 구리시 |
| 8 | 평택시 | 23 | 오산시 |
| 9 | 화성시 | 24 | 하남시 |
| 10 | 남양주시 | 25 | 광주시 |
| 11 | 파주시 | 26 | 연천군 |
| 12 | 김포시 | 27 | 여주시 |
| 13 | 이천시 | 28 | 가평군 |
| 14 | 안성시 | - | - |
| 15 | 의정부시 | - | - |

---

## 🔐 **6. 인증 API**

| 메서드 | 엔드포인트 | 설명 | 실제 응답 데이터 |
|--------|------------|------|------------------|
| `GET` | `/auth/kakao/login` | 카카오 로그인 시작 | ```json<br>HTTP 302 Found<br>Location: https://kauth.kakao.com/oauth/authorize?client_id=169cf2b3607149a96a21cb8461486eba&redirect_uri=http://localhost:8080/auth/kakao/callback&response_type=code<br><br>카카오 로그인 페이지로 리다이렉트``` |
| `POST` | `/auth/kakao/login` | 카카오 로그인 처리 | ```json<br>{<br>  "success": true,<br>  "message": "카카오 로그인 성공",<br>  "data": {<br>    "accessToken": "jwt_token_1_1703123456789",<br>    "user": {<br>      "id": 1,<br>      "kakaoId": "test_kakao_id_001",<br>      "nickname": "테스트 사용자",<br>      "profileImageUrl": "https://example.com/profile.jpg",<br>      "createdAt": "2024-01-01T00:00:00",<br>      "updatedAt": "2024-01-01T00:00:00"<br>    },<br>    "isNewUser": false<br>  },<br>  "timestamp": 1703123456789<br>}``` |

---

## 🧪 **API 테스트 예시**

### **1. 카카오 로그인 (JWT 토큰 획득)**
```bash
curl -X POST "http://localhost:8080/auth/kakao/login?authorizationCode=<카카오_인가코드>"
```

### **2. JWT 토큰을 사용한 API 호출**
```bash
# 현재 사용자 정보 조회
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/users/my

# 내 일정 목록 조회
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/schedules/my-schedules

# 내 비디오 목록 조회
curl -H "Authorization: Bearer <JWT_TOKEN>" http://localhost:8080/api/videos/my-videos

# 일정 생성
curl -H "Authorization: Bearer <JWT_TOKEN>" -X POST http://localhost:8080/schedules \
  -H "Content-Type: application/json" \
  -d '{"title": "서울 여행", "description": "1박 2일 서울 여행 계획"}'

# AI 일정 생성
curl -H "Authorization: Bearer <JWT_TOKEN>" -X POST http://localhost:8080/schedules/ai-generate \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "title": "서울 전역 여행",
    "description": "K-POP과 역사를 테마로 한 서울 여행",
    "isAiGenerated": true,
    "startDate": "2025-01-15T00:00:00.000Z",
    "endDate": "2025-01-17T00:00:00.000Z",
    "region": "seoul-all",
    "keywords": ["kpop", "history", "koreanfood"],
    "companion": "alone",
    "transportation": "car",
    "language": "ko"
  }'
```

### **3. 공개 API (인증 불필요)**
```bash
# 카테고리별 장소 검색
curl -X GET "http://localhost:8080/api/places/search?category=FD6&x=127.0276&y=37.4979&radius=5000"

# 장소 상세 조회
curl -X GET "http://localhost:8080/api/places/detail/1"
```

### **4. 채팅 API (JWT 토큰 필요)**
```bash
# 채팅방 자동 선택
curl -H "Authorization: Bearer <JWT_TOKEN>" -X GET "http://localhost:8080/api/chat?latitude=37.26631826253257&longitude=127.00364147711848"

# 메시지 전송
curl -H "Authorization: Bearer <JWT_TOKEN>" -X POST "http://localhost:8080/api/chat/1/messages/send?message=안녕하세요&latitude=37.4979&longitude=127.0276"
```

---

## ⚠️ **주의사항**

1. **JWT 인증**: 대부분의 API는 JWT 토큰 인증이 필요합니다
2. **공개 API**: `/auth/**`, `/static/**`, `/api/places/search` 등은 인증 없이 접근 가능
3. **토큰 만료**: JWT 토큰은 24시간 후 만료됩니다
4. **CORS**: 모든 Origin에서 접근 가능하도록 설정됨
5. **에러 처리**: 모든 API는 일관된 에러 응답 형식 사용
6. **위치 기반 서비스**: 채팅방과 장소 검색은 사용자 위치 정보 필요
7. **카카오 API**: 카카오맵 API 키가 설정되어 있어야 정상 작동
8. **데이터베이스**: MySQL 데이터베이스 사용 (외부 서버)
9. **JPA 설정**: `ddl-auto: update` 사용으로 테이블 자동 업데이트

---

## 🔧 **개발 환경 설정**

### **필수 요구사항**
- Java 17 이상
- Maven 3.6 이상
- MySQL 데이터베이스 접근 권한

### **실행 방법**
```bash
# 프로젝트 디렉토리로 이동
cd TripGG-Back

# Maven으로 Spring Boot 애플리케이션 실행
mvn spring-boot:run
```

### **접속 URL**
- **메인 페이지**: http://localhost:8080/
- **카카오 로그인**: http://localhost:8080/login.html
- **API 서버**: http://localhost:8080/api

---

**마지막 업데이트**: 2024년 1월
**버전**: 1.0.0
