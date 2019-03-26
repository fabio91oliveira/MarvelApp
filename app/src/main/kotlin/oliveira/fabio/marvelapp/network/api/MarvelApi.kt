package oliveira.fabio.marvelapp.network.api

import io.reactivex.Flowable
import oliveira.fabio.marvelapp.model.response.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelApi {
    @GET("v1/public/characters")
    fun getCharacters(@Query("name") name: String?, @Query("limit") limit: Int, @Query("offset") offset: Int): Flowable<CharactersResponse>

    @GET("v1/public/characters/{characterId}/comics")
    fun getComics(@Path("characterId") id: Int, @Query("limit") limit: Int): Flowable<ComicsResponse>

    @GET("v1/public/characters/{characterId}/events")
    fun getEvents(@Path("characterId") id: Int, @Query("limit") limit: Int): Flowable<EventsResponse>

    @GET("v1/public/characters/{characterId}/stories")
    fun getStories(@Path("characterId") id: Int, @Query("limit") limit: Int): Flowable<StoriesResponse>

    @GET("v1/public/characters/{characterId}/series")
    fun getSeries(@Path("characterId") id: Int, @Query("limit") limit: Int): Flowable<SeriesResponse>
}