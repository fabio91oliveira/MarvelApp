package oliveira.fabio.marvelapp.model.repository

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import oliveira.fabio.marvelapp.model.response.CharactersResponse
import oliveira.fabio.marvelapp.network.api.MarvelApi

class CharactersRepository(private val marvelApi: MarvelApi) {
    fun getCharacters(limit: Int, offset: Int, name: String? = null): Flowable<CharactersResponse> =
        marvelApi.getCharacters(name, limit, offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}