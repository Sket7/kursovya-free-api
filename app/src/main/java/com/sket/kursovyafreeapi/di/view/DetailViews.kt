package com.sket.kursovyafreeapi.di.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sket.kursovyafreeapi.di.presentation.CharacterDetailsViewModel
import com.sket.kursovyafreeapi.di.presentation.CommentsViewModel
import com.sket.kursovyafreeapi.di.presentation.EpisodeDetailsViewModel
import com.sket.kursovyafreeapi.di.presentation.LocationDetailsViewModel
import com.sket.kursovyafreeapi.di.data.MAX_COMMENT_LENGTH
import com.sket.kursovyafreeapi.ui.shared.PortalHero
import com.sket.kursovyafreeapi.ui.shared.PortalScreen
import com.sket.kursovyafreeapi.ui.shared.SectionTitle
import com.sket.kursovyafreeapi.ui.shared.StateMessage
import com.sket.kursovyafreeapi.ui.theme.RickAndMortyTheme

@Composable
fun EpisodeDetailsView(viewModel: EpisodeDetailsViewModel, onBack: () -> Unit, onCharacter: (Int) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var watched by remember { mutableStateOf(false) }; var rating by remember { mutableStateOf<Int?>(null) }; var comment by remember { mutableStateOf("") }
    LaunchedEffect(state.userData) { state.userData?.let { watched = it.isWatched; rating = it.rating; comment = it.comment } }
    PortalScreen("Эпизод", onBack) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            state.item?.let { episode -> item { PortalHero("${episode.code} · ${episode.airDate}", episode.name, "${episode.characterIds.size} персонажей появляются в этом эпизоде") } }
            item { NoteEditor(watched, { watched = it }, rating, { rating = it }, comment, { comment = it.take(MAX_COMMENT_LENGTH) }, state.isSaving, { viewModel.save(watched, rating, comment) }, state.message) }
            item { SectionTitle("Персонажи", "Нажмите на героя, чтобы открыть подробности") }
            items(state.related) { CharacterCard(it, onCharacter) }
            if (state.isLoading) item { StateMessage("Открываем эпизод…", "Загружаем детали") }
            if (state.isOffline) item { StateMessage("Офлайн-режим", "Показаны доступные данные из Room") }
        }
    }
}

@Composable
private fun NoteEditor(watched: Boolean, onWatched: (Boolean) -> Unit, rating: Int?, onRating: (Int) -> Unit, comment: String, onComment: (String) -> Unit, saving: Boolean, save: () -> Unit, message: String?) {
    ElevatedCard(Modifier.fillMaxWidth(), shape = RickAndMortyTheme.shapes.extraLarge) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionTitle("Моё впечатление", "Хранится только на этом устройстве")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text("Просмотрено", style = RickAndMortyTheme.typography.titleMedium); Text(if (watched) "Эпизод отмечен" else "Ещё не смотрел", style = RickAndMortyTheme.typography.bodySmall, color = RickAndMortyTheme.colors.onSurfaceVariant) }; Switch(watched, onWatched) }
            Text("Оценка", style = RickAndMortyTheme.typography.labelLarge)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) { (1..5).forEach { value -> FilterChip(rating == value, { onRating(value) }, label = { Text("★ $value") }) } }
            OutlinedTextField(comment, onComment, Modifier.fillMaxWidth(), label = { Text("Комментарий") }, supportingText = { Text("${comment.length}/${MAX_COMMENT_LENGTH}") }, minLines = 3, maxLines = 5, shape = RickAndMortyTheme.shapes.medium)
            Button(save, Modifier.fillMaxWidth(), enabled = !saving) { Text(if (saving) "Сохраняем…" else "Сохранить впечатление") }
            message?.let { Text(it, style = RickAndMortyTheme.typography.bodySmall, color = RickAndMortyTheme.colors.primary) }
        }
    }
}

@Composable
fun CharacterDetailsView(viewModel: CharacterDetailsViewModel, onBack: () -> Unit, onEpisode: (Int) -> Unit, onLocation: (Int) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PortalScreen("Персонаж", onBack) { padding -> LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        state.item?.let { value -> item { ElevatedCard(shape = RickAndMortyTheme.shapes.extraLarge) { AsyncImage(value.imageUrl, value.name, Modifier.fillMaxWidth(), contentScale = ContentScale.Crop); Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) { Text(value.name, style = RickAndMortyTheme.typography.headlineMedium); Text("${value.status} · ${value.species} · ${value.gender}", color = RickAndMortyTheme.colors.primary); Text("Происхождение · ${value.origin.name}"); value.origin.id?.let { OutlinedButton({ onLocation(it) }) { Text("Открыть происхождение") } }; Text("Последняя локация · ${value.location.name}"); value.location.id?.let { OutlinedButton({ onLocation(it) }) { Text("Открыть локацию") } } } } } }
        item { SectionTitle("Эпизоды", "Все появления персонажа") }; items(state.related) { EpisodeCard(it, onEpisode) }; if (state.isLoading) item { StateMessage("Ищем персонажа…", "Это займёт пару секунд") }
    } }
}

@Composable
fun LocationDetailsView(viewModel: LocationDetailsViewModel, onBack: () -> Unit, onCharacter: (Int) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PortalScreen("Локация", onBack) { padding -> LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        state.item?.let { value -> item { PortalHero(value.type, value.name, "Измерение: ${value.dimension} · жителей: ${value.residentIds.size}") } }; item { SectionTitle("Жители", "Последний раз их видели здесь") }; items(state.related) { CharacterCard(it, onCharacter) }; if (state.isLoading) item { StateMessage("Ищем локацию…", "Сверяем координаты мультивселенной") }
    } }
}

@Composable
fun PersonalCommentsView(viewModel: CommentsViewModel, onBack: () -> Unit, onEpisode: (Int) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PortalScreen("Мои комментарии", onBack) { padding -> LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionTitle("Личные впечатления", "Доступны без подключения к интернету") }; if (!state.isLoading && state.items.isEmpty()) item { StateMessage("Здесь пока пусто", "Оцените эпизод и добавьте комментарий — он появится здесь") }
        items(state.items, key = { it.userData.episodeId }) { value -> ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) { Text(value.episode?.name ?: "Эпизод #${value.userData.episodeId}", style = RickAndMortyTheme.typography.titleMedium); Text("${value.episode?.code.orEmpty()} · ★ ${value.userData.rating ?: "—"}/5", style = RickAndMortyTheme.typography.labelLarge, color = RickAndMortyTheme.colors.primary); Text(value.userData.comment, style = RickAndMortyTheme.typography.bodyMedium); Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Button({ onEpisode(value.userData.episodeId) }) { Text("Открыть") }; OutlinedButton({ viewModel.delete(value.userData.episodeId) }) { Text("Удалить") } } } } }
    } }
}

