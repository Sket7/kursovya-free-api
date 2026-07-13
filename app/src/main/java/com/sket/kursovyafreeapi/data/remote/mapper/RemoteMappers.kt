package com.sket.kursovyafreeapi.data.remote.mapper

import com.sket.kursovyafreeapi.data.remote.dto.CharacterDto
import com.sket.kursovyafreeapi.data.remote.dto.EpisodeDto
import com.sket.kursovyafreeapi.data.remote.dto.LocationDto
import com.sket.kursovyafreeapi.data.remote.dto.NamedUrlDto
import com.sket.kursovyafreeapi.data.remote.dto.PageDto
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.CharacterGender
import com.sket.kursovyafreeapi.model.CharacterStatus
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.model.LocationReference
import com.sket.kursovyafreeapi.model.Page
import com.sket.kursovyafreeapi.model.PageInfo

fun CharacterDto.toModel() = Character(
    id = id,
    name = name,
    status = status.toCharacterStatus(),
    species = species,
    type = type,
    gender = gender.toCharacterGender(),
    origin = origin.toLocationReference(),
    location = location.toLocationReference(),
    imageUrl = image,
    episodeIds = episode.mapNotNull(String::resourceIdOrNull),
    resourceUrl = url,
    createdAt = created
)

fun EpisodeDto.toModel() = Episode(
    id = id,
    name = name,
    airDate = airDate,
    code = episode,
    characterIds = characters.mapNotNull(String::resourceIdOrNull),
    resourceUrl = url,
    createdAt = created
)

fun LocationDto.toModel() = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residentIds = residents.mapNotNull(String::resourceIdOrNull),
    resourceUrl = url,
    createdAt = created
)

fun <T, R> PageDto<T>.toModel(transform: (T) -> R) = Page(
    info = PageInfo(
        count = info.count,
        pages = info.pages,
        next = info.next,
        previous = info.previous
    ),
    items = results.map(transform)
)

private fun NamedUrlDto.toLocationReference() = LocationReference(
    name = name,
    url = url.ifBlank { null },
    id = url.resourceIdOrNull()
)

private fun String.toCharacterStatus() = when (lowercase()) {
    "alive" -> CharacterStatus.ALIVE
    "dead" -> CharacterStatus.DEAD
    else -> CharacterStatus.UNKNOWN
}

private fun String.toCharacterGender() = when (lowercase()) {
    "female" -> CharacterGender.FEMALE
    "male" -> CharacterGender.MALE
    "genderless" -> CharacterGender.GENDERLESS
    else -> CharacterGender.UNKNOWN
}

internal fun String.resourceIdOrNull(): Int? =
    trimEnd('/').substringAfterLast('/').toIntOrNull()
