package com.example.movieapptmdb.ui.component

import android.util.Log
import androidx.annotation.RawRes
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.*

@Composable
fun LoopReverseLottieLoader(
    modifier: Modifier = Modifier,
    @RawRes lottieFile: Int,
    alignment: Alignment = Alignment.Center,
    enableMergePaths: Boolean = true,
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(lottieFile))
    val reverse = remember { mutableStateOf(false) }
    val anim = rememberLottieAnimatable()
    if (reverse.value.not())
        LaunchedEffect(key1 = composition) {
            anim.animate(composition = composition, speed = 1f)
            reverse.value = true
        }
    if (reverse.value) {
        LaunchedEffect(composition) {
            anim.animate(composition = composition, speed = -1f)
            reverse.value = false
        }
    }

    LottieAnimation(
        composition,
        anim.value,
        modifier = modifier,
        enableMergePaths = remember { enableMergePaths },
        alignment = alignment
    )
}

@Composable
fun LottieLoader(
    modifier: Modifier = Modifier,
    @RawRes lottieFile: Int
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(lottieFile),
        onRetry = { failCount, exception ->
            exception.localizedMessage?.let { Log.d("Failed ${failCount}X with exception. Reason:", it) }
            false
        }
    )
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier
    )
}