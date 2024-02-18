package com.example.movieapptmdb.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.movieapptmdb.R
import com.example.movieapptmdb.viewModel.SearchViewModel
import com.example.movieapptmdb.viewModel.SearchViewModelFactory
import com.example.movieapptmdb.viewModel.ViewModel
import com.example.movieapptmdb.viewModel.ViewModelFactory
import com.example.movieapptmdb.constants.Constants.BASE_POSTER_IMAGE_URL
import com.example.movieapptmdb.ui.component.BackButton
import com.example.movieapptmdb.ui.component.SearchBar
import com.example.movieapptmdb.ui.component.SearchResultItem
import com.example.movieapptmdb.ui.theme.AppOnPrimaryColor
import com.example.movieapptmdb.ui.theme.AppPrimaryColor
import com.example.tmdb_sdk.services.responses.FilmResponse

@Composable
fun SearchScreen(
    onGoToDetails: (FilmResponse.Film)-> Unit,
    onNavUp: () -> Unit)
{
    val viewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory())
    val homeViewModel: ViewModel = viewModel(factory = ViewModelFactory())
    val searchResult = viewModel.multiSearchState.value.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppPrimaryColor)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 16.dp)
                .fillMaxWidth(fraction = 0.60F)
        ) {
            val focusManager = LocalFocusManager.current
            BackButton {
                focusManager.clearFocus()
                onNavUp.invoke()
            }

            Text(
                text = "Search",
                modifier = Modifier.padding(start = 50.dp),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = AppOnPrimaryColor
            )
        }

        SearchBar(
            autoFocus = true,
            onSearch = {
                viewModel.searchRemoteMovie(true)
            })

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            when (searchResult.loadState.refresh) {
                is LoadState.NotLoading -> {
                    items(searchResult) { film ->
                        val focus = LocalFocusManager.current
                        SearchResultItem(
                            title = film!!.title,
                            mediaType = film.mediaType,
                            posterImage = "$BASE_POSTER_IMAGE_URL/${film.posterPath}",
                            genres = homeViewModel.filmGenres.filter { genre ->
                                return@filter if (film.genreIds.isNullOrEmpty()) false else
                                    film.genreIds!!.contains(genre.id)
                            },
                            rating = (film.voteAverage ?: 0) as Double,
                            releaseYear = film.releaseDate,
                            onClick = {
                                val navFilm = FilmResponse.Film(
                                    adult = film.adult ?: false,
                                    backdropPath = film.backdropPath,
                                    posterPath = film.posterPath,
                                    genreIds = film.genreIds,
                                    genres = film.genres,
                                    mediaType = film.mediaType,
                                    id = film.id ?: 0,
                                    imdbId = film.imdbId,
                                    originalLanguage = film.originalLanguage ?: "",
                                    overview = film.overview ?: "",
                                    popularity = film.popularity ?: 0F.toDouble(),
                                    releaseDate = film.releaseDate ?: "",
                                    runtime = film.runtime,
                                    title = film.title ?: "",
                                    video = film.video ?: false,
                                    voteAverage = film.voteAverage ?: 0F.toDouble(),
                                    voteCount = film.voteCount ?: 0
                                )
                                focus.clearFocus()
                                onGoToDetails(navFilm)
                            })
                    }
                    if (searchResult.itemCount == 0) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.no_match_found),
                                    contentDescription = null
                                )
                            }

                        }
                    }
                }

                is LoadState.Loading -> item {
                    if (viewModel.searchParam.value.isNotEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                else -> item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_match_found),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}