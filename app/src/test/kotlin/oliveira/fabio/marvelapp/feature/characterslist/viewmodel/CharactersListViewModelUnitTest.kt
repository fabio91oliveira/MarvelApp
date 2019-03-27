package oliveira.fabio.marvelapp.feature.characterslist.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.Flowable
import oliveira.fabio.marvelapp.di.unitTestModule
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.model.repository.CharactersRepository
import oliveira.fabio.marvelapp.util.JsonUtil
import oliveira.fabio.marvelapp.util.Response
import org.junit.*
import org.koin.standalone.StandAloneContext
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito

class CharactersListViewModelUnitTest : KoinTest {

    @Rule
    @JvmField
    val testSchedulerRule = InstantTaskExecutorRule()

    private val charactersRepository: CharactersRepository by inject()
    private val charactersListViewModel: CharactersListViewModel by inject()

    @Before
    fun before() {
        StandAloneContext.startKoin(listOf(unitTestModule))
    }

    @After
    fun after() {
        StandAloneContext.closeKoin()
    }

    @Test
    fun shouldGetFavoritesList() {
        charactersListViewModel.getFavoritesList()

        charactersListViewModel.mutableLiveDataResults.value?.getContentIfNotHandled()?.let {
            Assert.assertEquals(it.statusEnum, Response.StatusEnum.SUCCESS)
        }
    }

    @Test
    fun shouldRefreshFavoritesList() {
        val characterList = createCharactersList()

        Mockito.`when`(charactersRepository.getAllFavorites())
            .then { Flowable.just(characterList) }

//        charactersListViewModel.refreshFavoritesList()

        Mockito.verify(charactersRepository).getAllFavorites()
        Assert.assertEquals(characterList, charactersListViewModel.listOfAllFavorites)
    }

    @Test
    fun shouldGetCharactersListWithNoNameWithSuccess() {
        val characterList = createCharactersList()
        val limit = 20

        Mockito.`when`(charactersRepository.getAllFavorites())
            .then { Flowable.just(characterList) }

        Mockito.`when`(charactersRepository.getCharacters(limit, charactersListViewModel.offset))
            .then { Flowable.just(JsonUtil.characterResponseMocked) }

        charactersListViewModel.getCharactersList()

        Mockito.verify(charactersRepository).getAllFavorites()
        Mockito.verify(charactersRepository).getCharacters(limit, charactersListViewModel.offset)
        Assert.assertEquals(characterList, charactersListViewModel.listOfAllFavorites)
        Assert.assertNotNull(charactersListViewModel.mutableLiveDataResults.value)

        val status = charactersListViewModel.mutableLiveDataResults.value?.getContentIfNotHandled()?.statusEnum
        Assert.assertEquals(Response.StatusEnum.SUCCESS, status)
    }

    @Test
    fun shouldGetCharactersListWithNoNameWithError() {
        val limit = 20

        Mockito.`when`(charactersRepository.getAllFavorites())
            .then { Flowable.just(Throwable()) }

        Mockito.`when`(charactersRepository.getCharacters(limit, charactersListViewModel.offset))
            .then { Flowable.just(Throwable()) }

        charactersListViewModel.getCharactersList()

        Mockito.verify(charactersRepository).getAllFavorites()
        Mockito.verify(charactersRepository).getCharacters(limit, charactersListViewModel.offset)

        val status = charactersListViewModel.mutableLiveDataResults.value?.getContentIfNotHandled()?.statusEnum
        Assert.assertEquals(Response.StatusEnum.ERROR, status)
    }

    @Test
    fun shouldGetCharactersListWithNameWithSuccess() {
        val characterList = createCharactersList()
        val limit = 20
        val name = "name"

        Mockito.`when`(charactersRepository.getAllFavorites())
            .then { Flowable.just(characterList) }

        Mockito.`when`(charactersRepository.getCharacters(limit, charactersListViewModel.offset, name))
            .then { Flowable.just(JsonUtil.characterResponseMocked) }

        charactersListViewModel.getCharactersList(name)

        Mockito.verify(charactersRepository).getAllFavorites()
        Mockito.verify(charactersRepository).getCharacters(limit, charactersListViewModel.offset, name)
        Assert.assertEquals(characterList, charactersListViewModel.listOfAllFavorites)
        Assert.assertNotNull(charactersListViewModel.mutableLiveDataResults.value)

        val status = charactersListViewModel.mutableLiveDataResults.value?.getContentIfNotHandled()?.statusEnum
        Assert.assertEquals(Response.StatusEnum.SUCCESS, status)
    }

    @Test
    fun shouldGetCharactersListWithNameWithError() {
        val limit = 20
        val name = "name"

        Mockito.`when`(charactersRepository.getAllFavorites())
            .then { Flowable.just(Throwable()) }

        Mockito.`when`(charactersRepository.getCharacters(limit, charactersListViewModel.offset, name))
            .then { Flowable.just(Throwable()) }

        charactersListViewModel.getCharactersList(name)

        Mockito.verify(charactersRepository).getAllFavorites()
        Mockito.verify(charactersRepository).getCharacters(limit, charactersListViewModel.offset, name)
        Assert.assertNotNull(charactersListViewModel.mutableLiveDataResults.value)

        val status = charactersListViewModel.mutableLiveDataResults.value?.getContentIfNotHandled()?.statusEnum
        Assert.assertEquals(Response.StatusEnum.ERROR, status)
    }

    @Test
    fun shouldAddFavorite() {
        val characterList = createCharactersList()
        val character = createCharacterToAdd()
        val id = 1L

        Mockito.`when`(charactersRepository.deleteFavorite(character))
            .then { Flowable.just(id) }
        Mockito.`when`(charactersRepository.getAllFavorites())
            .then { Flowable.just(characterList) }
        Mockito.`when`(charactersRepository.addFavoriteCharacter(character))
            .then { Flowable.just(id) }

        charactersListViewModel.addRemoveFavorite(character)

        Mockito.verify(charactersRepository).deleteFavorite(character)
        Mockito.verify(charactersRepository).getAllFavorites()
        Mockito.verify(charactersRepository).addFavoriteCharacter(character)

        Assert.assertEquals(characterList, charactersListViewModel.listOfAllFavorites)
    }

    @Test
    fun shouldRemoveFavorite() {
        val characterList = createCharactersList()
        val character = createCharacterToRemove()
        val id = 1L

        Mockito.`when`(charactersRepository.deleteFavorite(character))
            .then { Flowable.just(id) }
        Mockito.`when`(charactersRepository.getAllFavorites())
            .then { Flowable.just(characterList) }
        Mockito.`when`(charactersRepository.addFavoriteCharacter(character))
            .then { Flowable.just(id) }

        charactersListViewModel.addRemoveFavorite(character)

        Mockito.verify(charactersRepository).deleteFavorite(character)
        Mockito.verify(charactersRepository).getAllFavorites()
        Mockito.verify(charactersRepository).addFavoriteCharacter(character)

        Assert.assertEquals(characterList, charactersListViewModel.listOfAllFavorites)
    }

    private fun createCharacterToAdd() = Character("name", false, "description", "resourceURI", "urlImage")
    private fun createCharacterToRemove() = Character("name", true, "description", "resourceURI", "urlImage")

    private fun createCharactersList() = arrayListOf<Character>().apply {
        val character = Character("name", true, "description", "resourceURI", "urlImage")
        add(character)
    }

}