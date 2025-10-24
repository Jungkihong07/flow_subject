# flow_subject

플로우 개발 과제 : 파일 확장자에 따라 특정 형식의 파일을 첨부하거나 전송하지 못하도록 제안

# 파일 확장자 차단 과제

## 과제 개요

보안상 위험한 파일 확장자를 서버에 업로드하지 못하도록 차단하는 기능을 구현합니다.  
특히 **`.exe`**, **`.sh`** 등의 실행 파일은 서버에서 실행될 위험이 있으므로  
해당 확장자들은 기본적으로 차단 대상에 포함되어야 합니다.

## 사용 기술 스택

### Backend

- **Spring Boot 3.3 (Java 21)**
- **Spring Data JPA + H2 Database**
- **Gradle**

### Frontend

- **React (Vite + JavaScript)**
- **Fetch API** 통신
- **React Hooks (`useState`, `useEffect`)** 기반 UI 업데이트

### Database

- **PostgreSQL** (Docker 컨테이너)
- **H2** (로컬 개발용)

### Deployment

- **Backend**: Google Compute Engine (GCE) + Docker Compose
- **Frontend**: Firebase Hosting

## 배포된 서비스

- **프론트엔드**: https://compact-factor-476014-p9.web.app
- **백엔드 API**: GCE 인스턴스에서 실행 (포트 8080)

## 로컬 개발 환경 설정

### 백엔드 실행

```bash
cd backend
./gradlew bootRun
```

### 프론트엔드 실행

```bash
cd frontend
npm install
npm run dev
```

### Docker로 백엔드 실행

```bash
cd backend
docker build -t extension-backend:latest .
docker run -d -p 8080:8080 extension-backend:latest
```

## 요구사항

### 1. 고정 확장자 관리

- **1-1.** 고정 확장자는 자주 차단되는 확장자들을 **리스트 형태**로 제공합니다.
  - 기본값(`default`)은 **unChecked** 상태입니다.
  - 확장자들은 **알파벳 순서로 정렬**되어 표시됩니다.
- **1-2.** 고정 확장자를 **check/uncheck**할 경우 해당 상태를 **DB에 저장**해야 합니다.
  - 새로고침해도 상태가 유지되어야 합니다.
  - 체크박스 상태 변경 시에도 **정렬 순서가 유지**됩니다.
  - 단, **커스텀 확장자 영역에는 적용되지 않습니다.**

---

### 2. 커스텀 확장자 추가

- **2-1.** 확장자 입력란의 **최대 입력 길이**는 `20자`입니다.
- **2-2.** **추가 버튼 클릭 시** 다음을 수행합니다:
  - 클라이언트 측에서 현재 등록된 커스텀 확장자 목록을 확인하여 중복 입력을 1차 방지합니다.
  - **고정 확장자와의 중복도 검증**하여 고정 확장자 이름은 커스텀 확장자로 추가할 수 없습니다.
  - 서버에는 해당 확장자를 새로 생성하도록 요청합니다.
- **2-3.** DB에 새로운 확장자 생성이 성공하면, 해당 확장자는 리스트에 즉시 표시되어야 합니다.
  - 서버는 중복된 확장자 추가 요청 시 409 Conflict 에러를 반환해야 합니다.
  - 고정 확장자와 중복되는 경우에도 409 Conflict 에러를 반환합니다.

---

### 3. 커스텀 확장자 관리

- **3-1.** 커스텀 확장자는 **최대 200개까지 추가**할 수 있습니다.
- **3-2.** 각 확장자 옆의 **X (삭제)** 버튼 클릭 시 해당 확장자가 **DB에서 삭제**되어야 합니다.
- **3-3.** 삭제 후 목록에 즉시 반영되어야 합니다.

---

## 구현 시 유의사항

- [x] DB 연동을 통해 check/uncheck, 추가, 삭제가 모두 **지속적으로 반영**되어야 함.
- [x] 브라우저 새로고침 시에도 저장된 **모든 상태 (고정 확장자 차단 여부, 커스텀 확장자 목록, 고정 확장자의 정렬 순서)**가 유지되어야 함.
- [x] 고정 확장자(Fixed)는 상태 관리에 중점을 두며, 커스텀 확장자(Custom)는 **목록 관리(CRUD)**에 중점을 두어 구분 로직을 명확히 해야 함.
- [x] 확장자 입력 시 **공백 제거, 특수문자 제거, 대소문자 통일(lowercase 변환)** 등을 고려해야 함.
- [x] **서버 단 업로드 차단**은 `POST /upload` API에서 최종적으로 판단하도록 구현할 것.
- [x] 고정 확장자를 커스텀 확장자에는 추가할 수 없어야 함.

---

## UI 흐름

### 개요

- 초기 로딩 시, 고정 확장자의 차단 상태와 등록된 모든 커스텀 확장자 목록을 DB에서 동시에 조회하여 표시합니다.
- 모든 데이터는 **DB 기반 CRUD 결과가 즉시 반영**되어야 합니다.

---

### UI 동작 시나리오

#### 1. 초기 로딩

