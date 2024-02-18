package com.example.movieapptmdb.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.paging.compose.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.movieapptmdb.constants.FilmType
import com.example.movieapptmdb.viewModel.ViewModel
import com.example.movieapptmdb.viewModel.ViewModelFactory
import com.example.movieapptmdb.R
import com.example.movieapptmdb.constants.Constants.BASE_BACKDROP_IMAGE_URL
import com.example.movieapptmdb.constants.Constants.BASE_POSTER_IMAGE_URL
import com.example.movieapptmdb.ui.component.LoopReverseLottieLoader
import com.example.movieapptmdb.ui.theme.AppOnPrimaryColor
import com.example.movieapptmdb.ui.theme.AppPrimaryColor
import com.example.movieapptmdb.ui.theme.ButtonColor
import com.example.tmdb_sdk.services.responses.FilmResponse
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage
import retrofit2.HttpException
import java.io.IOException

@Composable
fun Home(
    onGoToDetails: (FilmResponse.Film)-> Unit,
    onGotToSearch: ()-> Unit,
) {
    val viewModel: ViewModel = viewModel(
        factory = ViewModelFactory()
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopSearchBar(onGotToSearch)
        NestedScroll(onGoToDetails,viewModel)
    }
}


@Composable
fun TopSearchBar(onGotToSearch: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 4.dp, end = 8.dp)
            .padding(5.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column{
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text =  "Movies",
                        fontWeight = FontWeight.Bold ,
                        fontSize = 20.sp,
                        color = Color.LightGray.copy(alpha = 0.78F),
                        modifier = Modifier
                            .padding(top = 8.dp)
                    )
            }
        }

        IconButton(
            onClick = {
                onGotToSearch.invoke()
            }
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "search icon",
                tint = AppOnPrimaryColor
            )
        }
    }
}

@Composable
fun NestedScroll(
    onGoToDetails: (FilmResponse.Film) -> Unit,
    viewModel: ViewModel
) {

    val trendingFilms = viewModel.trendingMoviesState.value.collectAsLazyPagingItems()
    val popularFilms = viewModel.popularFilmsState.value.collectAsLazyPagingItems()
    val topRatedFilms = viewModel.topRatedFilmState.value.collectAsLazyPagingItems()
    val nowPlayingFilms = viewModel.nowPlayingMoviesState.value.collectAsLazyPagingItems()
    val upcomingMovies = viewModel.upcomingMoviesState.value.collectAsLazyPagingItems()
    val selectedFilmType = viewModel.selectedFilmType.value

    val listState: LazyListState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .fillMaxSize()
    ) {
        item {
            Text(
                text = "Genres",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(all = 4.dp)
            )
        }
        item {
            val genres = viewModel.filmGenres
            LazyRow(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                items(count = genres.size) {
                    SelectableGenreChip(
                        genre = genres[it].name,
                        selected = genres[it].name == viewModel.selectedGenre.value.name,
                        onclick = {
                            if (viewModel.selectedGenre.value.name != genres[it].name) {
                                viewModel.selectedGenre.value = genres[it]
                                viewModel.filterBySetSelectedGenre(genre = genres[it])
                            }
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = "Trending",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }
        item {
            ScrollableMovieItems(
                landscape = true,
                navigator = onGoToDetails,
                pagingItems = trendingFilms,
                selectedFilmType = selectedFilmType,
                onErrorClick = {
                    viewModel.refreshAll()
                }
            )
        }

        item {
            Text(
                text = "Popular",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 6.dp)
            )
        }
        item {
            ScrollableMovieItems(
                navigator = onGoToDetails,
                pagingItems = popularFilms,
                selectedFilmType = selectedFilmType,
                onErrorClick = {
                    viewModel.refreshAll()
                }
            )
        }

        item {
            Text(
                text = "Top Rated",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 14.dp, bottom = 8.dp)
            )
        }
        item {
            ScrollableMovieItems(
                navigator = onGoToDetails,
                pagingItems = topRatedFilms,
                selectedFilmType = selectedFilmType,
                onErrorClick = {
                    viewModel.refreshAll()
                }
            )
        }

        item {
            Text(
                text = "Now Playing",
                fontSize = 24.sp,
                color = AppOnPrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 14.dp, bottom = 4.dp)
            )
        }
        item {
            ScrollableMovieItems(
                navigator = onGoToDetails,
                pagingItems = nowPlayingFilms,
                selectedFilmType = selectedFilmType,
                onErrorClick = {
                    viewModel.refreshAll()
                }
            )
        }

        if (viewModel.selectedFilmType.value == FilmType.MOVIE) {
            item {
                Text(
                    text = "Upcoming",
                    fontSize = 24.sp,
                    color = AppOnPrimaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, top = 14.dp, bottom = 4.dp)
                )
            }

            item {
                ScrollableMovieItems(
                    navigator = onGoToDetails,
                    pagingItems = upcomingMovies,
                    selectedFilmType = selectedFilmType,
                    onErrorClick = {
                        viewModel.refreshAll()
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MovieItem(
    imageUrl: String,
    title: String,
    modifier: Modifier,
    landscape: Boolean,
    onclick: () -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(all = 4.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onclick()
            },
        horizontalAlignment = Alignment.Start
    ) {
        CoilImage(
            imageModel = imageUrl,
            shimmerParams = ShimmerParams(
                baseColor = AppPrimaryColor,
                highlightColor = ButtonColor,
                durationMillis = 500,
                dropOff = 0.65F,
                tilt = 20F
            ),
            failure = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.image_not_available),
                        contentDescription = "no image"
                    )
                }
            },
            previewPlaceholder = R.drawable.popcorn,
            contentScale = ContentScale.Crop,
            circularReveal = CircularReveal(duration = 1000),
            modifier = modifier.clip(RoundedCornerShape(8.dp)),
            contentDescription = "Movie item"
        )

        AnimatedVisibility(visible = landscape) {
            Text(
                text = trimTitle(title),
                modifier = Modifier
                    .padding(start = 4.dp, top = 4.dp)
                    .fillMaxWidth(),
                maxLines = 1,
                color = AppOnPrimaryColor,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start
            )
        }
    }
}

