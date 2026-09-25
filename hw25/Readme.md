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
for img in hw25-billing-service hw25-notification-service hw25-order-service; do
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
skaffold delete            # or: kubectl delete -k k8s/manifests
```

## Логирование и трассировка

Каждый сервис логирует ключевые бизнес-события (создание пользователя, пополнение/снятие средств,
отправка email, этапы обработки заказа) и поддерживает распределенную трассировку с помощью
Micrometer Tracing (Brave) с экспортом данных в Zipkin. На уровне кода трассировка по умолчанию
**отключена**; она активируется переменной окружения `TRACING_ENABLED=true` —
эта переменная уже задана в `docker-compose.yml` и в ConfigMap каждого сервиса
(в директории `k8s/manifests`), поэтому трассировка работает «из коробки» при запуске
через Compose или Kubernetes.

```bash
# Docker Compose: интерфейс Zipkin
open http://localhost:9411

# Kubernetes
kubectl -n hw25 port-forward svc/zipkin 9411:9411
open http://localhost:9411
```

После оформления заказа в Zipkin можно найти трассировку, охватывающую все три сервиса
(order-service → billing-service → notification-service) и объединенную одним
идентификатором `traceId`. При включенной трассировке логи каждого сервиса
(например, `docker compose logs -f order-service`) также содержат `traceId` и `spanId`.

## Тесты Postman / Newman

Файл [`postman/hw25.postman_collection.json`](postman/hw25.postman_collection.json)
(совместно с [`postman/hw25.postman_environment.json`](postman/hw25.postman_environment.json),
где `{{baseUrl}}` по умолчанию имеет значение `http://arch.homework`) выполняет полный сценарий:
создание пользователя → пополнение счета →
оформление заказа при достаточном балансе → оформление заказа при недостаточном балансе.
Имя пользователя и email генерируются случайным образом при каждом запуске,
поэтому тесты можно запускать повторно на одной и той же базе данных.

```bash
npm install -g newman
newman run postman/hw25.postman_collection.json -e postman/hw25.postman_environment.json
```

Метод, URL и тело каждого запроса, а также полученный статус и тело ответа выводятся в консоль,
благодаря чему при запуске через Newman полные данные запроса и ответа отображаются непосредственно в выводе.

Для того чтобы адрес по умолчанию `{{baseUrl}}` перенаправить на другой адрес используем параметр `--env-var`:

```bash
newman run postman/hw25.postman_collection.json -e postman/hw25.postman_environment.json \
  --env-var "baseUrl=http://127.0.0.1:8090"
```