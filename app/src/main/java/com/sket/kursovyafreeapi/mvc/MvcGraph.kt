package com.sket.kursovyafreeapi.mvc

import android.content.Context
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApiFactory
import com.sket.kursovyafreeapi.mvc.model.CatalogModel
import com.sket.kursovyafreeapi.mvc.model.EpisodeNotesModel

class MvcGraph private constructor(
    private val database: RickAndMortyDatabase
) : AutoCloseable {
    private val api = RickAndMortyApiFactory.create()

    val catalog = CatalogModel(api = api, database = database)
    val episodeNotes = EpisodeNotesModel(database = database)

    override fun close() {
        database.close()
    }

    companion object {
        fun create(context: Context): MvcGraph = MvcGraph(
            database = RickAndMortyDatabase.create(context.applicationContext)
        )
    }
}
