package com.sket.kursovyafreeapi.mvi.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.sket.kursovyafreeapi.mvi.MviGraph
import com.sket.kursovyafreeapi.mvi.navigation.MviRoute
import com.sket.kursovyafreeapi.mvi.presentation.CharacterDetailsStore
import com.sket.kursovyafreeapi.mvi.presentation.CharactersStore
import com.sket.kursovyafreeapi.mvi.presentation.CommentsStore
import com.sket.kursovyafreeapi.mvi.presentation.EpisodeDetailsStore
import com.sket.kursovyafreeapi.mvi.presentation.EpisodesStore
import com.sket.kursovyafreeapi.mvi.presentation.LocationDetailsStore
import com.sket.kursovyafreeapi.mvi.presentation.LocationsStore

@Composable
fun MviApp() {
    val context = LocalContext.current
    val graph = remember(context) { MviGraph.create(context) }
    val backStack = rememberNavBackStack(MviRoute.Home)
    DisposableEffect(graph) { onDispose(graph::close) }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<MviRoute.Home> { HomeView(backStack::add) }
            entry<MviRoute.Episodes> {
                val vm = viewModel { EpisodesStore(graph.catalog) }
                EpisodesView(vm, { backStack.removeLastOrNull() }) { backStack.add(MviRoute.EpisodeDetails(it)) }
            }
            entry<MviRoute.EpisodeDetails> { route ->
                val vm = viewModel { EpisodeDetailsStore(route.id, graph.catalog, graph.notes) }
                EpisodeDetailsView(vm, { backStack.removeLastOrNull() }) { backStack.add(MviRoute.CharacterDetails(it)) }
            }
            entry<MviRoute.Characters> {
                val vm = viewModel { CharactersStore(graph.catalog) }
                CharactersView(vm, { backStack.removeLastOrNull() }) { backStack.add(MviRoute.CharacterDetails(it)) }
            }
            entry<MviRoute.CharacterDetails> { route ->
                val vm = viewModel { CharacterDetailsStore(route.id, graph.catalog) }
                CharacterDetailsView(
                    vm,
                    { backStack.removeLastOrNull() },
                    { backStack.add(MviRoute.EpisodeDetails(it)) },
                    { backStack.add(MviRoute.LocationDetails(it)) }
                )
            }
            entry<MviRoute.Locations> {
                val vm = viewModel { LocationsStore(graph.catalog) }
                LocationsView(vm, { backStack.removeLastOrNull() }) { backStack.add(MviRoute.LocationDetails(it)) }
            }
            entry<MviRoute.LocationDetails> { route ->
                val vm = viewModel { LocationDetailsStore(route.id, graph.catalog) }
                LocationDetailsView(vm, { backStack.removeLastOrNull() }) { backStack.add(MviRoute.CharacterDetails(it)) }
            }
            entry<MviRoute.Comments> {
                val vm = viewModel { CommentsStore(graph.notes) }
                PersonalCommentsView(vm, { backStack.removeLastOrNull() }) { backStack.add(MviRoute.EpisodeDetails(it)) }
            }
        }
    )
}

