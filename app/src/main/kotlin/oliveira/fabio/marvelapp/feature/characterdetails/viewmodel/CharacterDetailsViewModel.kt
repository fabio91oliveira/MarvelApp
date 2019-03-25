package oliveira.fabio.marvelapp.feature.characterdetails.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function4
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.model.response.*
import oliveira.fabio.marvelapp.util.Event

class CharacterDetailsViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }
    val mutableLiveDataEventsList by lazy { MutableLiveData<Event<Response<HashMap<String, List<EventValueObject>>>>>() }

    fun getDatasByCharacterId(characterId: Int) {
        val source1 = charactersRepository.getComics(characterId)
        val source2 = charactersRepository.getEvents(characterId)
        val source3 = charactersRepository.getSeries(characterId)
        val source4 = charactersRepository.getStories(characterId)

        compositeDisposable.add(

            Flowable.zip(
                source1,
                source2,
                source3,
                source4,
                Function4<ComicsResponse, EventsResponse, SeriesResponse, StoriesResponse, Four<ComicsResponse, EventsResponse, SeriesResponse, StoriesResponse>> { t1, t2, t3, t4 ->
                    Four(
                        t1,
                        t2,
                        t3,
                        t4
                    )
                }).map { createListOfEventsToView(it) }
                .subscribe({
                    mutableLiveDataEventsList.value = Event(Response.success(it))
                }, {})
        )

    }

    private fun createListOfEventsToView(it: Four<ComicsResponse, EventsResponse, SeriesResponse, StoriesResponse>): HashMap<String, List<EventValueObject>> {
        val hash by lazy { hashMapOf<String, List<EventValueObject>>() }
        val comicsList by lazy { mutableListOf<EventValueObject>() }
        val eventsList by lazy { mutableListOf<EventValueObject>() }
        val seriesList by lazy { mutableListOf<EventValueObject>() }
        val storiesList by lazy { mutableListOf<EventValueObject>() }

        it.first.data?.results?.forEachIndexed { index, list ->
            if (index + 1 <= MAX_ITEMS_PER_LIST) {
                val event = EventValueObject("", "")
                list?.title.let { event.title = it }
                list?.description?.let { event.description = it }
                comicsList.add(event)
            }
        }

        it.second.data?.results?.forEachIndexed { index, list ->
            if (index + 1 <= MAX_ITEMS_PER_LIST) {
                val event = EventValueObject("", "")
                list?.title.let { event.title = it }
                list?.description?.let { event.description = it }
                eventsList.add(event)
            }
        }

        it.third.data?.results?.forEachIndexed { index, list ->
            if (index + 1 <= MAX_ITEMS_PER_LIST) {
                val event = EventValueObject("", "")
                list?.title.let { event.title = it }
                list?.description?.let { event.description = it }
                seriesList.add(event)
            }
        }

        it.fourth.data?.results?.forEachIndexed { index, list ->
            if (index + 1 <= MAX_ITEMS_PER_LIST) {
                val event = EventValueObject("", "")
                list?.title.let { event.title = it }
                list?.description?.let { event.description = it }
                storiesList.add(event)
            }
        }

        hash[EventValueObject.COMICS_TAG] = comicsList
        hash[EventValueObject.EVENTS_TAG] = eventsList
        hash[EventValueObject.SERIES_TAG] = seriesList
        hash[EventValueObject.STORIES_TAG] = storiesList

        return hash
    }

    fun onDestroy() = compositeDisposable.takeIf { it.isDisposed }?.run { dispose() }

    companion object {
        private const val MAX_ITEMS_PER_LIST = 3
    }

}