1. 클라이언트가 페이지 로딩과 동시에 다음 두 가지 요청을 보냅니다:

- `GET /extensions/fixed` (백엔드 코드에 정의된 목록과 DB의 차단 상태 병합)
- `GET /extensions/custom` (DB에 등록된 모든 커스텀 확장자 목록)

2. 프론트엔드는 두 API의 응답을 받아 고정 확장자 리스트와 커스텀 확장자 리스트를 화면에 동시에 렌더링합니다.

---

#### 2. 커스텀 확장자 추가

1. 사용자가 입력란에 확장자 이름을 입력하고 “추가” 버튼을 클릭합니다.
2. 프론트엔드는 로컬 목록에서 중복을 확인한 후, 중복이 아닐 경우 `POST /extensions/custom` 요청을 전송합니다.
3. 서버 로직:
   - 중복 검사를 다시 한번 수행하고, 등록 가능한 경우 DB에 새로운 확장자를 생성 후 저장합니다.
   - 서버는 새로 생성된 확장자 정보를 반환합니다.
4. 반환된 커스텀 확장자가 UI 리스트에 즉시 표시됩니다.

---

#### 3. 고정 확장자 체크/해제

1. 사용자가 체크박스를 클릭합니다.
2. 프론트는 **로컬 상태를 즉시 변경(Optimistic Update)** 합니다.
3. 동시에 `PATCH /extensions/fixed/{name}` 요청을 전송합니다.
4. 서버는 **DB 상태를 갱신**합니다.
5. 실패 시 UI를 롤백하고 에러 메시지를 표시합니다.

---

#### 4. 커스텀 확장자 삭제

1. X 버튼 클릭 시 확인 다이얼로그 표시 → 확인 후 `DELETE /extensions/custom/{name}` 요청 전송
2. 서버는 해당 확장자를 **DB에서 삭제**합니다.
3. UI에서는 즉시 해당 항목이 제거됩니다.

---

#### 5. 새로고침 시

- 프론트엔드는 다시 `GET /extensions/fixed` 요청을 보냅니다.

---

## 🌐 GCE 배포 가이드

### 1. GCE 인스턴스 생성

1. **Google Cloud Console**에서 Compute Engine 인스턴스 생성
2. **권장 사양**:
   - 머신 유형: `e2-micro` (무료 티어) 또는 `e2-small`
   - 운영체제: Ubuntu 20.04 LTS 또는 22.04 LTS
   - 부팅 디스크: 10GB (표준 영구 디스크)
   - 방화벽: HTTP 트래픽 허용 체크

### 2. 방화벽 규칙 설정

```bash
# GCE 인스턴스에서 실행
sudo ufw allow 8080
sudo ufw enable
```

또는 Google Cloud Console에서:
- VPC 네트워크 → 방화벽 → 방화벽 규칙 생성
- 대상: 모든 인스턴스
- 소스 IP 범위: 0.0.0.0/0
- 프로토콜 및 포트: TCP 8080

### 3. Docker 및 Docker Compose 설치

```bash
# Docker 설치
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 재로그인 또는 새 터미널 세션 시작
```

### 4. 프로젝트 배포

```bash
# 1. 프로젝트 클론
git clone <repository-url>
cd flow_subject

# 2. 환경 변수 설정
cp .env.example .env
nano .env  # DB_PASSWORD를 안전한 비밀번호로 변경

# 3. 자동 배포 스크립트 실행
chmod +x deploy-gce.sh
./deploy-gce.sh
```

### 5. 프론트엔드 업데이트

```bash
# 프론트엔드 디렉토리에서
cd frontend

# GCE 인스턴스 IP로 API URL 설정
export VITE_API_URL=http://[GCE_IP]:8080
npm run build

# Firebase에 재배포
firebase deploy
```

### 6. 서비스 관리

```bash
# 서비스 상태 확인
docker-compose ps

# 서비스 로그 확인
docker-compose logs -f

# 서비스 재시작
docker-compose restart

# 서비스 중지
docker-compose down

# 서비스 시작
docker-compose up -d
```

### 7. 데이터베이스 백업

```bash
# PostgreSQL 덤프 생성
docker-compose exec db pg_dump -U postgres extensiondb > backup_$(date +%Y%m%d_%H%M%S).sql

# 백업 복원
docker-compose exec -T db psql -U postgres extensiondb < backup_file.sql
```

---

## 📚 기존 Cloud Run 배포 정보 (참고용)

### Cloud Run 배포 (이전 방식)

- **백엔드**: Google Cloud Run
- **데이터베이스**: Cloud SQL (PostgreSQL)
- **프론트엔드**: Firebase Hosting

**배포된 서비스**:
- 프론트엔드: https://compact-factor-476014-p9.web.app
- 백엔드 API: https://extension-backend-681456847825.asia-northeast3.run.app

**프로덕션 빌드**:
```bash
cd frontend
npm install
# 프로덕션 빌드 시 Cloud Run API URL 설정
export VITE_API_URL=https://extension-backend-681456847825.asia-northeast3.run.app
npm run build
firebase deploy
```