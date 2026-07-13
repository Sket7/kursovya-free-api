package com.sket.kursovyafreeapi.clean.view

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
import com.sket.kursovyafreeapi.clean.CleanGraph
import com.sket.kursovyafreeapi.clean.navigation.CleanRoute
import com.sket.kursovyafreeapi.clean.presentation.CharacterDetailsViewModel
import com.sket.kursovyafreeapi.clean.presentation.CharactersViewModel
import com.sket.kursovyafreeapi.clean.presentation.CommentsViewModel
import com.sket.kursovyafreeapi.clean.presentation.EpisodeDetailsViewModel
import com.sket.kursovyafreeapi.clean.presentation.EpisodesViewModel
import com.sket.kursovyafreeapi.clean.presentation.LocationDetailsViewModel
import com.sket.kursovyafreeapi.clean.presentation.LocationsViewModel

@Composable
fun CleanApp() {
    val context = LocalContext.current
    val graph = remember(context) { CleanGraph.create(context) }
    val backStack = rememberNavBackStack(CleanRoute.Home)
    DisposableEffect(graph) { onDispose(graph::close) }

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<CleanRoute.Home> { HomeView(backStack::add) }
            entry<CleanRoute.Episodes> {
                val vm = viewModel { EpisodesViewModel(graph.useCases.episodes) }
                EpisodesView(vm, { backStack.removeLastOrNull() }) { backStack.add(CleanRoute.EpisodeDetails(it)) }
            }
            entry<CleanRoute.EpisodeDetails> { route ->
                val vm = viewModel { EpisodeDetailsViewModel(route.id, graph.useCases.episodeDetails, graph.useCases.observeNote, graph.useCases.saveNote) }
                EpisodeDetailsView(vm, { backStack.removeLastOrNull() }) { backStack.add(CleanRoute.CharacterDetails(it)) }
            }
            entry<CleanRoute.Characters> {
                val vm = viewModel { CharactersViewModel(graph.useCases.characters) }
                CharactersView(vm, { backStack.removeLastOrNull() }) { backStack.add(CleanRoute.CharacterDetails(it)) }
            }
            entry<CleanRoute.CharacterDetails> { route ->
                val vm = viewModel { CharacterDetailsViewModel(route.id, graph.useCases.characterDetails) }
                CharacterDetailsView(
                    vm,
                    { backStack.removeLastOrNull() },
                    { backStack.add(CleanRoute.EpisodeDetails(it)) },
                    { backStack.add(CleanRoute.LocationDetails(it)) }
                )
            }
            entry<CleanRoute.Locations> {
                val vm = viewModel { LocationsViewModel(graph.useCases.locations) }
                LocationsView(vm, { backStack.removeLastOrNull() }) { backStack.add(CleanRoute.LocationDetails(it)) }
            }
            entry<CleanRoute.LocationDetails> { route ->
                val vm = viewModel { LocationDetailsViewModel(route.id, graph.useCases.locationDetails) }
                LocationDetailsView(vm, { backStack.removeLastOrNull() }) { backStack.add(CleanRoute.CharacterDetails(it)) }
            }
            entry<CleanRoute.Comments> {
                val vm = viewModel { CommentsViewModel(graph.useCases.comments, graph.useCases.deleteComment) }
                PersonalCommentsView(vm, { backStack.removeLastOrNull() }) { backStack.add(CleanRoute.EpisodeDetails(it)) }
            }
        }
    )
}

