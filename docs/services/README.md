## Сервисы

| Сервис | Стек | Владеет данными |
|---|---|---|
| [Сервис каталога франшиз и магазинов](franchise-store-directory-service.md) | Spring Boot | БД магазинов |
| [Сервис меню и каталога](menu-catalog-service.md) | Strapi (headless CMS) | БД каталога |
| [Сервис заказов](order-service.md) | Spring Boot | БД заказов |
| [Сервис ценообразования и акций](pricing-promotions-service.md) | Spring Boot | БД акций |
| [Сервис платежей](payment-service.md) | Spring Boot (в зоне PCI) | БД транзакций (Ledger) |
| [Сервис выполнения заказов (Fulfillment)](fulfillment-service.md) | Spring Boot | БД выполнения заказов |
| [Сервис диспетчеризации доставки](delivery-dispatch-service.md) | Spring Boot | БД диспетчеризации |
| [Сервис маршрутизации и карт](directions-mapping-service.md) | Spring Boot | Кэш маршрутов (Redis) |
| [Notification Service](notification-service.md) | Spring Boot (легковесный адаптер) | — (нет; история хранится в Novu) |
| [API Gateway / BFF](infrastructure.md#api-gateway--bff-traefik) | Traefik | — (конфигурация без сохранения состояния) |
| [Identity Provider](infrastructure.md#identity-provider-keycloak) | Keycloak | — (готовое решение) |
| [Notification Infrastructure](infrastructure.md#notification-infrastructure-novu) | Novu | — (готовое решение) |
| [Event Bus](infrastructure.md#event-bus-apache-kafka) | Apache Kafka | — (топики Kafka) |

> Аутентификация и авторизация реализованы в виде отдельного
> микросервиса — каждый сервис делегирует эти функции Keycloak (см.
> [infrastructure.md](infrastructure.md#identity-provider-keycloak)).
> Меню и каталог — также работают на базе Strapi, headless-CMS с открытым исходным кодом (см.
> [menu-catalog-service.md](menu-catalog-service.md), где указано,
> что генерируется автоматически, а что написано вручную). Доставка
> уведомлений (провайдеры, шаблоны, повторные попытки) делегирована
> сервису Novu — теперь Notification Service представляет собой лишь
> легковесный адаптер, преобразующий события в рабочие процессы (см.
> [notification-service.md](notification-service.md)).