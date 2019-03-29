package oliveira.fabio.marvelapp.feature.characterdetails

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import br.com.concretesolutions.requestmatcher.RequestMatcherRule
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.activity.CharacterDetailsActivity
import oliveira.fabio.marvelapp.model.persistence.Character

class CharacterDetailsRobot(
    private val rule: ActivityTestRule<CharacterDetailsActivity>,
    private val server: RequestMatcherRule
) {

    companion object {
        private const val CHARACTER_TAG = "CHARACTER"
        private const val LIST_OF_FAVORITES_TAG = "LIST_OF_FAVORITES"
        private val character by lazy {
            Character("", false, "", "", "").apply {
                id = 1017100
            }
        }

        private const val API_MARVEL_CHARACTER_DETAILS_COMICS_REQUEST = "/v1/public/characters/1017100/comics"
        private const val API_MARVEL_CHARACTER_DETAILS_COMICS_RESPONSE = "character_details_comics.json"
        private const val API_MARVEL_CHARACTER_DETAILS_EVENTS_REQUEST = "/v1/public/characters/1017100/events"
        private const val API_MARVEL_CHARACTER_DETAILS_EVENTS_RESPONSE = "character_details_events.json"
        private const val API_MARVEL_CHARACTER_DETAILS_SERIES_REQUEST = "/v1/public/characters/1017100/series"
        private const val API_MARVEL_CHARACTER_DETAILS_SERIES_RESPONSE = "character_details_series.json"
        private const val API_MARVEL_CHARACTER_DETAILS_STORIES_REQUEST = "/v1/public/characters/1017100/stories"
        private const val API_MARVEL_CHARACTER_DETAILS_STORIES_RESPONSE = "character_details_stories.json"
    }

    val intent = Intent()

    fun setupIntentDependencies() {
        intent.putExtra(CHARACTER_TAG, character)
        intent.putExtra(LIST_OF_FAVORITES_TAG, arrayListOf<Character>())
    }

    fun setupIntentDependenciesWithFavoritesList() {
        character.isFavorite = true
        intent.putExtra(CHARACTER_TAG, character)
        intent.putExtra(LIST_OF_FAVORITES_TAG, arrayListOf<Character>().apply { add(character) })
    }

    fun initIntent() {
        Intents.init()
    }

    fun releaseIntent() {
        Intents.release()
    }


    fun initActivity() {
        rule.launchActivity(intent)
    }

    fun setupCharacterDetailsRequests() {
        server.addFixture(API_MARVEL_CHARACTER_DETAILS_COMICS_RESPONSE)
            .ifRequestMatches()
            .pathIs(API_MARVEL_CHARACTER_DETAILS_COMICS_REQUEST)

        server.addFixture(API_MARVEL_CHARACTER_DETAILS_EVENTS_RESPONSE)
            .ifRequestMatches()
            .pathIs(API_MARVEL_CHARACTER_DETAILS_EVENTS_REQUEST)

        server.addFixture(API_MARVEL_CHARACTER_DETAILS_SERIES_RESPONSE)
            .ifRequestMatches()
            .pathIs(API_MARVEL_CHARACTER_DETAILS_SERIES_REQUEST)

        server.addFixture(API_MARVEL_CHARACTER_DETAILS_STORIES_RESPONSE)
            .ifRequestMatches()
            .pathIs(API_MARVEL_CHARACTER_DETAILS_STORIES_REQUEST)
    }

    fun shouldShowCharacterDetailsTextsAndImage() {
        onView(ViewMatchers.withId(R.id.imgCharacter))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.txtName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.txtCharacterDescription))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.rvCharacterInfoList))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.chkFavorite)).check(matches(isNotChecked()))
    }

    fun shouldShowCharacterDetailsTextsAndImageAndFavorite() {
        onView(ViewMatchers.withId(R.id.imgCharacter))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.txtName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.txtCharacterDescription))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(ViewMatchers.withId(R.id.rvCharacterInfoList))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        onView(withId(R.id.chkFavorite)).check(matches(isChecked()))
    }

    fun shouldClickFavoriteCheck() {
        onView(withId(R.id.chkFavorite)).perform(click())
        onView(withId(R.id.chkFavorite)).check(matches(isChecked()))
    }

}