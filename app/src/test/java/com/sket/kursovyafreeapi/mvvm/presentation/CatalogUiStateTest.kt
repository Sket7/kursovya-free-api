package com.sket.kursovyafreeapi.mvvm.presentation

import com.sket.kursovyafreeapi.mvvm.data.CharacterQuery
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit-тест правил пагинации и пустого состояния каталога.
 * Сгенерирован ИИ-агентом по промту docs/ai-prompts/01-unit-tests.md.
 */
class CatalogUiStateTest {

    @Test
    fun paginationFlags_dependOnPageAndLoadingState() {
        val first = CatalogUiState<String, CharacterQuery>(CharacterQuery(), items = listOf("Rick"), page = 1, totalPages = 3)
        assertFalse(first.canGoBack)
        assertTrue(first.canGoForward)

        val middle = first.copy(page = 2)
        assertTrue(middle.canGoBack)
        assertTrue(middle.canGoForward)

        val last = first.copy(page = 3)
        assertTrue(last.canGoBack)
        assertFalse(last.canGoForward)

        // Во время загрузки и фонового обновления листать нельзя
        assertFalse(middle.copy(isLoading = true).canGoForward)
        assertFalse(middle.copy(isRefreshing = true).canGoBack)

        // «Ничего не найдено» показывается только без загрузки, данных и ошибки
        val empty = CatalogUiState<String, CharacterQuery>(CharacterQuery())
        assertTrue(empty.isEmpty)
        assertFalse(empty.copy(isLoading = true).isEmpty)
        assertFalse(empty.copy(errorMessage = "Нет сети").isEmpty)
    }
}
