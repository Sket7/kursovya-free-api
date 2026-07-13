package com.sket.kursovyafreeapi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val originName: String,
    val originUrl: String?,
    val originId: Int?,
    val locationName: String,
    val locationUrl: String?,
    val locationId: Int?,
    val imageUrl: String,
    val episodeIds: List<Int>,
    val resourceUrl: String,
    val createdAt: String,
    val cachedAt: Long
)
