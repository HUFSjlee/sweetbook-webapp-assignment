# TravelogBook
여행 사진과 짧은 메모를 정리한 뒤 SweetBook Sandbox API로 실제 포토북 생성과 주문까지 이어지는 웹앱입니다.

## 1. 서비스 소개
- 여행 프로젝트를 만들고 사진, 코멘트, 촬영일, 장소를 정리할 수 있습니다.
- SweetBook 판형과 테마를 조회하고 실제 출력용 포토북을 생성할 수 있습니다.
- 견적 조회 후 주문 생성, 주문 상세 조회, 주문 취소까지 연결됩니다.
- 첫 실행 시 더미 여행과 샘플 사진이 자동으로 생성됩니다.

## 2. 기술 구성
- Frontend: Nuxt 4, Vue 3
- Backend: Spring Boot 4, JPA
- Printing API: SweetBook Sandbox API
- Storage:
  - 기본값은 로컬 저장소 + H2 fallback 입니다.
  - 환경변수를 채우면 Supabase Postgres / Supabase Storage로 전환할 수 있습니다.

## 3. 실행 방법
### Backend
1. `backend/.env`에 필요한 값을 채웁니다.
2. 기본 로컬 실행:
```bash
cd backend
./gradlew bootRun
```
3. 8080 포트가 이미 사용 중이면 `SERVER_PORT`를 다른 값으로 바꾸세요.

### Frontend
1. `frontend/.env`에 API 주소를 설정합니다.
2. 실행:
```bash
cd frontend
npm install
npm run dev
```

### 기본 동작 모드
- DB:
  - `SUPABASE_DB_URL`이 없으면 로컬 H2 파일 DB로 실행됩니다.
  - 값을 넣으면 Supabase Postgres에 연결됩니다.
- 파일 저장:
  - `APP_STORAGE_MODE=local`이면 로컬 파일 저장을 사용합니다.
  - `APP_STORAGE_MODE=supabase`이면 Supabase Storage를 사용합니다.

## 4. 핵심 API
### 내부 API
- `POST /api/travels`
- `GET /api/travels`
- `GET /api/travels/{id}`
- `PUT /api/travels/{id}`
- `DELETE /api/travels/{id}`
- `POST /api/travels/{id}/photos`
- `GET /api/travels/{id}/photos`
- `PUT /api/photos/{id}`
- `DELETE /api/photos/{id}`
- `PUT /api/travels/{id}/photos/reorder`
- `GET /api/bookspecs`
- `GET /api/templates`
- `POST /api/travels/{id}/build`
- `POST /api/travels/{id}/estimate`
- `POST /api/travels/{id}/order`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders/{id}/cancel`

### 외부 API
- SweetBook Sandbox:
  - `GET /book-specs`
  - `GET /templates`
  - `GET /templates/{templateUid}`
  - `POST /books`
  - `POST /books/{bookUid}/photos`
  - `POST /books/{bookUid}/cover`
  - `POST /books/{bookUid}/contents`
  - `POST /books/{bookUid}/finalization`
  - `POST /orders/estimate`
  - `POST /orders`
  - `GET /orders/{orderUid}`
  - `POST /orders/{orderUid}/cancel`

## 5. 현재 구현 상태
- 여행 프로젝트 CRUD 완료
- 사진 업로드/수정/삭제/정렬 완료
- SweetBook 판형/테마 조회 완료
- SweetBook 포토북 생성 플로우 완료
- 주문 견적/생성/조회/취소 완료
- 더미 데이터 자동 시딩 완료
- Nuxt 프론트 화면 구현 완료

## 6. 검증
- `backend`: `./gradlew test` 통과
- `frontend`: `npm run build` 통과

## 7. AI 도구 사용 내역
- PRD 분석
- Spring Boot 백엔드 구조 설계 및 구현
- Nuxt 프론트엔드 구조 설계 및 구현
- SweetBook Swagger 확인 및 요청 포맷 반영
- UI 스타일링 및 실행 검증
