# 덕메랑
Spring Boot 기반 모놀리식 백엔드 서버로 AWS EKS 위에서 동작하며 GitHub Actions → ECR → ArgoCD 파이프라인으로 자동 배포됩니다.

---

## Tech Stack

**Backend**
![Java](https://img.shields.io/badge/Java_17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=flat-square&logo=swagger&logoColor=black)

**Database**
![MySQL](https://img.shields.io/badge/MySQL_8-4479A1?style=flat-square&logo=mysql&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB_7-47A248?style=flat-square&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis_7-FF4438?style=flat-square&logo=redis&logoColor=white)

**Messaging & Realtime**
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat-square&logo=rabbitmq&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket%2FSTOMP-010101?style=flat-square)
![Firebase](https://img.shields.io/badge/Firebase_FCM-DD2C00?style=flat-square&logo=firebase&logoColor=white)

**Infra**
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=flat-square&logo=kubernetes&logoColor=white)
![AWS EKS](https://img.shields.io/badge/AWS_EKS-FF9900?style=flat-square&logo=amazonaws&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white)
![Amazon ECR](https://img.shields.io/badge/Amazon_ECR-FF9900?style=flat-square&logo=amazonaws&logoColor=white)

**GitOps & Monitoring**
![ArgoCD](https://img.shields.io/badge/ArgoCD-EF7B4D?style=flat-square&logo=argo&logoColor=white)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?style=flat-square&logo=prometheus&logoColor=white)
![Grafana](https://img.shields.io/badge/Grafana-F46800?style=flat-square&logo=grafana&logoColor=white)

**Networking**
![ingress-nginx](https://img.shields.io/badge/ingress--nginx-009639?style=flat-square&logo=nginx&logoColor=white)
![cert-manager](https://img.shields.io/badge/cert--manager-0070E0?style=flat-square)
![Let's Encrypt](https://img.shields.io/badge/Let's_Encrypt-003A70?style=flat-square&logo=letsencrypt&logoColor=white)

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