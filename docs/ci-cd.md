# CI/CD 파이프라인

```
개발자 → git push (main) → GitHub Actions → ECR → kubectl rollout restart → 새 이미지로 Pod 재시작
```

## 흐름 상세

```
[GitHub main 브랜치에 push]
        │
        ▼
[GitHub Actions: .github/workflows/workflow.yml]
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

## GitHub Secrets 설정 필요 항목

| Secret 이름 | 설명 |
|-------------|------|
| `AWS_KEY` | AWS IAM Access Key ID |
| `AWS_SECRET` | AWS IAM Secret Access Key |

## Dockerfile (멀티 스테이지)

```
1단계 (build): gradle:8.8-jdk17 → gradle bootJar → app.jar 생성
2단계 (runtime): amazoncorretto:17-alpine → jar만 복사 → 경량 이미지
```

최종 이미지는 Alpine 기반 Amazon Corretto 17로 용량이 작고 보안 패치가 빠릅니다.
