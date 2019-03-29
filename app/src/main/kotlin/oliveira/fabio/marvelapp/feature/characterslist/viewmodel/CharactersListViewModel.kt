package oliveira.fabio.marvelapp.feature.characterslist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.model.response.CharactersResponse
import oliveira.fabio.marvelapp.util.Event
import oliveira.fabio.marvelapp.util.Response

class CharactersListViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {

    private var pageType = REGULAR_LIST
    private var latestData: CharactersResponse.Data? = null
    private val compositeDisposable by lazy { CompositeDisposable() }
    val mutableLiveDataResults by lazy { MutableLiveData<Event<Response<List<Character>>>>() }
    val mutableLiveDataFavorites by lazy { MutableLiveData<Event<Response<List<Character>>>>() }
    val listOfAllFavorites by lazy { arrayListOf<Character>() }
    val listOfAllResults by lazy { mutableListOf<Character>() }
    val latestResults by lazy { mutableListOf<Character>() }

    var isQuerySearch: Boolean = false
    var offset = INITIAL_VALUE
    var firstTime = true
    var lastNameSearched: String? = null

    fun getCharactersList() {
        val source1 = charactersRepository.getAllFavorites()
        val source2 = charactersRepository.getCharacters(LIMIT_PER_PAGE, offset, lastNameSearched)

        compositeDisposable.add(
            Flowable.zip(
                source1,
                source2,
                BiFunction<List<Character>, CharactersResponse, Pair<List<Character>, CharactersResponse>> { t1, t2 ->
                    Pair(t1, t2)
                }).map {
                Pair(it.first, parseToCharacterList(it.second.data))
            }.subscribe({
                listOfAllFavorites.clear()
                listOfAllFavorites.addAll(it.first)
//                latestResults.clear() // TODO VERIFY IT
                latestResults.addAll(validateFavoriteCharacters(it.second))
                listOfAllResults.addAll(validateFavoriteCharacters(it.second))
                mutableLiveDataResults.value = Event(Response.success(it.second))
                mutableLiveDataFavorites.value = Event(Response.success(listOfAllFavorites))
            }, {
                mutableLiveDataResults.value = Event(Response.error(it.message))
            })
        )
    }

    fun getFavoritesList(character: Character? = null) {
        character?.let {
            refreshFavoritesList(character)
            refreshLatestResults()
            mutableLiveDataResults.value = Event(Response.success(latestResults))
        }
        mutableLiveDataFavorites.value = Event(Response.success(listOfAllFavorites))
    }

    fun addRemoveFavorite(character: Character, resetFirstTab: Boolean? = null) {
        compositeDisposable.add(
            if (findIdInFavoriteList(character.id)) {
                charactersRepository.deleteFavorite(character)
                    .subscribe {
                        refreshFavoritesList(character)
                        mutableLiveDataFavorites.value = Event(Response.success(listOfAllFavorites))
                        resetFirstTab?.apply {
                            refreshLatestResults()
                            mutableLiveDataResults.value = Event(Response.success(latestResults))
                        }
                    }
            } else {
                charactersRepository.addFavoriteCharacter(character)
                    .subscribe {
                        listOfAllFavorites.add(character)
                        mutableLiveDataFavorites.value = Event(Response.success(listOfAllFavorites))
                    }
            }

        )
    }

    fun onDestroy() = compositeDisposable.takeIf { it.isDisposed }?.run { dispose() }

    fun changeToRegularListPageType() {
        pageType = REGULAR_LIST
    }

    fun changeToFavoriteListPageType() {
        pageType = FAVORITE_LIST
    }

    fun isFavoriteListPageType() = pageType == FAVORITE_LIST

    private fun parseToCharacterList(data: CharactersResponse.Data): MutableList<Character> {
        val charactersList = mutableListOf<Character>()
        latestData = data
        latestData?.results?.forEach {
            val character = Character(
                it.name,
                false,
                it.description,
                it.resourceURI,
                it.thumbnail.getFullImageUrl()
            )
            character.id = it.id.toLong()
            charactersList.add(character)
        }

        return charactersList
    }

    private fun refreshFavoritesList(character: Character) {
        val listOfAllFavoritesAux by lazy { arrayListOf<Character>() }
        listOfAllFavorites.forEach {
            if (it.id != character.id) {
                listOfAllFavoritesAux.add(it)
            }
        }
        listOfAllFavorites.clear()
        listOfAllFavorites.addAll(listOfAllFavoritesAux)
    }

    private fun refreshLatestResults() {
        if (listOfAllFavorites.isEmpty()) {
            latestResults.forEach { it.isFavorite = false }
        } else {
            listOfAllFavorites.forEach { favorite ->
                latestResults.forEach { character ->
                    character.isFavorite = favorite.id == character.id
                }
            }

        }
    }

    private fun validateFavoriteCharacters(charactersList: MutableList<Character>): MutableList<Character> {
        charactersList.forEach {
            it.isFavorite = findIdInFavoriteList(it.id)
        }

        return charactersList
    }

    private fun findIdInFavoriteList(id: Long): Boolean {
        listOfAllFavorites.forEach {
            if (it.id == id) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val LIMIT_PER_PAGE = 20
        const val INITIAL_VALUE = 0
        const val REGULAR_LIST = 0
        const val FAVORITE_LIST = 1
    }
}