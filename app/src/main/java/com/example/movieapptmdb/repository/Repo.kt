package com.example.movieapptmdb.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.movieapptmdb.constants.FilmType
import com.example.movieapptmdb.constants.Resource
import com.example.movieapptmdb.paging.NowPlayingFilmSource
import com.example.movieapptmdb.paging.PopularFilmSource
import com.example.movieapptmdb.paging.SearchFilmSource
import com.example.movieapptmdb.paging.SimilarFilmSource
import com.example.movieapptmdb.paging.TopRatedFilmSource
import com.example.movieapptmdb.paging.TrendingFilmSource
import com.example.movieapptmdb.paging.UpcomingFilmSource
import com.example.tmdb_sdk.services.APi
import com.example.tmdb_sdk.services.ApiClient
import com.example.tmdb_sdk.services.responses.CastResponse
import com.example.tmdb_sdk.services.responses.FilmResponse
import com.example.tmdb_sdk.services.responses.GenreResponse
import com.example.tmdb_sdk.services.responses.MultiSearchResponse
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

object Repo {
    private val api: APi = ApiClient.publicApi

    fun getPopularFilms(filmType: FilmType): Flow<PagingData<FilmResponse.Film>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                PopularFilmSource(api = api, filmType)
            }
        ).flow
    }

    fun getTrendingFilms(filmType: FilmType): Flow<PagingData<FilmResponse.Film>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                TrendingFilmSource(api = api, filmType)
            }
        ).flow
    }

    fun getUpcomingTvShows(): Flow<PagingData<FilmResponse.Film>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                UpcomingFilmSource(api = api)
            }
        ).flow
    }

    fun getTopRatedFilm(filmType: FilmType): Flow<PagingData<FilmResponse.Film>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                TopRatedFilmSource(api = api, filmType)
            }
        ).flow
    }

    fun getNowPlayingFilms(filmType: FilmType): Flow<PagingData<FilmResponse.Film>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                NowPlayingFilmSource(api = api, filmType)
            }
        ).flow
    }

    fun getSimilarFilms(movieId: Int, filmType: FilmType): Flow<PagingData<FilmResponse.Film>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                SimilarFilmSource(api = api, filmId = movieId, filmType)
            }
        ).flow
    }

    suspend fun getMoviesGenre(filmType: FilmType): Resource<GenreResponse> {
        val response = try {
            api.getMovieGenres()
        } catch (e: Exception){
            return Resource.Error("Unknown error occurred!")
        }
        return Resource.Success(response)
    }

    suspend fun getFilmCast(filmId: Int, filmType: FilmType): Resource<CastResponse> {
        val response = try {
            api.getMovieCast(filmId = filmId)
        } catch (e: Exception) {
            return Resource.Error("Error when loading movie cast")
        }
        return Resource.Success(response)
    }

    fun multiSearch(searchParams: String, includeAdult: Boolean): Flow<PagingData<MultiSearchResponse.Search>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                SearchFilmSource(api = api, searchParams = searchParams, includeAdult)
            }
        ).flow
    }
}