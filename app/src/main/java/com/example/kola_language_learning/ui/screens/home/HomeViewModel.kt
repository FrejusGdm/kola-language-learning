package com.example.kola_language_learning.ui.screens.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// ui/screens/home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    data class HomeUiState(
        val userName: String = "",
        val selectedLanguage: String = "",
        val topics: List<TopicCard> = emptyList()
    )

    data class TopicCard(
        val title: String,
        val subtitle: String,
        val imageRes: Int
    )
}