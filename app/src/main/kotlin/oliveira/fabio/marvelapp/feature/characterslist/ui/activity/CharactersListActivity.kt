package oliveira.fabio.marvelapp.feature.characterslist.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_characters_list.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.activity.CharacterDetailsActivity
import oliveira.fabio.marvelapp.feature.characterslist.ui.adapter.CharactersAdapter
import oliveira.fabio.marvelapp.feature.characterslist.ui.custom.CustomSearchViewToolbar
import oliveira.fabio.marvelapp.feature.characterslist.ui.listener.InfiniteScrollListener
import oliveira.fabio.marvelapp.feature.characterslist.viewmodel.CharactersListViewModel
import oliveira.fabio.marvelapp.model.response.Character
import oliveira.fabio.marvelapp.model.response.Response
import org.koin.android.viewmodel.ext.android.viewModel


class CharactersListActivity : AppCompatActivity(), CustomSearchViewToolbar.OnSearchButtonKeyboardPressed,
    CharactersAdapter.OnClickCharacterListener {

    override fun onLikeButtonClick(character: Character) {
        charactersListViewModel.addRemoveFavorite(character)
    }

    private var firstTime = true
    private val charactersListViewModel: CharactersListViewModel by viewModel()
    private val charactersAdapter by lazy { CharactersAdapter(this) }
    private val layoutManager by lazy { LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false) }
    private val infiniteScrollListener by lazy {
        object :
            InfiniteScrollListener(layoutManager, charactersListViewModel.offset) {
            override fun onLoadMore(totalLatestResult: Int) {
                showFeedbackToUser(resources.getString(R.string.characters_list_loading), false)
                setLatestTotalResult(totalLatestResult)
                charactersListViewModel.getCharactersList()
            }
        }
    }

    override fun onCharacterClick(character: Character) {
        Intent(this, CharacterDetailsActivity::class.java).apply {
            putExtra(CHARACTER_TAG, character)
            putExtra(LIST_OF_FAVORITES_TAG, charactersListViewModel.listOfAllFavorites)
            startActivityForResult(this, 200)
        }
    }

    override fun search(s: String) {
        // open new actiity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters_list)

        savedInstanceState?.let {
            initCustomSearchViewToolbar()
            initLiveDatas()
            initRecyclerView()
            showContent()
        } ?: run {
            init()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            val character = data?.getSerializableExtra(CHARACTER_TAG) as Character
            charactersAdapter.validateCharacterFavorite(character)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showInitialLoading()
        charactersListViewModel.onDestroy()
    }

    override fun onBackPressed() {
        when (searchViewToolbar.isVisible()) {
            true -> searchViewToolbar.closeSearch()
            false -> super.onBackPressed()
        }
    }

    private fun init() {
        showInitialLoading()
        initCustomSearchViewToolbar()
        initLiveDatas()
        initRecyclerView()
        charactersListViewModel.getCharactersList()
    }

    private fun initLiveDatas() {
        charactersListViewModel.mutableLiveDataResults.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response.statusEnum) {
                    Response.StatusEnum.SUCCESS -> {
                        response.data?.run {
                            when (isNotEmpty()) {
                                true -> {
                                    addResults(this)
                                    showContent()
                                    if (firstTime.not()) {
                                        showFeedbackToUser(
                                            resources.getString(R.string.characters_list_has_been_loaded),
                                            true
                                        )
                                    }
                                }
                                else -> {
                                    if (firstTime.not()) {
                                        showFeedbackToUser(
                                            resources.getString(R.string.characters_list_no_more_results),
                                            true
                                        )
                                    }
                                }
                            }
                            if (firstTime) firstTime = false
                            hideInitialLoading()

                        }
                    }
                    Response.StatusEnum.ERROR -> {
                        hideInitialLoading()
                        showFeedbackToUser(
                            resources.getString(R.string.error_dialog_network_error_description),
                            false,
                            View.OnClickListener {
                                charactersListViewModel.getCharactersList()
                                showFeedbackToUser(resources.getString(R.string.characters_list_loading), false)
                            })
                    }
                }
            }
        })
    }

    private fun initRecyclerView() {
        rvCharactersList.layoutManager = layoutManager
        rvCharactersList.adapter = charactersAdapter
        startInfiniteScroll()

        if (charactersListViewModel.lastestResults.isNotEmpty()) {
            addResults(charactersListViewModel.lastestResults)
            showContent()
        }
    }

    private fun initCustomSearchViewToolbar() = searchViewToolbar.setListener(this)

    private fun setLatestTotalResult(offset: Int) {
        charactersListViewModel.offset = offset
    }

    private fun addResults(character: List<Character>) = charactersAdapter.addResults(character)
    private fun startInfiniteScroll() = rvCharactersList.addOnScrollListener(infiniteScrollListener)

    private fun showFeedbackToUser(message: String, shortTime: Boolean, listener: View.OnClickListener? = null) =
        Snackbar.make(container, message, if (shortTime) Snackbar.LENGTH_SHORT else Snackbar.LENGTH_LONG).apply {
            if (shortTime) {
                setAction(
                    resources.getString(
                        oliveira.fabio.marvelapp.R.string.snack_bar_hide
                    ), listener
                )
            } else {
                setAction(
                    resources.getString(
                        oliveira.fabio.marvelapp.R.string.error_dialog_try_again
                    ), listener
                )
            }
            view.setBackgroundColor(ContextCompat.getColor(this@CharactersListActivity, R.color.colorPrimaryDark))
        }.show()

    private fun showContent() {
        rvCharactersList.visibility = View.VISIBLE
    }

    private fun hideContent() {
        rvCharactersList.visibility = View.GONE
    }

    private fun showInitialLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideInitialLoading() {
        progressBar.visibility = View.GONE
    }

    companion object {
        const val CHARACTER_TAG = "CHARACTER"
        const val LIST_OF_FAVORITES_TAG = "LIST_OF_FAVORITES"
    }

}
