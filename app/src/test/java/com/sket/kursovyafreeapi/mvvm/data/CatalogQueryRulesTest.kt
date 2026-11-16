package com.sket.kursovyafreeapi.mvvm.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Unit-тесты бизнес-правил поисковых запросов каталога.
 * Сгенерированы ИИ-агентом по промту docs/ai-prompts/01-unit-tests.md.
 */
class CatalogQueryRulesTest {

    @Test
    fun cacheKey_normalizesAndEncodesFilters_andBlankFilterIsNotSentToApi() {
        val query = CharacterQuery(name = "  Rick Sanchez ", status = "alive", species = "Human")

        val key = query.cacheKey()

        assertEquals(
            "character|page=1|name=Rick+Sanchez|status=alive|species=Human|type=|gender=",
            key
        )
        // Тот же запрос с лишними пробелами должен попадать в ту же запись кеша
        assertEquals(key, CharacterQuery(name = "Rick Sanchez", status = " alive ", species = "Human ").cacheKey())
        // Пустой фильтр не передаётся в API
        assertNull("   ".asApiFilter())
        assertEquals("Morty", " Morty ".asApiFilter())
    }

    @Test
    fun queries_rejectNonPositivePageNumber() {
        assertThrows(IllegalArgumentException::class.java) { CharacterQuery(page = 0) }
        assertThrows(IllegalArgumentException::class.java) { EpisodeQuery(page = -1) }
        assertThrows(IllegalArgumentException::class.java) { LocationQuery(page = 0) }
        assertEquals(3, EpisodeQuery(page = 3).page)
    }
}
