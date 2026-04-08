# TravelogBook

TravelogBook은 여행 사진과 메모를 정리하고, 실제 포토북 생성과 주문 흐름까지 연결해주는 여행 포토북 웹앱입니다.

## 1. 서비스 소개

### 한 문장 설명
여행의 순간을 사진과 기록으로 정리한 뒤, 한 권의 포토북으로 이어주는 서비스입니다.

### 타겟 고객
- 여행 사진을 보기 좋게 정리하고 싶은 사용자
- 포토북으로 추억을 남기고 싶은 사용자
- 디지털 사진을 실제 결과물로 만들고 싶은 사용자

### 주요 기능
- 여행 프로젝트 생성, 조회, 수정, 삭제
- 사진 업로드 및 메모 작성
- 사진 순서 변경
- 포토북 판형/템플릿 조회
- 포토북 프리뷰 및 실제 책 생성
- 주문 견적 조회
- 주문 생성 및 주문 내역 조회

## 2. 실행 방법

검토자가 바로 실행할 수 있도록 기본 실행 기준으로 정리했습니다.

### 실행 환경
- Java 17 이상
- Node.js 20 이상
- npm

### 빠른 실행

#### 1) 프론트 의존성 설치
```bash
cd frontend
npm install
```

#### 2) 환경변수 파일 생성
아래 두 파일을 직접 만들어주세요.

`backend/.env`
```env
SWEETBOOK_API_KEY=your_sweetbook_api_key_here
SWEETBOOK_API_BASE_URL=https://api-sandbox.sweetbook.com/v1

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

#### 3) 백엔드 실행
```bash
cd backend
./gradlew bootRun
```

#### 4) 프론트 실행
```bash
cd frontend
npm run dev
```

#### 5) 접속 주소
- 프론트: `http://localhost:3000`
- 백엔드: `http://localhost:8080`

### 참고
- 기본 실행은 `H2 + local storage` 기준이라 Supabase 설정 없이도 실행됩니다.
- 더미 데이터가 자동으로 생성되어, 실행 직후 바로 흐름 확인이 가능합니다.
- `SWEETBOOK_API_KEY`가 없으면 앱은 실행되더라도 포토북 생성/주문 기능은 제한됩니다.

## 3. 사용한 API 목록

본 프로젝트는 SweetBook Sandbox Book Print API를 사용했습니다.

| 엔드포인트 | 메서드 | 사용 목적 |
| --- | --- | --- |
| `/book-specs` | `GET` | 포토북 판형 조회 |
| `/templates` | `GET` | 템플릿 목록 조회 |
| `/templates/{templateUid}` | `GET` | 템플릿 상세 조회 |
| `/books` | `POST` | 포토북 초안 생성 |
| `/books?bookUid=...` | `GET` | 생성된 책 상태 및 페이지 수 확인 |
| `/books/{bookUid}/photos` | `POST` | 책에 사용할 사진 업로드 |
| `/books/{bookUid}/cover` | `POST` | 표지 생성 |
| `/books/{bookUid}/contents` | `POST` | 내지 페이지 추가 |
| `/books/{bookUid}/finalization` | `POST` | 포토북 최종 생성 |
| `/orders/estimate` | `POST` | 주문 견적 조회 |
| `/orders` | `POST` | 주문 생성 |
| `/orders/{orderUid}` | `GET` | 주문 상세 조회 |
| `/orders/{orderUid}/cancel` | `POST` | 주문 취소 시도 |

## 4. AI 도구 사용 내역

### 사용한 도구
- OpenAI Codex
- ChatGPT 계열 도구

### 활용 방법
- PRD 분석 및 기능 구조 정리
- 백엔드/프론트 코드 초안 작성 및 리팩터링
- SweetBook API 연동 흐름 점검
- 오류 원인 분석 및 수정
- README 및 제출 문서 정리

## 5. 설계 의도

### 왜 이 서비스를 선택했는가
단순한 사진 저장이 아니라, 실제 포토북 제작까지 이어지는 흐름이 사용자 가치가 더 분명하다고 판단했습니다. 여행이라는 주제는 사진, 메모, 감정이 함께 남기 쉬워 포토북 서비스와 잘 맞는다고 보았습니다.

### 비즈니스 가능성
- 여행 사진을 정리하지 못하고 쌓아두는 사용자가 많습니다.
- 디지털 사진을 실물 포토북으로 전환하는 경험은 기념, 선물, 보관 측면에서 가치가 있습니다.
- 향후 자동 사진 큐레이션, 테마 추천, 여행별 편집 템플릿 기능으로 확장 가능성이 있습니다.

### 더 시간이 있었다면 추가했을 기능
- 로그인 및 사용자별 프로젝트 관리
- 드래그 앤 드롭 사진 정렬
- 표지 문구/테마 커스터마이징 강화
- 배송 상태 추적
- Supabase 실연결 자동 검증
- 모바일 UI 최적화 추가 개선

## 기술 스택
- Frontend: Nuxt 4, Vue 3
- Backend: Spring Boot, Spring Data JPA
- External API: SweetBook Sandbox API
- DB: H2 fallback, Supabase Postgres 연동 가능
- Storage: Local storage fallback, Supabase Storage 연동 가능
