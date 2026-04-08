# PRD — 트래블로그북 (TravelogBook)

> 여행 중 찍은 사진과 짧은 코멘트를 올리면, 감각적인 포토북으로 완성해주는 웹 서비스

---

## 1. 프로젝트 개요

### 1-1. 컨셉

유튜브에서 "여행자에게 카메라를 맡기면, 그 여행자가 자유롭게 담아온 사진·영상을 감성적으로 편집해주는" 채널이 인기를 끌고 있다. 이 컨셉을 오마주하여, **여행자가 사진을 업로드하고 가벼운 코멘트만 남기면 감각적인 포토북이 자동으로 완성되는 서비스**를 만든다.

인스타그램·스레드 같은 SNS는 디지털 공유에 그치지만, 트래블로그북은 **물리적 포토북**이라는 실물 결과물을 제공하는 차별점을 가진다.

### 1-2. 타겟 고객

- 여행을 좋아하고 사진을 많이 찍지만, 포토북 편집에 시간 쓰기 싫은 2030 세대
- 여행 선물(커플 기념, 가족 여행 등)로 포토북을 주고 싶은 사람
- 감성적인 기록물을 좋아하는 사람

### 1-3. 핵심 가치

- **간편함**: 사진 업로드 + 코멘트 작성만으로 포토북 완성
- **감성**: 자동 레이아웃과 템플릿이 감각적인 결과물을 보장
- **실물 배송**: 디지털이 아닌 실제 책으로 받아볼 수 있음

---

## 2. 기술 스택

| 영역 | 기술 |
|---|---|
| 프론트엔드 | Nuxt 3 (Vue 3, SSR/SPA) |
| 백엔드 | Spring Boot 3 (Java 17+) |
| 데이터베이스 | Supabase (PostgreSQL + Storage) |
| 외부 API | SweetBook Book Print API (Sandbox) |
| IDE | IntelliJ IDEA |
| AI 코딩 도구 | Codex |

---

## 3. 프로젝트 구조

```
root/
├── backend/          # Spring Boot 프로젝트
│   ├── src/
│   │   ├── main/java/com/travelogbook/
│   │   │   ├── config/          # CORS, Supabase, RestTemplate 설정
│   │   │   ├── controller/      # REST 컨트롤러
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   ├── dto/             # 요청/응답 DTO
│   │   │   ├── entity/          # JPA 엔티티 (또는 Supabase 매핑 모델)
│   │   │   └── client/          # SweetBook API 호출 클라이언트
│   │   └── resources/
│   │       ├── application.yml
│   │       └── dummy/           # 더미 데이터 (이미지·JSON)
│   └── build.gradle
├── frontend/         # Nuxt 3 프로젝트
│   ├── pages/
│   ├── components/
│   ├── composables/
│   ├── layouts/
│   ├── assets/
│   └── nuxt.config.ts
├── docs/             # 문서
│   └── PRD.md
├── .env.example      # 환경변수 템플릿 (키 값 없이)
├── .gitignore
└── README.md
```

---

## 4. 핵심 기능 명세

### 4-1. 여행 프로젝트 생성

- 사용자가 **여행 제목**, **여행 기간**, **대표 색상/분위기**(선택)를 입력하여 프로젝트를 생성한다.
- Supabase에 프로젝트 레코드 저장.

### 4-2. 사진 업로드 & 코멘트 작성

- 사진을 드래그앤드롭 또는 파일 선택으로 업로드 (Supabase Storage 활용).
- 각 사진에 짧은 코멘트(한 줄 메모)를 작성할 수 있다.
- 사진 순서를 드래그로 변경할 수 있다.
- 날짜·장소 정보를 선택적으로 입력 가능.

### 4-3. 포토북 미리보기 & 템플릿 선택

- SweetBook API의 `GET /book-specs`로 판형 목록을 조회하여 사용자에게 보여준다.
- `GET /templates`로 해당 판형에 맞는 템플릿 목록을 보여주고, 사용자가 분위기에 맞는 템플릿을 선택한다.
- 선택한 템플릿 + 업로드한 사진으로 포토북 구성을 미리보기 형태로 보여준다.

### 4-4. 포토북 생성 (SweetBook API 연동)

백엔드에서 다음 순서로 SweetBook API를 호출한다:

