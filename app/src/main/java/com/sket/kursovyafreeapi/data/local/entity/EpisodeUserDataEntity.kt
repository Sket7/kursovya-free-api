package com.sket.kursovyafreeapi.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "episode_user_data",
    indices = [Index(value = ["isWatched"])]
)
data class EpisodeUserDataEntity(
    @PrimaryKey
    val episodeId: Int,
    val isWatched: Boolean,
    val rating: Int?,
    val comment: String,
    val updatedAt: Long
) {
    init {
        require(rating == null || rating in 1..5) {
            "Episode rating must be null or between 1 and 5"
        }
    }
}
