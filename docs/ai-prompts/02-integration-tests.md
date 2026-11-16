# b. 3 интеграционных теста data – domain – view

## Промт

```text
Используя общий контекст проекта, напиши ровно 3 интеграционных теста, которые проверяют
взаимодействие слоёв data → domain → view (ViewModel) в ветке mvvm.

Условия:
- тесты инструментальные (app/src/androidTest, AndroidJUnit4), запускаются на эмуляторе командой
  ./gradlew connectedDebugAndroidTest;
- используй настоящие компоненты: RickAndMortyApiFactory (Retrofit + kotlinx.serialization),
  Room in-memory (Room.inMemoryDatabaseBuilder), CatalogRepository, EpisodeNotesRepository и
  ViewModel из mvvm/presentation;
- подменяй только сетевой транспорт: создай в пакете testing класс FakeRickAndMortyBackend —
  OkHttp Interceptor, который по пути запроса отдаёт JSON в формате The Rick and Morty API
  (фикстуры в объекте Fixtures), отвечает 404 для неизвестных путей и умеет имитировать отсутствие
  сети флагом offline (IOException). MockWebServer не используй: на targetSdk 37 cleartext-трафик к
  localhost запрещён, а перехватчик сохраняет настоящий https BASE_URL;
- клиент передавай через параметр client фабрики RickAndMortyApiFactory.create, production-код не
  меняй;
- ожидание состояния ViewModel делай через StateFlow.first { условие } внутри withTimeout.

Сценарии:
1. CharactersViewModel получает первую страницу персонажей из API через CatalogRepository:
   в состоянии 2 персонажа в исходном порядке, totalCount = 826, totalPages = 42, canGoForward,
   нет ошибки и признака кеша; страница сохранена в Room (cachedCharacters).
2. Офлайн-сценарий: первый ViewModel наполняет кеш, затем сеть пропадает; новый
   CharactersViewModel показывает данные из Room, isFromCache = true и сообщение об ошибке.
3. EpisodeDetailsViewModel загружает эпизод «Pilot» (S01E01) и связанных персонажей batch-запросом
   character/1,2; затем save(watched = true, rating = 5, comment с пробелами) сохраняет заметку
   через EpisodeNotesRepository в Room, состояние получает userData с обрезанным комментарием и
   сообщение «Сохранено в Room».

Класс назови DataDomainViewIntegrationTest (пакет integration). Добавь androidTest-зависимости
(androidx.test.ext:junit, runner, core, espresso-core, kotlinx-coroutines-test) и
testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner".
```

## Результат

- `app/src/androidTest/java/com/sket/kursovyafreeapi/integration/DataDomainViewIntegrationTest.kt` — 3 теста;
- `app/src/androidTest/java/com/sket/kursovyafreeapi/testing/FakeRickAndMortyBackend.kt` — поддельный backend;
- `app/src/androidTest/java/com/sket/kursovyafreeapi/testing/Fixtures.kt` — JSON-фикстуры.

Production-код не изменялся. Все 3 теста проходят на эмуляторе.
