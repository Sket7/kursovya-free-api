package com.sket.kursovyafreeapi.mvvm

import android.content.Context
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApiFactory
import com.sket.kursovyafreeapi.mvvm.data.CatalogRepository
import com.sket.kursovyafreeapi.mvvm.data.EpisodeNotesRepository

class MvvmGraph private constructor(private val database: RickAndMortyDatabase) : AutoCloseable {
    private val api = RickAndMortyApiFactory.create()
    val catalog = CatalogRepository(api, database)
    val notes = EpisodeNotesRepository(database)
    override fun close() = database.close()
    companion object { fun create(context: Context) = MvvmGraph(RickAndMortyDatabase.create(context.applicationContext)) }
}
