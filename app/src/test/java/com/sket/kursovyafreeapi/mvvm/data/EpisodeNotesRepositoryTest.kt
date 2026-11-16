package com.sket.kursovyafreeapi.mvvm.data

import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.data.local.dao.EpisodeUserDataDao
import com.sket.kursovyafreeapi.data.local.entity.EpisodeUserDataEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit-тест бизнес-правил сохранения личной заметки (оценка, комментарий).
 * Room изолирована моками MockK. Сгенерирован ИИ-агентом по промту docs/ai-prompts/01-unit-tests.md.
 */
class EpisodeNotesRepositoryTest {

    private val dao = mockk<EpisodeUserDataDao>(relaxed = true)
    private val database = mockk<RickAndMortyDatabase> { every { episodeUserDataDao() } returns dao }
    private val repository = EpisodeNotesRepository(database, now = { 1_700_000_000_000L })

    @Test
    fun save_trimsAndLimitsComment_andRejectsInvalidRating() = runTest {
        val saved = slot<EpisodeUserDataEntity>()
        coEvery { dao.upsert(capture(saved)) } returns Unit
        val longComment = "  " + "а".repeat(EpisodeNotesRepository.MAX_COMMENT_LENGTH + 50) + "  "

        repository.save(episodeId = 7, isWatched = true, rating = 5, comment = longComment)

        assertEquals(7, saved.captured.episodeId)
        assertEquals(5, saved.captured.rating)
        assertEquals(EpisodeNotesRepository.MAX_COMMENT_LENGTH, saved.captured.comment.length)
        assertTrue(saved.captured.comment.all { it == 'а' })
        assertEquals(1_700_000_000_000L, saved.captured.updatedAt)

        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking { repository.save(7, true, rating = 6, comment = "") }
        }
        coVerify(exactly = 1) { dao.upsert(any()) }
    }
}
