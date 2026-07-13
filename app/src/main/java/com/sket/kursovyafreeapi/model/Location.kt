package com.sket.kursovyafreeapi.model

data class Location(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residentIds: List<Int>,
    val resourceUrl: String,
    val createdAt: String
)
