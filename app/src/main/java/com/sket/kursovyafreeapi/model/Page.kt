package com.sket.kursovyafreeapi.model

data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val previous: String?
)

data class Page<T>(
    val info: PageInfo,
    val items: List<T>
)
