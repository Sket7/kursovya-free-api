# Rick and Morty Architecture Samples

Одно Android-приложение на Kotlin и Jetpack Compose в нескольких архитектурных вариантах. Полное
техническое задание и правила веток находятся в `AGENTS.md`.

Текущая ветка `mvi` реализует однонаправленный поток `Intent → Store → Reducer → State → View`.
Repository отвечает за Retrofit, Room и кеш, а одноразовые результаты доставляются как Effect.

Общая дизайн-система использует Material 3, Russo One для выразительных заголовков и Manrope для
основного интерфейсного текста. Шрифты встроены в приложение по лицензии SIL Open Font License 1.1.

Архитектура подробно описана в [docs/MVI.md](docs/MVI.md).

```shell
./gradlew assembleDebug lintDebug
```
