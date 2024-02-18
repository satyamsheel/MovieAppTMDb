package com.example.movieapptmdb.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.movieapptmdb.constants.FilmType
import com.example.movieapptmdb.viewModel.MovieDetailsViewModel
import com.example.movieapptmdb.viewModel.ViewModel
import com.example.movieapptmdb.viewModel.ViewModelFactory
import com.example.movieapptmdb.viewModel.ViewModelFactoryDetails
import com.example.movieapptmdb.R
import com.example.movieapptmdb.constants.Constants.BASE_BACKDROP_IMAGE_URL
import com.example.movieapptmdb.constants.Constants.BASE_POSTER_IMAGE_URL
import com.example.movieapptmdb.ui.component.BackButton
import com.example.movieapptmdb.ui.component.ExpandableText
import com.example.movieapptmdb.ui.component.MovieGenreTile
import com.example.movieapptmdb.ui.theme.AppOnPrimaryColor
import com.example.movieapptmdb.ui.theme.AppPrimaryColor
import com.example.movieapptmdb.ui.theme.ButtonColor
import com.example.tmdb_sdk.services.responses.CastResponse
import com.example.tmdb_sdk.services.responses.FilmResponse
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage


@Composable
fun MovieDetailScreen(onNavUp: () -> Unit, film: FilmResponse.Film)
{
    val viewModel: MovieDetailsViewModel = viewModel(factory = ViewModelFactoryDetails())
    val viewModelDetails: ViewModel = viewModel(factory = ViewModelFactory())
    val selectedFilmType = FilmType.MOVIE

    var film by remember { mutableStateOf(film) }
    val filmType: FilmType = remember { selectedFilmType }
    val similarFilms = viewModel.similarMovies.value.collectAsLazyPagingItems()
    val filmCastList = viewModel.filmCast.value

    LaunchedEffect(key1 = film) {
        viewModel.getSimilarFilms(filmId = film.id, filmType)
        viewModel.getFilmCast(filmId = film.id, filmType)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF180E36))
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.33F)
        ) {
            val (
                backdropImage,
                backButton,
                movieTitleBox,
                moviePosterImage,
                translucentBr
            ) = createRefs()

            CoilImage(
                imageModel = "$BASE_BACKDROP_IMAGE_URL${film.backdropPath}",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .fillMaxHeight()
                    .constrainAs(backdropImage) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                failure = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.backdrop_not_available),
                            contentDescription = "no image"
                        )
                    }
                },
                shimmerParams = ShimmerParams(
                    baseColor = AppPrimaryColor,
                    highlightColor = ButtonColor,
                    durationMillis = 500,
                    dropOff = 0.65F,
                    tilt = 20F
                ),
                contentScale = ContentScale.Crop,
                contentDescription = "Header backdrop image",
            )

            BackButton(modifier = Modifier
                .constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 16.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                }) {
                onNavUp.invoke()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0XFF180E36).copy(alpha = 0.5F),
                                Color(0XFF180E36)
                            ),
                            startY = 0.1F
                        )
                    )
                    .constrainAs(translucentBr) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(backdropImage.bottom)
                    }
            )

            Column(
                modifier = Modifier.constrainAs(movieTitleBox) {
                    start.linkTo(moviePosterImage.end, margin = 12.dp)
                    end.linkTo(parent.end, margin = 12.dp)
                    bottom.linkTo(moviePosterImage.bottom, margin = 10.dp)
                },
                verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Movie",
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(size = 4.dp))
                            .background(Color.DarkGray.copy(alpha = 0.65F))
                            .padding(2.dp),
                        color = AppOnPrimaryColor.copy(alpha = 0.78F),
                        fontSize = 12.sp,
                    )
                    Text(
                        text = when (film.adult) {
                            true -> "18+"
                            else -> "PG"
                        },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(shape = RoundedCornerShape(size = 4.dp))
                            .background(
                                if (film.adult) Color(0xFFFF7070) else Color.DarkGray.copy(
                                    alpha = 0.65F
                                )
                            )
                            .padding(2.dp),
                        color = AppOnPrimaryColor.copy(alpha = 0.78F),
                        fontSize = 12.sp,
                    )
                }

                Text(
                    text = film.title,
                    modifier = Modifier
                        .padding(top = 2.dp, start = 4.dp, bottom = 4.dp)
                        .fillMaxWidth(0.5F),
                    maxLines = 2,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.78F)
                )

                Text(
                    text = film.releaseDate,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White.copy(alpha = 0.56F)
                )

                RatingBar(
                    value = (film.voteAverage / 2).toFloat(),
                    modifier = Modifier.padding(start = 6.dp, bottom = 4.dp, top = 4.dp),
                    config = RatingBarConfig()
                        .style(RatingBarStyle.Normal)
                        .isIndicator(true)
                        .activeColor(Color(0XFFC9F964))
                        .hideInactiveStars(false)
                        .inactiveColor(Color.LightGray.copy(alpha = 0.3F))
                        .stepSize(StepSize.HALF)
                        .numStars(5)
                        .size(16.dp)
                        .padding(4.dp),
                    onValueChange = {},
                    onRatingChanged = {}
                )
            }

            CoilImage(
                imageModel = "$BASE_POSTER_IMAGE_URL/${film.posterPath}",
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .width(115.dp)
                    .height(172.5.dp)
                    .constrainAs(moviePosterImage) {
                        top.linkTo(backdropImage.bottom)
                        bottom.linkTo(backdropImage.bottom)
                        start.linkTo(parent.start)
                    }, failure = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.image_not_available),
                            contentDescription = "no image"
                        )
                    }
                },
                shimmerParams = ShimmerParams(
                    baseColor = AppPrimaryColor,
                    highlightColor = ButtonColor,
                    durationMillis = 500,
                    dropOff = 0.65F,
                    tilt = 20F
                ),
                previewPlaceholder = R.drawable.popcorn,
                contentScale = ContentScale.Crop,
                circularReveal = CircularReveal(duration = 1000),
                contentDescription = "movie poster"
            )
        }

        LazyRow(
            modifier = Modifier
                .padding(top = (96).dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                .fillMaxWidth()
        ) {
            val filmGenres: List<FilmResponse.Genre> = viewModelDetails.filmGenres.filter { genre ->
                return@filter if (film.genreIds.isNullOrEmpty()) false else
                    film.genreIds!!.contains(genre.id)
            }
            filmGenres.forEach { genre ->
                item {
                    MovieGenreTile(
                        background = ButtonColor,
                        textColor = AppOnPrimaryColor,
                        genre = genre.name
                    )
                }
            }
        }

        ExpandableText(
            text = film.overview,
            modifier = Modifier
                .padding(top = 3.dp, bottom = 4.dp, start = 4.dp, end = 4.dp)
                .fillMaxWidth()
        )

        LazyColumn(
            horizontalAlignment = Alignment.Start
        ) {
            item {
                AnimatedVisibility(visible = (filmCastList.isNotEmpty())) {
                    Text(
                        text = "Cast",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppOnPrimaryColor,
                        modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 4.dp)
                    )
                }
            }
            item {
                LazyRow(modifier = Modifier.padding(4.dp)) {
                    filmCastList.forEach { cast ->
                        item { CastMember(cast = cast) }
                    }
                }
            }
            item {
                if (similarFilms.itemCount != 0) {
                    Text(
                        text = "Similar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = AppOnPrimaryColor,
                        modifier = Modifier.padding(start = 4.dp, top = 6.dp, bottom = 4.dp)
                    )
                }
            }

            item {
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(similarFilms) { thisMovie ->
                        CoilImage(
                            imageModel = "${BASE_POSTER_IMAGE_URL}/${thisMovie!!.posterPath}",
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
                                    modifier = Modifier.fillMaxSize()
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
                            modifier = Modifier
                                .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .size(130.dp, 195.dp)
                                .clickable {
                                    film = thisMovie
                                },
                            contentDescription = "Movie item"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CastMember(cast: CastResponse.Cast?) {
    Column(
        modifier = Modifier.padding(end = 8.dp, top = 2.dp, bottom = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoilImage(
            modifier = Modifier
                .clip(CircleShape)
                .size(70.dp),
            imageModel = "$BASE_POSTER_IMAGE_URL/${cast!!.profilePath}",
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        modifier = Modifier.size(70.dp),
                        painter = painterResource(id = R.drawable.ic_user),
                        tint = Color.LightGray,
                        contentDescription = null
                    )
                }
            },
            previewPlaceholder = R.drawable.ic_user,
            contentScale = ContentScale.Crop,
            circularReveal = CircularReveal(duration = 1000),
            contentDescription = "cast image"
        )
        Text(
            text = trimName(cast.name),
            maxLines = 1,
            color = AppOnPrimaryColor.copy(alpha = 0.5F),
            fontSize = 14.sp,
        )
        Text(
            text = trimName(cast.department),
            maxLines = 1,
            color = AppOnPrimaryColor.copy(alpha = 0.45F),
            fontSize = 12.sp,
        )
    }
}

fun trimName(name: String): String {
    return if (name.length <= 10) name else {
        name.removeRange(8..name.lastIndex) + "..."
    }
}








