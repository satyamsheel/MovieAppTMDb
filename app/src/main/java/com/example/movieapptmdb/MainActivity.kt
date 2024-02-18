package com.example.movieapptmdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movieapptmdb.constants.Destinations.DETAILS
import com.example.movieapptmdb.constants.Destinations.HOME
import com.example.movieapptmdb.constants.Destinations.SEARCH
import com.example.movieapptmdb.constants.Destinations.WELCOME
import com.example.movieapptmdb.ui.Home
import com.example.movieapptmdb.ui.MovieDetailScreen
import com.example.movieapptmdb.ui.SearchScreen
import com.example.movieapptmdb.ui.SplashScreen
import com.example.movieapptmdb.ui.theme.AppTheme
import com.example.tmdb_sdk.services.responses.FilmResponse

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                NavHost()
            }
        }
    }
}

@Composable
fun NavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = WELCOME,
    ) {
        composable(WELCOME) {
            SplashScreen(
                onGoToNextPage = {
                    navController.popBackStack()
                    navController.navigate(HOME)
                },
            )
        }

        composable(HOME) {
            Home(onGoToDetails = { film ->
                navController.currentBackStackEntry?.savedStateHandle?.set("film", film)
                navController.navigate(DETAILS)
            }, onGotToSearch = { navController.navigate(SEARCH) })
        }

        composable(DETAILS) {
            val film: FilmResponse.Film? =
                navController.previousBackStackEntry?.savedStateHandle?.get("film")
            if (film != null) {
                MovieDetailScreen(
                    onNavUp = navController::navigateUp, film
                )
            }
        }

        composable(SEARCH) {
            SearchScreen(
                onGoToDetails = { film ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("film", film)
                    navController.navigate(DETAILS)
                }, onNavUp = navController::navigateUp
            )
        }
    }
}




