package com.sket.kursovyafreeapi.di.data

import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.EpisodeUserData
import kotlinx.coroutines.flow.Flow

const val MAX_COMMENT_LENGTH = 300

data class PersonalComment(
    val episode: Episode?,
    val userData: EpisodeUserData
)

interface EpisodeNotesRepository {
    fun observe(episodeId: Int): Flow<EpisodeUserData?>
    fun observePersonalComments(): Flow<List<PersonalComment>>

    suspend fun save(
        episodeId: Int,
        isWatched: Boolean,
        rating: Int?,
        comment: String
    )

    suspend fun deleteComment(episodeId: Int)
}
