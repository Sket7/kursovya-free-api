package com.sket.kursovyafreeapi.integration

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.mvvm.data.CatalogRepository
import com.sket.kursovyafreeapi.mvvm.data.CharacterQuery
import com.sket.kursovyafreeapi.mvvm.data.EpisodeNotesRepository
import com.sket.kursovyafreeapi.mvvm.presentation.CharactersViewModel
import com.sket.kursovyafreeapi.mvvm.presentation.EpisodeDetailsViewModel
import com.sket.kursovyafreeapi.testing.FakeRickAndMortyBackend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Интеграционные тесты взаимодействия слоёв data – domain – view (ViewModel).
 *
 * Реальные: Retrofit + kotlinx.serialization, Room (in-memory), CatalogRepository,
 * EpisodeNotesRepository, ViewModel. Подменён только сетевой транспорт (FakeRickAndMortyBackend).
 * Сгенерированы ИИ-агентом по промту docs/ai-prompts/02-integration-tests.md.
 */
@RunWith(AndroidJUnit4::class)
class DataDomainViewIntegrationTest {

    private lateinit var backend: FakeRickAndMortyBackend
    private lateinit var database: RickAndMortyDatabase
    private lateinit var catalog: CatalogRepository

    @Before
    fun setUp() {
        backend = FakeRickAndMortyBackend()
        database = FakeRickAndMortyBackend.inMemoryDatabase(ApplicationProvider.getApplicationContext())
        catalog = CatalogRepository(backend.api(), database)
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun charactersPage_flowsFromApiThroughRepositoryIntoViewModelState() = runBlocking {
        val viewModel = CharactersViewModel(catalog)

        val state = withTimeout(TIMEOUT) {
            viewModel.state.first { !it.isLoading && !it.isRefreshing && it.items.isNotEmpty() }
        }

        assertEquals(listOf("Rick Sanchez", "Morty Smith"), state.items.map { it.name })
        assertEquals(826, state.totalCount)
        assertEquals(42, state.totalPages)
        assertTrue(state.canGoForward)
        assertFalse(state.isFromCache)
        assertNull(state.errorMessage)
        // Страница сохранена в Room вместе с порядком элементов
        val cached = catalog.cachedCharacters(CharacterQuery())
        assertEquals(listOf(1, 2), cached?.items?.map { it.id })
    }

    @Test
    fun whenNetworkFails_viewModelShowsCachedPageAndError() = runBlocking {
        // 1. Первый запуск онлайн наполняет кеш Room
        val online = CharactersViewModel(catalog)
        withTimeout(TIMEOUT) { online.state.first { it.items.isNotEmpty() && !it.isRefreshing } }

        // 2. Сеть пропала — новый экран должен показать кеш и сообщение об ошибке
        backend.offline = true
        val offline = CharactersViewModel(catalog)
        val state = withTimeout(TIMEOUT) { offline.state.first { it.errorMessage != null } }

        assertEquals(listOf("Rick Sanchez", "Morty Smith"), state.items.map { it.name })
        assertTrue(state.isFromCache)
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
    }

    @Test
    fun episodeDetails_loadsRelatedCharacters_andSavesPersonalNote() = runBlocking {
        val notes = EpisodeNotesRepository(database)
        val viewModel = EpisodeDetailsViewModel(id = 1, catalog = catalog, notes = notes)

        val loaded = withTimeout(TIMEOUT) { viewModel.state.first { it.item != null && !it.isLoading } }
        assertEquals("Pilot", loaded.item?.name)
        assertEquals("S01E01", loaded.item?.code)
        assertEquals(listOf("Rick Sanchez", "Morty Smith"), loaded.related.map { it.name })
        assertFalse(loaded.isOffline)

        viewModel.save(watched = true, rating = 5, comment = "  Отличный пилот  ")

        val saved = withTimeout(TIMEOUT) {
            viewModel.state.first { it.userData?.rating == 5 && it.message == "Сохранено в Room" }
        }
        assertNotNull(saved.userData)
        assertEquals("Отличный пилот", saved.userData?.comment)
        assertTrue(saved.userData!!.isWatched)
        assertEquals("Отличный пилот", notes.observe(1).first()?.comment)
    }

    private companion object {
        const val TIMEOUT = 10_000L
    }
}
