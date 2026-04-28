# Kubernetes 구성

## 네임스페이스

| 네임스페이스 | 용도 |
|-------------|------|
| `duckmelang` | 애플리케이션 + 미들웨어 |
| `argocd` | ArgoCD |
| `monitoring` | Prometheus + Grafana + node-exporter |

## k8s/app 디렉토리

ArgoCD가 감시하는 디렉토리입니다. 이 안의 파일을 수정하고 main에 push하면 자동 배포됩니다.

### 애플리케이션 (depl_svc.yml)

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

### Secret 생성 (최초 1회)

```bash
kubectl create secret generic my-app-secrets \
  --from-literal=DB_HOST=<RDS_ENDPOINT> \
  --from-literal=DB_PW=<DB_PASSWORD> \
  -n duckmelang
```

## 미들웨어

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

## 최초 클러스터 설정 순서

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

## 배포 도메인

| 도메인 | 연결 대상 | TLS |
|--------|-----------|-----|
| `server.handdoc.store` | 백엔드 API 서버 | Let's Encrypt 자동 갱신 |
| `argo.handdoc.store` | ArgoCD Web UI | Let's Encrypt 자동 갱신 |