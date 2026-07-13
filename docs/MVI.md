# MVI-реализация

Ветка `mvi` реализует приложение с однонаправленным потоком данных:

`View → Intent → Store → Reducer → immutable State → View`.

- `mvi/data` — Repository для Retrofit, Room и кеша;
- `mvi/presentation` — Store принимает только типизированные Intent и изменяет состояние через
  reducer; одноразовые события публикуются как Effect;
- `mvi/view` — Compose наблюдает State и отправляет Intent, не вызывает Repository напрямую;
- `mvi/navigation` — Navigation 3 и типобезопасные маршруты.

Внешний вид полностью переиспользует `ui.shared`. Koin не используется.

## Reducer и эффекты

Store не предоставляет View отдельные команды загрузки или сохранения. Единственная точка входа —
`accept(Intent)`. Асинхронный результат превращается во внутреннюю Mutation, а чистый reducer
создаёт новый State. Результат сохранения заметки не хранится в постоянном State и доставляется как
одноразовый Effect.

Каждая Navigation 3 entry имеет собственный ViewModelStore. State собирается lifecycle-aware.
Repository использует cache-first/stale-while-revalidate, batch endpoints и отдельную Room-таблицу
для пользовательских данных. Изображения кешируются Coil.

Реализованы все общие сценарии: каталоги, детали, фильтры, пагинация, связанные ресурсы, оценки,
комментарии, офлайн-кеш и экран личных комментариев.
