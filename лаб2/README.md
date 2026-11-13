# ЛР2 — клиент гостиницы

Учебный вариант: **7 Java-файлов — 6 классов и 1 интерфейс**.

| Тип | Назначение |
|---|---|
| `Client` | Клиент с ID; сравнение по паспорту, валидация из ЛР1 |
| `ClientShort` | ID, фамилия с инициалами и телефон |
| `ClientRepository` | Общий интерфейс CRUD, страниц, сортировки и подсчёта |
| `ClientRepJson` | JSON и общая файловая логика |
| `ClientRepYaml` | Делегирует файловые операции с настройкой YAML |
| `ClientRepDb` | PostgreSQL через JDBC; Singleton |
| `ClientDbAdapter` | Адаптирует SQL-операции к общему интерфейсу |

Конкретные классы друг от друга не наследуются. Обязательные пункты 1–6 и 9 сохранены; необязательные декораторы 7* и 8** убраны по просьбе сократить работу.

`getKNShortList(k, n)` — страница с номером `k`, по `n` элементов, начиная с 1. `(2, 20)` — элементы 21–40. `getById` возвращает `Optional`, замена и удаление — `boolean`. Файловые изменения сохраняются через `writeAll()`; `readAll()` перечитывает файл. JSON/YAML хранят массив `clients` и счётчик `nextId`, чтобы не использовать удалённые ID повторно. Паспорта уникальны. Сортировка — по фамилии.

## Запуск

JDK 17+, Maven. Из папки `лаб2`:

```sh
mvn verify
mvn exec:java
```

Первая команда компилирует код и запускает Checkstyle. Вторая запускает небольшой JSON-пример; отдельного класса `Main` нет. Образцы файлов находятся в `data`.

Для БД нужен обычный PostgreSQL с базой `hotel_lab2`. Параметры: `LAB2_DB_URL` (по умолчанию `jdbc:postgresql://localhost:5432/hotel_lab2`), `LAB2_DB_USER` (по умолчанию `postgres`), `LAB2_DB_PASSWORD` (по умолчанию пустой).

```java
ClientRepDb.getInstance().initializeSchema();
ClientRepository database = new ClientDbAdapter();
```

`initializeSchema()` создаёт таблицу клиентов. SQL находится в `ClientRepDb`, используются `PreparedStatement` и `ResultSet`, без ORM и SQLite. Каждая операция закрывает своё JDBC-соединение.

Диаграмма: [docs/class-diagram.svg](docs/class-diagram.svg). Исходник: `docs/class-diagram.puml`.
