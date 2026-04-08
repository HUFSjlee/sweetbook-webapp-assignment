# TravelogBook

여행 사진과 메모를 정리한 뒤 실제 포토북 생성과 주문까지 이어주는 여행 포토북 웹앱입니다.

## 1. 서비스 소개

### 한 문장 소개
TravelogBook은 여행의 순간을 사진과 메모로 정리하고, SweetBook API를 통해 실제 포토북 제작 흐름까지 연결하는 서비스입니다.

### 타겟 고객
- 여행 사진을 그냥 앨범에 두지 않고 한 권의 책으로 남기고 싶은 사용자
- 여행 기록을 보기 좋게 정리하고 싶은 사용자
- 포토북 제작 과정을 간단한 웹 흐름으로 경험하고 싶은 사용자

### 주요 기능
- 여행 프로젝트 생성, 조회, 수정, 삭제
- 여행별 사진 업로드 및 메모 작성
- 사진 순서 변경 및 상세 정보 수정
- SweetBook 판형과 템플릿 조회
- 포토북 프리뷰 및 실제 포토북 생성
- 주문 견적 조회
- 주문 생성 및 주문 내역 조회

## 2. 실행 방법

### 1) 설치
```bash
cd frontend
npm install
```

### 2) 환경변수 설정
루트의 `.env.example` 내용을 참고해서 아래 두 파일을 직접 만들어주세요.

- `backend/.env`
- `frontend/.env`

예시:

`backend/.env`
```env
SWEETBOOK_API_KEY=your_sweetbook_api_key_here
SWEETBOOK_API_BASE_URL=https://api-sandbox.sweetbook.com/v1

SUPABASE_SERVICE_ROLE_KEY=your_supabase_service_role_key_here
SUPABASE_STORAGE_BUCKET=travel-assets

SUPABASE_DB_URL=jdbc:postgresql://db.your-project-ref.supabase.co:5432/postgres
SUPABASE_DB_USERNAME=postgres
SUPABASE_DB_PASSWORD=your_database_password

SERVER_PORT=8080
APP_PUBLIC_BASE_URL=http://localhost:8080
APP_STORAGE_MODE=local
APP_STORAGE_LOCAL_ROOT=./build/storage
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
```

`frontend/.env`
```env
NUXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
```

### 3) 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

기본 포트는 `8080`입니다.

### 4) 프론트엔드 실행
```bash
cd frontend
npm run dev
```

기본 주소는 `http://localhost:3000`입니다.

### 참고
- `SUPABASE_DB_URL`이 없으면 로컬 H2 DB로 실행됩니다.
- `APP_STORAGE_MODE=local`이면 로컬 파일 저장소를 사용합니다.
- `APP_STORAGE_MODE=supabase`로 바꾸면 Supabase Storage 연동 모드로 전환할 수 있습니다.

## 3. 사용한 API 목록

본 프로젝트는 SweetBook Sandbox Book Print API를 사용했습니다.

| 엔드포인트 | 메서드 | 사용 목적 |
| --- | --- | --- |
| `/book-specs` | `GET` | 포토북 판형 목록 조회 |
| `/templates` | `GET` | 판형별 템플릿 목록 조회 |
| `/templates/{templateUid}` | `GET` | 템플릿 상세 조회 |
| `/books` | `POST` | 포토북 초안 생성 |
| `/books?bookUid=...` | `GET` | 생성된 책 상태 및 페이지 수 확인 |
| `/books/{bookUid}/photos` | `POST` | 책에 사용할 사진 업로드 |
| `/books/{bookUid}/cover` | `POST` | 표지 생성 |
| `/books/{bookUid}/contents` | `POST` | 내지 페이지 추가 |
| `/books/{bookUid}/finalization` | `POST` | 포토북 최종 생성 완료 |
| `/orders/estimate` | `POST` | 주문 견적 조회 |
| `/orders` | `POST` | 주문 생성 |
| `/orders/{orderUid}` | `GET` | 주문 상세 조회 |
| `/orders/{orderUid}/cancel` | `POST` | 주문 취소 시도 |

참고:
- 주문 취소 엔드포인트는 연동했지만, SweetBook Sandbox 상태 제약으로 실제 취소는 제한될 수 있습니다.

## 4. AI 도구 사용 내역

개발 과정에서 아래 AI 도구를 활용했습니다.

- OpenAI Codex
  - 백엔드 구조 설계 보조
  - 프론트 페이지 구조 정리
  - SweetBook 연동 흐름 구현 보조
  - 오류 원인 분석 및 수정
  - README 및 제출 문서 초안 작성

- ChatGPT 계열 도구
  - 문구 다듬기
  - 화면 구성 아이디어 정리
  - 기능 설명과 설계 의도 정리

AI는 전체 방향과 구현 속도를 높이는 보조 도구로 사용했고, 실제 코드 반영과 실행 검증은 프로젝트 환경에서 직접 확인했습니다.

## 5. 설계 의도

### 왜 이 서비스를 선택했는가
단순한 사진 업로드 앱보다, 실제 결과물인 포토북 제작까지 이어지는 흐름이 더 명확한 사용자 가치를 보여준다고 판단했습니다.  
특히 여행은 사진, 감정, 기록이 함께 남는 경험이기 때문에 포토북 서비스와 잘 맞는 주제라고 보았습니다.

### 비즈니스 가능성
- 여행 후 사진을 정리하지 못하고 방치하는 사용자가 많다는 점에서 수요가 있습니다.
- 디지털 사진을 실제 인쇄물로 전환하는 경험은 선물, 기념, 보관 측면에서 가치가 있습니다.
- 향후 여행 일정 추천, 자동 사진 큐레이션, 테마별 포토북 추천 기능까지 확장 가능성이 있습니다.

### 더 시간이 있었다면 추가했을 기능
- 사용자 로그인 및 개인 프로젝트 저장
- 드래그 앤 드롭 기반 사진 순서 편집
- 포토북 표지 문구 및 테마 커스터마이징 강화
- 결제 상태와 배송 상태 추적
- Supabase 실연결 검증 자동화
- 모바일 화면 최적화 추가 개선

## 기술 스택

- Frontend: Nuxt 4, Vue 3
- Backend: Spring Boot, Spring Data JPA
- External API: SweetBook Sandbox API
- DB: H2 fallback, Supabase Postgres 연동 가능
- Storage: Local Storage fallback, Supabase Storage 연동 가능
