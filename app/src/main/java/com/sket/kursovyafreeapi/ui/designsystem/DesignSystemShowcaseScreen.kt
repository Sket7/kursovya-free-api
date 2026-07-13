package com.sket.kursovyafreeapi.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sket.kursovyafreeapi.ui.theme.RickAndMortyTheme
import kotlinx.coroutines.launch

@Composable
fun DesignSystemShowcaseScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dataFoundation = remember(context) {
        DataFoundationShowcase.create(context.applicationContext)
    }

    DisposableEffect(dataFoundation) {
        onDispose(dataFoundation::close)
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .safeDrawingPadding()
                .padding(RickAndMortyTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.large)
        ) {
            PortalHeader()
            TypographySample()
            StatusSample()
            ActionSample()
            DataFoundationSample(dataFoundation)
        }
    }
}

@Composable
private fun DataFoundationSample(dataFoundation: DataFoundationShowcase) {
    val scope = rememberCoroutineScope()
    var characterState by remember { mutableStateOf(CheckState()) }
    var episodeState by remember { mutableStateOf(CheckState()) }
    var locationState by remember { mutableStateOf(CheckState()) }
    var isEpisodeSaved by remember { mutableStateOf(false) }
    var isWatched by rememberSaveable { mutableStateOf(true) }
    var rating by rememberSaveable { mutableStateOf<Int?>(null) }
    var comment by rememberSaveable { mutableStateOf("") }
    var storageMessage by remember { mutableStateOf("Проверяем Room…") }
    var isStorageLoading by remember { mutableStateOf(true) }

    LaunchedEffect(dataFoundation) {
        runCatching { dataFoundation.readEpisodeNote() }
            .onSuccess { savedNote ->
                isEpisodeSaved = savedNote != null
                savedNote?.let { note ->
                    isWatched = note.isWatched
                    rating = note.rating
                    comment = note.comment
                }
                storageMessage = savedNote?.storageSummary() ?: "Записи в Room нет"
            }
            .onFailure { error -> storageMessage = error.displayMessage() }
        isStorageLoading = false
    }

    fun runCheck(
        update: (CheckState) -> Unit,
        request: suspend () -> DataCheckResult
    ) {
        scope.launch {
            update(CheckState(isLoading = true, message = "Выполняется запрос…"))
            runCatching { request() }
                .onSuccess { result ->
                    update(
                        CheckState(
                            message = if (result.isFullyCached) {
                                "Получено ${result.receivedCount}, записано и прочитано из Room ${result.cachedCount}"
                            } else {
                                "Получено ${result.receivedCount}, найдено в Room ${result.cachedCount}"
                            },
                            isSuccess = result.isFullyCached,
                            result = result
                        )
                    )
                }
                .onFailure { error -> update(CheckState(message = error.displayMessage())) }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.medium)) {
        Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.extraSmall)) {
            Text("Проверка data-основы", style = RickAndMortyTheme.typography.headlineSmall)
            Text(
                text = "Каждая кнопка выполняет REST-запрос, сразу делает upsert ответа в Room, а затем читает эти же записи из базы.",
                style = RickAndMortyTheme.typography.bodyMedium,
                color = RickAndMortyTheme.colors.onSurfaceVariant
            )
        }

        DataCheckCard(
            title = "Персонажи",
            state = characterState,
            onClick = {
                runCheck({ characterState = it }, dataFoundation::checkCharacters)
            }
        )
        DataCheckCard(
            title = "Эпизоды",
            state = episodeState,
            onClick = {
                runCheck({ episodeState = it }, dataFoundation::checkEpisodes)
            }
        )
        DataCheckCard(
            title = "Локации",
            state = locationState,
            onClick = {
                runCheck({ locationState = it }, dataFoundation::checkLocations)
            }
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = RickAndMortyTheme.colors.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(RickAndMortyTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
            ) {
                Text("Локальная заметка к эпизоду №1", style = RickAndMortyTheme.typography.titleMedium)
                Text(
                    text = "Поля сохраняются одной записью в таблице episode_user_data.",
                    style = RickAndMortyTheme.typography.bodySmall,
                    color = RickAndMortyTheme.colors.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
                ) {
                    if (isStorageLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    }
                    StatusChip(
                        text = storageMessage,
                        color = if (isEpisodeSaved) {
                            RickAndMortyTheme.extendedColors.watched
                        } else {
                            RickAndMortyTheme.extendedColors.unknown
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Эпизод просмотрен", style = RickAndMortyTheme.typography.labelLarge)
                        Text(
                            text = if (isWatched) "Да" else "Нет",
                            style = RickAndMortyTheme.typography.bodySmall,
                            color = RickAndMortyTheme.colors.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isWatched,
                        enabled = !isStorageLoading,
                        onCheckedChange = { isWatched = it }
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.extraSmall)) {
                    Text("Оценка", style = RickAndMortyTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.extraSmall)
                    ) {
                        (1..5).forEach { value ->
                            FilterChip(
                                modifier = Modifier.weight(1f),
                                selected = rating == value,
                                enabled = !isStorageLoading,
                                onClick = { rating = value },
                                label = { Text(value.toString()) }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = comment,
                    enabled = !isStorageLoading,
                    onValueChange = { value ->
                        comment = value.take(MAX_SHOWCASE_COMMENT_LENGTH)
                    },
                    label = { Text("Комментарий") },
                    placeholder = { Text("Например: отличный пилотный эпизод") },
                    supportingText = {
                        Text("${comment.length}/$MAX_SHOWCASE_COMMENT_LENGTH")
                    },
                    minLines = 3,
                    maxLines = 5
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = !isStorageLoading,
                        onClick = {
                            scope.launch {
                                isStorageLoading = true
                                runCatching {
                                    dataFoundation.saveEpisodeNote(
                                        ShowcaseEpisodeNote(
                                            isWatched = isWatched,
                                            rating = rating,
                                            comment = comment.trim()
                                        )
                                    )
                                }
                                    .onSuccess { savedNote ->
                                        isEpisodeSaved = true
                                        isWatched = savedNote.isWatched
                                        rating = savedNote.rating
                                        comment = savedNote.comment
                                        storageMessage = savedNote.storageSummary()
                                    }
                                    .onFailure { error -> storageMessage = error.displayMessage() }
                                isStorageLoading = false
                            }
                        }
                    ) {
                        Text("Сохранить")
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        enabled = !isStorageLoading && isEpisodeSaved,
                        onClick = {
                            scope.launch {
                                isStorageLoading = true
                                runCatching { dataFoundation.deleteEpisodeNote() }
                                    .onSuccess {
                                        isEpisodeSaved = dataFoundation.readEpisodeNote() != null
                                        storageMessage = "Удалено из Room: запись не найдена"
                                    }
                                    .onFailure { error -> storageMessage = error.displayMessage() }
                                isStorageLoading = false
                            }
                        }
                    ) {
                        Text("Удалить")
                    }
                }
            }
        }
    }
}

@Composable
private fun DataCheckCard(
    title: String,
    state: CheckState,
    onClick: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(RickAndMortyTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.medium)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.extraSmall)
                ) {
                    Text(title, style = RickAndMortyTheme.typography.titleMedium)
                    Text(
                        text = state.message,
                        style = RickAndMortyTheme.typography.bodySmall,
                        color = when (state.isSuccess) {
                            true -> RickAndMortyTheme.extendedColors.alive
                            false -> RickAndMortyTheme.colors.onSurfaceVariant
                        }
                    )
                }
                Button(enabled = !state.isLoading, onClick = onClick) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = RickAndMortyTheme.colors.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Проверить")
                    }
                }
            }

            state.result?.let { result ->
                DataCheckDetails(result)
            }
        }
    }
}

