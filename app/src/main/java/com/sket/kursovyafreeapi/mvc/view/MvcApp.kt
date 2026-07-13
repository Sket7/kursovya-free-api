package com.sket.kursovyafreeapi.mvc.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.sket.kursovyafreeapi.mvc.MvcGraph
import com.sket.kursovyafreeapi.mvc.navigation.MvcRoute

@Composable
fun MvcApp() {
    val context = LocalContext.current
    val graph = remember(context) { MvcGraph.create(context) }
    val backStack = rememberNavBackStack(MvcRoute.Home)
    DisposableEffect(graph) { onDispose(graph::close) }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<MvcRoute.Home> {
                HomeView(onNavigate = backStack::add)
            }
            entry<MvcRoute.Episodes> {
                EpisodesView(graph.catalog, onBack = { backStack.removeLastOrNull() }) {
                    backStack.add(MvcRoute.EpisodeDetails(it))
                }
            }
            entry<MvcRoute.EpisodeDetails> { route ->
                EpisodeDetailsView(route.id, graph, { backStack.removeLastOrNull() }) {
                    backStack.add(MvcRoute.CharacterDetails(it))
                }
            }
            entry<MvcRoute.Characters> {
                CharactersView(graph.catalog, onBack = { backStack.removeLastOrNull() }) {
                    backStack.add(MvcRoute.CharacterDetails(it))
                }
            }
            entry<MvcRoute.CharacterDetails> { route ->
                CharacterDetailsView(
                    id = route.id,
                    model = graph.catalog,
                    onBack = { backStack.removeLastOrNull() },
                    onEpisode = { backStack.add(MvcRoute.EpisodeDetails(it)) },
                    onLocation = { backStack.add(MvcRoute.LocationDetails(it)) }
                )
            }
            entry<MvcRoute.Locations> {
                LocationsView(graph.catalog, onBack = { backStack.removeLastOrNull() }) {
                    backStack.add(MvcRoute.LocationDetails(it))
                }
            }
            entry<MvcRoute.LocationDetails> { route ->
                LocationDetailsView(route.id, graph.catalog, { backStack.removeLastOrNull() }) {
                    backStack.add(MvcRoute.CharacterDetails(it))
                }
            }
            entry<MvcRoute.PersonalComments> {
                PersonalCommentsView(graph.episodeNotes, { backStack.removeLastOrNull() }) {
                    backStack.add(MvcRoute.EpisodeDetails(it))
                }
            }
        }
    )
}
