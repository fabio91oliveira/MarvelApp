package oliveira.fabio.marvelapp.feature.characterdetails.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.Flowable
import oliveira.fabio.marvelapp.di.unitTestModule
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.util.JsonUtil
import oliveira.fabio.marvelapp.util.Response
import org.junit.*
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito

class CharacterDetailsViewModelUnitTest : KoinTest {

    @Rule
    @JvmField
    val testSchedulerRule = InstantTaskExecutorRule()

    private val charactersRepository: CharactersRepository by inject()
    private val characterDetailsViewModel: CharacterDetailsViewModel by inject()

    @Before
    fun before() {
        StandAloneContext.startKoin(listOf(unitTestModule))
    }

    @After
    fun after() {
        StandAloneContext.stopKoin()
    }

    @Test
    fun shouldGetDatasByCharacterIdWithSuccess() {
        val id = 1
        val limit = 3
        val orderBy = "modified"

        Mockito.`when`(charactersRepository.getComics(id, limit, orderBy))
            .then { Flowable.just(JsonUtil.comicsResponseMocked) }
        Mockito.`when`(charactersRepository.getEvents(id, limit, orderBy))
            .then { Flowable.just(JsonUtil.eventsResponseMocked) }
        Mockito.`when`(charactersRepository.getSeries(id, limit, orderBy))
            .then { Flowable.just(JsonUtil.seriesResponseMocked) }
        Mockito.`when`(charactersRepository.getStories(id, limit, orderBy))
            .then { Flowable.just(JsonUtil.storiesResponseMocked) }

        characterDetailsViewModel.getDatasByCharacterId(id)

        Mockito.verify(charactersRepository).getComics(id, limit, orderBy)
        Mockito.verify(charactersRepository).getEvents(id, limit, orderBy)
        Mockito.verify(charactersRepository).getSeries(id, limit, orderBy)
        Mockito.verify(charactersRepository).getStories(id, limit, orderBy)

        val status = characterDetailsViewModel.mutableLiveDataEventsList.value?.getContentIfNotHandled()?.statusEnum
        Assert.assertEquals(Response.StatusEnum.SUCCESS, status)
    }

    @Test
    fun shouldGetDatasByCharacterIdWithError() {
        val id = 1
        val limit = 3
        val orderBy = "modified"

        Mockito.`when`(charactersRepository.getComics(id, limit, orderBy))
            .then { Flowable.just(Throwable()) }
        Mockito.`when`(charactersRepository.getEvents(id, limit, orderBy))
            .then { Flowable.just(Throwable()) }
        Mockito.`when`(charactersRepository.getSeries(id, limit, orderBy))
            .then { Flowable.just(Throwable()) }
        Mockito.`when`(charactersRepository.getStories(id, limit, orderBy))
            .then { Flowable.just(Throwable()) }

        characterDetailsViewModel.getDatasByCharacterId(id)

        Mockito.verify(charactersRepository).getComics(1, limit, orderBy)
        Mockito.verify(charactersRepository).getEvents(1, limit, orderBy)
        Mockito.verify(charactersRepository).getSeries(1, limit, orderBy)
        Mockito.verify(charactersRepository).getStories(1, limit, orderBy)

        val status = characterDetailsViewModel.mutableLiveDataEventsList.value?.getContentIfNotHandled()?.statusEnum
        Assert.assertEquals(Response.StatusEnum.ERROR, status)
    }

}