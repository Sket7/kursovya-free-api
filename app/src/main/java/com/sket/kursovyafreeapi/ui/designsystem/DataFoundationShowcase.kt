package com.sket.kursovyafreeapi.ui.designsystem

import android.content.Context
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.local.entity.CharacterEntity
import com.sket.kursovyafreeapi.data.local.entity.EpisodeUserDataEntity
import com.sket.kursovyafreeapi.data.local.entity.EpisodeEntity
import com.sket.kursovyafreeapi.data.local.entity.LocationEntity
import com.sket.kursovyafreeapi.data.local.mapper.toEntity
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApi
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApiFactory
import com.sket.kursovyafreeapi.data.remote.mapper.toModel

class DataFoundationShowcase private constructor(
    private val api: RickAndMortyApi,
    private val database: RickAndMortyDatabase
) : AutoCloseable {
    suspend fun checkCharacters(): DataCheckResult {
        val models = api.getCharacters(page = 1).results.map { it.toModel() }
        database.characterDao().upsert(models.map { it.toEntity(System.currentTimeMillis()) })
        val cached = database.characterDao().getByIds(models.map { it.id }).sortedBy { it.id }
        return DataCheckResult(
            endpoint = "GET /character?page=1",
            receivedCount = models.size,
            cachedCount = cached.size,
            receivedPreview = models.take(PREVIEW_SIZE).map {
                "#${it.id} ${it.name} · ${it.status.name} · ${it.species}"
            },
            cachedPreview = cached.take(PREVIEW_SIZE).map(CharacterEntity::preview)
        )
    }

    suspend fun checkEpisodes(): DataCheckResult {
        val models = api.getEpisodes(page = 1).results.map { it.toModel() }
        database.episodeDao().upsert(models.map { it.toEntity(System.currentTimeMillis()) })
        val cached = database.episodeDao().getByIds(models.map { it.id }).sortedBy { it.id }
        return DataCheckResult(
            endpoint = "GET /episode?page=1",
            receivedCount = models.size,
            cachedCount = cached.size,
            receivedPreview = models.take(PREVIEW_SIZE).map {
                "#${it.id} ${it.name} · ${it.code} · ${it.airDate}"
            },
            cachedPreview = cached.take(PREVIEW_SIZE).map(EpisodeEntity::preview)
        )
    }

    suspend fun checkLocations(): DataCheckResult {
        val models = api.getLocations(page = 1).results.map { it.toModel() }
        database.locationDao().upsert(models.map { it.toEntity(System.currentTimeMillis()) })
        val cached = database.locationDao().getByIds(models.map { it.id }).sortedBy { it.id }
        return DataCheckResult(
            endpoint = "GET /location?page=1",
            receivedCount = models.size,
            cachedCount = cached.size,
            receivedPreview = models.take(PREVIEW_SIZE).map {
                "#${it.id} ${it.name} · ${it.type} · ${it.dimension}"
            },
            cachedPreview = cached.take(PREVIEW_SIZE).map(LocationEntity::preview)
        )
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
        private const val PREVIEW_SIZE = 3

        fun create(context: Context) = DataFoundationShowcase(
            api = RickAndMortyApiFactory.create(),
            database = RickAndMortyDatabase.create(context)
        )
    }
}

data class DataCheckResult(
    val endpoint: String,
    val receivedCount: Int,
    val cachedCount: Int,
    val receivedPreview: List<String>,
    val cachedPreview: List<String>
) {
    val isFullyCached: Boolean = receivedCount == cachedCount
}

private fun CharacterEntity.preview(): String =
    "#$id $name · $status · $species"

private fun EpisodeEntity.preview(): String =
    "#$id $name · $code · $airDate"

private fun LocationEntity.preview(): String =
    "#$id $name · $type · $dimension"
