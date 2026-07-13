package com.sket.kursovyafreeapi.mvvm.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sket.kursovyafreeapi.mvvm.MvvmGraph
import com.sket.kursovyafreeapi.mvvm.navigation.MvvmRoute
import com.sket.kursovyafreeapi.mvvm.presentation.CharacterDetailsViewModel
import com.sket.kursovyafreeapi.mvvm.presentation.CharactersViewModel
import com.sket.kursovyafreeapi.mvvm.presentation.CommentsViewModel
import com.sket.kursovyafreeapi.mvvm.presentation.EpisodeDetailsViewModel
import com.sket.kursovyafreeapi.mvvm.presentation.EpisodesViewModel
import com.sket.kursovyafreeapi.mvvm.presentation.LocationDetailsViewModel
import com.sket.kursovyafreeapi.mvvm.presentation.LocationsViewModel

@Composable
fun MvvmApp() {
    val context = LocalContext.current
    val graph = remember(context) { MvvmGraph.create(context) }
    val backStack = rememberNavBackStack(MvvmRoute.Home)
    DisposableEffect(graph) { onDispose(graph::close) }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<MvvmRoute.Home> { HomeView(backStack::add) }
            entry<MvvmRoute.Episodes> {
                val vm = viewModel { EpisodesViewModel(graph.catalog) }
                EpisodesView(vm, { backStack.removeLastOrNull() }) { backStack.add(MvvmRoute.EpisodeDetails(it)) }
            }
            entry<MvvmRoute.EpisodeDetails> { route ->
                val vm = viewModel { EpisodeDetailsViewModel(route.id, graph.catalog, graph.notes) }
                EpisodeDetailsView(vm, { backStack.removeLastOrNull() }) { backStack.add(MvvmRoute.CharacterDetails(it)) }
            }
            entry<MvvmRoute.Characters> {
                val vm = viewModel { CharactersViewModel(graph.catalog) }
                CharactersView(vm, { backStack.removeLastOrNull() }) { backStack.add(MvvmRoute.CharacterDetails(it)) }
            }
            entry<MvvmRoute.CharacterDetails> { route ->
                val vm = viewModel { CharacterDetailsViewModel(route.id, graph.catalog) }
                CharacterDetailsView(
                    vm,
                    { backStack.removeLastOrNull() },
                    { backStack.add(MvvmRoute.EpisodeDetails(it)) },
                    { backStack.add(MvvmRoute.LocationDetails(it)) }
                )
            }
            entry<MvvmRoute.Locations> {
                val vm = viewModel { LocationsViewModel(graph.catalog) }
                LocationsView(vm, { backStack.removeLastOrNull() }) { backStack.add(MvvmRoute.LocationDetails(it)) }
            }
            entry<MvvmRoute.LocationDetails> { route ->
                val vm = viewModel { LocationDetailsViewModel(route.id, graph.catalog) }
                LocationDetailsView(vm, { backStack.removeLastOrNull() }) { backStack.add(MvvmRoute.CharacterDetails(it)) }
            }
            entry<MvvmRoute.Comments> {
                val vm = viewModel { CommentsViewModel(graph.notes) }
                PersonalCommentsView(vm, { backStack.removeLastOrNull() }) { backStack.add(MvvmRoute.EpisodeDetails(it)) }
            }
        }
    )
}