1. `POST /books` — 책 생성 (draft 상태)
2. `POST /books/{bookUid}/photos` — 사진 업로드
3. `POST /books/{bookUid}/cover` — 표지 추가 (제목 + 대표 사진)
4. `POST /books/{bookUid}/contents` — 내지 추가 (사진 + 코멘트, 반복 호출)
5. `POST /books/{bookUid}/finalization` — 최종화

### 4-5. 주문 & 결제

1. `POST /orders/estimate` — 예상 가격 조회 후 사용자에게 표시
2. 사용자가 **배송 정보**(이름, 주소, 연락처) 입력
3. `POST /orders` — 주문 생성
4. `GET /orders/{orderUid}` — 주문 상태 조회

### 4-6. 마이페이지 / 주문 내역

- `GET /orders` — 내 주문 목록 조회
- 주문 상태(제작 중, 배송 중, 완료 등) 표시
- `POST /orders/{orderUid}/cancel` — 주문 취소 기능

---

## 5. SweetBook API 사용 목록

> **필수**: Books API + Orders API

| API | 엔드포인트 | 용도 |
|---|---|---|
| BookSpecs | `GET /book-specs` | 판형 목록 조회 (사용자 선택용) |
| Templates | `GET /templates` | 템플릿 목록 조회 (사용자 선택용) |
| Templates | `GET /templates/{templateUid}` | 템플릿 상세 조회 |
| **Books** | `POST /books` | 포토북 생성 (draft) |
| **Books** | `POST /books/{bookUid}/photos` | 사진 업로드 |
| **Books** | `POST /books/{bookUid}/cover` | 표지 추가 |
| **Books** | `POST /books/{bookUid}/contents` | 내지 추가 (반복) |
| **Books** | `POST /books/{bookUid}/finalization` | 포토북 최종화 |
| **Books** | `GET /books` | 내 책 목록 조회 |
| **Orders** | `POST /orders/estimate` | 가격 견적 조회 |
| **Orders** | `POST /orders` | 주문 생성 |
| **Orders** | `GET /orders` | 주문 목록 조회 |
| **Orders** | `GET /orders/{orderUid}` | 주문 상세 조회 |
| **Orders** | `POST /orders/{orderUid}/cancel` | 주문 취소 |

- Base URL (Sandbox): `https://api-sandbox.sweetbook.com/v1`
- 인증: `Authorization: Bearer {SWEETBOOK_API_KEY}`

---

## 6. 데이터 모델 (Supabase)

### travels (여행 프로젝트)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | uuid (PK) | |
| title | varchar | 여행 제목 |
| description | text | 한 줄 소개 |
| start_date | date | 여행 시작일 |
| end_date | date | 여행 종료일 |
| mood | varchar | 분위기 태그 (감성, 활기, 힐링 등) |
| status | varchar | draft / building / completed / ordered |
| book_uid | varchar | SweetBook 책 UID (생성 후 저장) |
| created_at | timestamptz | |

### photos (사진)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | uuid (PK) | |
| travel_id | uuid (FK) | 소속 여행 프로젝트 |
| image_url | text | Supabase Storage URL |
| comment | text | 사용자 코멘트 |
| location | varchar | 촬영 장소 (선택) |
| taken_date | date | 촬영 일자 (선택) |
| sort_order | int | 정렬 순서 |
| created_at | timestamptz | |

### orders (주문)

| 컬럼 | 타입 | 설명 |
|---|---|---|
| id | uuid (PK) | |
| travel_id | uuid (FK) | |
| order_uid | varchar | SweetBook 주문 UID |
| status | varchar | 주문 상태 |
| recipient_name | varchar | 수령인 |
| address | text | 배송 주소 |
| phone | varchar | 연락처 |
| estimated_price | int | 예상 가격 |
| created_at | timestamptz | |

---

## 7. 페이지 구성 (프론트엔드 라우팅)

| 경로 | 페이지 | 설명 |
|---|---|---|
| `/` | 랜딩 페이지 | 서비스 소개, CTA 버튼 |
| `/travels` | 내 여행 목록 | 여행 프로젝트 카드 리스트 |
| `/travels/new` | 여행 생성 | 제목·기간·분위기 입력 폼 |
| `/travels/:id` | 여행 상세 | 사진 업로드·코멘트·순서 편집 |
| `/travels/:id/preview` | 포토북 미리보기 | 판형·템플릿 선택 + 레이아웃 프리뷰 |
| `/travels/:id/order` | 주문하기 | 가격 확인 + 배송 정보 입력 |
| `/orders` | 주문 내역 | 주문 상태 리스트 |
| `/orders/:id` | 주문 상세 | 상태 조회·취소 |

