package com.sket.kursovyafreeapi.model

data class Episode(
    val id: Int,
    val name: String,
    val airDate: String,
    val code: String,
    val characterIds: List<Int>,
    val resourceUrl: String,
    val createdAt: String
)
