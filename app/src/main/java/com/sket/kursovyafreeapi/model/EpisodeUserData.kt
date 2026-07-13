package com.sket.kursovyafreeapi.model

data class EpisodeUserData(
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
