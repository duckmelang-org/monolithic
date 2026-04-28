# ArgoCD

GitOps 방식으로 Kubernetes 배포를 관리합니다. GitHub 저장소의 `k8s/app` 디렉토리가 클러스터의 실제 상태와 일치하도록 지속적으로 동기화합니다.

## 접속

- URL: https://argo.handdoc.store
- 인증서: cert-manager + Let's Encrypt 자동 발급 (`k8s/argocd/https.yml`)

## Application 설정 (k8s/argocd/argocd-application.yml)

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

## ArgoCD 서비스 (k8s/argocd/argocd-service.yml)

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

## ArgoCD 설치 (최초 1회)

```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

# 초기 admin 비밀번호 확인
kubectl get secret argocd-initial-admin-secret -n argocd \
  -o jsonpath='{.data.password}' | base64 -d
```