package com.sket.kursovyafreeapi.mvvm.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.mvvm.data.CachedPage
import com.sket.kursovyafreeapi.mvvm.data.CatalogRepository
import com.sket.kursovyafreeapi.mvvm.data.CharacterQuery
import com.sket.kursovyafreeapi.mvvm.data.EpisodeQuery
import com.sket.kursovyafreeapi.mvvm.data.LocationQuery
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CatalogUiState<T, Q>(val query: Q, val items: List<T> = emptyList(), val page: Int = 1, val totalPages: Int = 1, val totalCount: Int = 0, val loading: Boolean = false, val refreshing: Boolean = false, val fromCache: Boolean = false, val error: String? = null) {
    val empty get() = !loading && items.isEmpty() && error == null
    val canBack get() = page > 1 && !loading && !refreshing
    val canForward get() = page < totalPages && !loading && !refreshing
}

open class CatalogViewModel<T, Q>(initial: Q, private val pageOf: (Q) -> Int, private val withPage: (Q, Int) -> Q, private val cached: suspend (Q) -> CachedPage<T>?, private val refresh: suspend (Q) -> CachedPage<T>) : ViewModel() {
    private val mutableState = MutableStateFlow(CatalogUiState<T, Q>(initial, page = pageOf(initial)))
    val state = mutableState.asStateFlow(); private var job: Job? = null
    init { load(initial) }
    fun update(query: Q, debounce: Boolean = true) = load(withPage(query, 1), if (debounce) 450 else 0)
    fun retry() = load(mutableState.value.query)
    fun previous() { mutableState.value.takeIf { it.canBack }?.let { load(withPage(it.query, it.page - 1)) } }
    fun next() { mutableState.value.takeIf { it.canForward }?.let { load(withPage(it.query, it.page + 1)) } }
    private fun load(query: Q, wait: Long = 0) { job?.cancel(); job = viewModelScope.launch { if (wait > 0) delay(wait); val local = cached(query); mutableState.value = local?.state(query, pageOf(query), refreshing = true, fromCache = true) ?: CatalogUiState(query, page = pageOf(query), loading = true); try { mutableState.value = refresh(query).state(query, pageOf(query)) } catch (e: Throwable) { if (e is CancellationException) throw e; mutableState.value = mutableState.value.copy(loading = false, refreshing = false, fromCache = mutableState.value.items.isNotEmpty(), error = e.userMessage()) } } }
    private fun CachedPage<T>.state(query: Q, page: Int, refreshing: Boolean = false, fromCache: Boolean = false) = CatalogUiState(query, items, page, totalPages.coerceAtLeast(1), totalCount, refreshing = refreshing, fromCache = fromCache)
}

class EpisodesViewModel(repo: CatalogRepository) : CatalogViewModel<Episode, EpisodeQuery>(EpisodeQuery(), EpisodeQuery::page, { q, p -> q.copy(page = p) }, repo::cachedEpisodes, repo::refreshEpisodes)
class CharactersViewModel(repo: CatalogRepository) : CatalogViewModel<Character, CharacterQuery>(CharacterQuery(), CharacterQuery::page, { q, p -> q.copy(page = p) }, repo::cachedCharacters, repo::refreshCharacters)
class LocationsViewModel(repo: CatalogRepository) : CatalogViewModel<Location, LocationQuery>(LocationQuery(), LocationQuery::page, { q, p -> q.copy(page = p) }, repo::cachedLocations, repo::refreshLocations)

internal fun Throwable.userMessage() = if (message?.contains("404") == true) "Ничего не найдено" else localizedMessage?.take(160) ?: "Не удалось загрузить данные"
