package oliveira.fabio.marvelapp.feature.characterslist

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import oliveira.fabio.marvelapp.base.BaseTest
import oliveira.fabio.marvelapp.di.testRemoteModule
import oliveira.fabio.marvelapp.feature.characterslist.ui.activity.CharactersListActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext.loadKoinModules

@RunWith(AndroidJUnit4::class)
class CharactersListTest : BaseTest() {

    @Rule
    @JvmField
    val rule = ActivityTestRule<CharactersListActivity>(CharactersListActivity::class.java, false, false)

    private fun robots(func: CharactersListRobot.() -> Unit) = CharactersListRobot(rule, server).apply(func)

    @Before
    fun setup() {
        loadKoinModules(testRemoteModule)
    }

    @Test
    fun shouldExistAtLeastOneItem() {
        robots {
            setupCharactersListRequest()
            initActivity()
            shouldRecyclerViewDisplay()
        }
    }

    @Test
    fun shouldAppearsCharacterDetailsScreen() {
        robots {
            initIntent()
            setupCharactersListRequest()
            setupCharacterDetailsRequests()
            initActivity()
            shouldClickFirstItem()
            shouldCharacterDetailActivityOpen()
            releaseIntent()
        }
    }

    @Test
    fun shouldSearchByTextAndExistAtLeastOneItem() {
        robots {
            setupCharactersListRequest()
            setupCharactersListRequest()
            initActivity()
            shouldClickSearchButtonMenu()
            shouldTypeOnSearchViewInput()
            shouldRecyclerViewDisplay()
        }
    }

    @Test
    fun shouldShowErrorScreen() {
        robots {
            setupCharactersListRequestError()
            initActivity()
            shouldShouldErrorMessage()
        }
    }
}