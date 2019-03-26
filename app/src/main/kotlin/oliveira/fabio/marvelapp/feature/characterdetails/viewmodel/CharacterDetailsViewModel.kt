package oliveira.fabio.marvelapp.feature.characterdetails.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.model.response.ComicsResponse
import oliveira.fabio.marvelapp.model.response.EventsResponse
import oliveira.fabio.marvelapp.model.response.SeriesResponse
import oliveira.fabio.marvelapp.model.response.StoriesResponse
import oliveira.fabio.marvelapp.model.vo.Four
import oliveira.fabio.marvelapp.model.vo.HeaderItem
import oliveira.fabio.marvelapp.model.vo.Item
import oliveira.fabio.marvelapp.model.vo.SubItem
import oliveira.fabio.marvelapp.util.Event
import oliveira.fabio.marvelapp.util.Response

class CharacterDetailsViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }
    val mutableLiveDataEventsList by lazy { MutableLiveData<Event<Response<MutableList<Item>>>>() }
    val listOfAllFavorites by lazy { mutableListOf<Character>() }
    val lastResultsInfo by lazy { mutableListOf<Item>() }
    var isLoadedWithNoResults = false

    fun getDatasByCharacterId(characterId: Int) {
        val source1 = charactersRepository.getComics(characterId, LIMIT)
        val source2 = charactersRepository.getEvents(characterId, LIMIT)
        val source3 = charactersRepository.getSeries(characterId, LIMIT)
        val source4 = charactersRepository.getStories(characterId, LIMIT)

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
                }).map { parseToItem(it) }
                .subscribe({
                    lastResultsInfo.clear()
                    lastResultsInfo.addAll(it)
                    mutableLiveDataEventsList.value = Event(Response.success(it))
                }, {
                    mutableLiveDataEventsList.value = Event(Response.error(it.message))
                })
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

    private fun parseToItem(it: Four<ComicsResponse, EventsResponse, SeriesResponse, StoriesResponse>): MutableList<Item> {
        val list = mutableListOf<Item>()

        val comics = it.first
        val events = it.second
        val series = it.third
        val stories = it.fourth

        comics.data?.let {
            it.count?.run { if (toInt() >= MIN_ITEMS) list.add(HeaderItem(COMICS_TAG)) }
            it.results?.forEach { item ->
                item?.getTitleFormatted()?.run {
                    item.getDescriptionFormatted().let { description ->
                        list.add(
                            SubItem(
                                handleEmptyText(this, NO_TITLE_FROM_API),
                                handleEmptyText(description, NO_DESCRIPTION_FROM_API)
                            )
                        )
                    }
                }
            }
        }

        events.data?.let {
            it.count?.run { if (toInt() >= MIN_ITEMS) list.add(HeaderItem(EVENTS_TAG)) }
            it.results?.forEach { item ->
                item?.getTitleFormatted()?.run {
                    item.getDescriptionFormatted().let { description ->
                        list.add(
                            SubItem(
                                handleEmptyText(this, NO_TITLE_FROM_API),
                                handleEmptyText(description, NO_DESCRIPTION_FROM_API)
                            )
                        )
                    }
                }
            }
        }

        series.data?.let {
            it.count?.run { if (toInt() >= MIN_ITEMS) list.add(HeaderItem(SERIES_TAG)) }
            it.results?.forEach { item ->
                item?.getTitleFormatted()?.run {
                    item.getDescriptionFormatted().let { description ->
                        list.add(
                            SubItem(
                                handleEmptyText(this, NO_TITLE_FROM_API),
                                handleEmptyText(description, NO_DESCRIPTION_FROM_API)
                            )
                        )
                    }
                }
            }
        }

        stories.data?.let {
            it.count?.run { if (toInt() >= MIN_ITEMS) list.add(HeaderItem(STORIES_TAG)) }
            it.results?.forEach { item ->
                item?.getTitleFormatted()?.run {
                    item.getDescriptionFormatted().let { description ->
                        list.add(
                            SubItem(
                                handleEmptyText(this, NO_TITLE_FROM_API),
                                handleEmptyText(description, NO_DESCRIPTION_FROM_API)
                            )
                        )
                    }
                }
            }
        }

        return list
    }

    private fun handleEmptyText(string: String, message: String) = if (string.isEmpty()) message else string

    private fun findIdInFavoriteList(id: Long): Boolean {
        listOfAllFavorites.forEach {
            if (it.id == id) {
                return true
            }
        }

        return false
    }

    fun onDestroy() = compositeDisposable.takeIf { it.isDisposed }?.run { dispose() }

    companion object {
        private const val LIMIT = 3
        private const val MIN_ITEMS = 1
        private const val COMICS_TAG = "Comics"
        private const val EVENTS_TAG = "Events"
        private const val STORIES_TAG = "Stories"
        private const val SERIES_TAG = "Series"
        private const val NO_TITLE_FROM_API = "There is no title from de API."
        private const val NO_DESCRIPTION_FROM_API = "There is no description from de API."
    }

}
