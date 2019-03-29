package oliveira.fabio.marvelapp.network.api

import io.reactivex.Flowable
import oliveira.fabio.marvelapp.model.response.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelApi {
    @GET("v1/public/characters")
    fun getCharacters(@Query("nameStartsWith") name: String?, @Query("limit") limit: Int, @Query("offset") offset: Int): Flowable<CharactersResponse>

    @GET("v1/public/characters/{characterId}/comics")
    fun getComics(@Path("characterId") id: Int, @Query("limit") limit: Int, @Query("orderBy") orderBy: String): Flowable<ComicsResponse>

    @GET("v1/public/characters/{characterId}/events")
    fun getEvents(@Path("characterId") id: Int, @Query("limit") limit: Int, @Query("orderBy") orderBy: String): Flowable<EventsResponse>

    @GET("v1/public/characters/{characterId}/stories")
    fun getStories(@Path("characterId") id: Int, @Query("limit") limit: Int, @Query("orderBy") orderBy: String): Flowable<StoriesResponse>

    @GET("v1/public/characters/{characterId}/series")
    fun getSeries(@Path("characterId") id: Int, @Query("limit") limit: Int, @Query("orderBy") orderBy: String): Flowable<SeriesResponse>
}