# Clean Architecture

Направление зависимостей: `presentation → domain ← data`.

- `domain` содержит repository interfaces, query/value types и отдельные Use Cases;
- `data` реализует domain-контракты через общие Retrofit и Room;
- `presentation` зависит только от domain Use Cases и публикует StateFlow;
- `view` использует общий `ui.shared`, одинаковый для всех веток.

Domain не импортирует Android, Retrofit, Room, DTO или Entity. Koin не используется.

## Поток данных

View отправляет событие во ViewModel, ViewModel вызывает отдельный Use Case, а тот работает только с
domain repository interface. Реализация интерфейса находится в `data` и преобразует Retrofit/Room в
общие модели. Composition root — единственное место, где data implementation связывается с domain.

Используется cache-first/stale-while-revalidate, batch-загрузка связей, Coil image cache и отдельное
хранение оценок/комментариев. Реализованы каталоги, детали, серверные фильтры, пагинация, офлайн-кеш,
оценки и экран личных комментариев. Navigation 3 предоставляет отдельный ViewModelStore каждой entry.
