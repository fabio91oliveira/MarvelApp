package oliveira.fabio.marvelapp.feature.characterslist.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.model.response.Character
import oliveira.fabio.marvelapp.model.response.CharactersResponse
import oliveira.fabio.marvelapp.model.response.Response
import oliveira.fabio.marvelapp.util.Event

class CharactersListViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }
    val listOfAllFavorites by lazy { arrayListOf<Character>() }
    val mutableLiveDataResults by lazy { MutableLiveData<Event<Response<List<Character>>>>() }

    var lastestData: CharactersResponse.Data? = null

    val lastestResults by lazy { mutableListOf<Character>() }

    var offset = 0

    fun getCharactersList() {
        val source1 = charactersRepository.getAllFavorites()
//            .subscribe {
//                listOfAllFavorites.addAll(it)
//            }

        val source2 = charactersRepository.getCharacters(LIMIT_PER_PAGE, offset)
//            .map {
//                parseToCharacterList(it.data)
//            }
//            .subscribe({
//                lastestResults.addAll(it)
//                mutableLiveDataResults.value = Event(Response.success(it))
//            },
//                {
//                    mutableLiveDataResults.value = Event(Response.error(it.message))
//                })


        compositeDisposable.add(
            Flowable.zip(
                source1,
                source2,
                BiFunction<List<Character>, CharactersResponse, Pair<List<Character>, CharactersResponse>> { t1, t2 ->
                    Pair(t1, t2)
                }).map {
                Pair(it.first, parseToCharacterList(it.second.data))
            }.subscribe {
                listOfAllFavorites.clear()
                listOfAllFavorites.addAll(it.first)
                validateFavoriteCharacters(it.second)
                lastestResults.addAll(it.second)
                mutableLiveDataResults.value = Event(Response.success(it.second))
            }
        )
    }

    fun addRemoveFavorite(character: Character) {
        compositeDisposable.add(
            if (findIdInFavoriteList(character.id)) {
                charactersRepository.deleteFavorite(character).subscribe({
                    Log.d("DELETADO", "ta la")
                    charactersRepository.getAllFavorites().subscribe {
                        listOfAllFavorites.clear()
                        listOfAllFavorites.addAll(it)
                    }
                }, {
                    Log.d("foi nao ", "hauha")
                })
            } else {
                charactersRepository.addFavoriteCharacter(character).subscribe({
                    Log.d("SALVO", "ta la")
                    charactersRepository.getAllFavorites().subscribe {
                        listOfAllFavorites.clear()
                        listOfAllFavorites.addAll(it)
                    }
                }, {
                    Log.d("foi nao ", "hauha")
                })
            }
        )
    }

    fun onDestroy() = compositeDisposable.takeIf { it.isDisposed }?.run { dispose() }

    private fun parseToCharacterList(data: CharactersResponse.Data): MutableList<Character> {
        val charactersList = mutableListOf<Character>()
        lastestData = data
        lastestData?.results?.forEach {
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

    private fun validateFavoriteCharacters(charactersList: MutableList<Character>) {
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

    companion object {
        private const val LIMIT_PER_PAGE = 20
    }
}