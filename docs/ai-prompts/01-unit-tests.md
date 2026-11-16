# a. 5 unit-тестов для domain-слоя (бизнес-логики)

## Промт

```text
Используя общий контекст проекта, напиши ровно 5 unit-тестов (5 методов @Test) для бизнес-логики
ветки mvvm. Тесты должны выполняться на JVM в app/src/test без эмулятора
(./gradlew testDebugUnitTest).

Покрой следующие бизнес-правила:
1. CharacterQuery.cacheKey(): фильтры нормализуются (trim) и URL-кодируются, одинаковые по смыслу
   запросы дают одинаковый ключ кеша; функция asApiFilter() не отправляет в API пустые фильтры.
2. CharacterQuery, EpisodeQuery, LocationQuery: номер страницы меньше 1 отклоняется
   IllegalArgumentException.
3. Доменная модель EpisodeUserData: оценка может быть только null или 1..5.
4. CatalogUiState: флаги canGoBack / canGoForward зависят от номера страницы, общего числа страниц и
   признаков isLoading / isRefreshing; isEmpty истинно только без загрузки, данных и ошибки.
5. EpisodeNotesRepository.save(): комментарий обрезается по краям и ограничивается
   MAX_COMMENT_LENGTH = 300 символов, время берётся из внедрённых часов now, некорректная оценка (6)
   отклоняется до обращения к базе. Room изолируй с помощью MockK (mockk<RickAndMortyDatabase>,
   relaxed-мок EpisodeUserDataDao, slot для захвата сохранённой сущности), корутины —
   kotlinx-coroutines-test (runTest).

Тестовые классы размести в тех же пакетах, что и тестируемый код (чтобы были доступны internal-
функции): CatalogQueryRulesTest, EpisodeUserDataRulesTest, CatalogUiStateTest,
EpisodeNotesRepositoryTest. Добавь testImplementation-зависимости junit, mockk,
kotlinx-coroutines-test в version catalog и app/build.gradle.kts.
```

## Результат

- `app/src/test/java/com/sket/kursovyafreeapi/mvvm/data/CatalogQueryRulesTest.kt` — 2 теста;
- `app/src/test/java/com/sket/kursovyafreeapi/model/EpisodeUserDataRulesTest.kt` — 1 тест;
- `app/src/test/java/com/sket/kursovyafreeapi/mvvm/presentation/CatalogUiStateTest.kt` — 1 тест;
- `app/src/test/java/com/sket/kursovyafreeapi/mvvm/data/EpisodeNotesRepositoryTest.kt` — 1 тест.

Production-код не изменялся. Все 5 тестов проходят (`./gradlew testDebugUnitTest`).
