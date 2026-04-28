# Ingress & cert-manager (HTTPS)

## 전체 흐름

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

## ingress-nginx 설치

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/aws/deploy.yaml
```

## cert-manager 설치

```bash
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml
```

## ClusterIssuer (k8s/app/https.yml)

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

## Certificate (k8s/app/https.yml)

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

## Ingress (k8s/app/ingress.yml)

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

## 인증서 발급 과정 (자동)

1. `Certificate` 리소스 생성 감지 → cert-manager가 ACME 서버에 인증서 요청
2. Let's Encrypt가 HTTP-01 챌린지 요청 → cert-manager가 nginx ingress에 임시 경로 생성
3. Let's Encrypt가 `http://server.handdoc.store/.well-known/acme-challenge/...` 접근 확인
4. 인증 성공 → TLS 인증서 발급 → `server-erika-com-tls` Secret에 저장
5. 만료 15일 전 자동 갱신