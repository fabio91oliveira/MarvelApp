package oliveira.fabio.marvelapp.feature.characterslist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.util.Event
import oliveira.fabio.marvelapp.util.Response

class CharactersFavoriteListViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }
    val listOfAllFavorites by lazy { arrayListOf<Character>() }
    val mutableLiveDataResults by lazy { MutableLiveData<Event<Response<List<Character>>>>() }
    val latestResults by lazy { mutableListOf<Character>() }

    var isQuerySearch: Boolean = false
    var offset = 0
    var firstTime = true

    fun getFavoritesList() {
        compositeDisposable.add(
            charactersRepository.getAllFavorites().subscribe {
                listOfAllFavorites.clear()
                listOfAllFavorites.addAll(it)
                validateFavoriteCharacters(it)

            }
        )
    }

    fun addRemoveFavorite(character: Character) {
        val source1 = charactersRepository.deleteFavorite(character)
        val source2 = charactersRepository.getAllFavorites()
        val source3 = charactersRepository.addFavoriteCharacter(character)

        compositeDisposable.add(
            if (findIdInFavoriteList(character.id)) {
                Flowable.zip(
                    source1,
                    source2,
                    BiFunction<Long, List<Character>, Pair<Long, List<Character>>> { t1, t2 ->
                        Pair(t1, t2)
                    }).map {
                    Pair(it.first, it.second)
                }.subscribe {
                    listOfAllFavorites.clear()
                    listOfAllFavorites.addAll(it.second)
                }
            } else {
                Flowable.zip(
                    source3,
                    source2,
                    BiFunction<Long, List<Character>, Pair<Long, List<Character>>> { t1, t2 ->
                        Pair(t1, t2)
                    }).map {
                    Pair(it.first, it.second)
                }.subscribe {
                    listOfAllFavorites.clear()
                    listOfAllFavorites.addAll(it.second)
                }
            }

        )
    }

    fun onDestroy() = compositeDisposable.takeIf { it.isDisposed }?.run { dispose() }

    private fun validateFavoriteCharacters(charactersList: List<Character>) {
        charactersList.forEach {
            it.isFavorite = findIdInFavoriteList(it.id)
        }
    }

    private fun findIdInFavoriteList(id: Long): Boolean {
        listOfAllFavorites.forEach {
            if (it.id == id) {
                return true
            }
        }
        return false
    }
}