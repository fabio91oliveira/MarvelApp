package oliveira.fabio.marvelapp.feature.characterslist.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                if (!charactersListViewModel.isQuerySearch && !charactersListViewModel.isFavoriteListPageType()) {
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

    override fun onResume() {
        super.onResume()
        charactersListViewModel.changeToRegularListPageType()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            initLiveDatas()
            initRecyclerView()
            initClickListener()
            initSearchViewListener()
            initSearchViewOnClickListener()
        } ?: run {
            init()
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

    override fun onUpdateClick() = refreshList()

    override fun onDestroy() {
        super.onDestroy()
        charactersListViewModel.onDestroy()
    }

    private fun init() {
        hideContent()
        showLoading()
        initLiveDatas()
        initRecyclerView()
        initClickListener()
        initSearchViewListener()
        initSearchViewOnClickListener()
        charactersListViewModel.getCharactersList()
    }

    private fun initClickListener() {
        imgNoResults.setOnClickListener { refreshList() }
        txtNoResults.setOnClickListener { refreshList() }
    }

    private fun initLiveDatas() {
        charactersListViewModel.mutableLiveDataResults.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response.statusEnum) {
                    Response.StatusEnum.SUCCESS -> {
                        response.data?.run {
                            when (isNotEmpty()) {
                                true -> {
                                    hideNoResultsMessage()
                                    if (charactersListViewModel.isQuerySearch) clearList()
                                    if (charactersListViewModel.isFavoriteListPageType()) {
                                        charactersAdapter.clearResults()
                                    }
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
                                    } else {
                                        showNoResultsMessage()
                                    }
                                    hideContent()
                                }
                            }
                            if (charactersListViewModel.firstTime) charactersListViewModel.firstTime = false
                            activity?.searchViewToolbar?.loading(false)
                            hideLoading()
                        }
                    }
                    Response.StatusEnum.ERROR -> {
                        hideLoading()
//                        hideContent()
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
        rvCharactersRegularList.layoutManager = layoutManager
        rvCharactersRegularList.adapter = charactersAdapter
        startInfiniteScroll()

        if (charactersListViewModel.listOfAllResults.isNotEmpty()) {
            addResults(charactersListViewModel.listOfAllResults)
            showContent()
        }
    }

    private fun clearList() {
        charactersListViewModel.latestResults.clear()
        charactersListViewModel.listOfAllResults.clear()
        charactersListViewModel.offset = 0
        charactersAdapter.clearResults()
        charactersAdapter.notifyDataSetChanged()
    }

    private fun setLatestTotalResult(offset: Int) {
        charactersListViewModel.offset = offset
    }

    private fun addResults(character: List<Character>) = charactersAdapter.addResults(character)
    private fun startInfiniteScroll() = rvCharactersRegularList.addOnScrollListener(infiniteScrollListener)

    private fun showFeedbackToUser(message: String, shortTime: Boolean) =
        Toast.makeText(context, message, if (shortTime) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()

    private fun refreshList() {
        charactersListViewModel.offset = 0
        charactersListViewModel.firstTime
        charactersListViewModel.isQuerySearch = false
        clearList()
        hideContent()
        hideNoResultsMessage()
        showLoading()
        initRecyclerView()
        activity?.searchViewToolbar?.closeSearch()
        infiniteScrollListener.clear()
        charactersListViewModel.getCharactersList()
        goToFirstTab()
    }

    private fun goToFirstTab() {
        activity?.navigation?.selectedItemId = TAB_REGULAR_LIST
    }

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

    private fun showNoResultsMessage() {
        grpNotFound.visibility = View.VISIBLE
    }

    private fun hideNoResultsMessage() {
        grpNotFound.visibility = View.GONE
    }

    companion object {
        const val CHARACTER_TAG = "CHARACTER"
        const val LIST_OF_FAVORITES_TAG = "LIST_OF_FAVORITES"
        private const val REQUEST_CODE_UPDATE_FAVORITE = 200
        private const val TAB_REGULAR_LIST = R.id.action_regular_list

        fun newInstance(): CharacterRegularListFragment {
            return CharacterRegularListFragment()
        }
    }

}