

```bash
cd docker
gcloud container builds submit --tag gcr.io/spatial-framing-163309/ibmjdk-on-debian:latest .

```

```bash
sbt clean docker:stage
cd target/docker/stage
gcloud container builds submit --tag gcr.io/spatial-framing-163309/try-gcp-on-play:latest .

```

```
cd docker/k8s
kubectl apply -f service.yml
kubectl apply -f deployment.yml


```