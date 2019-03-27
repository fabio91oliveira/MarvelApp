package oliveira.fabio.marvelapp.feature.characterslist.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_characters_list.*
import kotlinx.android.synthetic.main.fragment_character_regular_list.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.activity.CharacterDetailsActivity
import oliveira.fabio.marvelapp.feature.characterslist.ui.adapter.CharactersAdapter
import oliveira.fabio.marvelapp.feature.characterslist.ui.custom.CustomSearchViewToolbar
import oliveira.fabio.marvelapp.feature.characterslist.ui.listener.InfiniteScrollListener
import oliveira.fabio.marvelapp.feature.characterslist.viewmodel.CharactersListViewModel
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.util.Response
import oliveira.fabio.marvelapp.util.extensions.doRotateAnimation
import oliveira.fabio.marvelapp.util.extensions.hideKeyboard
import org.koin.android.viewmodel.ext.android.sharedViewModel


class CharacterRegularListFragment : Fragment(), CharactersAdapter.OnClickCharacterListener,
    CustomSearchViewToolbar.OnClickListener, TextWatcher {

    private val charactersListViewModel: CharactersListViewModel by sharedViewModel()

    private fun initSearchViewListener() = activity?.searchViewToolbar?.setTextWatcherListener(this)
    private fun initSearchViewOnClickListener() = activity?.searchViewToolbar?.setOnClickListener(this)

    private val charactersAdapter by lazy { CharactersAdapter(this) }
    private val layoutManager by lazy { LinearLayoutManager(context, RecyclerView.VERTICAL, false) }
    private val infiniteScrollListener by lazy {
        object :
            InfiniteScrollListener(layoutManager, charactersListViewModel.offset) {
            override fun onLoadMore(totalLatestResult: Int) {
                if (!charactersListViewModel.isQuerySearch && !charactersListViewModel.isFavoriteList) {
                    showFeedbackToUser(resources.getString(R.string.characters_list_loading), false)
                    setLatestTotalResult(totalLatestResult)
                    charactersListViewModel.getCharactersList()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_UPDATE_FAVORITE) {
            val character = data?.getSerializableExtra(CHARACTER_TAG) as Character
            charactersAdapter.validateCharacterFavorite(character)
            charactersListViewModel.addRemoveFavorite(character)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_character_regular_list, container, false)
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (!firstTimeToBeVisible) {
//            if (isVisibleToUser) {
//                charactersListViewModel.refreshFavoriteList()
//            }
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        activity?.tabLayout?.selectedTabPosition?.let { outState.putInt(CURRENT_TAB, it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            activity?.tabLayout?.getTabAt(it.getInt(CURRENT_TAB))?.select()
            initLiveDatas()
            initRecyclerView()
            initSearchViewListener()
            initSearchViewOnClickListener()
        } ?: run {
            init()
            charactersListViewModel.offset = 33
        }
    }

    override fun onLikeButtonClick(character: Character) {
        charactersListViewModel.addRemoveFavorite(character)
    }

    override fun onCharacterClick(character: Character) {
        val intent = Intent(activity, CharacterDetailsActivity::class.java)
        intent.putExtra(CHARACTER_TAG, character)
        intent.putExtra(LIST_OF_FAVORITES_TAG, charactersListViewModel.listOfAllFavorites)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_FAVORITE)
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isNotEmpty()) {
            charactersListViewModel.isQuerySearch = true
            charactersListViewModel.getCharactersList(s.toString())
            activity?.searchViewToolbar?.loading(true)
        }
    }

    override fun onUpdateClick() {
        charactersListViewModel.offset = 0
        charactersListViewModel.firstTime
        clearList()
        hideContent()
        showLoading()
        initRecyclerView()
        activity?.searchViewToolbar?.closeSearch()
        infiniteScrollListener.clear()
        charactersListViewModel.getCharactersList()
        goToFirstTab()
    }

    override fun onSearchClick() {
        goToFirstTab()
    }

    override fun onDestroy() {
        super.onDestroy()
        charactersListViewModel.onDestroy()
    }

    private fun init() {
        hideContent()
        showLoading()
        initLiveDatas()
        initRecyclerView()
        initSearchViewListener()
        initSearchViewOnClickListener()
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
                                    charactersListViewModel.latestResults.addAll(this)
                                    addResults(this)
                                    showContent()
                                    if (charactersListViewModel.firstTime.not()) {
                                        showFeedbackToUser(
                                            resources.getString(R.string.characters_list_has_been_loaded),
                                            true
                                        )
                                    }
                                }
                                else -> {
                                    if (!charactersListViewModel.isQuerySearch) {
                                        val message =
                                            if (charactersListViewModel.firstTime) resources.getString(R.string.characters_list_no_results) else resources.getString(
                                                R.string.characters_list_no_more_results
                                            )
                                        showFeedbackToUser(message, true)
                                    }
                                    hideContent()
                                }
                            }
                            if (charactersListViewModel.firstTime) charactersListViewModel.firstTime =
                                false
                            activity?.searchViewToolbar?.loading(false)
                            hideLoading()
                        }
                    }
                    Response.StatusEnum.ERROR -> {
                        hideLoading()
                        showFeedbackToUser(
                            resources.getString(R.string.characters_list_error_network_error_description),
                            false
                        )
                    }
                }
            }
        })
    }


    private fun initRecyclerView() {
        if (rvCharactersRegularList.layoutManager == null) rvCharactersRegularList.layoutManager = layoutManager
        if (rvCharactersRegularList.adapter == null) rvCharactersRegularList.adapter = charactersAdapter
        startInfiniteScroll()

        if (charactersListViewModel.latestResults.isNotEmpty()) {
            addResults(charactersListViewModel.latestResults)
            showContent()
        }
    }

    private fun clearList() {
        charactersListViewModel.latestResults.clear()
        charactersListViewModel.offset = 0
        charactersAdapter.clearResults()
        charactersAdapter.notifyDataSetChanged()
    }

    private fun setLatestTotalResult(offset: Int) {
        charactersListViewModel.offset = offset
    }

    private fun addResults(character: List<Character>) = charactersAdapter.addResults(character)
    private fun startInfiniteScroll() = rvCharactersRegularList.addOnScrollListener(infiniteScrollListener)

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
                        oliveira.fabio.marvelapp.R.string.error_try_again
                    ), listener
                )
            }
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }.show()

    private fun goToFirstTab() = activity?.tabLayout?.getTabAt(TAB_REGULAR_LIST)?.select()

    private fun showContent() {
        rvCharactersRegularList.visibility = View.VISIBLE
    }

    private fun hideContent() {
        rvCharactersRegularList.visibility = View.GONE
    }

    private fun showLoading() {
        loading.doRotateAnimation()
        loading.visibility = View.VISIBLE
        loading.hideKeyboard()
    }

    private fun hideLoading() {
        loading.clearAnimation()
        loading.visibility = View.GONE
    }

    companion object {
        const val CHARACTER_TAG = "CHARACTER"
        const val LIST_OF_FAVORITES_TAG = "LIST_OF_FAVORITES"
        private const val CURRENT_TAB = "CURRENT_TAB"
        private const val REQUEST_CODE_UPDATE_FAVORITE = 200
        private const val TAB_REGULAR_LIST = 0
    }

}