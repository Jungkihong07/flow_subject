#!/bin/bash

# GCE Docker Compose 배포 스크립트
# Extension Backend + PostgreSQL 배포

set -e  # 에러 발생 시 스크립트 중단

echo "🚀 Extension Backend GCE 배포를 시작합니다..."

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 함수 정의
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 1. 필수 도구 확인
log_info "필수 도구 설치 상태 확인 중..."

if ! command -v docker &> /dev/null; then
    log_error "Docker가 설치되지 않았습니다."
    log_info "Docker 설치 명령어:"
    echo "curl -fsSL https://get.docker.com -o get-docker.sh"
    echo "sudo sh get-docker.sh"
    echo "sudo usermod -aG docker \$USER"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    log_error "Docker Compose가 설치되지 않았습니다."
    log_info "Docker Compose 설치 명령어:"
    echo "sudo curl -L \"https://github.com/docker/compose/releases/latest/download/docker-compose-\$(uname -s)-\$(uname -m)\" -o /usr/local/bin/docker-compose"
    echo "sudo chmod +x /usr/local/bin/docker-compose"
    exit 1
fi

log_success "필수 도구 확인 완료"

# 2. .env 파일 확인
log_info ".env 파일 확인 중..."

if [ ! -f ".env" ]; then
    log_warning ".env 파일이 없습니다."
    log_info ".env 파일을 생성합니다..."
    
    if [ -f ".env.example" ]; then
        cp .env.example .env
        log_success ".env 파일이 생성되었습니다."
        log_warning "⚠️  .env 파일에서 DB_PASSWORD를 안전한 비밀번호로 변경하세요!"
        echo ""
        echo "현재 .env 파일 내용:"
        cat .env
        echo ""
        read -p "비밀번호를 변경했습니까? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            log_error "비밀번호를 변경한 후 다시 실행하세요."
            exit 1
        fi
    else
        log_error ".env.example 파일이 없습니다."
        exit 1
    fi
fi

log_success ".env 파일 확인 완료"

# 3. 방화벽 규칙 확인
log_info "방화벽 규칙 확인 중..."

if command -v ufw &> /dev/null; then
    if ! ufw status | grep -q "8080"; then
        log_warning "포트 8080이 방화벽에서 열려있지 않습니다."
        log_info "방화벽 규칙 추가 명령어:"
        echo "sudo ufw allow 8080"
        echo ""
        read -p "지금 방화벽 규칙을 추가하시겠습니까? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            sudo ufw allow 8080
            log_success "방화벽 규칙이 추가되었습니다."
        else
            log_warning "방화벽 규칙을 수동으로 추가하세요."
        fi
    else
        log_success "포트 8080이 이미 열려있습니다."
    fi
else
    log_warning "ufw가 설치되지 않았습니다. 방화벽 설정을 수동으로 확인하세요."
fi

# 4. 기존 컨테이너 정리
log_info "기존 컨테이너 정리 중..."

if [ "$(docker-compose ps -q)" ]; then
    log_info "기존 컨테이너를 중지합니다..."
    docker-compose down
    log_success "기존 컨테이너 정리 완료"
else
    log_info "실행 중인 컨테이너가 없습니다."
fi

# 5. 이미지 빌드 및 컨테이너 실행
log_info "Docker 이미지 빌드 및 컨테이너 실행 중..."

docker-compose up --build -d

log_success "컨테이너 실행 완료"

# 6. 헬스체크
log_info "서비스 헬스체크 중..."

# PostgreSQL 헬스체크
log_info "PostgreSQL 연결 확인 중..."
sleep 10  # PostgreSQL 초기화 대기

if docker-compose exec -T db pg_isready -U postgres -d extensiondb; then
    log_success "PostgreSQL 연결 성공"
else
    log_error "PostgreSQL 연결 실패"
    log_info "PostgreSQL 로그 확인:"
    docker-compose logs db
    exit 1
fi

# Backend 헬스체크
log_info "Backend 서비스 확인 중..."
sleep 20  # Backend 초기화 대기

if curl -f http://localhost:8080/actuator/health 2>/dev/null; then
    log_success "Backend 서비스 정상 작동"
else
    log_warning "Backend 헬스체크 실패, 로그를 확인하세요."
    log_info "Backend 로그:"
    docker-compose logs backend
fi

# 7. 서비스 상태 확인
log_info "서비스 상태 확인:"
docker-compose ps

# 8. 외부 IP 확인
log_info "외부 접근 정보:"
EXTERNAL_IP=$(curl -s ifconfig.me 2>/dev/null || curl -s ipinfo.io/ip 2>/dev/null || echo "확인 불가")
echo "외부 IP: $EXTERNAL_IP"
echo "API URL: http://$EXTERNAL_IP:8080"
echo ""

# 9. 프론트엔드 업데이트 안내
log_info "프론트엔드 업데이트 안내:"
echo "프론트엔드에서 다음 환경 변수를 설정하고 재빌드하세요:"
echo "VITE_API_URL=http://$EXTERNAL_IP:8080"
echo ""
echo "빌드 명령어:"
echo "cd frontend"
echo "VITE_API_URL=http://$EXTERNAL_IP:8080 npm run build"
echo "firebase deploy"
echo ""

# 10. 유용한 명령어 안내
log_info "유용한 명령어:"
echo "서비스 로그 확인: docker-compose logs -f"
echo "서비스 상태 확인: docker-compose ps"
echo "서비스 중지: docker-compose down"
echo "서비스 재시작: docker-compose restart"
echo "PostgreSQL 접속: docker-compose exec db psql -U postgres -d extensiondb"
echo ""

log_success "🎉 배포가 완료되었습니다!"
log_info "서비스가 정상적으로 작동하는지 확인하세요."
