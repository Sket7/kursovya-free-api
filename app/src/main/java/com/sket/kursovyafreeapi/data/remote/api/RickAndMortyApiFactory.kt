package com.sket.kursovyafreeapi.data.remote.api

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RickAndMortyApiFactory {
    const val BASE_URL = "https://rickandmortyapi.com/api/"

    private val defaultJson = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    fun create(
        client: OkHttpClient = OkHttpClient(),
        json: Json = defaultJson
    ): RickAndMortyApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RickAndMortyApi::class.java)
}
