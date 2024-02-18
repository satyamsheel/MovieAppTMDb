package com.example.movieapptmdb.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.movieapptmdb.repository.Repo
import com.example.tmdb_sdk.services.responses.MultiSearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel()  {

    private var _multiSearch = mutableStateOf<Flow<PagingData<MultiSearchResponse.Search>>>(emptyFlow())
    val multiSearchState: State<Flow<PagingData<MultiSearchResponse.Search>>> = _multiSearch

    var searchParam = mutableStateOf("")
    var previousSearch = mutableStateOf("")

    init {
        searchParam.value = ""
    }

    fun searchRemoteMovie(includeAdult: Boolean) {
        viewModelScope.launch {
            if (searchParam.value.isNotEmpty()) {
                _multiSearch.value = Repo.multiSearch(
                    searchParams = searchParam.value,
                    includeAdult
                ).map { result ->
                    result.filter {
                        ((it.title != null || it.originalName != null || it.originalTitle != null) &&
                                (it.mediaType == "tv" || it.mediaType == "movie"))
                    }
                }.cachedIn(viewModelScope)
            }
        }
    }
}

class SearchViewModelFactory(
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}