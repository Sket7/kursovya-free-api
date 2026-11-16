# Уточняющие промты по итогам прогона

## Итерация 1 — предупреждения компилятора Compose UI Test

Первый прогон `./gradlew connectedDebugAndroidTest` на эмуляторе Medium_Phone_API_36.0 завершился
успешно (5 из 5), но компилятор выдал предупреждения об устаревших `createComposeRule` и
`createAndroidComposeRule`.

```text
Прогон connectedDebugAndroidTest зелёный (5/5), но Kotlin выдаёт предупреждения:
'createComposeRule(...)' is deprecated. Use androidx.compose.ui.test.junit4.v2.createComposeRule
instead; то же для createAndroidComposeRule. Переведи оба UI-теста на API junit4.v2. Учти, что v2
использует StandardTestDispatcher: убедись, что все ожидания идут через waitUntil / runOnIdle, а не
через немедленное выполнение. Логику и проверки тестов не меняй.
```

Результат: импорты заменены на `androidx.compose.ui.test.junit4.v2.*`, предупреждения исчезли,
повторный прогон — 5 из 5 успешно.

## Итерация 2 — проверка unit-тестов

```text
Запусти ./gradlew testDebugUnitTest и убедись, что выполняется ровно 5 тестов и мок
RickAndMortyDatabase создаётся на JVM без Robolectric. Если MockK не сможет замокать RoomDatabase,
предложи вынести DAO в параметр конструктора как тестовый шов.
```

Результат: 5 из 5 успешно, тестовый шов не понадобился, production-код не изменялся.
