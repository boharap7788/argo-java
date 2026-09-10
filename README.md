# Argo CD Java Labs

A small Spring Boot backend used to learn three ways to manage multiple services with Argo CD. The Java application responds on `/api/hello`; the separate user and order images return different messages so they are easy to identify.

## One-time setup

Requirements: Docker, kind, kubectl, Java 17, and Maven.

Create a kind cluster and install Argo CD with the official manifest:

```bash
kind create cluster --name argo-lab
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
kubectl wait --for=condition=Available deployment/argocd-server -n argocd --timeout=180s
```

Build the two local images and load them into kind. No registry is needed for this lab:

```bash
mvn package -DskipTests
docker build -f docker/user-service/Dockerfile -t argo-java:user-service .
docker build -f docker/order-service/Dockerfile -t argo-java:order-service .
kind load docker-image argo-java:user-service --name argo-lab
kind load docker-image argo-java:order-service --name argo-lab
```

Push this repository to a Git remote before running a lab. Replace `YOUR_USERNAME` with the repository owner in every `repoURL` in [argocd/apps/user-service.yaml](argocd/apps/user-service.yaml), [argocd/apps/order-service.yaml](argocd/apps/order-service.yaml), [argocd/root-app.yaml](argocd/root-app.yaml), and [argocd/applicationset.yaml](argocd/applicationset.yaml).

The Argo CD UI is available locally with:

```bash
kubectl port-forward svc/argocd-server -n argocd 8080:443
```

Open `https://localhost:8080`. The admin password is the `argocd-initial-admin-secret` value in the `argocd` namespace.

## Lab 1: One Application per service

Apply two independent Argo CD Applications:

```bash
kubectl apply -f argocd/apps/user-service.yaml
kubectl apply -f argocd/apps/order-service.yaml
```

The UI shows two cards: `user-service` and `order-service`. Each service syncs on its own and points directly to its own folder under `k8s/`.

Check the services:

```bash
kubectl get applications -n argocd
kubectl get pods -n user-service
kubectl get pods -n order-service
```

Teardown before the next lab:

```bash
kubectl delete -f argocd/apps/
kubectl delete namespace user-service order-service --ignore-not-found
```

## Lab 2: App of Apps

Apply one parent Application:

```bash
kubectl apply -f argocd/root-app.yaml
```

`shop-root` reads [argocd/apps](argocd/apps) and creates the child Applications. The UI shows three cards: `shop-root`, `user-service`, and `order-service`.

Teardown before the next lab:

```bash
kubectl delete -f argocd/root-app.yaml
kubectl delete -f argocd/apps/
kubectl delete namespace user-service order-service --ignore-not-found
```

## Lab 3: ApplicationSet

Apply one ApplicationSet:

```bash
kubectl apply -f argocd/applicationset.yaml
```

The Git directory generator turns every folder matching `k8s/*` into an Application. The current folders create `user-service` and `order-service` cards. Add a tracked `k8s/payment-service/` folder containing Kubernetes manifests, push it to Git, and ApplicationSet creates a third card without a new Application YAML.

Teardown:

```bash
kubectl delete -f argocd/applicationset.yaml
kubectl delete namespace user-service order-service payment-service --ignore-not-found
```

## Important

Do not run Labs 1, 2, and 3 at the same time. They all use the names `user-service` and `order-service`, so the Application resources would clash. Run them in order and complete the teardown between labs.

One Argo CD installation can manage many microservices. These labs demonstrate three ownership models: explicit Applications, a parent that creates Applications, and an ApplicationSet that generates Applications from Git folders.