# 🦆 Duckmelang (덕멜랑)

Spring Boot 기반 모놀리식 백엔드 서버. AWS EKS 위에서 동작하며 GitHub Actions → ECR → ArgoCD 파이프라인으로 자동 배포됩니다.

---

## 목차

1. [기술 스택](#기술-스택)
2. [프로젝트 구조](#프로젝트-구조)
3. [로컬 개발 환경 설정](#로컬-개발-환경-설정)
4. [CI/CD 파이프라인](#cicd-파이프라인)
5. [Kubernetes 구성](#kubernetes-구성)
6. [Ingress & cert-manager (HTTPS)](#ingress--cert-manager-https)
7. [ArgoCD](#argocd)
8. [모니터링 (Prometheus + Grafana)](#모니터링-prometheus--grafana)
9. [도메인 설명](#도메인-설명)

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.1 |
| Build | Gradle 8 |
| RDBMS | MySQL 8 |
| NoSQL | MongoDB 7 (채팅 이력 저장) |
| Cache / Lock | Redis 7 + Redisson |
| Message Broker | RabbitMQ 3 |
| Real-time | WebSocket / STOMP |
| Push 알림 | Firebase FCM |
| Auth | JWT (Spring Security) |
| API 문서 | Swagger (springdoc-openapi 2.2.0) |
| Container | Docker (multi-stage build) |
| Orchestration | Kubernetes (AWS EKS) |
| GitOps | ArgoCD |
| CI | GitHub Actions |
| Image Registry | Amazon ECR |
| Monitoring | Prometheus + Grafana + node-exporter |
| Ingress | ingress-nginx |
| TLS | cert-manager + Let's Encrypt |

---

## 프로젝트 구조

```
monolithic/
├── src/
│   └── main/java/umc/duckmelang/
│       ├── domain/
│       │   ├── application/   # 신청 (동시성 제어 전략 포함)
│       │   ├── auth/          # JWT 인증/인가
│       │   ├── chat/          # 실시간 채팅 (WebSocket + RabbitMQ + MongoDB)
│       │   ├── member/        # 회원
│       │   └── post/          # 게시글 (조회수 Redis 캐싱)
│       └── global/
│           ├── apipayload/    # 공통 응답 포맷 & 예외 처리
│           └── config/        # Spring 설정 (Security, Redis, RabbitMQ 등)
├── k8s/
│   ├── app/                   # 앱 관련 K8s 매니페스트 (ArgoCD 감시 대상)
│   ├── argocd/                # ArgoCD 관련 K8s 매니페스트
│   └── monitoring/            # Prometheus, Grafana, node-exporter
├── .github/workflows/         # GitHub Actions CI/CD
├── Dockerfile                 # 멀티 스테이지 빌드
└── docker-compose.yml         # 로컬 개발용 인프라
```

---

## 로컬 개발 환경 설정

### 1. 인프라 실행 (Docker Compose)

로컬에서 MySQL, Redis, MongoDB, RabbitMQ를 한 번에 띄웁니다.

```bash
# 프로젝트 루트에 .env 파일 생성
cat > .env << EOF
MYSQL_DATABASE=duckmelang
MYSQL_USER=duckmelang
MYSQL_PASSWORD=yourpassword
MYSQL_ROOT_PASSWORD=rootpassword
MONGO_USERNAME=mongo
MONGO_PASSWORD=mongopassword
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest

# 앱 설정
MYSQL_URL=jdbc:mysql://localhost:13306/duckmelang?useSSL=false&allowPublicKeyRetrieval=true
MONGO_PORT=37017
REDIS_PORT=16379
SECRET_KEY=your-jwt-secret-key
ACCESS_EXPIRATION=3600000
REFRESH_EXPIRATION=3600000
EOF

docker-compose up -d
```

| 서비스 | 로컬 포트 | 설명 |
|--------|-----------|------|
| MySQL | 13306 | RDBMS |
| Redis | 16379 | 캐시 & 분산 락 |
| MongoDB | 37017 | 채팅 이력 |
| RabbitMQ AMQP | 5672 | 메시지 브로커 |
| RabbitMQ 관리 콘솔 | 15672 | http://localhost:15672 (guest/guest) |

### 2. 애플리케이션 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

- 서버: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Health check: http://localhost:8080/health

---

## CI/CD 파이프라인

```
개발자 → git push (main) → GitHub Actions → ECR → kubectl rollout restart → 새 이미지로 Pod 재시작
```

### 흐름 상세

```
[GitHub main 브랜치에 push]
        │
        ▼
[GitHub Actions: .github/workflows/deploy.yml]
  1. AWS 자격증명 설정 (Secrets: AWS_KEY, AWS_SECRET)
  2. kubeconfig 업데이트 (aws eks update-kubeconfig)
  3. Amazon ECR 로그인
  4. Docker 이미지 빌드 & ECR 푸시
     - 이미지 태그: latest
     - 레지스트리: 435998721170.dkr.ecr.ap-northeast-2.amazonaws.com/duckmelang
     - 플랫폼: linux/amd64 (M1/M2 Mac에서 빌드해도 EKS 호환)
  5. kubectl rollout restart deployment duckmelang -n duckmelang
     → imagePullPolicy: Always → 새 latest 이미지 Pull 후 Pod 교체
  6. kubectl rollout status 로 배포 완료 확인
```

### GitHub Secrets 설정 필요 항목

| Secret 이름 | 설명 |
|-------------|------|
| `AWS_KEY` | AWS IAM Access Key ID |
| `AWS_SECRET` | AWS IAM Secret Access Key |

### Dockerfile (멀티 스테이지)

```
1단계 (build): gradle:8.8-jdk17 → gradle bootJar → app.jar 생성
2단계 (runtime): amazoncorretto:17-alpine → jar만 복사 → 경량 이미지
```

최종 이미지는 Alpine 기반 Amazon Corretto 17로 용량이 작고 보안 패치가 빠릅니다.

---

## Kubernetes 구성

### 네임스페이스

| 네임스페이스 | 용도 |
|-------------|------|
| `duckmelang` | 애플리케이션 + 미들웨어 |
| `argocd` | ArgoCD |
| `monitoring` | Prometheus + Grafana + node-exporter |

### k8s/app 디렉토리

ArgoCD가 감시하는 디렉토리입니다. 이 안의 파일을 수정하고 main에 push하면 자동 배포됩니다.

#### 애플리케이션 (depl_svc.yml)

```
Deployment: duckmelang
  - replicas: 3
  - image: 435998721170.dkr.ecr.ap-northeast-2.amazonaws.com/duckmelang:latest
  - resources:
      requests: CPU 0.5 / Memory 250Mi
      limits:   CPU 1   / Memory 500Mi
  - readinessProbe: GET /health 포트 8080 (10초 간격, 초기 10초 대기)
  - env (Kubernetes Secret "my-app-secrets" 에서 주입):
      DB_HOST, DB_PW

Service: duckmelang-service
  - type: ClusterIP
  - port: 80 → targetPort: 8080
```

`readinessProbe`는 롤링 업데이트 시 트래픽 유입 전 애플리케이션이 정상 기동됐는지 확인합니다. 준비되지 않은 Pod에는 트래픽이 라우팅되지 않습니다.

> `revisionHistoryLimit: 2` — 롤백 가능한 ReplicaSet 이력을 2개로 제한해 자원 낭비를 방지합니다.

#### Secret 생성 (최초 1회)

```bash
kubectl create secret generic my-app-secrets \
  --from-literal=DB_HOST=<RDS_ENDPOINT> \
  --from-literal=DB_PW=<DB_PASSWORD> \
  -n duckmelang
```

#### 미들웨어

| 파일 | 리소스 | 포트 |
|------|--------|------|
| `mongodb.yml` | Deployment + Service (ClusterIP) | 27017 |
| `redis.yml` | Deployment + Service (ClusterIP) | 6379 |
| `rabbitmq.yml` | Deployment + Service (ClusterIP) | 5672 (AMQP), 15672 (관리 UI) |

모든 미들웨어는 ClusterIP로 클러스터 내부에서만 접근 가능합니다. 앱 설정(`application-prod.yml`)에서 서비스 이름으로 접근합니다.

```yaml
# application-prod.yml
spring.data.mongodb.uri:  mongodb://mongodb-service:27017/duckmelang
spring.data.redis.host:   redis-service
spring.rabbitmq.host:     rabbitmq-service
```

---

## Ingress & cert-manager (HTTPS)

### 전체 흐름

```
인터넷
  │
  ▼
[AWS NLB / ELB]   ← ingress-nginx 설치 시 자동 생성
  │
  ▼
[ingress-nginx controller]  (nginx 기반 L7 라우터)
  │
  ├─ server.handdoc.store → duckmelang-service:80
  └─ argo.handdoc.store   → argocd-server:80
  │
  ▼
[cert-manager]  TLS 인증서 자동 발급 & 갱신
  └─ Let's Encrypt ACME (HTTP-01 챌린지)
```

### ingress-nginx 설치

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/aws/deploy.yaml
```

### cert-manager 설치

```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
```

### ClusterIssuer (k8s/app/https.yml)

클러스터 전체에서 공유하는 Let's Encrypt 인증서 발급자입니다.

```yaml
kind: ClusterIssuer
metadata:
  name: my-issuer
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: soohee2001@gmail.com       # 만료 알림 수신 주소
    solvers:
      - http01:
          ingress:
            class: nginx              # HTTP-01 챌린지를 nginx ingress로 처리
```

### Certificate (k8s/app/https.yml)

```yaml
kind: Certificate
metadata:
  name: server-erika-com-tls
spec:
  secretName: server-erika-com-tls   # 발급된 인증서가 저장될 Secret 이름
  duration:    2160h  # 90일
  renewBefore:  360h  # 만료 15일 전 자동 갱신
  issuerRef:
    name: my-issuer
    kind: ClusterIssuer
  dnsNames:
    - server.handdoc.store
```

### Ingress (k8s/app/ingress.yml)

```yaml
kind: Ingress
metadata:
  annotations:
    cert-manager.io/cluster-issuer: my-issuer   # 이 Ingress의 TLS를 my-issuer로 발급
spec:
  tls:
    - hosts: [server.handdoc.store]
      secretName: server-erika-com-tls           # Certificate의 secretName과 일치해야 함
  rules:
    - host: server.handdoc.store
      http:
        paths:
          - path: /
            backend:
              service:
                name: duckmelang-service
                port: 80
```

**인증서 발급 과정 (자동)**

1. `Certificate` 리소스 생성 감지 → cert-manager가 ACME 서버에 인증서 요청
2. Let's Encrypt가 HTTP-01 챌린지 요청 → cert-manager가 nginx ingress에 임시 경로 생성
3. Let's Encrypt가 `http://server.handdoc.store/.well-known/acme-challenge/...` 접근 확인
4. 인증 성공 → TLS 인증서 발급 → `server-erika-com-tls` Secret에 저장
5. 만료 15일 전 자동 갱신

---

## ArgoCD

GitOps 방식으로 Kubernetes 배포를 관리합니다. GitHub 저장소의 `k8s/app` 디렉토리가 클러스터의 실제 상태와 일치하도록 지속적으로 동기화합니다.

### 접속

- URL: https://argo.handdoc.store
- 인증서: cert-manager + Let's Encrypt 자동 발급 (`k8s/argocd/https.yml`)

### Application 설정 (k8s/argocd/argocd-application.yml)

```yaml
kind: Application
metadata:
  name: duckmelang
  namespace: argocd
spec:
  source:
    repoURL: https://github.com/duckmelang-org/monolithic.git
    targetRevision: main          # main 브랜치 감시
    path: k8s/app                 # 이 디렉토리의 변경 사항을 클러스터에 반영
  destination:
    namespace: duckmelang
  syncPolicy:
    automated:
      prune: true      # Git에서 삭제된 리소스를 클러스터에서도 삭제
      selfHeal: true   # 클러스터 직접 수정 감지 시 Git 상태로 되돌림
  ignoreDifferences:
    - group: cert-manager.io
      kind: Certificate
      jsonPointers: [/metadata/annotations, /spec, /status]
      # cert-manager가 Certificate를 자동 갱신하며 spec을 수정하는데,
      # ArgoCD가 이를 "drift"로 감지해 롤백하지 않도록 무시 처리
```

### ArgoCD 서비스 (k8s/argocd/argocd-service.yml)

기본 ArgoCD 설치 시 `argocd-server` 서비스는 NodePort 또는 LoadBalancer입니다. 이를 ClusterIP로 변경하고 ingress-nginx 뒤에 위치시킵니다.

```yaml
kind: Service
metadata:
  name: argocd-server
  namespace: argocd
spec:
  type: ClusterIP
  ports:
    - port: 80
      targetPort: 8080
```

### ArgoCD 설치 (최초 1회)

```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# 초기 admin 비밀번호 확인
kubectl get secret argocd-initial-admin-secret -n argocd \
  -o jsonpath='{.data.password}' | base64 -d
```

---

## 모니터링 (Prometheus + Grafana)

클러스터 내 모든 노드의 시스템 메트릭을 수집하고 시각화합니다.

### 아키텍처

```
[각 노드] node-exporter (DaemonSet, 포트 9100)
    └─ /proc, /sys, / 마운트 → CPU, 메모리, 디스크, 네트워크 메트릭 수집
            │
            │ HTTP Scrape (15초 간격)
            ▼
[Prometheus] (namespace: monitoring, 포트 9090)
    ├─ kubernetes_sd_configs role:node 로 노드 자동 탐색
    ├─ relabel: :10250 → :9100 포트 변환
    └─ ConfigMap "prometheus-config" → prometheus.yml 설정 주입
            │
            ▼
[Grafana] (namespace: monitoring, 포트 3000)
    └─ Prometheus를 Data Source로 연결 → 대시보드 시각화
```

### node-exporter (k8s/monitoring/node_exporter.yml)

`DaemonSet`으로 배포되어 **클러스터의 모든 노드에 자동으로 1개씩** 배포됩니다.

```yaml
kind: DaemonSet
spec:
  containers:
    - image: prom/node-exporter
      ports:
        - containerPort: 9100
          hostPort: 9100      # 노드의 IP:9100 으로 직접 노출
      volumeMounts:
        - /host/proc ← /proc  (CPU, 메모리, 프로세스 정보)
        - /host/sys  ← /sys   (커널, 하드웨어 정보)
        - /rootfs    ← /      (디스크 사용량)
```

### Prometheus RBAC (k8s/monitoring/prometheus-rbac.yml)

Prometheus Pod가 Kubernetes API를 통해 노드 목록을 조회할 수 있도록 권한을 부여합니다.

```
ClusterRole "prometheus"
  └─ nodes, nodes/metrics 에 대한 get, list, watch 권한
        │
        ▼ ClusterRoleBinding
ServiceAccount "prometheus" (namespace: monitoring)
        │
        ▼ Deployment spec.serviceAccountName
Prometheus Pod
```

### Prometheus 설정 (k8s/monitoring/prometheus-config.yml)

`ConfigMap`으로 `prometheus.yml`을 관리합니다. 설정 변경 시 ConfigMap을 수정하고 Prometheus Pod를 재시작합니다.

```yaml
scrape_configs:
  - job_name: 'node-exporter'
    kubernetes_sd_configs:
      - role: node                  # K8s API로 노드 자동 탐색
    relabel_configs:
      - source_labels: [__address__]
        regex: '(.*):10250'
        replacement: '${1}:9100'   # kubelet 포트(10250) → node-exporter 포트(9100)
        target_label: __address__
```

### Grafana 초기 설정

```bash
# 포트 포워딩으로 로컬 접근
kubectl port-forward svc/grafana 3000:3000 -n monitoring
# http://localhost:3000  (기본 계정: admin / admin, 최초 로그인 후 변경)
```

1. Configuration → Data Sources → Add data source → Prometheus
2. URL: `http://prometheus-service:9090`
3. Import 대시보드: Node Exporter Full (ID: 1860)

---

## 도메인 설명

### 인증 (auth)

- JWT 기반 인증 (Access Token + Refresh Token)
- Spring Security 필터 체인에 `JwtAuthorizationFilter` 등록
- WebSocket 연결 시 STOMP 헤더의 JWT를 `StompAuthChannelInterceptor`로 검증

### 게시글 (post)

- 조회수 카운터를 Redis에 캐싱하고, `PostViewCountSyncScheduler`가 주기적으로 MySQL에 동기화 (Write-behind 패턴)

### 신청 (application)

동시에 여러 사용자가 동일한 게시글에 신청하는 **동시성 문제**를 다양한 전략으로 구현 및 비교합니다.

| Facade 클래스 | 전략 |
|--------------|------|
| `SynchronizedApplicationFacade` | Java `synchronized` (단일 서버 전용) |
| `OptimisticLockApplicationFacade` | JPA Optimistic Lock (`@Version`) |
| `PessimisticLockApplicationFacade` | JPA Pessimistic Lock (DB 레벨 락) |
| `NamedLockApplicationFacade` | MySQL Named Lock |
| `LettuceLockApplicationFacade` | Redis Lettuce (스핀락) |
| `RedissonLockApplicationFacade` | Redis Redisson (pub/sub 기반 분산 락) |

### 채팅 (chat)

```
[클라이언트 A] --STOMP/WebSocket--> [서버]
                                       │ ChatMessagePublisher
                                       ▼
                                  [RabbitMQ]
                                       │ ChatMessageConsumer
                                       ▼
                  [MongoDB: 채팅 이력 저장]   [Redis pub/sub: 실시간 전달]
                                                        │
                                       [클라이언트 B] <──┘
                                   + [FCM 푸시 알림 (앱 미실행 시)]
```

---

## 운영 체크리스트

### 최초 클러스터 설정 순서

```bash
# 1. 네임스페이스 생성
kubectl create namespace duckmelang
kubectl create namespace monitoring

# 2. ingress-nginx 설치
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/aws/deploy.yaml

# 3. cert-manager 설치
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# 4. ArgoCD 설치
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# 5. Secret 생성
kubectl create secret generic my-app-secrets \
  --from-literal=DB_HOST=<RDS_ENDPOINT> \
  --from-literal=DB_PW=<DB_PASSWORD> \
  -n duckmelang

# 6. ClusterIssuer 적용 (cert-manager CRD 준비 후)
kubectl apply -f k8s/app/https.yml

# 7. ArgoCD Application 등록
kubectl apply -f k8s/argocd/argocd-application.yml
kubectl apply -f k8s/argocd/argocd-service.yml
kubectl apply -f k8s/argocd/argocd-ingress.yml
kubectl apply -f k8s/argocd/https.yml

# 8. 모니터링 스택 배포
kubectl apply -f k8s/monitoring/
```

이후 `k8s/app` 디렉토리를 수정하고 main에 push하면 ArgoCD가 자동으로 클러스터에 반영합니다.

### 배포 도메인 구성

| 도메인 | 연결 대상 | TLS |
|--------|-----------|-----|
| `server.handdoc.store` | 백엔드 API 서버 | Let's Encrypt 자동 갱신 |
| `argo.handdoc.store` | ArgoCD Web UI | Let's Encrypt 자동 갱신 |