package com.sket.kursovyafreeapi.data.remote.api

import com.sket.kursovyafreeapi.data.remote.dto.CharacterDto
import com.sket.kursovyafreeapi.data.remote.dto.EpisodeDto
import com.sket.kursovyafreeapi.data.remote.dto.LocationDto
import com.sket.kursovyafreeapi.data.remote.dto.PageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,
        @Query("gender") gender: String? = null
    ): PageDto<CharacterDto>

    @GET("character/{id}")
    suspend fun getCharacter(@Path("id") id: Int): CharacterDto

    @GET("character/{ids}")
    suspend fun getCharacters(@Path(value = "ids", encoded = true) ids: String): List<CharacterDto>

    @GET("location")
    suspend fun getLocations(
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("type") type: String? = null,
        @Query("dimension") dimension: String? = null
    ): PageDto<LocationDto>

    @GET("location/{id}")
    suspend fun getLocation(@Path("id") id: Int): LocationDto

    @GET("location/{ids}")
    suspend fun getLocations(@Path(value = "ids", encoded = true) ids: String): List<LocationDto>

    @GET("episode")
    suspend fun getEpisodes(
        @Query("page") page: Int? = null,
        @Query("name") name: String? = null,
        @Query("episode") episode: String? = null
    ): PageDto<EpisodeDto>

    @GET("episode/{id}")
    suspend fun getEpisode(@Path("id") id: Int): EpisodeDto

    @GET("episode/{ids}")
    suspend fun getEpisodes(@Path(value = "ids", encoded = true) ids: String): List<EpisodeDto>
}
