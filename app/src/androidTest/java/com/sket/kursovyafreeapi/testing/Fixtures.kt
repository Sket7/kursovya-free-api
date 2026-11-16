package com.sket.kursovyafreeapi.testing

/** JSON-ответы в формате https://rickandmortyapi.com/documentation. */
object Fixtures {
    private const val BASE = "https://rickandmortyapi.com/api"

    fun character(id: Int, name: String, status: String, species: String, location: String) = """
        {"id":$id,"name":"$name","status":"$status","species":"$species","type":"","gender":"Male",
         "origin":{"name":"Earth (C-137)","url":"$BASE/location/1"},
         "location":{"name":"$location","url":"$BASE/location/3"},
         "image":"$BASE/character/avatar/$id.jpeg",
         "episode":["$BASE/episode/1","$BASE/episode/2"],
         "url":"$BASE/character/$id","created":"2017-11-04T18:48:46.250Z"}
    """.trimIndent()

    val RICK = character(1, "Rick Sanchez", "Alive", "Human", "Citadel of Ricks")
    val MORTY = character(2, "Morty Smith", "Alive", "Human", "Citadel of Ricks")

    val CHARACTERS_PAGE_1 = """
        {"info":{"count":826,"pages":42,"next":"$BASE/character?page=2","prev":null},
         "results":[$RICK,$MORTY]}
    """.trimIndent()

    val EPISODE_1 = """
        {"id":1,"name":"Pilot","air_date":"December 2, 2013","episode":"S01E01",
         "characters":["$BASE/character/1","$BASE/character/2"],
         "url":"$BASE/episode/1","created":"2017-11-10T12:56:33.798Z"}
    """.trimIndent()

    /** Маршрутизация по пути запроса относительно /api/. null → HTTP 404. */
    fun route(path: String): String? = when (path) {
        "character" -> CHARACTERS_PAGE_1
        "character/1" -> RICK
        "character/2" -> MORTY
        "character/1,2" -> "[$RICK,$MORTY]"
        "episode/1" -> EPISODE_1
        else -> null
    }
}
