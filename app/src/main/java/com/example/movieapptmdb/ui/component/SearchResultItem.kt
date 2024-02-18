package com.example.movieapptmdb.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieapptmdb.ui.theme.AppOnPrimaryColor
import com.example.movieapptmdb.ui.theme.AppPrimaryColor
import com.example.movieapptmdb.ui.theme.ButtonColor
import com.example.movieapptmdb.R
import com.example.tmdb_sdk.services.responses.FilmResponse
import com.gowtham.ratingbar.RatingBar
import com.gowtham.ratingbar.RatingBarConfig
import com.gowtham.ratingbar.RatingBarStyle
import com.gowtham.ratingbar.StepSize
import com.skydoves.landscapist.CircularReveal
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun SearchResultItem(
    title: String?,
    mediaType: String?,
    posterImage: String?,
    genres: List<FilmResponse.Genre>?,
    rating: Double,
    releaseYear: String?,
    onClick: () -> Unit?
) {
    Box(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, bottom = 12.dp)
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(ButtonColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoilImage(
                imageModel = posterImage,
                circularReveal = CircularReveal(duration = 1000),
                previewPlaceholder = R.drawable.popcorn,
                shimmerParams = ShimmerParams(
                    baseColor = AppPrimaryColor,
                    highlightColor = ButtonColor,
                    durationMillis = 500,
                    dropOff = 0.65F,
                    tilt = 20F
                ),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(86.67.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxSize()
            ) {
                var paddingValue by remember { mutableStateOf(2) }
                Text(
                    text = when (mediaType) {
                        "tv" -> {
                            paddingValue = 2
                            "Series"
                        }
                        "movie" -> {
                            paddingValue = 2
                            "Movie"
                        }
                        else -> {
                            paddingValue = 0
                            ""
                        }
                    },
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(size = 4.dp))
                        .background(Color.LightGray.copy(alpha = 0.2F))
                        .padding(paddingValue.dp),
                    color = AppOnPrimaryColor.copy(alpha = 0.78F),
                    fontSize = 12.sp,
                )
                Text(
                    text = title ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    color = AppOnPrimaryColor
                )

                Text(
                    text = releaseYear ?: "",
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.LightGray
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RatingBar(
                        value = rating.toFloat() / 2,
                        modifier = Modifier.padding(end = 6.dp),
                        config = RatingBarConfig()
                            .style(RatingBarStyle.Normal)
                            .isIndicator(true)
                            .activeColor(Color(0XFFC9F964))
                            .hideInactiveStars(false)
                            .inactiveColor(Color.LightGray.copy(alpha = 0.2F))
                            .stepSize(StepSize.HALF)
                            .numStars(5)
                            .size(16.dp)
                            .padding(4.dp),
                        onValueChange = {},
                        onRatingChanged = {}
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    genres?.forEach {
                        item {
                            MovieGenreTile(genre = it.name)
                        }
                    }
                }
            }
        }
    }
}
