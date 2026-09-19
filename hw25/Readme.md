# hw25 — сервисы заказов, биллинга и уведомлений

Три сервиса (каждый — в отдельном Docker-контейнере и с собственной базой данных Postgres),
работающие за API-шлюзом Traefik, который выполняет маршрутизацию по префиксу пути:

- **order-service** — `POST /api/orders`: списывает средства через `billing-service`,
  отправляет результат по электронной почте через `notification-service`, затем сохраняет заказ.
- **billing-service** — управляет пользователями и счетами; при создании пользователя
  в той же транзакции создается счет с нулевым балансом; поддерживает операции пополнения и списания.
- **notification-service** — сохраняет «отправленные» письма (фактической отправки
  не происходит) и предоставляет возможность поиска писем по получателю.

Полное описание [`ARCHITECTURE.md`](ARCHITECTURE.md) схема архитектуры,
а также IDL (OpenAPI + AsyncAPI) для каждого сервиса.

## Запуск локально с помощью Docker Compose

```bash
cp .env.example .env
echo '127.0.0.1 arch.homework' | sudo tee -a /etc/hosts   # выполнить один раз, если записи еще нет
docker compose up -d --build
```

Traefik слушает порт 80 на хосте, поэтому адрес `{{baseUrl}}` = `http://arch.homework`.

```bash
curl -s -X POST http://arch.homework/api/users \
-H 'Content-Type: application/json' \
-d '{"username":"alice","email":"alice@example.com"}'

curl -s -X POST http://arch.homework/api/accounts/1/deposit \
-H 'Content-Type: application/json' -d '{"amount":1000}'

curl -s -X POST http://arch.homework/api/orders \
-H 'Content-Type: application/json' -d '{"userId":1,"price":300}'

curl -s http://arch.homework/api/accounts/1
curl -s "http://arch.homework/api/notifications?recipient=alice@example.com"
```

```bash
docker compose down
```

## Установка в Kubernetes (minikube), пространство имен `hw25`

```bash
minikube start
eval $(minikube -p <profile> docker-env) 
docker compose build
for img in hw25-billing-service hw25-notification-service hw25-order-service;
``` do
minikube image load "$img:latest"
done

kubectl apply -k k8s/manifests
kubectl -n hw25 get pods -w
```

Или в один шаг с помощью Skaffold :

```bash
minikube start
skaffold dev        # skaffold run
```

```bash
kubectl -n hw25 port-forward svc/traefik 80:80
```

```bash
curl -s -X POST http://arch.homework/api/orders \
-H 'Content-Type: application/json' -d '{"userId":1,"price":300}'
```

```bash
skaffold delete            # или: kubectl delete -k k8s/manifests
```

```bash
npm install -g newman
newman run postman/hw25.postman_collection.json -e postman/hw25.postman_environment.json
```

```bash
newman run postman/hw25.postman_collection.json -e postman/hw25.postman_environment.json \
--env-var "baseUrl=http://127.0.0.1:8090"
```