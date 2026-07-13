package com.sket.kursovyafreeapi.di

import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApi
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApiFactory
import com.sket.kursovyafreeapi.di.data.CatalogRepository
import com.sket.kursovyafreeapi.di.data.DefaultCatalogRepository
import com.sket.kursovyafreeapi.di.data.EpisodeNotesRepository
import com.sket.kursovyafreeapi.di.data.RoomEpisodeNotesRepository
import org.koin.android.ext.koin.androidContext
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
