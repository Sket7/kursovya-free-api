package com.sket.kursovyafreeapi.data.local.mapper

import com.sket.kursovyafreeapi.data.local.entity.CharacterEntity
import com.sket.kursovyafreeapi.data.local.entity.EpisodeEntity
import com.sket.kursovyafreeapi.data.local.entity.EpisodeUserDataEntity
import com.sket.kursovyafreeapi.data.local.entity.LocationEntity
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.CharacterGender
import com.sket.kursovyafreeapi.model.CharacterStatus
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.EpisodeUserData
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.model.LocationReference

fun Character.toEntity(cachedAt: Long) = CharacterEntity(
    id = id,
    name = name,
    status = status.name,
    species = species,
    type = type,
    gender = gender.name,
    originName = origin.name,
    originUrl = origin.url,
    originId = origin.id,
    locationName = location.name,
    locationUrl = location.url,
    locationId = location.id,
    imageUrl = imageUrl,
    episodeIds = episodeIds,
    resourceUrl = resourceUrl,
    createdAt = createdAt,
    cachedAt = cachedAt
)

fun CharacterEntity.toModel() = Character(
    id = id,
    name = name,
    status = enumValueOrDefault(status, CharacterStatus.UNKNOWN),
    species = species,
    type = type,
    gender = enumValueOrDefault(gender, CharacterGender.UNKNOWN),
    origin = LocationReference(originName, originUrl, originId),
    location = LocationReference(locationName, locationUrl, locationId),
    imageUrl = imageUrl,
    episodeIds = episodeIds,
    resourceUrl = resourceUrl,
    createdAt = createdAt
)

fun Episode.toEntity(cachedAt: Long) = EpisodeEntity(
    id = id,
    name = name,
    airDate = airDate,
    code = code,
    characterIds = characterIds,
    resourceUrl = resourceUrl,
    createdAt = createdAt,
    cachedAt = cachedAt
)

fun EpisodeEntity.toModel() = Episode(
    id = id,
    name = name,
    airDate = airDate,
    code = code,
    characterIds = characterIds,
    resourceUrl = resourceUrl,
    createdAt = createdAt
)

fun Location.toEntity(cachedAt: Long) = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residentIds = residentIds,
    resourceUrl = resourceUrl,
    createdAt = createdAt,
    cachedAt = cachedAt
)

fun LocationEntity.toModel() = Location(
    id = id,
    name = name,
    type = type,
    dimension = dimension,
    residentIds = residentIds,
    resourceUrl = resourceUrl,
    createdAt = createdAt
)

fun EpisodeUserData.toEntity() = EpisodeUserDataEntity(
    episodeId = episodeId,
    isWatched = isWatched,
    rating = rating,
    comment = comment,
    updatedAt = updatedAt
)

fun EpisodeUserDataEntity.toModel() = EpisodeUserData(
    episodeId = episodeId,
    isWatched = isWatched,
    rating = rating,
    comment = comment,
    updatedAt = updatedAt
)

private inline fun <reified T : Enum<T>> enumValueOrDefault(value: String, default: T): T =
    enumValues<T>().firstOrNull { it.name == value } ?: default
