package oliveira.fabio.marvelapp.feature.characterslist.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
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
    CharactersAdapter.OnClickCharacterListener, TextWatcher {

    private var firstTime = true
    private val charactersListViewModel: CharactersListViewModel by viewModel()
    private val charactersAdapter by lazy { CharactersAdapter(this) }
    private val layoutManager by lazy { LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false) }
    private val infiniteScrollListener by lazy {
        object :
            InfiniteScrollListener(layoutManager, charactersListViewModel.offset) {
            override fun onLoadMore(totalLatestResult: Int) {
                if (!charactersListViewModel.isQuerySearch) {
                    showFeedbackToUser(resources.getString(R.string.characters_list_loading), false)
                    setLatestTotalResult(totalLatestResult)
                    charactersListViewModel.getCharactersList()
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isNotEmpty()) {
            unselectTabs()
            charactersListViewModel.isQuerySearch = true
            charactersListViewModel.getCharactersList(s.toString())
            searchViewToolbar.loading(true)
        }
    }

    override fun onLikeButtonClick(character: Character) {
        charactersListViewModel.addRemoveFavorite(character)
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
            if (savedInstanceState.getInt(CURRENT_TAB) != null) tabLayout.getTabAt(savedInstanceState.getInt(CURRENT_TAB))?.select()
            setupTabLayout()
            initLiveDatas()
            initRecyclerView()
            initSearchViewListener()
        } ?: run {
            init()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_TAB, tabLayout.selectedTabPosition)
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
        hideContent()
        showInitialLoading()
        setupTabLayout()
        initLiveDatas()
        initRecyclerView()
        initSearchViewListener()
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
                                    if (charactersListViewModel.isQuerySearch) clearList()
                                    charactersListViewModel.lastestResults.addAll(this)
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
                                    hideContent()
                                    showFeedbackToUser(
                                        resources.getString(R.string.characters_list_no_more_results),
                                        true
                                    )
                                }
                            }
                            if (firstTime) firstTime = false
                            searchViewToolbar.loading(false)
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
//            showContent()
        }
    }

    private fun clearList() {
        charactersListViewModel.lastestResults
        charactersListViewModel.offset = 0
        charactersAdapter.clearResults()
        charactersAdapter.notifyDataSetChanged()
    }

    private fun setupTabLayout() {
        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            tab.requestLayout()
            tab.setOnClickListener {
                when (tab.contentDescription) {
                    resources.getString(R.string.characters_list_regular_list) -> {
                        // TODO
                    }
                    resources.getString(R.string.characters_list_favorite_list) -> {
                        // TODO
                    }
                }
                tab.isSelected = true
                changeTabLayoutSelectedTabColor(R.color.colorAccent)
                charactersListViewModel.isQuerySearch = false
                charactersListViewModel.offset = 0
                clearList()
                hideContent()
                showInitialLoading()
                initRecyclerView()
                charactersListViewModel.getCharactersList()
            }
        }
    }

    private fun initSearchViewListener() = searchViewToolbar.setTextWatcherListener(this)

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

    private fun unselectTabs() {
        changeTabLayoutSelectedTabColor(R.color.colorPrimary)
        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            tab.isSelected = false
        }
    }

    private fun changeTabLayoutSelectedTabColor(color: Int) =
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, color))

    companion object {
        const val CHARACTER_TAG = "CHARACTER"
        const val LIST_OF_FAVORITES_TAG = "LIST_OF_FAVORITES"
        private const val CURRENT_TAB = "CURRENT_TAB"
    }

}
