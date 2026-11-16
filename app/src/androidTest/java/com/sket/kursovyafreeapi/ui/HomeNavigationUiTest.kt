package com.sket.kursovyafreeapi.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sket.kursovyafreeapi.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI-тест главного экрана и навигации Navigation 3 на реальной MainActivity.
 * Сгенерирован ИИ-агентом по промту docs/ai-prompts/03-ui-tests.md.
 */
@RunWith(AndroidJUnit4::class)
class HomeNavigationUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeScreen_showsSections_andOpensCharactersCatalog() {
        composeRule.onNodeWithText("Мультивселенная ждёт").assertIsDisplayed()
        composeRule.onNodeWithText("Эпизоды").assertIsDisplayed()
        composeRule.onNodeWithText("Локации").assertIsDisplayed()

        composeRule.onNodeWithText("Персонажи").performClick()

        // Экран каталога персонажей отображает панель фильтров независимо от состояния сети
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Поиск по имени").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Поиск по имени").assertIsDisplayed()
        composeRule.onNodeWithText("Живы").assertIsDisplayed()
    }
}
