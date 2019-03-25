package oliveira.fabio.marvelapp.feature.characterdetails.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.model.response.ComicsResponse
import oliveira.fabio.marvelapp.model.response.Response
import oliveira.fabio.marvelapp.util.Event

class CharacterDetailsViewModel_old(private val charactersRepository: CharactersRepository) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }
    val mutableLiveDataComics by lazy { MutableLiveData<Event<Response<ComicsResponse.Data>>>() }
    val mutableLiveDataEventsList by lazy { MutableLiveData<Event<Response<ComicsResponse.Data>>>() }
    val mutableLiveDataSeriesList by lazy { MutableLiveData<Event<Response<ComicsResponse.Data>>>() }
    val mutableLiveDataStoriesList by lazy { MutableLiveData<Event<Response<ComicsResponse.Data>>>() }

    fun getDatasByCharacterId(characterId: Int) {
//        val source1 = charactersRepository.getComics(characterId).doOnSubscribe { Log.d("STARTOU 1", "STARTOU 1") }
//            .doOnError { Log.d("error", "error") }
//            .onErrorResumeNext(Flowable.empty())
//        val source2 = charactersRepository.getEvents(characterId).doOnSubscribe { Log.d("STARTOU 2", "STARTOU 2") }
//            .doOnError { Log.d("comic", "comic") }
//            .onErrorResumeNext(Flowable.empty())
//        val source3 = charactersRepository.getSeries(characterId).doOnSubscribe { Log.d("STARTOU 3", "STARTOU 4") }
//            .doOnError { Log.d("comic", "comic") }
//            .onErrorResumeNext(Flowable.empty())
//        val source4 = charactersRepository.getStories(characterId).doOnSubscribe { Log.d("STARTOU 4", "STARTOU 4") }
//            .doOnError { Log.d("comic", "comic") }
//            .onErrorResumeNext(Flowable.empty())
//
//        compositeDisposable.add(
//            Flowable.merge(source1, source2, source3, source4)
//                .subscribe { any ->
//                    when (any) {
//                        is ComicsResponse -> {
//                            any.let { mutableLiveDataComics.value = Event(Response.success(it.data)) }
//                        }
//                        is EventsResponse -> Log.d("2", "2")
//                        is SeriesResponse -> Log.d("3", "3")
//                        is StoriesResponse -> Log.d("4", "4")
//                    }
//                }
//        )
    }

    fun onDestroy() = compositeDisposable.takeIf { it.isDisposed }?.run { dispose() }

}
