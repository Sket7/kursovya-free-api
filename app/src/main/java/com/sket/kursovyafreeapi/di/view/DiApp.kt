package com.sket.kursovyafreeapi.di.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.sket.kursovyafreeapi.di.navigation.DiRoute
import com.sket.kursovyafreeapi.di.presentation.CharacterDetailsViewModel
import com.sket.kursovyafreeapi.di.presentation.CharactersViewModel
import com.sket.kursovyafreeapi.di.presentation.CommentsViewModel
import com.sket.kursovyafreeapi.di.presentation.EpisodeDetailsViewModel
import com.sket.kursovyafreeapi.di.presentation.EpisodesViewModel
import com.sket.kursovyafreeapi.di.presentation.LocationDetailsViewModel
import com.sket.kursovyafreeapi.di.presentation.LocationsViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DiApp() {
    val backStack = rememberNavBackStack(DiRoute.Home)

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<DiRoute.Home> { HomeView(backStack::add) }
            entry<DiRoute.Episodes> {
                val viewModel = koinViewModel<EpisodesViewModel>()
                EpisodesView(
                    viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onOpen = { backStack.add(DiRoute.EpisodeDetails(it)) }
                )
            }
            entry<DiRoute.EpisodeDetails> { route ->
                val viewModel = koinViewModel<EpisodeDetailsViewModel> {
                    parametersOf(route.id)
                }
                EpisodeDetailsView(
                    viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onCharacter = { backStack.add(DiRoute.CharacterDetails(it)) }
                )
            }
            entry<DiRoute.Characters> {
                val viewModel = koinViewModel<CharactersViewModel>()
                CharactersView(
                    viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onOpen = { backStack.add(DiRoute.CharacterDetails(it)) }
                )
            }
            entry<DiRoute.CharacterDetails> { route ->
                val viewModel = koinViewModel<CharacterDetailsViewModel> {
                    parametersOf(route.id)
                }
                CharacterDetailsView(
                    viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onEpisode = { backStack.add(DiRoute.EpisodeDetails(it)) },
                    onLocation = { backStack.add(DiRoute.LocationDetails(it)) }
                )
            }
            entry<DiRoute.Locations> {
                val viewModel = koinViewModel<LocationsViewModel>()
                LocationsView(
                    viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onOpen = { backStack.add(DiRoute.LocationDetails(it)) }
                )
            }
            entry<DiRoute.LocationDetails> { route ->
                val viewModel = koinViewModel<LocationDetailsViewModel> {
                    parametersOf(route.id)
                }
                LocationDetailsView(
                    viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onCharacter = { backStack.add(DiRoute.CharacterDetails(it)) }
                )
            }
            entry<DiRoute.Comments> {
                val viewModel = koinViewModel<CommentsViewModel>()
                PersonalCommentsView(
                    viewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onEpisode = { backStack.add(DiRoute.EpisodeDetails(it)) }
                )
            }
        }
    )
}
