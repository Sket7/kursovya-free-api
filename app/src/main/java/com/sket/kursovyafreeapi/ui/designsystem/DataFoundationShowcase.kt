package com.sket.kursovyafreeapi.ui.designsystem

import android.content.Context
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.local.entity.EpisodeUserDataEntity
import com.sket.kursovyafreeapi.data.local.mapper.toEntity
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApi
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApiFactory
import com.sket.kursovyafreeapi.data.remote.mapper.toModel

class DataFoundationShowcase private constructor(
    private val api: RickAndMortyApi,
    private val database: RickAndMortyDatabase
) : AutoCloseable {
    suspend fun checkCharacters(): String {
        val models = api.getCharacters(page = 1).results.map { it.toModel() }
        database.characterDao().upsert(models.map { it.toEntity(System.currentTimeMillis()) })
        val cachedCount = database.characterDao().getByIds(models.map { it.id }).size
        return "API: ${models.size}, кеш Room: $cachedCount"
    }

    suspend fun checkEpisodes(): String {
        val models = api.getEpisodes(page = 1).results.map { it.toModel() }
        database.episodeDao().upsert(models.map { it.toEntity(System.currentTimeMillis()) })
        val cachedCount = database.episodeDao().getByIds(models.map { it.id }).size
        return "API: ${models.size}, кеш Room: $cachedCount"
    }

    suspend fun checkLocations(): String {
        val models = api.getLocations(page = 1).results.map { it.toModel() }
        database.locationDao().upsert(models.map { it.toEntity(System.currentTimeMillis()) })
        val cachedCount = database.locationDao().getByIds(models.map { it.id }).size
        return "API: ${models.size}, кеш Room: $cachedCount"
    }

    suspend fun readEpisodeMark(): Boolean =
        database.episodeUserDataDao().getByEpisodeId(SHOWCASE_EPISODE_ID) != null

    suspend fun saveEpisodeMark() {
        database.episodeUserDataDao().upsert(
            EpisodeUserDataEntity(
                episodeId = SHOWCASE_EPISODE_ID,
                isWatched = true,
                rating = null,
                comment = "",
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteEpisodeMark() {
        database.episodeUserDataDao().delete(SHOWCASE_EPISODE_ID)
    }

    override fun close() {
        database.close()
    }

    companion object {
        const val SHOWCASE_EPISODE_ID = 1

        fun create(context: Context) = DataFoundationShowcase(
            api = RickAndMortyApiFactory.create(),
            database = RickAndMortyDatabase.create(context)
        )
    }
}
