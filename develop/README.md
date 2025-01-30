# Развёртывание проекта

## Описание проекта
Проект представляет собой телеграм-бота для автоматизации процесса сбора заявок на обучение. Основные компоненты:
1. **dbService** — сервис работы с базой данных PostgreSQL.
2. **apiService** — API для взаимодействия с Telegram и бизнес-логикой.
3. **common-lib** — общая библиотека, содержащая переиспользуемые модули.

---

## Требования для запуска
- **Java**: версия 21 и выше.
- **Maven**: версия 3.8 и выше.
- **Docker**: версия 20.10 и выше.
- **PostgreSQL**: сервер версии 13 и выше (если используется вне Docker).
- **Git**: для клонирования репозитория.

---

## Клонирование репозитория
```bash
git clone -b main https://git.codenrock.com/neo-hack-2025-1364/cnrprod1737640822-team-83234/razrabotka-telegram-bota-dlya-avtomatizacii-processa-sbora-zayavok-na-obuchenie-6462.git
cd razrabotka-telegram-bota-dlya-avtomatizacii-processa-sbora-zayavok-na-obuchenie-6462
```

## Развёртывание с помощью Docker Compose
Выполните команду
```bash
docker-compose up --build
```
# После этого сервисы будут доспупны :
    dbService: http://localhost:8081
    apiService: http://localhost:8082

# А так же можно обращаться к боту через ссылку в .env файле TELEGRAM_BOT_LINK

# Один user одна заявка , если при тестировании нужно  обращаться несколько раз нужно выполнить DELETE FROM users WHERE chat_id =12345678; для своего chatId 