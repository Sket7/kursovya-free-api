package com.sket.kursovyafreeapi.testing

import android.content.Context
import androidx.room.Room
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApi
import com.sket.kursovyafreeapi.data.remote.api.RickAndMortyApiFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

/**
 * Поддельный backend The Rick and Morty API для детерминированных тестов.
 *
 * Перехватчик OkHttp отвечает заранее подготовленным JSON вместо реальной сети, поэтому через тест
 * проходит настоящий стек data-слоя: Retrofit → kotlinx.serialization → DTO → mapper → Room.
 */
class FakeRickAndMortyBackend : Interceptor {
    /** Если true — имитируется отсутствие сети (IOException). */
    @Volatile var offline: Boolean = false

    /** Количество выполненных запросов, удобно для проверок. */
    @Volatile var requestCount: Int = 0
        private set

    override fun intercept(chain: Interceptor.Chain): Response {
        requestCount++
        if (offline) throw IOException("Нет подключения к сети")
        val request = chain.request()
        val path = request.url.encodedPath.removePrefix("/api/")
        val body = Fixtures.route(path)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(if (body != null) 200 else 404)
            .message(if (body != null) "OK" else "Not Found")
            .body((body ?: """{"error":"There is nothing here"}""").toResponseBody(JSON))
            .build()
    }

    fun api(): RickAndMortyApi =
        RickAndMortyApiFactory.create(client = OkHttpClient.Builder().addInterceptor(this).build())

    companion object {
        private val JSON = "application/json".toMediaType()

        fun inMemoryDatabase(context: Context): RickAndMortyDatabase =
            Room.inMemoryDatabaseBuilder(context, RickAndMortyDatabase::class.java).build()
    }
}
