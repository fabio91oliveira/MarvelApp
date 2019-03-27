package oliveira.fabio.marvelapp.feature.characterslist.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_regular_list.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.activity.CharacterDetailsActivity
import oliveira.fabio.marvelapp.feature.characterslist.ui.activity.CharactersListActivity
import oliveira.fabio.marvelapp.feature.characterslist.ui.adapter.CharactersAdapter
import oliveira.fabio.marvelapp.feature.characterslist.ui.listener.InfiniteScrollListener
import oliveira.fabio.marvelapp.feature.characterslist.viewmodel.CharactersListViewModel
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.util.Response
import oliveira.fabio.marvelapp.util.extensions.doRotateAnimation
import oliveira.fabio.marvelapp.util.extensions.hideKeyboard
import org.koin.android.viewmodel.ext.android.viewModel


class RegularListFragment : Fragment(), CharactersAdapter.OnClickCharacterListener {

    private val charactersListViewModel: CharactersListViewModel by viewModel()
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

        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            val character = data?.getSerializableExtra(CharactersListActivity.CHARACTER_TAG) as Character
            charactersAdapter.validateCharacterFavorite(character)
            charactersListViewModel.refreshFavoritesList()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_regular_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }
    override fun onLikeButtonClick(character: Character) {
        charactersListViewModel.addRemoveFavorite(character)
    }

    override fun onCharacterClick(character: Character) {
        val intent = Intent(activity, CharacterDetailsActivity::class.java)
        intent.putExtra(CharactersListActivity.CHARACTER_TAG, character)
        intent.putExtra(CharactersListActivity.LIST_OF_FAVORITES_TAG, charactersListViewModel.listOfAllFavorites)
        startActivityForResult(intent, 200)
    }

    override fun onDestroy() {
        super.onDestroy()
        charactersListViewModel.onDestroy()
    }

//    override fun onBackPressed() {
//        when (searchViewToolbar.isVisible()) {
//            true -> searchViewToolbar.closeSearch()
//            false -> super.onBackPressed()
//        }
//    }

    private fun init() {
        hideContent()
        showLoading()
//        setupTabLayout()
        initLiveDatas()
        initRecyclerView()
//        initSearchViewListener()
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
                            if (charactersListViewModel.firstTime) charactersListViewModel.firstTime = false
//                            searchViewToolbar.loading(false)
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
        if (rvCharactersList.layoutManager == null) rvCharactersList.layoutManager = layoutManager
        if (rvCharactersList.adapter == null) rvCharactersList.adapter = charactersAdapter
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
                        oliveira.fabio.marvelapp.R.string.error_try_again
                    ), listener
                )
            }
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        }.show()

    private fun showContent() {
        rvCharactersList.visibility = View.VISIBLE
    }

    private fun hideContent() {
        rvCharactersList.visibility = View.GONE
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
    }

}