@Composable
private fun DataCheckDetails(result: DataCheckResult) {
    Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.extraSmall)) {
        Text(
            text = "1. ${result.endpoint}",
            style = RickAndMortyTheme.typography.labelLarge
        )
        DataPreview(title = "2. Ответ API (${result.receivedCount})", items = result.receivedPreview)
        DataPreview(
            title = "3. Upsert → чтение из Room (${result.cachedCount})",
            items = result.cachedPreview
        )
    }
}

@Composable
private fun DataPreview(title: String, items: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.extraSmall)) {
        Text(
            text = title,
            style = RickAndMortyTheme.typography.labelMedium,
            color = RickAndMortyTheme.colors.onSurfaceVariant
        )
        items.forEach { item ->
            Text(
                text = "• $item",
                style = RickAndMortyTheme.typography.bodySmall
            )
        }
    }
}

private data class CheckState(
    val isLoading: Boolean = false,
    val message: String = "Ещё не проверено",
    val isSuccess: Boolean = false,
    val result: DataCheckResult? = null
)

private const val MAX_SHOWCASE_COMMENT_LENGTH = 300

private fun ShowcaseEpisodeNote.storageSummary(): String {
    val watched = if (isWatched) "просмотрено" else "не просмотрено"
    val score = rating?.let { "оценка $it/5" } ?: "без оценки"
    val note = if (comment.isBlank()) "без комментария" else "комментарий сохранён"
    return "Room: $watched · $score · $note"
}

