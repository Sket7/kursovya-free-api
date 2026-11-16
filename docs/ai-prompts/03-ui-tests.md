# c. 2 UI-теста

## Промт

```text
Используя общий контекст проекта и уже созданные FakeRickAndMortyBackend и Fixtures, напиши ровно
2 UI-теста на Jetpack Compose UI Test (androidx.compose.ui:ui-test-junit4, версия из Compose BOM)
в app/src/androidTest, пакет ui.

1. HomeNavigationUiTest — на реальной MainActivity (createAndroidComposeRule<MainActivity>):
   главный экран показывает заголовок «Мультивселенная ждёт» и карточки разделов «Эпизоды»,
   «Персонажи», «Локации»; нажатие на «Персонажи» открывает каталог персонажей — проверь
   появление поля «Поиск по имени» и фильтра «Живы» (они видны независимо от состояния сети).
   Для ожидания используй composeRule.waitUntil.
2. CharactersScreenUiTest — экран CharactersView в RickAndMortyAppTheme через createComposeRule,
   ViewModel построен на CatalogRepository с FakeRickAndMortyBackend и Room in-memory, чтобы данные
   были детерминированными: отображаются «826 героев», карточки «Rick Sanchez» и «Morty Smith»,
   индикатор «1 из 42», кнопка «← Назад» неактивна, «Далее →» активна; нажатие на карточку
   Rick Sanchez вызывает onOpen с id = 1.

Добавь debugImplementation(ui-test-manifest), чтобы createComposeRule мог запустить пустую
ComponentActivity. Не используй Thread.sleep.
```

## Результат

- `app/src/androidTest/java/com/sket/kursovyafreeapi/ui/HomeNavigationUiTest.kt` — 1 тест;
- `app/src/androidTest/java/com/sket/kursovyafreeapi/ui/CharactersScreenUiTest.kt` — 1 тест.

Production-код не изменялся. Оба теста проходят на эмуляторе emulator-5554.
