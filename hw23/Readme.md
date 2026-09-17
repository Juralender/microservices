# hw23 — микросервисы с JWT-аутентификацией и шлюзом Traefik

Три сервиса (каждый — в отдельном Docker-образе), объединенные через API-шлюз с функцией аутентификации:

```
                          ┌─────────────────────────┐
   client  ────────────▶  │        Traefik            │   (api-gateway)
                          │   entryPoint :80          │
                          └────────────┬──────────────┘
                                       │
   register / login  ── no middleware ┤
                                       │
   logout, /api/users/**,             │
   /api/records/**    ── forwardAuth ─┤── calls auth-service /api/auth/validate
                                       │
                          ┌────────────┴──────────────┐
                          ▼                            ▼
               ┌──────────────────┐          ┌───────────────────┐
               │  records-service  │          │    auth-service     │
               │  (create + read-  │          │ register/login/     │
               │   own records)    │          │ validate + own-     │
               │                    │          │ profile GET/PUT     │
               └─────────┬──────────┘          └─────────┬───────────┘
                         │                                │
                 ┌───────▼───────┐                ┌───────▼───────┐
                 │  records-db    │                │   auth-db      │
                 │  (PostgreSQL)  │                │  (PostgreSQL)  │
                 └────────────────┘                └────────────────┘
```

## Запуск локально через Docker Compose

```bash
cp .env.example .env
docker compose up -d --build
```

```bash
curl -s -X POST http://localhost:8080/api/auth/register \
-H 'Content-Type: application/json' \
-d '{"username":"alice","password":"s3cret!"}'

TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
-H 'Content-Type: application/json' \
-d '{"username":"alice","password":"s3cret!"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')

curl -s -X POST http://localhost:8080/api/records \
-H "Authorization: Bearer $TOKEN" \
-H 'Content-Type: application/json' \
-d '{"content":"my first note"}'

curl -s http://localhost:8080/api/records -H "Authorization: Bearer $TOKEN"

## Установка API-шлюза

```bash
kubectl apply -k k8s/manifests
```

## Развертывание в Kubernetes (minikube) с помощью Skaffold

```bash
minikube start
skaffold dev        # skaffold run
```
```bash
curl -s -X POST http://localhost:8080/api/auth/register \
-H 'Content-Type: application/json' \
-d '{"username":"alice","password":"s3cret!"}'

TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
-H 'Content-Type: application/json' \
-d '{"username":"alice","password":"s3cret!"}' | python3 -c 'import sys,json;print(json.load(sys.stdin)["token"])')

curl -s -X POST http://localhost:8080/api/records \
-H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
-d '{"content":"hello from k8s"}'

curl -s http://localhost:8080/api/records -H "Authorization: Bearer $TOKEN"
```

```bash
skaffold delete
```

## Тесты в Postman

Файл [`postman/hw23.postman_collection.json`](postman/hw23.postman_collection.json)
(в связке с [`postman/hw23.postman_environment.json`](postman/hw23.postman_environment.json)).

```bash
npm install -g newman   # выполнить один раз
newman run postman/hw23.postman_collection.json -e postman/hw23.postman_environment.json
```