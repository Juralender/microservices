# hw23 — микросервисы с Keycloak в роли единственного identity-provider'а

Регистрация, логин, хранение профиля и валидация токенов полностью делегированы **Keycloak**. 
Единственный написанный Spring-сервис — `records-service` — сам проверяет
JWT, выданный Keycloak (через `spring-boot-starter-oauth2-resource-server`),
и не полагается на заголовки от шлюза.

```
                          ┌─────────────────────────┐
   client  ────────────▶  │        Traefik            │   (api-gateway, чистая маршрутизация)
                          │   entryPoint :80          │
                          └────────────┬──────────────┘
                                       │
   /realms/**, /admin/**              │
   /resources/**       ──▶ Keycloak (регистрация, логин, Account API, JWKS)
                                       │
   /api/records/**     ──▶ records-service (сам валидирует JWT по JWKS Keycloak)
                                       │
                          ┌────────────┴──────────────┐
                          ▼                            ▼
               ┌──────────────────┐          ┌───────────────────┐
               │     keycloak      │          │  records-service   │
               │  (IdP, реалм hw23)│          │ (create + read-own)│
               └─────────┬──────────┘          └─────────┬───────────┘
                         │                                │
                 ┌───────▼───────┐                ┌───────▼───────┐
                 │  keycloak-db   │                │  records-db    │
                 │  (PostgreSQL)  │                │  (PostgreSQL)  │
                 └────────────────┘                └────────────────┘
```

## Запуск локально через Docker Compose

```bash
cp .env.example .env
docker compose up -d --build
```

```bash
SVC_TOKEN=$(curl -s -X POST http://localhost:8080/realms/hw23/protocol/openid-connect/token \
  -d grant_type=client_credentials -d client_id=hw23-service \
  -d client_secret=dev-only-service-secret-change-me \
  | python3 -c 'import sys,json;print(json.load(sys.stdin)["access_token"])')

curl -s -X POST http://localhost:8080/admin/realms/hw23/users \
  -H "Authorization: Bearer $SVC_TOKEN" -H 'Content-Type: application/json' \
  -d '{"email":"alice@example.com","enabled":true,
       "credentials":[{"type":"password","value":"s3cret!","temporary":false}]}'

TOKEN=$(curl -s -X POST http://localhost:8080/realms/hw23/protocol/openid-connect/token \
  -d grant_type=password -d client_id=hw23-public -d username=alice@example.com -d password=s3cret! \
  | python3 -c 'import sys,json;print(json.load(sys.stdin)["access_token"])')

curl -s -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"content":"my first note"}'

curl -s http://localhost:8080/api/records -H "Authorization: Bearer $TOKEN"

# профиль: свой — читается/редактируется, чужого адреса просто не существует
curl -s http://localhost:8080/realms/hw23/account -H "Authorization: Bearer $TOKEN"
```

## Установка API-шлюза

```bash
kubectl apply -k k8s/manifests
```

(`skaffold run`/`skaffold dev` ниже делает то же самое как часть пайплайна.)

## Развертывание в Kubernetes (minikube) с помощью Skaffold

```bash
minikube start
skaffold dev        # или: skaffold run
```

Skaffold собирает образ `records-service` (единственный, который пишем
сами), применяет Kustomize-базу из `k8s/manifests` (namespace `hw23`,
Keycloak + его Postgres, `records-service` + его Postgres, Traefik) и
пробрасывает шлюз на `localhost:8080`.

```bash
skaffold delete
```

## Тесты в Postman

```bash
npm install -g newman   # один раз
newman run postman/hw23.postman_collection.json -e postman/hw23.postman_environment.json
```