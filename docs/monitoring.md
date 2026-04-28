# 모니터링 (Prometheus + Grafana)

클러스터 내 모든 노드의 시스템 메트릭을 수집하고 시각화합니다.

## 아키텍처

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

## node-exporter (k8s/monitoring/node_exporter.yml)

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

## Prometheus RBAC (k8s/monitoring/prometheus-rbac.yml)

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

## Prometheus 설정 (k8s/monitoring/prometheus-config.yml)

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

## Grafana 초기 설정

```bash
# 포트 포워딩으로 로컬 접근
kubectl port-forward svc/grafana 3000:3000 -n monitoring
# http://localhost:3000  (기본 계정: admin / admin, 최초 로그인 후 변경)
```

1. Configuration → Data Sources → Add data source → Prometheus
2. URL: `http://prometheus-service:9090`
3. Import 대시보드: Node Exporter Full (ID: 1860)