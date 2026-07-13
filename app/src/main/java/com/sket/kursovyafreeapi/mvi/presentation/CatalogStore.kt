package com.sket.kursovyafreeapi.mvi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.mvi.data.CachedPage
import com.sket.kursovyafreeapi.mvi.data.CatalogRepository
import com.sket.kursovyafreeapi.mvi.data.CharacterQuery
import com.sket.kursovyafreeapi.mvi.data.EpisodeQuery
import com.sket.kursovyafreeapi.mvi.data.LocationQuery
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface CatalogIntent<out Q> {
    data class ChangeQuery<Q>(val query: Q, val debounce: Boolean = true) : CatalogIntent<Q>
    data object Retry : CatalogIntent<Nothing>
    data object PreviousPage : CatalogIntent<Nothing>
    data object NextPage : CatalogIntent<Nothing>
}

data class CatalogState<T, Q>(val query: Q, val items: List<T> = emptyList(), val page: Int = 1, val totalPages: Int = 1, val totalCount: Int = 0, val isLoading: Boolean = false, val isRefreshing: Boolean = false, val isFromCache: Boolean = false, val errorMessage: String? = null) {
    val isEmpty get() = !isLoading && items.isEmpty() && errorMessage == null
    val canGoBack get() = page > 1 && !isLoading && !isRefreshing
    val canGoForward get() = page < totalPages && !isLoading && !isRefreshing
}

private sealed interface CatalogMutation<out T, out Q> {
    data class Loading<Q>(val query: Q, val page: Int) : CatalogMutation<Nothing, Q>
    data class Content<T, Q>(val query: Q, val page: Int, val data: CachedPage<T>, val refreshing: Boolean, val cached: Boolean) : CatalogMutation<T, Q>
    data class Failed(val message: String) : CatalogMutation<Nothing, Nothing>
}

open class CatalogStore<T, Q>(initial: Q, private val pageOf: (Q) -> Int, private val withPage: (Q, Int) -> Q, private val cached: suspend (Q) -> CachedPage<T>?, private val refresh: suspend (Q) -> CachedPage<T>) : ViewModel() {
    private val mutableState = MutableStateFlow(CatalogState<T, Q>(initial, page = pageOf(initial))); val state = mutableState.asStateFlow(); private var job: Job? = null
    init { load(initial) }
    fun accept(intent: CatalogIntent<Q>) { when (intent) { is CatalogIntent.ChangeQuery -> load(withPage(intent.query, 1), if (intent.debounce) 450 else 0); CatalogIntent.Retry -> load(mutableState.value.query); CatalogIntent.PreviousPage -> mutableState.value.takeIf { it.canGoBack }?.let { load(withPage(it.query, it.page - 1)) }; CatalogIntent.NextPage -> mutableState.value.takeIf { it.canGoForward }?.let { load(withPage(it.query, it.page + 1)) } } }
    private fun load(query: Q, wait: Long = 0) { job?.cancel(); job = viewModelScope.launch { if (wait > 0) delay(wait); val local = cached(query); dispatch(local?.let { CatalogMutation.Content(query, pageOf(query), it, true, true) } ?: CatalogMutation.Loading(query, pageOf(query))); try { dispatch(CatalogMutation.Content(query, pageOf(query), refresh(query), false, false)) } catch (e: Throwable) { if (e is CancellationException) throw e; dispatch(CatalogMutation.Failed(e.userMessage())) } } }
    private fun dispatch(mutation: CatalogMutation<T, Q>) { mutableState.value = reduce(mutableState.value, mutation) }
    private fun reduce(old: CatalogState<T, Q>, mutation: CatalogMutation<T, Q>): CatalogState<T, Q> = when (mutation) {
        is CatalogMutation.Loading -> CatalogState(mutation.query, page = mutation.page, isLoading = true)
        is CatalogMutation.Content -> CatalogState(mutation.query, mutation.data.items, mutation.page, mutation.data.totalPages.coerceAtLeast(1), mutation.data.totalCount, isRefreshing = mutation.refreshing, isFromCache = mutation.cached)
        is CatalogMutation.Failed -> old.copy(isLoading = false, isRefreshing = false, isFromCache = old.items.isNotEmpty(), errorMessage = mutation.message)
    }
}

class EpisodesStore(repo: CatalogRepository) : CatalogStore<Episode, EpisodeQuery>(EpisodeQuery(), EpisodeQuery::page, { q, p -> q.copy(page = p) }, repo::cachedEpisodes, repo::refreshEpisodes)
class CharactersStore(repo: CatalogRepository) : CatalogStore<Character, CharacterQuery>(CharacterQuery(), CharacterQuery::page, { q, p -> q.copy(page = p) }, repo::cachedCharacters, repo::refreshCharacters)
class LocationsStore(repo: CatalogRepository) : CatalogStore<Location, LocationQuery>(LocationQuery(), LocationQuery::page, { q, p -> q.copy(page = p) }, repo::cachedLocations, repo::refreshLocations)

internal fun Throwable.userMessage() = if (message?.contains("404") == true) "Ничего не найдено" else localizedMessage?.take(160) ?: "Не удалось загрузить данные"
