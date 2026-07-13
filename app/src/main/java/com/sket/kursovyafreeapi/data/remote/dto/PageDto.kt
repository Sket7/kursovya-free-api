package com.sket.kursovyafreeapi.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageDto<T>(
    val info: PageInfoDto,
    val results: List<T>
)

@Serializable
data class PageInfoDto(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    @SerialName("prev")
    val previous: String? = null
)

@Serializable
data class NamedUrlDto(
    val name: String,
    val url: String
)
