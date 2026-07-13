package com.sket.kursovyafreeapi.di

import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApi
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApiFactory
import com.sket.kursovyafreeapi.di.data.CatalogRepository
import com.sket.kursovyafreeapi.di.data.DefaultCatalogRepository
import com.sket.kursovyafreeapi.di.data.EpisodeNotesRepository
import com.sket.kursovyafreeapi.di.data.RoomEpisodeNotesRepository
import com.sket.kursovyafreeapi.di.presentation.CharacterDetailsViewModel
import com.sket.kursovyafreeapi.di.presentation.CharactersViewModel
import com.sket.kursovyafreeapi.di.presentation.CommentsViewModel
import com.sket.kursovyafreeapi.di.presentation.EpisodeDetailsViewModel
import com.sket.kursovyafreeapi.di.presentation.EpisodesViewModel
import com.sket.kursovyafreeapi.di.presentation.LocationDetailsViewModel
import com.sket.kursovyafreeapi.di.presentation.LocationsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

private val infrastructureModule = module {
    single<RickAndMortyApi> { RickAndMortyApiFactory.create() }
    single { RickAndMortyDatabase.create(androidContext()) }
}

private val repositoryModule = module {
    single<CatalogRepository> { DefaultCatalogRepository(api = get(), database = get()) }
    single<EpisodeNotesRepository> { RoomEpisodeNotesRepository(database = get()) }
}

val dataModules: List<Module> = listOf(infrastructureModule, repositoryModule)

private val presentationModule = module {
    viewModel { EpisodesViewModel(repo = get()) }
    viewModel { CharactersViewModel(repo = get()) }
    viewModel { LocationsViewModel(repo = get()) }
    viewModel { parameters ->
        EpisodeDetailsViewModel(id = parameters.get(), catalog = get(), notes = get())
    }
    viewModel { parameters ->
        CharacterDetailsViewModel(id = parameters.get(), repo = get())
    }
    viewModel { parameters ->
        LocationDetailsViewModel(id = parameters.get(), repo = get())
    }
    viewModel { CommentsViewModel(notes = get()) }
}

val appModules: List<Module> = dataModules + presentationModule
