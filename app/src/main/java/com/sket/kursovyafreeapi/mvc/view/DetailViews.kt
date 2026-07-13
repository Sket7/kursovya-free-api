package com.sket.kursovyafreeapi.mvc.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sket.kursovyafreeapi.mvc.MvcGraph
import com.sket.kursovyafreeapi.mvc.controller.CharacterDetailsController
import com.sket.kursovyafreeapi.mvc.controller.EpisodeDetailsController
import com.sket.kursovyafreeapi.mvc.controller.LocationDetailsController
import com.sket.kursovyafreeapi.mvc.controller.PersonalCommentsController
import com.sket.kursovyafreeapi.mvc.model.CatalogModel
import com.sket.kursovyafreeapi.mvc.model.EpisodeNotesModel
import com.sket.kursovyafreeapi.ui.theme.RickAndMortyTheme

@Composable
fun EpisodeDetailsView(id: Int, graph: MvcGraph, onBack: () -> Unit, onCharacter: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    val controller = remember(id, graph, scope) { EpisodeDetailsController(id, graph.catalog, graph.episodeNotes, scope) }
    val state by controller.state.collectAsState()
    var watched by remember { mutableStateOf(false) }
    var rating by remember { mutableStateOf<Int?>(null) }
    var comment by remember { mutableStateOf("") }
    LaunchedEffect(controller) { controller.start() }
    LaunchedEffect(state.userData) { state.userData?.let { watched = it.isWatched; rating = it.rating; comment = it.comment } }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { BackHeader("Эпизод", onBack) }
        state.item?.let { episode -> item { Text(episode.name, style = RickAndMortyTheme.typography.headlineMedium); Text("${episode.code} · ${episode.airDate}") } }
        item {
            Card { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Моя заметка", style = RickAndMortyTheme.typography.titleLarge)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Просмотрено"); Switch(watched, { watched = it }) }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { (1..5).forEach { value -> FilterChip(rating == value, { rating = value }, label = { Text(value.toString()) }) } }
                OutlinedTextField(comment, { comment = it.take(EpisodeNotesModel.MAX_COMMENT_LENGTH) }, Modifier.fillMaxWidth(), label = { Text("Комментарий") }, minLines = 3)
                Button({ controller.save(watched, rating, comment) }, enabled = !state.isSaving) { Text(if (state.isSaving) "Сохранение…" else "Сохранить в Room") }
                state.message?.let { Text(it) }
            } }
        }
        item { Text("Персонажи", style = RickAndMortyTheme.typography.titleLarge) }
        items(state.related) { CharacterCard(it, onCharacter) }
        if (state.isLoading) item { Text("Загрузка…") }
        if (state.isOffline) item { Text("Показаны доступные данные из кеша") }
    }
}

@Composable
fun CharacterDetailsView(
    id: Int,
    model: CatalogModel,
    onBack: () -> Unit,
    onEpisode: (Int) -> Unit,
    onLocation: (Int) -> Unit
) {
    val scope = rememberCoroutineScope(); val controller = remember(id, model, scope) { CharacterDetailsController(id, model, scope) }; val state by controller.state.collectAsState()
    LaunchedEffect(controller) { controller.start() }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { BackHeader("Персонаж", onBack) }
        state.item?.let { value -> item { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { AsyncImage(value.imageUrl, value.name, Modifier.fillMaxWidth()); Text(value.name, style = RickAndMortyTheme.typography.headlineMedium); Text("${value.status} · ${value.species} · ${value.gender}"); Text("Происхождение: ${value.origin.name}"); value.origin.id?.let { OutlinedButton({ onLocation(it) }) { Text("Открыть происхождение") } }; Text("Локация: ${value.location.name}"); value.location.id?.let { OutlinedButton({ onLocation(it) }) { Text("Открыть локацию") } } } } }
        item { Text("Эпизоды", style = RickAndMortyTheme.typography.titleLarge) }
        items(state.related) { EpisodeCard(it, onEpisode) }
        if (state.isLoading) item { Text("Загрузка…") }
    }
}

@Composable
fun LocationDetailsView(id: Int, model: CatalogModel, onBack: () -> Unit, onCharacter: (Int) -> Unit) {
    val scope = rememberCoroutineScope(); val controller = remember(id, model, scope) { LocationDetailsController(id, model, scope) }; val state by controller.state.collectAsState()
    LaunchedEffect(controller) { controller.start() }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { BackHeader("Локация", onBack) }
        state.item?.let { value -> item { Text(value.name, style = RickAndMortyTheme.typography.headlineMedium); Text("${value.type} · ${value.dimension}") } }
        item { Text("Жители", style = RickAndMortyTheme.typography.titleLarge) }
        items(state.related) { CharacterCard(it, onCharacter) }
        if (state.isLoading) item { Text("Загрузка…") }
    }
}

@Composable
fun PersonalCommentsView(notes: EpisodeNotesModel, onBack: () -> Unit, onEpisode: (Int) -> Unit) {
    val scope = rememberCoroutineScope(); val controller = remember(notes, scope) { PersonalCommentsController(notes, scope) }; val state by controller.state.collectAsState()
    LaunchedEffect(controller) { controller.start() }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { BackHeader("Мои комментарии", onBack) }
        if (!state.isLoading && state.items.isEmpty()) item { Text("Вы ещё не оставили комментариев") }
        items(state.items, key = { it.userData.episodeId }) { value ->
            Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(value.episode?.name ?: "Эпизод #${value.userData.episodeId}", style = RickAndMortyTheme.typography.titleMedium)
                Text(value.episode?.code.orEmpty()); Text(value.userData.comment); Text("Оценка: ${value.userData.rating ?: "—"}/5 · ${if (value.userData.isWatched) "просмотрено" else "не просмотрено"}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { Button({ onEpisode(value.userData.episodeId) }) { Text("Открыть") }; OutlinedButton({ controller.deleteComment(value.userData.episodeId) }) { Text("Удалить комментарий") } }
            } }
        }
    }
}

@Composable private fun BackHeader(title: String, onBack: () -> Unit) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { OutlinedButton(onBack) { Text("Назад") }; Text(title, style = RickAndMortyTheme.typography.titleLarge) } }