private fun Throwable.displayMessage(): String =
    localizedMessage?.take(120) ?: "Неизвестная ошибка"

@Composable
private fun PortalHeader() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = RickAndMortyTheme.colors.primaryContainer
        ),
        shape = RickAndMortyTheme.shapes.extraLarge
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            RickAndMortyTheme.colors.primaryContainer,
                            RickAndMortyTheme.colors.secondaryContainer
                        )
                    )
                )
                .padding(RickAndMortyTheme.spacing.large)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .height(88.dp)
                    .fillMaxWidth(0.28f)
                    .background(
                        color = RickAndMortyTheme.extendedColors.portalGlow.copy(alpha = 0.42f),
                        shape = CircleShape
                    )
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart),
                verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
            ) {
                Text(
                    text = "МУЛЬТИВСЕЛЕННАЯ",
                    style = RickAndMortyTheme.typography.labelLarge,
                    color = RickAndMortyTheme.colors.onPrimaryContainer
                )
                Text(
                    text = "Рик и Морти",
                    style = RickAndMortyTheme.typography.displaySmall,
                    color = RickAndMortyTheme.colors.onPrimaryContainer
                )
                Text(
                    text = "Эпизоды, персонажи и локации в одном портале.",
                    style = RickAndMortyTheme.typography.bodyLarge,
                    color = RickAndMortyTheme.colors.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun TypographySample() {
    Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)) {
        Text("Типографика", style = RickAndMortyTheme.typography.headlineSmall)
        Text("Заголовки — Russo One", style = RickAndMortyTheme.typography.titleLarge)
        Text(
            text = "Основной текст набран Manrope: он остаётся читаемым в карточках, фильтрах и длинных комментариях.",
            style = RickAndMortyTheme.typography.bodyLarge,
            color = RickAndMortyTheme.colors.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusSample() {
    Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)) {
        Text("Состояния", style = RickAndMortyTheme.typography.headlineSmall)
        Row(
            horizontalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
        ) {
            StatusChip("Жив", RickAndMortyTheme.extendedColors.alive)
            StatusChip("Мёртв", RickAndMortyTheme.extendedColors.dead)
            StatusChip("Неизвестно", RickAndMortyTheme.extendedColors.unknown)
        }
        AssistChip(
            onClick = {},
            label = { Text("★ 4 из 5") }
        )
    }
}

@Composable
private fun StatusChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.14f),
        contentColor = color,
        shape = RickAndMortyTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = RickAndMortyTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ActionSample() {
    Column(verticalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)) {
        Text("Действия", style = MaterialTheme.typography.headlineSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(RickAndMortyTheme.spacing.small)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {}
            ) {
                Text("Открыть портал")
            }
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {}
            ) {
                Text("Фильтры")
            }
        }
    }
}
