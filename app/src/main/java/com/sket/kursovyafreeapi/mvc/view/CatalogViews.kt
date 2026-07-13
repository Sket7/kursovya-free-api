package com.sket.kursovyafreeapi.mvc.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.mvc.controller.CatalogScreenState
import com.sket.kursovyafreeapi.mvc.controller.PagedCatalogController
import com.sket.kursovyafreeapi.mvc.model.CatalogModel
import com.sket.kursovyafreeapi.mvc.model.CharacterQuery
import com.sket.kursovyafreeapi.mvc.model.EpisodeQuery
import com.sket.kursovyafreeapi.mvc.model.LocationQuery
import com.sket.kursovyafreeapi.mvc.navigation.MvcRoute
import com.sket.kursovyafreeapi.ui.theme.RickAndMortyTheme

@Composable
fun HomeView(onNavigate: (MvcRoute) -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("ДОБРО ПОЖАЛОВАТЬ В МУЛЬТИВСЕЛЕННУЮ", style = RickAndMortyTheme.typography.headlineLarge)
                Text(
                    "«Рик и Морти» — история о гениальном учёном Рике Санчезе и его внуке Морти, которые путешествуют по измерениям, встречают странных существ и постоянно спасают реальность от последствий собственных приключений.",
                    style = RickAndMortyTheme.typography.bodyLarge
                )
            }
            items(
                listOf(
                    "Эпизоды" to MvcRoute.Episodes,
                    "Персонажи" to MvcRoute.Characters,
                    "Локации" to MvcRoute.Locations,
                    "Мои комментарии" to MvcRoute.PersonalComments
                )
            ) { (title, route) ->
                Card(Modifier.fillMaxWidth().clickable { onNavigate(route) }) {
                    Text(title, Modifier.padding(24.dp), style = RickAndMortyTheme.typography.titleLarge)
                }
            }
        }
    }
}

@Composable
fun EpisodesView(model: CatalogModel, onBack: () -> Unit, onOpen: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    val controller = remember(model, scope) {
        PagedCatalogController(EpisodeQuery(), scope, EpisodeQuery::page, { q, p -> q.copy(page = p) }, model::cachedEpisodes, model::refreshEpisodes)
    }
    val state by controller.state.collectAsState()
    LaunchedEffect(controller) { controller.start() }
    CatalogFrame("Эпизоды", state, onBack, controller::retry, controller::previousPage, controller::nextPage, filters = {
        SearchField("Название", state.query.name) { controller.updateQuery(state.query.copy(name = it)) }
        SearchField("Код, например S01E01", state.query.code) { controller.updateQuery(state.query.copy(code = it)) }
    }) { episode -> EpisodeCard(episode, onOpen) }
}

@Composable
fun CharactersView(model: CatalogModel, onBack: () -> Unit, onOpen: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    val controller = remember(model, scope) {
        PagedCatalogController(CharacterQuery(), scope, CharacterQuery::page, { q, p -> q.copy(page = p) }, model::cachedCharacters, model::refreshCharacters)
    }
    val state by controller.state.collectAsState()
    LaunchedEffect(controller) { controller.start() }
    CatalogFrame("Персонажи", state, onBack, controller::retry, controller::previousPage, controller::nextPage, filters = {
        SearchField("Имя", state.query.name) { controller.updateQuery(state.query.copy(name = it)) }
        SearchField("Вид", state.query.species) { controller.updateQuery(state.query.copy(species = it)) }
        SearchField("Тип", state.query.type) { controller.updateQuery(state.query.copy(type = it)) }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(null to "Все", "alive" to "Жив", "dead" to "Мёртв", "unknown" to "Неизвестно").forEach { (value, label) ->
                FilterChip(selected = state.query.status == value, onClick = { controller.updateQuery(state.query.copy(status = value), false) }, label = { Text(label) })
            }
        }
        SearchField("Пол: female, male, genderless, unknown", state.query.gender.orEmpty()) { controller.updateQuery(state.query.copy(gender = it)) }
    }) { character -> CharacterCard(character, onOpen) }
}

@Composable
fun LocationsView(model: CatalogModel, onBack: () -> Unit, onOpen: (Int) -> Unit) {
    val scope = rememberCoroutineScope()
    val controller = remember(model, scope) {
        PagedCatalogController(LocationQuery(), scope, LocationQuery::page, { q, p -> q.copy(page = p) }, model::cachedLocations, model::refreshLocations)
    }
    val state by controller.state.collectAsState()
    LaunchedEffect(controller) { controller.start() }
    CatalogFrame("Локации", state, onBack, controller::retry, controller::previousPage, controller::nextPage, filters = {
        SearchField("Название", state.query.name) { controller.updateQuery(state.query.copy(name = it)) }
        SearchField("Тип", state.query.type) { controller.updateQuery(state.query.copy(type = it)) }
        SearchField("Измерение", state.query.dimension) { controller.updateQuery(state.query.copy(dimension = it)) }
    }) { location -> LocationCard(location, onOpen) }
}

@Composable
private fun <T, Q> CatalogFrame(title: String, state: CatalogScreenState<T, Q>, onBack: () -> Unit, retry: () -> Unit, previous: () -> Unit, next: () -> Unit, filters: @Composable () -> Unit, item: @Composable (T) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) { OutlinedButton(onClick = onBack) { Text("Назад") }; Text(title, style = RickAndMortyTheme.typography.headlineSmall) } }
        item { Column(verticalArrangement = Arrangement.spacedBy(6.dp)) { filters() } }
        state.errorMessage?.let { message -> item { Card { Column(Modifier.padding(12.dp)) { Text(message); Button(onClick = retry) { Text("Повторить") } } } } }
        if (state.isLoading) item { Text("Загрузка…") }
        if (state.isEmpty) item { Text("Ничего не найдено") }
        items(state.items) { item(it) }
        if (state.items.isNotEmpty()) item { Row(Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.SpaceBetween) { OutlinedButton(previous, enabled = state.canGoBack) { Text("Назад") }; Text("${state.page} / ${state.totalPages}"); Button(next, enabled = state.canGoForward) { Text("Далее") } } }
    }
}

@Composable private fun SearchField(label: String, value: String, changed: (String) -> Unit) = OutlinedTextField(value, changed, Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true)

@Composable internal fun CharacterCard(value: Character, open: (Int) -> Unit) { Card(Modifier.fillMaxWidth().clickable { open(value.id) }) { Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { AsyncImage(value.imageUrl, value.name, Modifier.clip(CircleShape).fillMaxWidth(.22f), contentScale = ContentScale.Crop); Column { Text(value.name, style = RickAndMortyTheme.typography.titleMedium); Text("${value.status} · ${value.species}"); Text(value.location.name) } } } }
@Composable internal fun EpisodeCard(value: Episode, open: (Int) -> Unit) { Card(Modifier.fillMaxWidth().clickable { open(value.id) }) { Column(Modifier.padding(16.dp)) { Text(value.name, style = RickAndMortyTheme.typography.titleMedium); Text("${value.code} · ${value.airDate}") } } }
@Composable internal fun LocationCard(value: Location, open: (Int) -> Unit) { Card(Modifier.fillMaxWidth().clickable { open(value.id) }) { Column(Modifier.padding(16.dp)) { Text(value.name, style = RickAndMortyTheme.typography.titleMedium); Text("${value.type} · ${value.dimension}") } } }
