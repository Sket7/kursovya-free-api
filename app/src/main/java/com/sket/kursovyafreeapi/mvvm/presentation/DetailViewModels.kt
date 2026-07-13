package com.sket.kursovyafreeapi.mvvm.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.EpisodeUserData
import com.sket.kursovyafreeapi.model.Location
import com.sket.kursovyafreeapi.mvvm.data.CatalogRepository
import com.sket.kursovyafreeapi.mvvm.data.EpisodeNotesRepository
import com.sket.kursovyafreeapi.mvvm.data.PersonalComment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState<T, R>(val item: T? = null, val related: List<R> = emptyList(), val userData: EpisodeUserData? = null, val loading: Boolean = true, val saving: Boolean = false, val offline: Boolean = false, val message: String? = null)

class EpisodeDetailsViewModel(private val id: Int, private val catalog: CatalogRepository, private val notes: EpisodeNotesRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailUiState<Episode, Character>()); val state = mutableState.asStateFlow()
    init { viewModelScope.launch { notes.observe(id).collect { mutableState.value = mutableState.value.copy(userData = it) } }; viewModelScope.launch { val local = catalog.cachedEpisode(id); if (local != null) show(local, true); runCatching { catalog.refreshEpisode(id) }.onSuccess { show(it, false) }.onFailure { mutableState.value = mutableState.value.copy(loading = false, offline = local != null, message = it.userMessage()) } } }
    fun save(watched: Boolean, rating: Int?, comment: String) { viewModelScope.launch { mutableState.value = mutableState.value.copy(saving = true); runCatching { notes.save(id, watched, rating, comment) }.onSuccess { mutableState.value = mutableState.value.copy(saving = false, message = "Сохранено в Room") }.onFailure { mutableState.value = mutableState.value.copy(saving = false, message = it.userMessage()) } } }
    private suspend fun show(value: Episode, offline: Boolean) { val related = catalog.resolveCharacters(value.characterIds); mutableState.value = mutableState.value.copy(item = value, related = related.items, loading = false, offline = offline || !related.isComplete, message = null) }
}

class CharacterDetailsViewModel(private val id: Int, private val repo: CatalogRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailUiState<Character, Episode>()); val state = mutableState.asStateFlow()
    init { viewModelScope.launch { val local = repo.cachedCharacter(id); if (local != null) show(local, true); runCatching { repo.refreshCharacter(id) }.onSuccess { show(it, false) }.onFailure { mutableState.value = mutableState.value.copy(loading = false, offline = local != null, message = it.userMessage()) } } }
    private suspend fun show(value: Character, offline: Boolean) { val related = repo.resolveEpisodes(value.episodeIds); mutableState.value = DetailUiState(value, related.items, loading = false, offline = offline || !related.isComplete) }
}

class LocationDetailsViewModel(private val id: Int, private val repo: CatalogRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailUiState<Location, Character>()); val state = mutableState.asStateFlow()
    init { viewModelScope.launch { val local = repo.cachedLocation(id); if (local != null) show(local, true); runCatching { repo.refreshLocation(id) }.onSuccess { show(it, false) }.onFailure { mutableState.value = mutableState.value.copy(loading = false, offline = local != null, message = it.userMessage()) } } }
    private suspend fun show(value: Location, offline: Boolean) { val related = repo.resolveCharacters(value.residentIds); mutableState.value = DetailUiState(value, related.items, loading = false, offline = offline || !related.isComplete) }
}

class CommentsViewModel(private val notes: EpisodeNotesRepository) : ViewModel() {
    private val mutableState = MutableStateFlow<List<PersonalComment>>(emptyList()); val state = mutableState.asStateFlow()
    init { viewModelScope.launch { notes.observePersonalComments().collect { mutableState.value = it } } }
    fun delete(id: Int) { viewModelScope.launch { notes.deleteComment(id) } }
}
