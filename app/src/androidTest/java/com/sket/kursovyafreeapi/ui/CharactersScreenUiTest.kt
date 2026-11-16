package com.sket.kursovyafreeapi.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sket.kursovyafreeapi.data.local.RickAndMortyDatabase
import com.sket.kursovyafreeapi.mvvm.data.CatalogRepository
import com.sket.kursovyafreeapi.mvvm.presentation.CharactersViewModel
import com.sket.kursovyafreeapi.mvvm.view.CharactersView
import com.sket.kursovyafreeapi.testing.FakeRickAndMortyBackend
import com.sket.kursovyafreeapi.ui.theme.RickAndMortyAppTheme
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI-тест экрана каталога персонажей на детерминированных данных поддельного API.
 * Сгенерирован ИИ-агентом по промту docs/ai-prompts/03-ui-tests.md.
 */
@RunWith(AndroidJUnit4::class)
class CharactersScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var database: RickAndMortyDatabase

    @Before
    fun setUp() {
        database = FakeRickAndMortyBackend.inMemoryDatabase(ApplicationProvider.getApplicationContext())
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun charactersList_showsCardsAndPagination_andOpensDetailsOnClick() {
        val viewModel = CharactersViewModel(CatalogRepository(FakeRickAndMortyBackend().api(), database))
        var openedId: Int? = null

        composeRule.setContent {
            RickAndMortyAppTheme(darkTheme = false) {
                CharactersView(viewModel = viewModel, onBack = {}, onOpen = { openedId = it })
            }
        }

        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodesWithText("Rick Sanchez").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("826 героев").assertIsDisplayed()
        composeRule.onNodeWithText("Rick Sanchez").assertIsDisplayed()
        composeRule.onNodeWithText("Morty Smith").assertIsDisplayed()
        composeRule.onNodeWithText("1 из 42").assertIsDisplayed()
        composeRule.onNodeWithText("← Назад").assertIsNotEnabled()
        composeRule.onNodeWithText("Далее →").assertIsEnabled()

        composeRule.onNodeWithText("Rick Sanchez").performClick()
        composeRule.runOnIdle { assertEquals(1, openedId) }
    }
}
