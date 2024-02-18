package com.example.movieapptmdb.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.movieapptmdb.constants.FilmType
import com.example.tmdb_sdk.services.APi
import com.example.tmdb_sdk.services.responses.FilmResponse
import retrofit2.HttpException
import java.io.IOException

class TrendingFilmSource(private val api: APi, private val filmType: FilmType) :
    PagingSource<Int, FilmResponse.Film>() {
    override fun getRefreshKey(state: PagingState<Int, FilmResponse.Film>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmResponse.Film> {
        return try {
            val nextPage = params.key ?: 1
            val trendingFilms =
                api.getTrendingMovies(page = nextPage)
            LoadResult.Page(
                data = trendingFilms.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (trendingFilms.results.isEmpty()) null else trendingFilms.page + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}
