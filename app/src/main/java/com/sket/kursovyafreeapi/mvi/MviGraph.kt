package com.sket.kursovyafreeapi.mvi

import android.content.Context
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApiFactory
import com.sket.kursovyafreeapi.mvi.data.CatalogRepository
import com.sket.kursovyafreeapi.mvi.data.EpisodeNotesRepository

class MviGraph private constructor(private val database: RickAndMortyDatabase) : AutoCloseable {
    val catalog = CatalogRepository(RickAndMortyApiFactory.create(), database)
    val notes = EpisodeNotesRepository(database)
    override fun close() = database.close()
    companion object { fun create(context: Context) = MviGraph(RickAndMortyDatabase.create(context.applicationContext)) }
}
