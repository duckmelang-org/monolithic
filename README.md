# 덕메랑 

Spring Boot 기반 모놀리식 백엔드 서버. AWS EKS 위에서 동작하며 GitHub Actions → ECR → ArgoCD 파이프라인으로 자동 배포됩니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.3.1 |
| Build | Gradle 8 |
| RDBMS | MySQL 8 |
| NoSQL | MongoDB 7 |
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
# 프로젝트 루트에 .env 파일 생성 후 인프라 실행
docker-compose up -d

# 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

| 서비스 | 로컬 포트 |
|--------|-----------|
| 앱 서버 | 8080 |
| MySQL | 13306 |
| Redis | 16379 |
| MongoDB | 37017 |
| RabbitMQ AMQP | 5672 |
| RabbitMQ 관리 콘솔 | 15672 |

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
