package com.sket.kursovyafreeapi.data.remote.api

fun Iterable<Int>.toResourceIdsPath(): String =
    distinct().joinToString(",").also { ids ->
        require(ids.isNotEmpty()) { "At least one resource ID is required" }
    }
