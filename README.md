# Rick and Morty Architecture Samples

Одно Android-приложение на Kotlin и Jetpack Compose в нескольких архитектурных вариантах. Полное
техническое задание и правила веток находятся в `AGENTS.md`.

Текущая ветка `clean-architecture` разделена на presentation, domain и data. Бизнес-сценарии
оформлены отдельными Use Cases, а зависимости направлены внутрь к domain repository interfaces.

Общая дизайн-система использует Material 3, Russo One для выразительных заголовков и Manrope для
основного интерфейсного текста. Шрифты встроены в приложение по лицензии SIL Open Font License 1.1.

Подробности находятся в [docs/CLEAN_ARCHITECTURE.md](docs/CLEAN_ARCHITECTURE.md).

```shell
./gradlew assembleDebug lintDebug
```
