package com.sket.kursovyafreeapi.mvvm.data

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class CharacterQuery(
    val page: Int = 1,
    val name: String = "",
    val status: String? = null,
    val species: String = "",
    val type: String = "",
    val gender: String? = null
) {
    init {
        require(page > 0) { "Page must be positive" }
    }

    internal fun cacheKey(): String = cacheKey(
        resource = "character",
        page = page,
        "name" to name,
        "status" to status,
        "species" to species,
        "type" to type,
        "gender" to gender
    )
}

data class EpisodeQuery(
    val page: Int = 1,
    val name: String = "",
    val code: String = ""
) {
    init {
        require(page > 0) { "Page must be positive" }
    }

    internal fun cacheKey(): String = cacheKey(
        resource = "episode",
        page = page,
        "name" to name,
        "episode" to code
    )
}

data class LocationQuery(
    val page: Int = 1,
    val name: String = "",
    val type: String = "",
    val dimension: String = ""
) {
    init {
        require(page > 0) { "Page must be positive" }
    }

    internal fun cacheKey(): String = cacheKey(
        resource = "location",
        page = page,
        "name" to name,
        "type" to type,
        "dimension" to dimension
    )
}

data class CachedPage<T>(
    val items: List<T>,
    val totalCount: Int,
    val totalPages: Int,
    val nextPage: Int?,
    val previousPage: Int?,
    val cachedAt: Long
)

data class RelatedItems<T>(
    val items: List<T>,
    val isComplete: Boolean
)

private fun cacheKey(
    resource: String,
    page: Int,
    vararg filters: Pair<String, String?>
): String = buildString {
    append(resource)
    append("|page=")
    append(page)
    filters.forEach { (name, rawValue) ->
        append('|')
        append(name)
        append('=')
        append(rawValue.normalized().encoded())
    }
}

internal fun String?.asApiFilter(): String? = normalized().ifBlank { null }

private fun String?.normalized(): String = orEmpty().trim()

private fun String.encoded(): String = URLEncoder.encode(this, StandardCharsets.UTF_8.name())

