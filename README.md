# TravelogBook

TravelogBook은 여행 사진과 메모를 정리하고, 실제 포토북 생성과 주문까지 이어주는 여행 포토북 웹앱입니다.

## 1. 서비스 소개

### 한 문장 설명
여행의 순간을 사진과 기록으로 정리한 뒤 한 권의 포토북으로 이어주는 서비스입니다.

### 타겟 고객
- 여행 사진을 정리하고 싶은 사용자
- 포토북으로 추억을 남기고 싶은 사용자
- 디지털 사진을 실제 결과물로 만들고 싶은 사용자

### 주요 기능
- 여행 프로젝트 생성 / 조회 / 수정 / 삭제
- 사진 업로드 및 메모 작성
- 사진 순서 변경
- 판형 / 템플릿 조회
- 포토북 생성
- 주문 견적 및 주문 생성
- 주문 내역 조회

## 2. 실행 방법

### 실행 환경
- Java 17+
- Node.js 20+
- npm

### 주의
- 전체 기능 실행에는 `SWEETBOOK_API_KEY`가 필요합니다.
- API Key는 GitHub에 포함하지 않았고 별도 전달이 필요합니다.

### 그대로 복사해서 실행

#### 1) 저장소 클론
```bash
git clone https://github.com/HUFSjlee/sweetbook-webapp-assignment.git
cd sweetbook-webapp-assignment
```

#### 2) 프론트 설치
```bash
cd frontend
npm install
cd ..
```

#### 3) 환경변수 파일 생성

Windows PowerShell:
```powershell
@'
SWEETBOOK_API_KEY=여기에_전달받은_API_KEY_입력
SWEETBOOK_API_BASE_URL=https://api-sandbox.sweetbook.com/v1
SERVER_PORT=8080
APP_PUBLIC_BASE_URL=http://localhost:8080
APP_STORAGE_MODE=local
APP_STORAGE_LOCAL_ROOT=./build/storage
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
'@ | Set-Content backend/.env

@'
NUXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
'@ | Set-Content frontend/.env
```

macOS / Linux:
```bash
cat > backend/.env <<'EOF'
SWEETBOOK_API_KEY=여기에_전달받은_API_KEY_입력
SWEETBOOK_API_BASE_URL=https://api-sandbox.sweetbook.com/v1
SERVER_PORT=8080
APP_PUBLIC_BASE_URL=http://localhost:8080
APP_STORAGE_MODE=local
APP_STORAGE_LOCAL_ROOT=./build/storage
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001
EOF

cat > frontend/.env <<'EOF'
NUXT_PUBLIC_API_BASE_URL=http://localhost:8080/api
EOF
```

#### 4) 백엔드 실행
Windows:
```powershell
cd backend
.\gradlew.bat bootRun
```

macOS / Linux:
```bash
cd backend
./gradlew bootRun
```

#### 5) 프론트 실행
새 터미널에서:
```bash
cd frontend
npm run dev
```

#### 6) 접속
- 프론트: `http://localhost:3000`
- 백엔드: `http://localhost:8080`

### 참고
- 기본 실행은 `H2 + local storage` 기준이라 Supabase 없이도 실행됩니다.
- 더미 데이터가 자동 생성됩니다.

## 3. 사용한 API 목록

| 엔드포인트 | 메서드 | 사용 목적 |
| --- | --- | --- |
| `/book-specs` | `GET` | 포토북 판형 조회 |
| `/templates` | `GET` | 템플릿 목록 조회 |
| `/templates/{templateUid}` | `GET` | 템플릿 상세 조회 |
| `/books` | `POST` | 포토북 초안 생성 |
| `/books?bookUid=...` | `GET` | 생성된 책 상태 및 페이지 수 확인 |
| `/books/{bookUid}/photos` | `POST` | 사진 업로드 |
| `/books/{bookUid}/cover` | `POST` | 표지 생성 |
| `/books/{bookUid}/contents` | `POST` | 내지 생성 |
| `/books/{bookUid}/finalization` | `POST` | 포토북 최종 생성 |
| `/orders/estimate` | `POST` | 주문 견적 조회 |
| `/orders` | `POST` | 주문 생성 |
| `/orders/{orderUid}` | `GET` | 주문 상세 조회 |
| `/orders/{orderUid}/cancel` | `POST` | 주문 취소 시도 |

## 4. AI 도구 사용 내역

- OpenAI Codex: 구조 설계, 코드 수정, 오류 분석, README 정리
- ChatGPT 계열 도구: 문구 정리, 화면 구성 아이디어 정리

## 5. 설계 의도

### 왜 이 서비스를 선택했는가
사진 저장에서 끝나는 것이 아니라 실제 포토북 제작까지 이어지는 흐름이 더 분명한 사용자 가치를 보여준다고 판단했습니다.

### 비즈니스 가능성
- 여행 사진을 정리하지 못하고 쌓아두는 사용자가 많습니다.
- 디지털 사진을 실물 포토북으로 전환하는 경험은 기념과 보관 측면에서 가치가 있습니다.

### 더 시간이 있었다면 추가했을 기능
- 로그인 및 사용자별 프로젝트 관리
- 드래그 앤 드롭 사진 정렬
- 표지/테마 커스터마이징 강화
- 배송 상태 추적
- 모바일 UI 추가 개선

## 기술 스택
- Frontend: Nuxt 4, Vue 3
- Backend: Spring Boot, Spring Data JPA
- External API: SweetBook Sandbox API
- DB: H2 fallback, Supabase Postgres 연동 가능
- Storage: Local storage fallback, Supabase Storage 연동 가능
