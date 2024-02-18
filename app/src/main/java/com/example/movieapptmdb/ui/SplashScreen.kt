package com.example.movieapptmdb.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.movieapptmdb.R
import com.example.movieapptmdb.ui.component.LottieLoader
import com.example.movieapptmdb.ui.theme.AppPrimaryColor
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onGoToNextPage: ()-> Unit
) {
    var animateLogo by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(AppPrimaryColor)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            LottieLoader(
                modifier = Modifier.size(270.dp),
                lottieFile = R.raw.movielottie
            )
            LaunchedEffect(Unit) {
                delay(2000)
                animateLogo = true
                delay(2000)
                onGoToNextPage()
            }
        }
    }
}
