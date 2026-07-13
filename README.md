# Rick and Morty Architecture Samples

Одно Android-приложение на Kotlin и Jetpack Compose в нескольких архитектурных вариантах. Полное
техническое задание и правила веток находятся в `AGENTS.md`.

Текущая ветка `di-koin` содержит полную реализацию приложения с Dependency Injection через Koin.
Koin создаёт Retrofit, Room, репозитории и AndroidX ViewModel, включая передачу runtime-параметров
для экранов деталей. Compose получает ViewModel непосредственно из контейнера и отображает единый
интерфейс из `ui.shared`.

Общая дизайн-система использует Material 3, Russo One для выразительных заголовков и Manrope для
основного интерфейсного текста. Шрифты встроены в приложение по лицензии SIL Open Font License 1.1.

Устройство DI-графа описано в [docs/DI_KOIN.md](docs/DI_KOIN.md).

```shell
./gradlew assembleDebug lintDebug
```
