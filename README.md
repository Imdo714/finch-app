# 🏗️ 계층별 역할 및 책임 가이드 (Architecture Guidelines)

이 가이드는 **DDD(도메인 주도 설계)** 와 **핵사고날 아키텍처(Hexagonal Architecture)** 를 기반으로 설계되었습니다. 특히 **관심사의 분리**와 **의존성 역전(DIP)** 을 통해 외부 기술 변화에 도메인이 오염되지 않도록 설계하였습니다.

---

### 📋 한눈에 보는 패키지 구조도

| 계층 (Layer) | 패키지 경로 (Package) | 주요 구성 요소 (Components) |
| :--- | :--- | :--- |
| **Presentation** | `presentation/` | `controller/`, `dto/` |
| **Application** | `application/` | `service/`, `port/in/`, `port/out/` |
| **Domain** | `domain/` | `model/entity/`, `repository/`, `service/` |
| **Infrastructure** | `infrastructure/` | `persistence/`, `external/` |

---

## 1. 🌐 Presentation Layer (표현 계층)
> **패키지 위치:** `presentation/`
> **역할:** 사용자의 요청을 받아 Application 계층으로 전달합니다.

### 📂 구성 요소
- **`controller/`**: REST API 엔드포인트 정의. **`application.port.in`** 인터페이스를 호출합니다.
- **`dto/`**: `Request`, `Response` 객체. 클라이언트와 데이터를 주고받는 껍데기 역할입니다.
- **책임:** HTTP 요청/응답 변환, 단순 입력값 검증, 유스케이스 실행 위임.

### 🚫 금지 (Restriction)
- **비즈니스 로직 포함 금지:** 순수한 전달자 역할만 수행해야 합니다.
- **`Entity` 노출 금지:** 클라이언트에게 도메인 `Entity`를 직접 반환하면 안 됩니다.
- **DB 직접 접근 금지:** `Repository`나 `Infrastructure`를 직접 호출하지 않습니다.
---

## 2. ⚙️ Application Layer (응용 계층)
> **패키지 위치:** `application/`
> **역할:** 도메인 객체와 인프라 자원을 조율하여 애플리케이션의 유스케이스(기능)를 실행합니다.

### 📂 구성 요소
- **`port/in/` (Input Port)**: 컨트롤러나 타 도메인에서 접근할 수 있는 인터페이스입니다. (`GetUserUseCase`, `LoginUseCase` 등)
- **`port/out/` (Output Port)**: 외부 의존성(소셜 API, 푸시 알림 등)에 대한 **추상화 인터페이스**입니다.
- **`service/`**: `port/in`의 구현체입니다. 실제 비즈니스 흐름을 제어하며 트랜잭션을 관리합니다.

### ✅ 핵심 설계
- **외부 통신 추상화:** 외부 API(카카오, 애플 등) 연동 시 직접 구현하지 않고 `port.out` 인터페이스를 통해 호출합니다.
- **CQRS 지향:** 조회(`Query`)와 상태 변경(`Command`) 유스케이스를 분리하여 단일 책임 원칙을 준수합니다.

### 🚫 금지 (Restriction)
- **비즈니스 판단 로직 금지:** "상태 변경 규칙" 등 핵심 로직은 `Domain`에 위임하고, 여기서는 **순서만 제어**합니다.
- **기술 종속성 배제:** `HttpServletRequest` 등 웹 관련 기술 코드가 섞이면 안 됩니다.
- **`SQL/Infra` 코드 금지:** 직접 쿼리를 작성하거나 외부 API를 호출하는 코드를 작성하지 않습니다.

---

## 3. 🧠 Domain Layer (도메인 계층)
> **패키지 위치:** `domain/`
> **역할:** 시스템의 핵심 비즈니스 규칙을 담당하는 심장부입니다.

### 📂 구성 요소
- **`model/entity/`**: 비즈니스 로직을 포함하는 핵심 객체입니다.
- **`repository/` (중요)**: 데이터 영속성을 위한 **인터페이스**입니다. 서비스 레이어는 이 인터페이스를 통해 DB에 접근합니다.
- **`service/`**: 엔티티 단독으로 처리하기 어려운 복합 비즈니스 로직을 수행합니다. (Domain Service)

### ✅ 설계 포인트
- **Repository 위치:** 영속성 추상화 인터페이스는 도메인의 일부로 간주하여 `domain/repository`에 위치합니다.
- **순수성 유지:** JPA, QueryDSL 등 특정 기술에 의존하지 않는 순수 자바 코드로 작성됩니다.

### 🚫 금지 (Restriction)
- **기술 의존성 제로(0):** SpringJPA, Hibernate 등 프레임워크나 인프라 기술에 의존하지 않는 POJO 상태 유지
- **구현체 포함 금지:** `Repository`의 실제 구현 코드(JPA 등)가 포함되면 안 됩니다. (오직 인터페이스만 존재)

---

## 4. 🔌 Infrastructure Layer (인프라 계층)
> **패키지 위치:** `infrastructure/`
> **역할:** 상위 계층에서 정의한 인터페이스의 실제 기술적 구현을 담당합니다.

### 📂 구성 요소
- **`persistence/`**: `domain/repository` 인터페이스를 **JPA, QueryDSL** 등으로 실제 구현합니다.
- **`external/`**: `application/port/out` 인터페이스를 구현하여 카카오, 애플 등 **외부 시스템과 실제 통신**을 수행합니다.

### ✅ 책임 (Responsibility)
- **기술적 결정:** 어떤 DB 기술을 쓸지, 어떤 통신 라이브러리를 쓸지는 오직 이 계층에서만 결정됩니다.
- **DIP(의존성 역전):** 인프라의 기술 사양이 변경되어도 Application과 Domain 계층은 영향을 받지 않습니다.

### 🚫 금지 (Restriction)
- **비즈니스 규칙 포함 금지:** 기술적인 동작만 담당해야 하며, 비즈니스 판단을 내려서는 안 됩니다.
- **도메인 오염 주의:** 구현체가 도메인 모델을 침범하거나 변경해서는 안 됩니다.
- **순환 참조 주의:** `Infrastructure`가 `Presentation`을 역으로 참조하면 안 됩니다.

---

### 💡 의존성 흐름 요약 (Dependency Flow)
**"모든 의존성은 내부(Domain)를 향하며, 구현체는 인터페이스를 통해 외부로 격리됩니다."**

1. **[Inbound]** `Controller` ➡️ `application.port.in` (Interface) ➡️ `application.service` (Implementation)
2. **[Domain Access]** `Service` ➡️ `domain.repository` (Interface) ➡️ `infrastructure.persistence` (Implementation)
3. **[Outbound]** `Service` ➡️ `application.port.out` (Interface) ➡️ `infrastructure.external` (Implementation)

