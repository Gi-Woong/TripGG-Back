# Copilot Instructions

## Overview
이 프로젝트는 **Spring Boot 3.x 기반 백엔드 애플리케이션**으로,  
Java 17과 Maven을 사용하며 Tibero 데이터베이스와 JPA를 통해 데이터를 관리합니다.  
Copilot은 **Spring Boot 개발 컨벤션**, **TDD(Test-Driven Development)**, **SOLID 원칙**, **Clean Code 원칙**을 철저히 준수해야 합니다.

---

## Goals
- 읽기 쉽고 유지보수 가능한 코드를 작성
- 테스트 가능한 구조로 코드 설계
- 서비스 로직과 인프라 로직을 명확히 분리
- 불필요한 주석, 로깅, 중복 제거
- 실용적이고 검증된 코드 생성

---

## Coding Style
- **중괄호 스타일:** K&R (한 줄 아래 `{}`)
- **들여쓰기:** 4 spaces
- **클래스:** PascalCase  
- **메서드/변수:** camelCase  
- **상수:** UPPER_SNAKE_CASE  
- **주석:** "무엇(what)"이 아니라 "왜(why)"를 설명
- **Lombok:** `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor` 적극 활용
- **로깅:** `@Slf4j` 사용, `System.out.println()` 금지

---

## Framework Conventions
- Controller → Service → Repository 구조 준수
- `@Transactional`은 Service 계층에만 명시
- DTO와 Entity를 명확히 구분
- `application.yml`의 다중 프로필(`local`, `dev`, `prod`) 환경 인식
- 예외 처리는 `@ControllerAdvice` + `@ExceptionHandler`로 통합 관리
- Bean 주입은 **필드 주입 금지**, 반드시 **생성자 주입** 사용

---

## TDD & Test Guidelines
- Copilot은 테스트 코드를 **항상 함께 제안**해야 함  
  (예: 서비스 메서드 작성 시 대응하는 테스트 메서드 포함)
- 테스트 클래스명: `*Test`
- 테스트 메서드명: `should_행동_결과` 형식 사용  
  예: `should_SaveAccount_WhenValidDataGiven()`
- 테스트 프레임워크: JUnit 5 (`@Test`, `@DisplayName`)
- Mock 객체: `Mockito` 또는 `@MockBean` 활용
- 단위 테스트 → 통합 테스트 → E2E 순으로 우선순위
- 테스트는 **AAA 패턴(Arrange–Act–Assert)** 구조를 유지해야 함
