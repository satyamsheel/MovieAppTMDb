package com.example.movieapptmdb.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tmdb_sdk.services.APi
import com.example.tmdb_sdk.services.responses.MultiSearchResponse
import retrofit2.HttpException
import java.io.IOException

class SearchFilmSource(
    private val api: APi,
    private val searchParams: String,
    private val includeAdult: Boolean
) : PagingSource<Int, MultiSearchResponse.Search>() {
    override fun getRefreshKey(state: PagingState<Int, MultiSearchResponse.Search>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MultiSearchResponse.Search> {
        return try {
            val nextPage = params.key ?: 1
            val searchMovies = api.multiSearch(
                page = nextPage,
                searchParams = searchParams,
                includeAdult = includeAdult
            )
            LoadResult.Page(
                data = searchMovies.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (searchMovies.results.isEmpty()) null else searchMovies.page + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}