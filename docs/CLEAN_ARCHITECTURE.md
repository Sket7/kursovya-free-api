# Clean Architecture

Направление зависимостей: `presentation → domain ← data`.

- `domain` содержит repository interfaces, query/value types и отдельные Use Cases;
- `data` реализует domain-контракты через общие Retrofit и Room;
- `presentation` зависит только от domain Use Cases и публикует StateFlow;
- `view` использует общий `ui.shared`, одинаковый для всех веток.

Domain не импортирует Android, Retrofit, Room, DTO или Entity. Koin не используется.
