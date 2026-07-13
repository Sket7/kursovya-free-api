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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.di.presentation.CatalogUiState
import com.sket.kursovyafreeapi.di.presentation.CharactersViewModel
import com.sket.kursovyafreeapi.di.presentation.EpisodesViewModel
import com.sket.kursovyafreeapi.di.presentation.LocationsViewModel
import com.sket.kursovyafreeapi.di.navigation.DiRoute
import com.sket.kursovyafreeapi.ui.shared.CharacterResourceCard
import com.sket.kursovyafreeapi.ui.shared.EpisodeResourceCard
import com.sket.kursovyafreeapi.ui.shared.LocationResourceCard
import com.sket.kursovyafreeapi.ui.shared.NavigationCard
import com.sket.kursovyafreeapi.ui.shared.PortalHero
import com.sket.kursovyafreeapi.ui.shared.PortalScreen
import com.sket.kursovyafreeapi.ui.shared.SectionTitle
import com.sket.kursovyafreeapi.ui.shared.StateMessage
import com.sket.kursovyafreeapi.ui.theme.RickAndMortyTheme

@Composable
fun HomeView(onNavigate: (DiRoute) -> Unit) {
    PortalScreen(title = "Rick and Morty") { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                PortalHero(
                    eyebrow = "Wubba lubba dub-dub!",
                    title = "Мультивселенная ждёт",
                    description = "Исследуйте эпизоды, знакомьтесь с героями и сохраняйте собственные впечатления."
                )
            }
            item { SectionTitle("Исследовать", "Все данные загружаются из The Rick and Morty API") }
            item { NavigationCard("Эпизоды", "Серии, оценки и личные заметки", RickAndMortyTheme.colors.primary) { onNavigate(DiRoute.Episodes) } }
            item { NavigationCard("Персонажи", "Герои со всех измерений", RickAndMortyTheme.colors.secondary) { onNavigate(DiRoute.Characters) } }
            item { NavigationCard("Локации", "Планеты, станции и измерения", RickAndMortyTheme.colors.tertiary) { onNavigate(DiRoute.Locations) } }
            item { NavigationCard("Мои комментарии", "Все сохранённые впечатления", RickAndMortyTheme.extendedColors.rating) { onNavigate(DiRoute.Comments) } }
        }
    }
}

@Composable
fun EpisodesView(viewModel: EpisodesViewModel, onBack: () -> Unit, onOpen: (Int) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CatalogFrame("Эпизоды", "${state.totalCount} серий", state, onBack, viewModel::retry, viewModel::previousPage, viewModel::nextPage, filters = {
        SearchField("Поиск по названию", state.query.name) { viewModel.updateQuery(state.query.copy(name = it)) }
        SearchField("Код эпизода · S01E01", state.query.code) { viewModel.updateQuery(state.query.copy(code = it)) }
    }) { EpisodeResourceCard(it) { onOpen(it.id) } }
}

@Composable
fun CharactersView(viewModel: CharactersViewModel, onBack: () -> Unit, onOpen: (Int) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CatalogFrame("Персонажи", "${state.totalCount} героев", state, onBack, viewModel::retry, viewModel::previousPage, viewModel::nextPage, filters = {
        SearchField("Поиск по имени", state.query.name) { viewModel.updateQuery(state.query.copy(name = it)) }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { listOf(null to "Все", "alive" to "Живы", "dead" to "Мертвы").forEach { (value, label) -> FilterChip(state.query.status == value, { viewModel.updateQuery(state.query.copy(status = value), false) }, label = { Text(label) }) } }
        SearchField("Вид", state.query.species) { viewModel.updateQuery(state.query.copy(species = it)) }
        SearchField("Тип", state.query.type) { viewModel.updateQuery(state.query.copy(type = it)) }
        SearchField("Пол", state.query.gender.orEmpty()) { viewModel.updateQuery(state.query.copy(gender = it)) }
    }) { CharacterCard(it, onOpen) }
}

@Composable
fun LocationsView(viewModel: LocationsViewModel, onBack: () -> Unit, onOpen: (Int) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CatalogFrame("Локации", "${state.totalCount} мест", state, onBack, viewModel::retry, viewModel::previousPage, viewModel::nextPage, filters = {
        SearchField("Поиск по названию", state.query.name) { viewModel.updateQuery(state.query.copy(name = it)) }
        SearchField("Тип локации", state.query.type) { viewModel.updateQuery(state.query.copy(type = it)) }
        SearchField("Измерение", state.query.dimension) { viewModel.updateQuery(state.query.copy(dimension = it)) }
    }) { LocationResourceCard(it) { onOpen(it.id) } }
}

@Composable
private fun <T, Q> CatalogFrame(title: String, subtitle: String, state: CatalogUiState<T, Q>, onBack: () -> Unit, retry: () -> Unit, previous: () -> Unit, next: () -> Unit, filters: @Composable () -> Unit, item: @Composable (T) -> Unit) {
    PortalScreen(title, onBack) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { SectionTitle(title, subtitle) }
            item { ElevatedCard(Modifier.fillMaxWidth()) { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { filters() } } }
            state.errorMessage?.let { message -> item { StateMessage(if (state.items.isEmpty()) "Не удалось загрузить" else "Показан кеш", message, "Повторить", retry) } }
            if (state.isLoading) item { StateMessage("Открываем портал…", "Загружаем данные из другого измерения") }
            if (state.isEmpty) item { StateMessage("Ничего не найдено", "Измените запрос или сбросьте фильтры") }
            items(state.items) { item(it) }
            if (state.items.isNotEmpty()) item { Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) { OutlinedButton(previous, enabled = state.canGoBack) { Text("← Назад") }; Text("${state.page} из ${state.totalPages}", Modifier.padding(top = 12.dp), style = RickAndMortyTheme.typography.labelLarge); Button(next, enabled = state.canGoForward) { Text("Далее →") } } }
        }
    }
}

@Composable private fun SearchField(label: String, value: String, changed: (String) -> Unit) = OutlinedTextField(value, changed, Modifier.fillMaxWidth(), label = { Text(label) }, singleLine = true, shape = RickAndMortyTheme.shapes.medium)
@Composable internal fun CharacterCard(value: Character, open: (Int) -> Unit) = CharacterResourceCard(value, image = { AsyncImage(value.imageUrl, value.name, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }) { open(value.id) }
@Composable internal fun EpisodeCard(value: Episode, open: (Int) -> Unit) = EpisodeResourceCard(value) { open(value.id) }
@Composable internal fun LocationCard(value: Location, open: (Int) -> Unit) = LocationResourceCard(value) { open(value.id) }

