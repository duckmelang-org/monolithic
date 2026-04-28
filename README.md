# 덕메랑 - GitOps on AWS EKS

> Spring Boot 기반 모놀리식 백엔드 서버로 AWS EKS 위에서 동작하며
> GitHub Actions → ECR → ArgoCD 파이프라인으로 자동 배포됩니다.

---

## Tech Stack

| 분류 | 기술 |
|------|------|
| **Backend** | Java 17, Spring Boot 3, Gradle, JWT, Swagger |
| **Database** | MySQL 8, MongoDB 7, Redis 7 |
| **Messaging & Realtime** | RabbitMQ, WebSocket / STOMP, Firebase FCM |
| **Infra** | Docker, Kubernetes, AWS EKS, Amazon ECR, GitHub Actions |
| **GitOps & Monitoring** | ArgoCD, Prometheus, Grafana |
| **Networking** | ingress-nginx, cert-manager, Let's Encrypt |

---

## 프로젝트 구조

```
monolithic/
├── src/main/java/umc/duckmelang/
│   ├── domain/
│   │   ├── application/   # 신청 (동시성 제어 전략 포함)
│   │   ├── auth/          # JWT 인증/인가
│   │   ├── chat/          # 실시간 채팅 (WebSocket + RabbitMQ + MongoDB)
│   │   ├── member/        # 회원
│   │   └── post/          # 게시글 (조회수 Redis 캐싱)
│   └── global/
│       ├── apipayload/    # 공통 응답 포맷 & 예외 처리
│       └── config/        # Spring 설정 (Security, Redis, RabbitMQ 등)
├── k8s/
│   ├── app/               # 앱 관련 K8s 매니페스트 (ArgoCD 감시 대상)
│   ├── argocd/            # ArgoCD 관련 K8s 매니페스트
│   └── monitoring/        # Prometheus, Grafana, node-exporter
├── .github/workflows/     # GitHub Actions CI/CD
├── Dockerfile             # 멀티 스테이지 빌드
└── docker-compose.yml     # 로컬 개발용 인프라
```

---

## 로컬 개발 환경

```bash
# 인프라 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

## 배포 도메인

| 도메인 | 대상 |
|--------|------|
| `server.handdoc.store` | 백엔드 API 서버 |
| `argo.handdoc.store` | ArgoCD Web UI |

---

## 문서

- [CI/CD 파이프라인](docs/ci-cd.md)
- [Kubernetes 구성](docs/kubernetes.md)
- [Ingress & cert-manager](docs/ingress-cert-manager.md)
- [ArgoCD](docs/argocd.md)
- [모니터링](docs/monitoring.md)
