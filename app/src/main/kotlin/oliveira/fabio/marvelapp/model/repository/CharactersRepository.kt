package oliveira.fabio.marvelapp.model.repository

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.model.persistence.CharacterDao
import oliveira.fabio.marvelapp.model.response.*
import oliveira.fabio.marvelapp.network.api.MarvelApi

class CharactersRepository(private val marvelApi: MarvelApi, private val characterDao: CharacterDao) {
    fun getCharacters(limit: Int, offset: Int, name: String? = null): Flowable<CharactersResponse> =
        marvelApi.getCharacters(name, limit, offset)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getComics(characterId: Int): Flowable<ComicsResponse> =
        marvelApi.getComics(characterId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getEvents(characterId: Int): Flowable<EventsResponse> =
        marvelApi.getEvents(characterId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getSeries(characterId: Int): Flowable<SeriesResponse> =
        marvelApi.getSeries(characterId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getStories(characterId: Int): Flowable<StoriesResponse> =
        marvelApi.getStories(characterId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun addFavoriteCharacter(character: Character): Flowable<Long> {
        return Flowable.fromCallable { characterDao.addFavorite(character) }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun deleteFavorite(character: Character): Flowable<Long> {
        return Flowable.fromCallable { characterDao.deleteFavorite(character).toLong() }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllFavorites(): Flowable<List<Character>> {
        return characterDao.findAllFavorites().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }
}