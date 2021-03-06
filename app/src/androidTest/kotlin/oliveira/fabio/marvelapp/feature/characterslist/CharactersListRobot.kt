package oliveira.fabio.marvelapp.feature.characterslist

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import br.com.concretesolutions.requestmatcher.RequestMatcherRule
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.activity.CharacterDetailsActivity
import oliveira.fabio.marvelapp.feature.characterslist.ui.activity.CharactersListActivity


class CharactersListRobot(
    private val rule: ActivityTestRule<CharactersListActivity>,
    private val server: RequestMatcherRule
) {

    companion object {
        private const val API_MARVEL_CHARACTERS_LIST_REQUEST = "/v1/public/characters"
        private const val API_MARVEL_CHARACTERS_LIST_JSON_RESPONSE = "characters_list.json"
        private const val API_MARVEL_CHARACTERS_LIST_JSON_RESPONSE_NO_RESULT = "characters_list_no_result.json"

        private const val API_MARVEL_CHARACTER_DETAILS_COMICS_REQUEST = "/v1/public/characters/1017100/comics"
        private const val API_MARVEL_CHARACTER_DETAILS_COMICS_RESPONSE = "character_details_comics.json"
        private const val API_MARVEL_CHARACTER_DETAILS_EVENTS_REQUEST = "/v1/public/characters/1017100/events"
        private const val API_MARVEL_CHARACTER_DETAILS_EVENTS_RESPONSE = "character_details_events.json"
        private const val API_MARVEL_CHARACTER_DETAILS_SERIES_REQUEST = "/v1/public/characters/1017100/series"
        private const val API_MARVEL_CHARACTER_DETAILS_SERIES_RESPONSE = "character_details_series.json"
        private const val API_MARVEL_CHARACTER_DETAILS_STORIES_REQUEST = "/v1/public/characters/1017100/stories"
        private const val API_MARVEL_CHARACTER_DETAILS_STORIES_RESPONSE = "character_details_stories.json"

        private const val PARAMETER_FOR_RESEARCH = "a"
        private const val SERVER_ERROR_CODE = 500
    }

    val intent = Intent()

    fun initActivity() {
        rule.launchActivity(intent)
    }

    fun initIntent() {
        Intents.init()
    }

    fun releaseIntent() {
        Intents.release()
    }

    fun setupCharactersListRequest() {
        server.addFixture(API_MARVEL_CHARACTERS_LIST_JSON_RESPONSE)
            .ifRequestMatches()
            .pathIs(API_MARVEL_CHARACTERS_LIST_REQUEST)
    }

    fun setupCharactersListRequestError() {
        server.addFixture(SERVER_ERROR_CODE, API_MARVEL_CHARACTERS_LIST_JSON_RESPONSE_NO_RESULT)
            .ifRequestMatches()
            .pathIs(API_MARVEL_CHARACTERS_LIST_REQUEST)
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

    fun shouldRecyclerViewDisplay() {
        onView(withId(R.id.rvCharactersRegularList)).check(matches(isDisplayed()))
    }

    fun shouldShouldErrorMessage() {
        onView(withId(R.id.txtWarning))
            .check(matches(withText(R.string.characters_list_error)))
    }

    fun shouldClickFirstItem() {
        onView(withId(R.id.rvCharactersRegularList))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
    }

    fun shouldCharacterDetailActivityOpen() {
        intended(hasComponent(CharacterDetailsActivity::class.java.name))
    }

    fun shouldClickSearchButtonMenu() {
        onView(withId(R.id.searchOpenButton)).check(matches(isDisplayed())).perform(click())
    }

    fun shouldTypeOnSearchViewInput() {
        onView(withId(R.id.searchEditText))
            .perform(typeText(PARAMETER_FOR_RESEARCH))
    }
}