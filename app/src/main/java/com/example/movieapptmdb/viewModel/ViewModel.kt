package com.example.movieapptmdb.viewModel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.movieapptmdb.constants.FilmType
import com.example.movieapptmdb.repository.Repo
import com.example.movieapptmdb.constants.Resource
import com.example.tmdb_sdk.services.responses.FilmResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {

    private var _filmGenres = mutableStateListOf(FilmResponse.Genre(null, "All"))
    val filmGenres: SnapshotStateList<FilmResponse.Genre> = _filmGenres

    var selectedGenre: MutableState<FilmResponse.Genre> = mutableStateOf(FilmResponse.Genre(null, "All"))
    var selectedFilmType: MutableState<FilmType> = mutableStateOf(FilmType.MOVIE)

    private var _trendingMovies = mutableStateOf<Flow<PagingData<FilmResponse.Film>>>(emptyFlow())
    val trendingMoviesState: State<Flow<PagingData<FilmResponse.Film>>> = _trendingMovies

    private var _popularFilms = mutableStateOf<Flow<PagingData<FilmResponse.Film>>>(emptyFlow())
    val popularFilmsState: State<Flow<PagingData<FilmResponse.Film>>> = _popularFilms

    private var _topRatedFilm = mutableStateOf<Flow<PagingData<FilmResponse.Film>>>(emptyFlow())
    val topRatedFilmState: State<Flow<PagingData<FilmResponse.Film>>> = _topRatedFilm

    private var _nowPlayingFilm = mutableStateOf<Flow<PagingData<FilmResponse.Film>>>(emptyFlow())
    val nowPlayingMoviesState: State<Flow<PagingData<FilmResponse.Film>>> = _nowPlayingFilm

    private var _upcomingFilms = mutableStateOf<Flow<PagingData<FilmResponse.Film>>>(emptyFlow())
    val upcomingMoviesState: State<Flow<PagingData<FilmResponse.Film>>> = _upcomingFilms


    init {
        refreshAll()
    }

    fun refreshAll(
        genreId: Int? = selectedGenre.value.id,
        filmType: FilmType = selectedFilmType.value
    ) {
        if (filmGenres.size == 1) {
            getFilmGenre(selectedFilmType.value)
        }
        if (genreId == null) {
            selectedGenre.value = FilmResponse.Genre(null, "All")
        }
        getTrendingFilms(genreId, filmType)
        getPopularFilms(genreId, filmType)
        getTopRatedFilm(genreId, filmType)
        getNowPlayingFilms(genreId, filmType)
        getUpcomingFilms(genreId)
    }

    fun filterBySetSelectedGenre(genre: FilmResponse.Genre) {
        selectedGenre.value = genre
        refreshAll(genre.id)
    }


    private fun getFilmGenre(filmType: FilmType = selectedFilmType.value) {
        viewModelScope.launch {
            val defaultGenre = FilmResponse.Genre(null, "All")
            when (val results = Repo.getMoviesGenre(filmType)) {
                is Resource.Success -> {
                    _filmGenres.clear()
                    _filmGenres.add(defaultGenre)
                    selectedGenre.value = defaultGenre
                    results.data?.genres?.forEach {
                        _filmGenres.add(it)
                    }
                }
                is Resource.Error -> {
                    Log.d("Error","Error loading Genres")
                }
                else -> { }
            }
        }
    }

    private fun getTrendingFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _trendingMovies.value = if (genreId != null) {
                Repo.getTrendingFilms(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds!!.contains(genreId)
                    }
                }.cachedIn(viewModelScope)
            } else {
                Repo.getTrendingFilms(filmType).cachedIn(viewModelScope)
            }
        }
    }

    private fun getUpcomingFilms(genreId: Int?) {
        viewModelScope.launch {
            _upcomingFilms.value = if (genreId != null) {
                Repo.getUpcomingTvShows().map { results ->
                    results.filter { movie ->
                        movie.genreIds!!.contains(genreId)
                    }
                }.cachedIn(viewModelScope)
            } else {
                Repo.getUpcomingTvShows().cachedIn(viewModelScope)
            }
        }
    }

    private fun getPopularFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _popularFilms.value = if (genreId != null) {
                Repo.getPopularFilms(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds!!.contains(genreId)
                    }
                }.cachedIn(viewModelScope)
            } else {
                Repo.getPopularFilms(filmType).cachedIn(viewModelScope)
            }
        }
    }

    private fun getNowPlayingFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _nowPlayingFilm.value = if (genreId != null) {
                Repo.getNowPlayingFilms(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds!!.contains(genreId)
                    }
                }.cachedIn(viewModelScope)
            } else {
                Repo.getNowPlayingFilms(filmType).cachedIn(viewModelScope)
            }
        }
    }

    private fun getTopRatedFilm(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _topRatedFilm.value = if (genreId != null) {
                Repo.getTopRatedFilm(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds!!.contains(genreId)
                    }
                }.cachedIn(viewModelScope)
            } else {
                Repo.getTopRatedFilm(filmType).cachedIn(viewModelScope)
            }
        }
    }
}

class ViewModelFactory(
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(com.example.movieapptmdb.viewModel.ViewModel::class.java)) {
            return com.example.movieapptmdb.viewModel.ViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}