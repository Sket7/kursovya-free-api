package com.sket.kursovyafreeapi.clean.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sket.kursovyafreeapi.clean.domain.*
import com.sket.kursovyafreeapi.model.Character
import com.sket.kursovyafreeapi.model.Episode
import com.sket.kursovyafreeapi.model.EpisodeUserData
import com.sket.kursovyafreeapi.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailUiState<T, R>(val item: T? = null, val related: List<R> = emptyList(), val userData: EpisodeUserData? = null, val isLoading: Boolean = true, val isSaving: Boolean = false, val isOffline: Boolean = false, val message: String? = null)

class EpisodeDetailsViewModel(private val id: Int, private val details: GetEpisodeDetails, observeNote: ObserveEpisodeNote, private val saveNote: SaveEpisodeNote) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailUiState<Episode, Character>()); val state = mutableState.asStateFlow()
    init { viewModelScope.launch { observeNote(id).collect { mutableState.value = mutableState.value.copy(userData = it) } }; load() }
    fun retry() = load()
    fun save(watched: Boolean, rating: Int?, comment: String) { viewModelScope.launch { mutableState.value = mutableState.value.copy(isSaving = true); runCatching { saveNote(id, watched, rating, comment) }.onSuccess { mutableState.value = mutableState.value.copy(isSaving = false, message = "Сохранено в Room") }.onFailure { mutableState.value = mutableState.value.copy(isSaving = false, message = it.userMessage()) } } }
    private fun load() { viewModelScope.launch { val local = details.cached(id); if (local != null) show(local, true); runCatching { details.refresh(id) }.onSuccess { show(it, false) }.onFailure { mutableState.value = mutableState.value.copy(isLoading = false, isOffline = local != null, message = it.userMessage()) } } }
    private fun show(value: EpisodeDetails, offline: Boolean) { mutableState.value = mutableState.value.copy(item = value.episode, related = value.characters.items, isLoading = false, isOffline = offline || !value.characters.isComplete, message = null) }
}

class CharacterDetailsViewModel(private val id: Int, private val details: GetCharacterDetails) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailUiState<Character, Episode>()); val state = mutableState.asStateFlow(); init { load() }
    private fun load() { viewModelScope.launch { val local = details.cached(id); if (local != null) show(local, true); runCatching { details.refresh(id) }.onSuccess { show(it, false) }.onFailure { mutableState.value = mutableState.value.copy(isLoading = false, isOffline = local != null, message = it.userMessage()) } } }
    private fun show(value: CharacterDetails, offline: Boolean) { mutableState.value = DetailUiState(value.character, value.episodes.items, isLoading = false, isOffline = offline || !value.episodes.isComplete) }
}

class LocationDetailsViewModel(private val id: Int, private val details: GetLocationDetails) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailUiState<Location, Character>()); val state = mutableState.asStateFlow(); init { load() }
    private fun load() { viewModelScope.launch { val local = details.cached(id); if (local != null) show(local, true); runCatching { details.refresh(id) }.onSuccess { show(it, false) }.onFailure { mutableState.value = mutableState.value.copy(isLoading = false, isOffline = local != null, message = it.userMessage()) } } }
    private fun show(value: LocationDetails, offline: Boolean) { mutableState.value = DetailUiState(value.location, value.residents.items, isLoading = false, isOffline = offline || !value.residents.isComplete) }
}

data class CommentsUiState(val items: List<PersonalComment> = emptyList(), val isLoading: Boolean = true)
class CommentsViewModel(comments: ObservePersonalComments, private val delete: DeletePersonalComment) : ViewModel() {
    private val mutableState = MutableStateFlow(CommentsUiState()); val state = mutableState.asStateFlow(); init { viewModelScope.launch { comments().collect { mutableState.value = CommentsUiState(it, false) } } }
    fun delete(id: Int) { viewModelScope.launch { delete.invoke(id) } }
}
