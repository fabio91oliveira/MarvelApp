package oliveira.fabio.marvelapp.feature.characterdetails.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function4
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.model.response.*
import oliveira.fabio.marvelapp.model.vo.HeaderItem
import oliveira.fabio.marvelapp.model.vo.Item
import oliveira.fabio.marvelapp.model.vo.SubItem
import oliveira.fabio.marvelapp.util.Event
import oliveira.fabio.marvelapp.util.Response

class CharacterDetailsViewModel(private val charactersRepository: CharactersRepository) : ViewModel() {

    private val compositeDisposable by lazy { CompositeDisposable() }
    val mutableLiveDataEventsList by lazy { MutableLiveData<Event<Response<MutableList<Item>>>>() }
    val listOfAllFavorites by lazy { mutableListOf<Character>() }
    val lastResultsInfos by lazy { mutableListOf<Item>() }

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
                }).map { parseToItem(it) }
                .subscribe({
                    lastResultsInfos.clear()
                    lastResultsInfos.addAll(it)
                    mutableLiveDataEventsList.value = Event(Response.success(it))
                }, {})
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

    private fun parseToItem(it: Four<ComicsResponse, EventsResponse, SeriesResponse, StoriesResponse>): MutableList<Item> {
        val list = mutableListOf<Item>()

        val comics = it.first
        val events = it.second
        val series = it.third
        val stories = it.fourth

        val comicsHeaderItem = HeaderItem(COMICS_TAG)
        list.add(comicsHeaderItem)
        comics.data?.results?.forEachIndexed { i, item ->
            if (i + 1 <= MAX_ITEMS_PER_LIST) {
                item?.title?.run {
                    item.description?.let {
                        list.add(SubItem(this, it))
                    }
                }
            }
        }

        val eventsHeaderItem = HeaderItem(EVENTS_TAG)
        list.add(eventsHeaderItem)
        events.data?.results?.forEachIndexed { i, item ->
            if (i + 1 <= MAX_ITEMS_PER_LIST) {
                item?.title?.run {
                    item.description?.let {
                        list.add(SubItem(this, it))
                    }
                }
            }
        }

        val seriesHeaderItem = HeaderItem(STORIES_TAG)
        list.add(seriesHeaderItem)
        series.data?.results?.forEachIndexed { i, item ->
            if (i + 1 <= MAX_ITEMS_PER_LIST) {
                item?.title?.run {
                    item.description?.let {
                        list.add(SubItem(this, it))
                    }
                }
            }
        }

        val storiesHeaderItem = HeaderItem(SERIES_TAG)
        list.add(storiesHeaderItem)
        stories.data?.results?.forEachIndexed { i, item ->
            if (i + 1 <= MAX_ITEMS_PER_LIST) {
                item?.title?.run {
                    item.description?.let {
                        list.add(SubItem(this, it))
                    }
                }
            }
        }

        return list
    }


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
        private const val MAX_ITEMS_PER_LIST = 3
        private const val COMICS_TAG = "Comics"
        private const val EVENTS_TAG = "Events"
        private const val STORIES_TAG = "Stories"
        private const val SERIES_TAG = "Series"
    }

}
