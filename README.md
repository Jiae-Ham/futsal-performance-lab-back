# ⚽ Futsal Performance Lab - Backend

> **UWB(Ultra-Wideband) 기술 기반의 실시간 풋살 경기 분석 플랫폼**

**Futsal Performance Lab**은 경기 중 발생하는 선수들의 위치 데이터를 실시간으로 수집하고 분석하여, 팀과 개인에게 과학적인 경기 인사이트를 제공하는 프로젝트입니다. 본 저장소는 데이터 모델링, API 엔드포인트 및 분석 로직을 담당하는 백엔드 시스템입니다.

---

## 🚀 Key Features

* **Real-time Tracking**: UWB 태그를 통해 수집된 선수들의 정밀한 위치 데이터 처리 및 저장
* **Match Lifecycle Management**: 경기장(Stadium) 정보부터 전체 경기(Game), 세부 매치(Set) 단위의 데이터 계층화
* **Performance Analytics**: 위치 데이터를 기반으로 한 활동량, 이동 경로 및 경기 통계 분석
* **Secure API Layer**: Spring Security를 적용하여 안전한 데이터 연동 및 권한별 접근 제어

---

## 🛠 Technology Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.3.11 |
| **Database** | PostgreSQL |
| **ORM** | Spring Data JPA (Hibernate) |
| **Security** | Spring Security |
| **Build Tool** | Gradle |
| **Infrastructure** | Azure, Docker |
| **Utilities** | Project Lombok, JUnit 5 |

---

## 🏗 System Architecture

1.  **Stadium**: 경기장 규격 및 상태 정보 관리
2.  **Game & Set**: 전체 경기 세션과 개별 세트 단위의 데이터 관계 정립
3.  **Player & Tag**: 선수 정보와 실제 UWB 하드웨어 태그의 1:1 매핑 및 데이터 바인딩
4.  **API Surface**: RESTful 원칙을 준수하는 인터페이스 제공

---

## 🏁 Getting Started

### Prerequisites
* JDK 17 이상
* Docker & Docker Compose
* PostgreSQL (Local 또는 Docker Container)

### Installation & Run
1. **Repository Clone**
   ```bash
   git clone [https://github.com/Jiae-Ham/futsal-performance-lab-back.git](https://github.com/Jiae-Ham/futsal-performance-lab-back.git)
   cd futsal-performance-lab-back

2. **Environment Configuration**
src/main/resources/application.yml 파일에서 PostgreSQL 접속 정보를 설정합니다.

3. **Build & Run**
    ```bash
    ./gradlew clean build
    java -jar build/libs/futsal-performance-lab-back-0.0.1-SNAPSHOT.jar
    
    Docker 사용 시:
    docker-compose up -d
---
## UI
<img width="735" height="557" alt="image" src="https://github.com/user-attachments/assets/eb9452f5-3a43-46de-b6e1-f691c7050d20" />


---
## 📂 Project Structure
  ```bash
  futsal-performance-lab-back
  ├── back/                         # Backend Module Main
  │   ├── src/main/java/com/alpaca/futsal_performance_lab_back/
  │   │   ├── config/               # DB, Security, JPA 관련 전역 설정
  │   │   ├── controller/           # API Endpoints (Controller 레이어)
  │   │   ├── dto/                  # Request/Response Data Mapping
  │   │   ├── entity/               # JPA Entities (Stadium, Game, Set, Player, Tag)
  │   │   ├── repository/           # Spring Data JPA Interface
  │   │   ├── security/             # Security Filter & Configuration
  │   │   ├── service/              # Core Business Logic
  │   │   └── FutsalPerformanceLabBackApplication.java # Entry Point
  │   └── src/main/resources/
  │       ├── application.yml       # 통합 설정 파일 (Dev/Prod 분리 가능)
  │       └── static/               # Static Resources
  ├── build.gradle                  # Gradle Project Configuration
  ├── settings.gradle               # Project Settings
  └── README.md


