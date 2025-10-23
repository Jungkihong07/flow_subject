# GitHub Secrets 설정 가이드

## 필요한 GitHub Secrets

다음 환경 변수들을 GitHub Repository의 Settings > Secrets and variables > Actions에서 설정해야 합니다:

### 1. GCP 관련
- `GCP_PROJECT_ID`: `compact-factor-476014-p9`
- `GCP_SA_KEY`: 서비스 계정 키 JSON (전체 내용)

### 2. 데이터베이스 관련
- `DB_USERNAME`: PostgreSQL 사용자명 (예: `postgres`)
- `DB_PASSWORD`: PostgreSQL 비밀번호
- `CLOUD_SQL_CONNECTION_NAME`: `compact-factor-476014-p9:asia-northeast3:extension-db`
- `CLOUD_SQL_DATABASE`: `extension_db`

### 3. Cloud Storage 관련
- `GCS_BUCKET_NAME`: `flow-frontend`

## 설정 방법

1. GitHub Repository로 이동
2. Settings > Secrets and variables > Actions
3. "New repository secret" 클릭
4. 위의 각 항목을 Name과 Secret으로 설정

## Cloud SQL 연결 이름 형식

Cloud SQL 연결 이름은 다음 형식이어야 합니다:
```
PROJECT_ID:REGION:INSTANCE_NAME
```

예시:
```
compact-factor-476014-p9:asia-northeast3:extension-db
```

## 확인 방법

GitHub Actions 실행 시 로그에서 다음을 확인할 수 있습니다:
- "Generated app-deploy.yaml content:" 섹션에서 환경 변수가 올바르게 주입되었는지 확인
- 배포 로그에서 데이터베이스 연결 성공 여부 확인
