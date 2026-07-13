# MVVM-реализация

Ветка `mvvm` реализует то же приложение в варианте Model–View–ViewModel.

## Границы

- `mvvm/data` — Repository координирует общие Retrofit API, Room DAO и кеш.
- `mvvm/presentation` — AndroidX `ViewModel` хранит `StateFlow` состояния экрана, принимает события и
  запускает операции в `viewModelScope`.
- `mvvm/view` — тонкий Compose binding получает ViewModel, lifecycle-aware наблюдает состояние и
  передаёт его в общие stateless-компоненты `ui.shared`.
- `mvvm/navigation` — типобезопасные маршруты Navigation 3.

Зависимости создаются вручную в composition root. Koin не используется: он является предметом
отдельной ветки `di-koin`.