---

## 8. 백엔드 API 설계 (Spring Boot)

> 프론트엔드 ↔ 백엔드 내부 API. 백엔드가 SweetBook API Key를 관리하고 대리 호출.

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/api/travels` | 여행 프로젝트 생성 |
| GET | `/api/travels` | 여행 목록 조회 |
| GET | `/api/travels/{id}` | 여행 상세 조회 |
| PUT | `/api/travels/{id}` | 여행 정보 수정 |
| DELETE | `/api/travels/{id}` | 여행 삭제 |
| POST | `/api/travels/{id}/photos` | 사진 업로드 |
| GET | `/api/travels/{id}/photos` | 사진 목록 조회 |
| PUT | `/api/photos/{id}` | 사진 코멘트·순서 수정 |
| DELETE | `/api/photos/{id}` | 사진 삭제 |
| PUT | `/api/travels/{id}/photos/reorder` | 사진 순서 일괄 변경 |
| GET | `/api/bookspecs` | 판형 목록 (SweetBook 프록시) |
| GET | `/api/templates` | 템플릿 목록 (SweetBook 프록시) |
| POST | `/api/travels/{id}/build` | 포토북 생성 (SweetBook 전체 플로우 실행) |
| POST | `/api/travels/{id}/estimate` | 가격 견적 조회 |
| POST | `/api/travels/{id}/order` | 주문 생성 |
| GET | `/api/orders` | 주문 목록 |
| GET | `/api/orders/{id}` | 주문 상세 |
| POST | `/api/orders/{id}/cancel` | 주문 취소 |

---

## 9. 환경 변수 (.env.example)

```env
# SweetBook API
SWEETBOOK_API_KEY=your_sandbox_api_key_here
SWEETBOOK_API_BASE_URL=https://api-sandbox.sweetbook.com/v1

# Supabase
SUPABASE_URL=https://your-project.supabase.co
SUPABASE_ANON_KEY=your_supabase_anon_key_here
SUPABASE_SERVICE_KEY=your_supabase_service_key_here

# Server
SERVER_PORT=8080

# Frontend
NUXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

---

## 10. 더미 데이터

> 과제 요구사항: "실행 직후 서비스를 바로 확인할 수 있도록 콘텐츠 더미 데이터를 레포에 포함"

`backend/src/main/resources/dummy/` 디렉토리에 다음을 포함한다:

- `dummy-travels.json` — 여행 프로젝트 2~3개 (제주도 여행, 도쿄 여행 등)
- `dummy-photos/` — 각 여행별 샘플 이미지 5~8장 (AI 생성 또는 무료 이미지)
- 앱 최초 실행 시 더미 데이터를 Supabase에 시딩하는 초기화 로직 포함 (`data.sql` 또는 `CommandLineRunner`)

---

## 11. UI/UX 방향

- **톤앤매너**: 따뜻하고 감성적. 무채색 베이스에 포인트 컬러 하나 (예: 테라코타, 올리브).
- **폰트**: Pretendard (본문) + 감성 서체 하나 (제목용)
- **사진 중심 UI**: 텍스트보다 사진이 크게 보이는 레이아웃
- **인터랙션**: 드래그앤드롭 사진 정렬, 스크롤 기반 미리보기
- **반응형**: 모바일 우선 (여행 중 스마트폰 업로드 가정)

---

## 12. 개발 우선순위

### Phase 1 — MVP (과제 제출용)

1. 여행 프로젝트 CRUD
2. 사진 업로드 + 코멘트 작성
3. 판형·템플릿 조회 (SweetBook API)
4. 포토북 생성 플로우 (books → photos → cover → contents → finalization)
5. 주문 생성 + 주문 내역 조회
6. 더미 데이터 시딩
7. README.md 작성

### Phase 2 — 개선 (시간 여유 시)

- 사진 EXIF 메타데이터로 날짜·위치 자동 입력
- 포토북 미리보기 시각화 (페이지 넘기기 효과)
- 웹훅 연동 (주문 상태 실시간 업데이트)
- 사용자 인증 (Supabase Auth)
- SNS 공유 기능

---

## 13. 제약 조건 & 참고

- SweetBook API는 **Sandbox 환경** 사용. 실제 인쇄·배송 없음.
- API Key는 **백엔드에서만 관리**. 프론트엔드에 노출 금지.
- `.env` 파일은 `.gitignore`에 포함. `.env.example`만 커밋.
- GitHub 저장소는 **Public** 설정.