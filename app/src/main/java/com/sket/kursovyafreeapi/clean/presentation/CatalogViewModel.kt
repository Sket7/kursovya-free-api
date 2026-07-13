package com.sket.kursovyafreeapi.clean.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.clean.domain.CachedPage
import com.sket.kursovyafreeapi.clean.domain.LoadCharacters
import com.sket.kursovyafreeapi.clean.domain.LoadEpisodes
import com.sket.kursovyafreeapi.clean.domain.LoadLocations
import com.sket.kursovyafreeapi.clean.domain.CharacterQuery
import com.sket.kursovyafreeapi.clean.domain.EpisodeQuery
import com.sket.kursovyafreeapi.clean.domain.LocationQuery
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CatalogUiState<T, Q>(val query: Q, val items: List<T> = emptyList(), val page: Int = 1, val totalPages: Int = 1, val totalCount: Int = 0, val isLoading: Boolean = false, val isRefreshing: Boolean = false, val isFromCache: Boolean = false, val errorMessage: String? = null) {
    val isEmpty get() = !isLoading && items.isEmpty() && errorMessage == null
    val canGoBack get() = page > 1 && !isLoading && !isRefreshing
    val canGoForward get() = page < totalPages && !isLoading && !isRefreshing
}

open class CatalogViewModel<T, Q>(initial: Q, private val pageOf: (Q) -> Int, private val withPage: (Q, Int) -> Q, private val cached: suspend (Q) -> CachedPage<T>?, private val refresh: suspend (Q) -> CachedPage<T>) : ViewModel() {
    private val mutableState = MutableStateFlow(CatalogUiState<T, Q>(initial, page = pageOf(initial)))
    val state = mutableState.asStateFlow(); private var job: Job? = null
    init { load(initial) }
    fun updateQuery(query: Q, debounce: Boolean = true) = load(withPage(query, 1), if (debounce) 450 else 0)
    fun retry() = load(mutableState.value.query)
    fun previousPage() { mutableState.value.takeIf { it.canGoBack }?.let { load(withPage(it.query, it.page - 1)) } }
    fun nextPage() { mutableState.value.takeIf { it.canGoForward }?.let { load(withPage(it.query, it.page + 1)) } }
    private fun load(query: Q, wait: Long = 0) { job?.cancel(); job = viewModelScope.launch { if (wait > 0) delay(wait); val local = cached(query); mutableState.value = local?.state(query, pageOf(query), isRefreshing = true, isFromCache = true) ?: CatalogUiState(query, page = pageOf(query), isLoading = true); try { mutableState.value = refresh(query).state(query, pageOf(query)) } catch (e: Throwable) { if (e is CancellationException) throw e; mutableState.value = mutableState.value.copy(isLoading = false, isRefreshing = false, isFromCache = mutableState.value.items.isNotEmpty(), errorMessage = e.userMessage()) } } }
    private fun CachedPage<T>.state(query: Q, page: Int, isRefreshing: Boolean = false, isFromCache: Boolean = false) = CatalogUiState(query, items, page, totalPages.coerceAtLeast(1), totalCount, isRefreshing = isRefreshing, isFromCache = isFromCache)
}

class EpisodesViewModel(useCase: LoadEpisodes) : CatalogViewModel<Episode, EpisodeQuery>(EpisodeQuery(), EpisodeQuery::page, { q, p -> q.copy(page = p) }, useCase::cached, useCase::refresh)
class CharactersViewModel(useCase: LoadCharacters) : CatalogViewModel<Character, CharacterQuery>(CharacterQuery(), CharacterQuery::page, { q, p -> q.copy(page = p) }, useCase::cached, useCase::refresh)
class LocationsViewModel(useCase: LoadLocations) : CatalogViewModel<Location, LocationQuery>(LocationQuery(), LocationQuery::page, { q, p -> q.copy(page = p) }, useCase::cached, useCase::refresh)

internal fun Throwable.userMessage() = if (message?.contains("404") == true) "Ничего не найдено" else localizedMessage?.take(160) ?: "Не удалось загрузить данные"

