package oliveira.fabio.marvelapp.feature.characterdetails

import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import oliveira.fabio.marvelapp.base.BaseTest
import oliveira.fabio.marvelapp.di.testRemoteModule
import oliveira.fabio.marvelapp.feature.characterdetails.ui.activity.CharacterDetailsActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext.loadKoinModules

@RunWith(AndroidJUnit4::class)
class CharacterDetailsTest : BaseTest() {

    @Rule
    @JvmField
    val rule = ActivityTestRule<CharacterDetailsActivity>(CharacterDetailsActivity::class.java, false, false)

    private fun robots(func: CharacterDetailsRobot.() -> Unit) = CharacterDetailsRobot(rule, server).apply(func)

    @Before
    fun setup() {
        loadKoinModules(testRemoteModule)
    }

    @Test
    fun shouldLoadCharacterDetails() {
        robots {
            initIntent()
            setupIntentDependencies()
            setupCharacterDetailsRequests()
            initActivity()
            shouldShowCharacterDetailsTextsAndImage()
            releaseIntent()
        }
    }

    @Test
    fun shouldLoadCharacterDetailsAsFavorite() {
        robots {
            initIntent()
            setupIntentDependenciesWithFavoritesList()
            setupCharacterDetailsRequests()
            initActivity()
            shouldShowCharacterDetailsTextsAndImageAndFavorite()
            releaseIntent()
        }
    }
}