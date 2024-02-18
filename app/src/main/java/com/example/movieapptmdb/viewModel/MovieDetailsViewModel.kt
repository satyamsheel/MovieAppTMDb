package com.example.movieapptmdb.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.movieapptmdb.constants.FilmType
import com.example.movieapptmdb.repository.Repo
import com.example.movieapptmdb.constants.Resource
import com.example.tmdb_sdk.services.responses.CastResponse
import com.example.tmdb_sdk.services.responses.FilmResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class MovieDetailsViewModel: ViewModel() {

    private var _similarFilms = mutableStateOf<Flow<PagingData<FilmResponse.Film>>>(emptyFlow())
    val similarMovies: State<Flow<PagingData<FilmResponse.Film>>> = _similarFilms

    private var _filmCast = mutableStateOf<List<CastResponse.Cast>>(emptyList())
    val filmCast: State<List<CastResponse.Cast>> = _filmCast


    fun getSimilarFilms(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            Repo.getSimilarFilms(filmId, filmType).also {
                _similarFilms.value = it
            }.cachedIn(viewModelScope)
        }
    }

    fun getFilmCast(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            Repo.getFilmCast(filmId = filmId, filmType).also {
                if (it is Resource.Success) {
                    _filmCast.value = it.data!!.castResult
                }
            }
        }
    }
}

class ViewModelFactoryDetails(
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieDetailsViewModel::class.java)) {
            return MovieDetailsViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}