private fun trimTitle(text: String) = if (text.length <= 26) text else {
    val textWithEllipsis = text.removeRange(startIndex = 26, endIndex = text.length)
    "$textWithEllipsis..."
}

@Composable
private fun ScrollableMovieItems(
    landscape: Boolean = false,
    selectedFilmType: FilmType,
    navigator: (FilmResponse.Film) -> Unit,
    pagingItems: LazyPagingItems<FilmResponse.Film>,
    onErrorClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (!landscape) 215.dp else 195.dp)
    ) {
        when (pagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                LoopReverseLottieLoader(lottieFile = R.raw.loader)
            }
            is LoadState.NotLoading -> {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(pagingItems) { film ->
                        val imagePath =
                            if (landscape) "$BASE_BACKDROP_IMAGE_URL/${film!!.backdropPath}"
                            else "$BASE_POSTER_IMAGE_URL/${film!!.posterPath}"

                        MovieItem(
                            landscape = landscape,
                            imageUrl = imagePath,
                            title = film.title,
                            modifier = Modifier
                                .width(if (landscape) 215.dp else 130.dp)
                                .height(if (landscape) 161.25.dp else 195.dp)
                        ) {
                            navigator(film)
                        }
                    }
                }
            }
            is LoadState.Error -> {
                val error = pagingItems.loadState.refresh as LoadState.Error
                val errorMessage = when (error.error) {
                    is HttpException -> "Sorry, Something went wrong!\nTap to retry"
                    is IOException -> "Connection failed. Tap to retry!"
                    else -> "Failed! Tap to retry!"
                }
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(161.25.dp) // maintain the vertical space between two categories
                        .clickable {
                            onErrorClick()
                        }
                ) {
                    Text(
                        text = errorMessage,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFFE28B8B),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            else -> {
            }
        }
    }
}

@Composable
fun SelectableGenreChip(
    genre: String,
    selected: Boolean,
    onclick: () -> Unit
) {
    val animateChipBackgroundColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFA0A1C2) else ButtonColor.copy(alpha = 0.5F),
        animationSpec = tween(
            durationMillis = if (selected) 100 else 50,
            delayMillis = 0,
            easing = LinearOutSlowInEasing
        )
    )

    Box(
        modifier = Modifier
            .padding(end = 4.dp)
            .clip(CircleShape)
            .background(
                color = animateChipBackgroundColor
            )
            .height(32.dp)
            .widthIn(min = 80.dp)
            .border(
                width = 0.2.dp,
                color = Color(0xC69495B1),
                shape = CircleShape
            )
            .padding(horizontal = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onclick()
            }
    ) {
        Text(
            text = genre,
            fontWeight = if (selected) FontWeight.Normal else FontWeight.Light,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center).padding(bottom = 3.dp),
            color = if (selected) Color(0XFF180E36) else Color.White.copy(alpha = 0.80F)
        )
    }
}
