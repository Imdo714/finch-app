# 🏗️ 계층별 역할 및 책임 가이드 (Architecture Guidelines)

각 계층은 **철저한 관심사의 분리** 를 원칙으로 하며, 의존성은 항상 **외부에서 내부(Domain)** 로 향해야 합니다.

### 📋 한눈에 보는 요약표

| 계층 (Layer) | 핵심 역할 (Core Responsibility) | 주요 키워드 (Keywords) |
| :--- | :--- | :--- |
| **Presentation** | 외부 요청 처리 및 응답 반환 | `Controller`, `Web Adapter`, `DTO` |
| **Application** | 비즈니스 흐름 제어 및 트랜잭션 관리 | `UseCase`, `Service`, `Transaction` |
| **Domain** | 핵심 비즈니스 규칙 및 엔티티 상태 변경 | `Entity`, `VO`, `Domain Service` |
| **Infrastructure** | 기술적 구현 및 외부 시스템 연동 | `RepositoryImpl`, `JPA`, `Redis`, `Client` |

---

## 1. 🌐 Presentation Layer (표현 계층)
> **사용자의 요청을 받아 Application 계층으로 전달하고, 결과를 사용자에게 반환합니다.**

### ✅ 역할 (Responsibility)
- **API 엔드포인트:** REST API 요청을 받는 진입점 (`Controller`)
- **데이터 변환:** HTTP 요청(`Request Body`)을 Application 전용 `DTO`로 변환
- **응답 처리:** 처리 결과를 클라이언트가 이해할 수 있는 포맷(`Response DTO`, `HTTP Status`)으로 반환
- **보안:** 기본적인 인증/인가 필터링 및 외부 어댑터 역할

### 🚫 금지 (Restriction)
- **비즈니스 로직 포함 금지:** 순수한 전달자 역할만 수행해야 합니다.
- **`Entity` 노출 금지:** 클라이언트에게 도메인 `Entity`를 직접 반환하면 안 됩니다.
- **DB 직접 접근 금지:** `Repository`를 직접 호출하지 않습니다.

---

## 2. ⚙️ Application Layer (응용 계층)
> **도메인 객체와 인프라 자원을 조율하여 애플리케이션의 유스케이스를 실행합니다.**

### ✅ 역할 (Responsibility)
- **유스케이스(UseCase) 처리:** 구체적인 사용자 요구사항(기능) 구현
- **흐름 제어:** 도메인 객체를 가져오고(`Repository`), 로직을 실행하고(`Domain`), 저장하는 흐름 관리
- **트랜잭션 관리:** `@Transactional` 등을 통한 데이터 일관성 보장
- **입력값 검증:** 유효성 검사 수행
- **인터페이스 사용:** `Infrastructure`의 구현체를 직접 의존하지 않고 인터페이스를 통해 호출

### 🚫 금지 (Restriction)
- **비즈니스 판단 로직 금지:** "상태가 A면 B로 바꾼다"는 규칙은 `Domain`에 있어야 합니다. 여기서는 순서만 제어합니다.
- **기술 종속성 배제:** `HttpServletRequest`, `JWT`, `Redis` 등 구체적인 기술 관련 코드가 섞이면 안 됩니다. (POJO 지향)
- **SQL/Infra 코드 금지:** 데이터베이스 쿼리나 외부 API 호출 코드를 직접 작성하지 않습니다.

---

## 3. 🧠 Domain Layer (도메인 계층)
> **소프트웨어의 심장부로, 가장 변하지 않는 핵심 비즈니스 규칙을 담습니다.**

### ✅ 역할 (Responsibility)
- **핵심 모델 정의:** `Entity`, `Value Object(VO)`, `Aggregate` 정의
- **비즈니스 규칙 구현:** 엔티티 스스로 상태를 변경하는 메서드 (예: `user.activate()`)
- **도메인 서비스:** 엔티티만으로 처리하기 힘든 도메인 규칙 정의 (인터페이스 주도)
- **추상화된 인터페이스 제공:** 외부 기술이 필요한 기능(저장소, 암호화 등)을 위한 인터페이스 정의 (예: `UserRepository`, `JwtProvider`)

### 🚫 금지 (Restriction)
- **기술 의존성 제로(0):** 프레임워크(`Spring`), DB, UI 등 외부 기술에 대한 코드가 단 한 줄도 없어야 합니다.
- **외부 호출 금지:** `DB` 접근이나 외부 `API` 호출을 직접 수행하지 않습니다.

---

## 4. 🔌 Infrastructure Layer (인프라 계층)
> **도메인과 애플리케이션 계층에서 정의한 인터페이스를 실제 기술로 구현합니다.**

### ✅ 역할 (Responsibility)
- **기술 구현체:** `JPA`, `MyBatis`, `Redis` 등을 활용한 Repository 인터페이스 구현
- **외부 시스템 연동:** 외부 API(`Kakao`, `Apple`), 결제 시스템(`PG`) 호출 구현
- **기반 기술 지원:** `JWT` 발급 구현체(`JwtProviderImpl`), 파일 저장(`S3`) 등
- **어댑터:** 애플리케이션이 필요로 하는 기술적 기능을 실제로 동작하게 만듦

### 🚫 금지 (Restriction)
- **비즈니스 규칙 포함 금지:** 기술적인 동작만 담당해야 하며, 비즈니스 판단을 내려서는 안 됩니다.
- **도메인 오염 주의:** 구현체가 도메인 모델을 침범하거나 변경해서는 안 됩니다.
- **`Presentation` 참조 금지:** 컨트롤러 등을 역으로 참조하면 안 됩니다.

---

### 💡 핵심: 의존성 규칙 (Dependency Rule)
모든 소스 코드의 의존성은 반드시 **외부에서 내부**로 향해야 합니다.

- `Infrastructure` ➡️ `Application` ➡️ `Domain` (⭕)
- `Domain` ➡️ `Infrastructure` (❌ 절대 금지)

이 원칙을 지키기 위해 **DIP(의존성 역전 원칙)** 를 사용하여, `Domain`은 인터페이스를 정의하고 `Infrastructure`가 이를 구현(`Implements`)합니다.