# Rick and Morty Architecture Samples

Одно Android-приложение на Kotlin и Jetpack Compose в нескольких архитектурных вариантах. Полное
техническое задание и правила веток находятся в `AGENTS.md`.

Текущая ветка `mvvm` содержит полную реализацию Model–View–ViewModel. Repository координирует
Retrofit, Room и кеш, AndroidX ViewModel хранит `StateFlow` и выполняет действия в `viewModelScope`,
а Compose lifecycle-aware отображает состояние через общий интерфейс `ui.shared`.

Общая дизайн-система использует Material 3, Russo One для выразительных заголовков и Manrope для
основного интерфейсного текста. Шрифты встроены в приложение по лицензии SIL Open Font License 1.1.

Архитектурные решения и политика кеша описаны в [docs/MVVM.md](docs/MVVM.md).

```shell
./gradlew assembleDebug lintDebug
```
