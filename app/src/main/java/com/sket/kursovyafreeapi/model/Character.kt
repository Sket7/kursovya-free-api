package com.sket.kursovyafreeapi.model

data class Character(
    val id: Int,
    val name: String,
    val status: CharacterStatus,
    val species: String,
    val type: String,
    val gender: CharacterGender,
    val origin: LocationReference,
    val location: LocationReference,
    val imageUrl: String,
    val episodeIds: List<Int>,
    val resourceUrl: String,
    val createdAt: String
)

data class LocationReference(
    val name: String,
    val url: String?,
    val id: Int?
)

enum class CharacterStatus {
    ALIVE,
    DEAD,
    UNKNOWN
}

enum class CharacterGender {
    FEMALE,
    MALE,
    GENDERLESS,
    UNKNOWN
}
