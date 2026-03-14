<img width="2375" height="1344" alt="Image" src="https://github.com/user-attachments/assets/864db73c-b926-4364-9c05-8a484532a9e1" />

---

#  Team Members (팀원 및 팀 소개)
| 김민기 | 임도현 | 강민지 | 유일송 |
|:------:|:------:|:------:|:------:|
| <img src="https://github.com/user-attachments/assets/c0f0b632-32e1-48d4-9c00-508b5c8cbbb5" alt="김민기" width="150"> | <img src="https://github.com/user-attachments/assets/eab6bb66-811f-4b6a-acb5-5e24a105a7c2" alt="임도현" width="150"/> | <img src="https://github.com/user-attachments/assets/0c444240-c652-4714-8cfa-10ba16b00a19" alt="강민지" width="150"> | <img alt="Image" src="https://github.com/user-attachments/assets/c0f0b632-32e1-48d4-9c00-508b5c8cbbb5" alt="유일송" width="150"> |
| PM | BE | Mobile | Mobile |
| [GitHub](https://github.com/LDK1009) | [GitHub](https://github.com/Imdo714) | [GitHub](https://github.com/Meezzi) | [GitHub](https://github.com/ilsong963) |

---

# 🎯 Project Overview (프로젝트 개요) 

## 1. 사회적 배경: 투자, 이제는 선택이 아닌 필수 📉

> **"화폐 가치 하락과 자산 가격 상승의 이중고"**

전 세계 경제성장률은 장기적으로 둔화되는 추세입니다. 2008년 금융위기 이후 각국의 양적완화(QE)는 금융 시장의 붕괴를 막는 역할을 했지만, 
2020년 팬데믹을 거치며 유동성이 크게 확대되면서 화폐가치 하락 압력과 자산가격 상승이 동시에 강화되었습니다. <br/>
그 결과, **`화폐 가치 하락`** 과 **`자산 가격 상승`** 이 동시에 진행되며 자산 보유 여부에 따른 격차가 더 빠르게 벌어지고 있습니다. 

---

## 2. 개인투자자의 문제: 정보 과잉과 경험 부족 🤯
> **"검색에서 멈추는 지식, 반복되는 실수"**

정보는 넘쳐나지만 대부분 단순 **검색**에서 멈추며, 자신만의 투자 기준을 세우지 못합니다. 
투자자마다 연봉, 가족 구성원, 리스크 성향이 모두 다른 만큼, 타인의 분석이 아닌 **직접 기록하고 복기하며 경험을 축적하는 과정**이 반드시 필요합니다.

---

## 📊 설문조사 결과
> **이처럼 많은 이들이 필요성을 느끼지만 실천하지 못하는 이유, 바로 '기록의 부재'에 있습니다.**

<img width="1112" height="538" alt="Image" src="https://github.com/user-attachments/assets/96659838-e6e2-4fc6-b5a8-13cd62a5360c" />

---

## 3. 핵심 해결책: 정보를 지식으로 바꾸는 매매일지 💡

> **"매매일지는 가장 빠르고 강력한 학습 도구입니다."**

매매일지는 단순한 기록이 아니라, **거래 경험을 회고하여 다음 의사결정을 돕는 지식으로 치환** 하는 장치입니다.
인지과학에서는 단순히 다시 보는 것보다 스스로 꺼내보는 **인출 연습(Testing effect)** 이 장기 기억과 이해를 더 크게 높인다고 보고합니다.
또한 복습을 시간차로 반복하고, 피드백으로 오류를 수정할 때 학습 효과가 강화됩니다(2006, 2008).

<img width="795" height="322" alt="Image" src="https://github.com/user-attachments/assets/5f80c9af-73da-4eb4-a6b5-8023f3e8729f" />

---

# 🚀 해결 (Solution)

> **"필요성은 알아도 꾸준히 쓰기 어려웠던 매매일지, 이제 FINCH가 해결합니다."**

1. **자동화된 데이터 연동:** 일일이 입력할 필요 없는 간편한 기록
2. **복기 유도 UI:** 작심삼일이 되지 않도록 돕는 UX 설계

![Image](https://github.com/user-attachments/assets/7ffdb050-8abf-46a2-a10e-01ef50e2ebc7)

## 1️⃣ 간단한 작성 방식
### 깔끔한 UI를 사용해 메모를 적는 것처럼 부담 없이 사용할 수 있는 다지인을 구현 했습니다.
![Image](https://github.com/user-attachments/assets/64a2abf0-20e3-4e28-9274-dc5512c6c4e7)
![Image](https://github.com/user-attachments/assets/9ae9abce-b209-4378-9b65-89bf38b82448)

## 2️⃣ 한눈에 들어오는 대시보드 및 자동 분류
### 매일의 수익률과 포트폴리오 상태를 차트로 시각화하여, 거시적인 관점에서 나의 자산을 파악할 수 있습니다.

![Image](https://github.com/user-attachments/assets/1765b6d9-5a21-4f37-9266-95a11377fed2)
![Image](https://github.com/user-attachments/assets/0bab3192-6fd4-4281-9a62-5a8061b1c286)

---

# 🏛️ Architecture & Rules

이 프로젝트는 **Clean Architecture**와 **Layered Architecture** 지향하며, 각 계층 간의 관심사를 엄격히 분리하여 유지보수성을 극대화했습니다.

### 📊 계층 요약 (Quick Summary)
| 계층 | 패키지 | 핵심 역할 | 의존성 방향 |
| :--- | :--- | :--- | :---: |
| **Presentation** | `presentation/` | UI 렌더링 및 사용자 입력 처리 | ⮕ Application |
| **Application** | `application/` | 비즈니스 흐름 제어 (Usecase) | ⮕ Domain |
| **Domain** | `domain/` | 핵심 비즈니스 로직 및 엔티티 | **심장부 (독립)** |
| **Infrastructure** | `infrastructure/` | DB, API 등 기술적 구현 | ⮕ Domain |

---

## 1. 🌐 Presentation Layer (표현 계층)
> 사용자의 요청을 받아 Application 계층으로 전달하는 게이트웨이입니다.

- **역할:** 사용자의 요청을 받아 Application 계층으로 전달합니다.

### 🚫 금지 (Restriction)
- **비즈니스 로직 포함 금지:** 순수한 전달자 역할만 수행해야 합니다.
- **`Entity` 노출 금지:** 클라이언트에게 도메인 `Entity`를 직접 반환하면 안 됩니다.
- **DB 직접 접근 금지:** `Repository`나 `Infrastructure`를 직접 호출하지 않습니다.
---

## 2. ⚙️ Application Layer (응용 계층)
> 도메인 객체와 인프라 자원을 조율하여 기능(Usecase)을 실행합니다.

- **역할:** 도메인 객체와 인프라 자원을 조율하여 애플리케이션의 유스케이스(기능)를 실행합니다.

### 🚫 금지 (Restriction)
- **비즈니스 판단 로직 금지:** "상태 변경 규칙" 등 핵심 로직은 `Domain`에 위임하고, 여기서는 **순서만 제어**합니다.
- **기술 종속성 배제:** `HttpServletRequest` 등 웹 관련 기술 코드가 섞이면 안 됩니다.
- **`SQL/Infra` 코드 금지:** 직접 쿼리를 작성하거나 외부 API를 호출하는 코드를 작성하지 않습니다.
---

## 3. 🧠 Domain Layer (도메인 계층)
> 시스템의 비즈니스 규칙을 담당하는 가장 순수한 영역입니다.

- **역할:** 시스템의 핵심 비즈니스 규칙을 담당하는 심장부입니다.

### 🚫 금지 (Restriction)
- **기술 의존성 제로(0):** SpringJPA, Hibernate 등 프레임워크나 인프라 기술에 의존하지 않는 POJO 상태 유지
- **구현체 포함 금지:** `Repository`의 실제 구현 코드(JPA 등)가 포함되면 안 됩니다. (오직 인터페이스만 존재)
---

## 4. 🔌 Infrastructure Layer (인프라 계층)
> 상위 계층에서 정의한 인터페이스의 실제 기술적 구현을 담당합니다.

- **역할:** 상위 계층에서 정의한 인터페이스의 실제 기술적 구현을 담당합니다.

### 🚫 금지 (Restriction)
- **비즈니스 규칙 포함 금지:** 기술적인 동작만 담당해야 하며, 비즈니스 판단을 내려서는 안 됩니다.
- **도메인 오염 주의:** 구현체가 도메인 모델을 침범하거나 변경해서는 안 됩니다.
- **순환 참조 주의:** `Infrastructure`가 `Presentation`을 역으로 참조하면 안 됩니다.
---

### 📂 Directory Structure
```bash
lib/
 ┣ application/  # Usecases
 ┣ domain/       # Entities & Interfaces
 ┣ infrastructure/ # Repositories Implementation
 ┗ presentation/  # UI & BLoC/Providers
```
