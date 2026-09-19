# Архитектура

Сервис `billing-service` отвечает как за пользователей, так и за их счета:
при создании пользователя в рамках той же транзакции создается и счет
с нулевым балансом. Все три сервиса работают за шлюзом Traefik, который
выполняет маршрутизацию на основе префикса пути.

![topology](docs/diagrams/00-architecture.svg)

- `order-service` — `POST /api/orders` управляет всем процессом обработки заказа.
- `billing-service` — `POST /api/users` создает пользователя и счет;
  `/api/accounts/{userId}/deposit|withdraw` выполняют операции с деньгами.
- `notification-service` — `POST /api/notifications/email` сохраняет email-сообщение; `GET /api/notifications?recipient=`
  позволяет получить сохраненные сообщения.