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
        StandAloneContext.closeKoin()
    }

    @Test
    fun shouldGetDatasByCharacterIdWithSuccess() {
        val id = 1

        Mockito.`when`(charactersRepository.getComics(1))
            .then { Flowable.just(JsonUtil.comicsResponseMocked) }
        Mockito.`when`(charactersRepository.getEvents(1))
            .then { Flowable.just(JsonUtil.eventsResponseMocked) }
        Mockito.`when`(charactersRepository.getSeries(1))
            .then { Flowable.just(JsonUtil.seriesResponseMocked) }
        Mockito.`when`(charactersRepository.getStories(1))
            .then { Flowable.just(JsonUtil.storiesResponseMocked) }

        characterDetailsViewModel.getDatasByCharacterId(id)

        Mockito.verify(charactersRepository).getComics(1)
        Mockito.verify(charactersRepository).getEvents(1)
        Mockito.verify(charactersRepository).getSeries(1)
        Mockito.verify(charactersRepository).getStories(1)

        val status = characterDetailsViewModel.mutableLiveDataEventsList.value?.getContentIfNotHandled()?.statusEnum
        Assert.assertEquals(Response.StatusEnum.SUCCESS, status)
    }

    @Test
    fun shouldGetDatasByCharacterIdWithError() {
        val id = 1

        Mockito.`when`(charactersRepository.getComics(1))
            .then { Flowable.just(Throwable()) }
        Mockito.`when`(charactersRepository.getEvents(1))
            .then { Flowable.just(Throwable()) }
        Mockito.`when`(charactersRepository.getSeries(1))
            .then { Flowable.just(Throwable()) }
        Mockito.`when`(charactersRepository.getStories(1))
            .then { Flowable.just(Throwable()) }

        characterDetailsViewModel.getDatasByCharacterId(id)

        Mockito.verify(charactersRepository).getComics(1)
        Mockito.verify(charactersRepository).getEvents(1)
        Mockito.verify(charactersRepository).getSeries(1)
        Mockito.verify(charactersRepository).getStories(1)

        val status = characterDetailsViewModel.mutableLiveDataEventsList.value?.getContentIfNotHandled()?.statusEnum
        Assert.assertEquals(Response.StatusEnum.ERROR, status)
    }

}