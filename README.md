# Rick and Morty Architecture Samples

Одно Android-приложение на Kotlin и Jetpack Compose в нескольких архитектурных вариантах. Полное
техническое задание и правила веток находятся в `AGENTS.md`.

Текущая ветка `mvc` содержит полную реализацию варианта Model–View–Controller. Compose отвечает
только за View, обычные Kotlin Controller обрабатывают действия и публикуют состояние, а Model
координирует Retrofit, Room и кеш. `ViewModel` и DI-контейнер намеренно не используются.

Общая дизайн-система использует Material 3, Russo One для выразительных заголовков и Manrope для
основного интерфейсного текста. Шрифты встроены в приложение по лицензии SIL Open Font License 1.1.

Подробные границы слоёв, политика кеша и список сценариев описаны в [docs/MVC.md](docs/MVC.md).

Проверка ветки:

```shell
./gradlew assembleDebug lintDebug
```
