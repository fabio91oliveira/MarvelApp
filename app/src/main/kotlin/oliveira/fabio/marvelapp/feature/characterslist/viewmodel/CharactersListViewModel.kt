package oliveira.fabio.marvelapp.feature.characterslist.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.model.response.CharactersResponse
import oliveira.fabio.marvelapp.model.response.Response
import oliveira.fabio.marvelapp.util.Event

class CharactersListViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }
    val mutableLiveDataResults by lazy { MutableLiveData<Event<Response<CharactersResponse.Data>>>() }

    var lastestData: CharactersResponse.Data? = null

    val lastestResults by lazy { mutableListOf<CharactersResponse.Data.Result>() }

    var offset = 0

    fun getCharactersList() {
        compositeDisposable.add(
            charactersRepository.getCharacters(LIMIT_PER_PAGE, offset)
                .subscribe({ charactersResponse ->
                    lastestResults.addAll(charactersResponse.data.results)
                    lastestData = charactersResponse.data
                    mutableLiveDataResults.value = Event(Response.success(lastestData))

                },
                    {
                        mutableLiveDataResults.value = Event(Response.error(it.message))
                    })
        )
    }

    fun onDestroy() = compositeDisposable.takeIf { it.isDisposed }?.run { dispose() }

    companion object {
        private const val LIMIT_PER_PAGE = 20
    }
}