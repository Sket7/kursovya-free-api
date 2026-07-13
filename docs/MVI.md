# MVI-реализация

Ветка `mvi` реализует приложение с однонаправленным потоком данных:

`View → Intent → Store → Reducer → immutable State → View`.

- `mvi/data` — Repository для Retrofit, Room и кеша;
- `mvi/presentation` — Store принимает только типизированные Intent и изменяет состояние через
  reducer; одноразовые события публикуются как Effect;
- `mvi/view` — Compose наблюдает State и отправляет Intent, не вызывает Repository напрямую;
- `mvi/navigation` — Navigation 3 и типобезопасные маршруты.

Внешний вид полностью переиспользует `ui.shared`. Koin не используется.
