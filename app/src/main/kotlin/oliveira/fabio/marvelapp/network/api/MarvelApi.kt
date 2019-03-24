package oliveira.fabio.marvelapp.network.api

import io.reactivex.Flowable
import oliveira.fabio.marvelapp.model.response.CharactersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MarvelApi {
    @GET("v1/public/characters")
    fun getCharacters(@Query("name") name: String?, @Query("limit") limit: Int, @Query("offset") offset: Int): Flowable<CharactersResponse>
}