package com.sket.kursovyafreeapi.mvi.data

import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.local.mapper.toEntity
import com.sket.kursovyafreeapi.data.local.mapper.toModel
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.EpisodeUserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class PersonalComment(
    val episode: Episode?,
    val userData: EpisodeUserData
)

class EpisodeNotesRepository(
    private val database: RickAndMortyDatabase,
    private val now: () -> Long = System::currentTimeMillis
) {
    fun observe(episodeId: Int): Flow<EpisodeUserData?> =
        database.episodeUserDataDao().observeByEpisodeId(episodeId).map { it?.toModel() }

    fun observePersonalComments(): Flow<List<PersonalComment>> =
        database.episodeUserDataDao().observeAll().map { entities ->
            val comments = entities
                .filter { it.comment.isNotBlank() }
                .sortedByDescending { it.updatedAt }
            val episodesById = database.episodeDao()
                .getByIds(comments.map { it.episodeId })
                .associateBy { it.id }
            comments.map { entity ->
                PersonalComment(
                    episode = episodesById[entity.episodeId]?.toModel(),
                    userData = entity.toModel()
                )
            }
        }

    suspend fun save(
        episodeId: Int,
        isWatched: Boolean,
        rating: Int?,
        comment: String
    ) {
        require(rating == null || rating in 1..5) { "Rating must be between 1 and 5" }
        database.episodeUserDataDao().upsert(
            EpisodeUserData(
                episodeId = episodeId,
                isWatched = isWatched,
                rating = rating,
                comment = comment.trim().take(MAX_COMMENT_LENGTH),
                updatedAt = now()
            ).toEntity()
        )
    }

    suspend fun deleteComment(episodeId: Int) {
        val current = database.episodeUserDataDao().getByEpisodeId(episodeId) ?: return
        if (!current.isWatched && current.rating == null) {
            database.episodeUserDataDao().delete(episodeId)
        } else {
            database.episodeUserDataDao().upsert(
                current.copy(comment = "", updatedAt = now())
            )
        }
    }

    companion object {
        const val MAX_COMMENT_LENGTH = 300
    }
}


