package com.sket.kursovyafreeapi.mvc.controller

import com.sket.kursovyafreeapi.mvc.model.CachedPage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CatalogScreenState<T, Q>(
    val query: Q,
    val items: List<T> = emptyList(),
    val page: Int = 1,
    val totalPages: Int = 1,
    val totalCount: Int = 0,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isFromCache: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty() && errorMessage == null
    val canGoBack: Boolean get() = page > 1 && !isLoading && !isRefreshing
    val canGoForward: Boolean get() = page < totalPages && !isLoading && !isRefreshing
}

class PagedCatalogController<T, Q>(
    initialQuery: Q,
    private val scope: CoroutineScope,
    private val pageOf: (Q) -> Int,
    private val withPage: (Q, Int) -> Q,
    private val readCache: suspend (Q) -> CachedPage<T>?,
    private val refresh: suspend (Q) -> CachedPage<T>
) {
    private val mutableState = MutableStateFlow(
        CatalogScreenState<T, Q>(query = initialQuery, page = pageOf(initialQuery))
    )
    val state: StateFlow<CatalogScreenState<T, Q>> = mutableState.asStateFlow()

    private var requestJob: Job? = null

    fun start() {
        if (requestJob == null && mutableState.value.items.isEmpty()) load(mutableState.value.query)
    }

    fun updateQuery(query: Q, debounce: Boolean = true) {
        load(withPage(query, 1), if (debounce) SEARCH_DEBOUNCE_MS else 0L)
    }

    fun retry() = load(mutableState.value.query)

    fun previousPage() {
        val current = mutableState.value
        if (current.canGoBack) load(withPage(current.query, current.page - 1))
    }

    fun nextPage() {
        val current = mutableState.value
        if (current.canGoForward) load(withPage(current.query, current.page + 1))
    }

    private fun load(query: Q, delayMs: Long = 0L) {
        requestJob?.cancel()
        requestJob = scope.launch {
            if (delayMs > 0) delay(delayMs)
            val cached = readCache(query)
            if (cached == null) {
                mutableState.value = CatalogScreenState(
                    query = query,
                    page = pageOf(query),
                    isLoading = true
                )
            } else {
                mutableState.value = cached.toState(
                    query = query,
                    page = pageOf(query),
                    isRefreshing = true,
                    isFromCache = true
                )
            }

            try {
                mutableState.value = refresh(query).toState(
                    query = query,
                    page = pageOf(query)
                )
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                val current = mutableState.value
                mutableState.value = current.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isFromCache = current.items.isNotEmpty(),
                    errorMessage = error.userMessage()
                )
            }
        }
    }

    private fun CachedPage<T>.toState(
        query: Q,
        page: Int,
        isRefreshing: Boolean = false,
        isFromCache: Boolean = false
    ) = CatalogScreenState(
        query = query,
        items = items,
        page = page,
        totalPages = totalPages.coerceAtLeast(1),
        totalCount = totalCount,
        isRefreshing = isRefreshing,
        isFromCache = isFromCache
    )

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 450L
    }
}

internal fun Throwable.userMessage(): String = when {
    message?.contains("404") == true -> "Ничего не найдено"
    else -> localizedMessage?.take(160) ?: "Не удалось загрузить данные"
}
