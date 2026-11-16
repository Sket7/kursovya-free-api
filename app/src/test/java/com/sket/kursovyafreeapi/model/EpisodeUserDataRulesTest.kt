package com.sket.kursovyafreeapi.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Unit-тест инварианта доменной модели личной заметки к эпизоду.
 * Сгенерирован ИИ-агентом по промту docs/ai-prompts/01-unit-tests.md.
 */
class EpisodeUserDataRulesTest {

    @Test
    fun rating_mustBeNullOrBetweenOneAndFive() {
        assertNull(note(rating = null).rating)
        assertEquals(1, note(rating = 1).rating)
        assertEquals(5, note(rating = 5).rating)

        assertThrows(IllegalArgumentException::class.java) { note(rating = 0) }
        assertThrows(IllegalArgumentException::class.java) { note(rating = 6) }
    }

    private fun note(rating: Int?) = EpisodeUserData(
        episodeId = 1,
        isWatched = true,
        rating = rating,
        comment = "",
        updatedAt = 0L
    )
}
