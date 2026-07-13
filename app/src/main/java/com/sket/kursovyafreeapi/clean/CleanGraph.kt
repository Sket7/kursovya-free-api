package com.sket.kursovyafreeapi.clean

import android.content.Context
import com.sket.kursovyafreeapi.clean.data.CatalogRepositoryImpl
import com.sket.kursovyafreeapi.clean.data.EpisodeNotesRepositoryImpl
import com.sket.kursovyafreeapi.clean.domain.*
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApiFactory

class CleanGraph private constructor(private val database: RickAndMortyDatabase) : AutoCloseable {
    private val catalog: CatalogRepository = CatalogRepositoryImpl(RickAndMortyApiFactory.create(), database)
    private val notes: EpisodeNotesRepository = EpisodeNotesRepositoryImpl(database)
    val useCases = CleanUseCases(
        LoadCharacters(catalog), LoadEpisodes(catalog), LoadLocations(catalog),
        GetEpisodeDetails(catalog), GetCharacterDetails(catalog), GetLocationDetails(catalog),
        ObserveEpisodeNote(notes), SaveEpisodeNote(notes), ObservePersonalComments(notes), DeletePersonalComment(notes)
    )
    override fun close() = database.close()
    companion object { fun create(context: Context) = CleanGraph(RickAndMortyDatabase.create(context.applicationContext)) }
}
