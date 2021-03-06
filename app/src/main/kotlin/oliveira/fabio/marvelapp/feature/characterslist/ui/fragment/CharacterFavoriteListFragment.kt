package oliveira.fabio.marvelapp.feature.characterslist.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_characters_list.*
import kotlinx.android.synthetic.main.fragment_character_favorite_list.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.activity.CharacterDetailsActivity
import oliveira.fabio.marvelapp.feature.characterslist.ui.adapter.CharactersAdapter
import oliveira.fabio.marvelapp.feature.characterslist.viewmodel.CharactersListViewModel
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.util.Response
import org.koin.android.viewmodel.ext.android.sharedViewModel


class CharacterFavoriteListFragment : Fragment(), CharactersAdapter.OnClickCharacterListener {

    private val charactersListViewModel: CharactersListViewModel by sharedViewModel()
    private val charactersAdapter by lazy { CharactersAdapter(this) }
    private val layoutManager by lazy { LinearLayoutManager(context, RecyclerView.VERTICAL, false) }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_UPDATE_FAVORITE) {
            val character = data?.getSerializableExtra(CHARACTER_TAG) as Character
            charactersAdapter.validateCharacterFavorite(character)
            charactersListViewModel.getFavoritesList(character)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_character_favorite_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        charactersListViewModel.changeToFavoriteListPageType()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            initLiveDatas()
            initRecyclerView()
            charactersListViewModel.getFavoritesList()
        } ?: run {
            init()
        }
    }

    override fun onFavoriteButtonClick(character: Character) {
        charactersListViewModel.addRemoveFavorite(character, true)
    }

    override fun onCharacterClick(character: Character) {
        val intent = Intent(activity, CharacterDetailsActivity::class.java)
        intent.putExtra(CHARACTER_TAG, character)
        intent.putExtra(LIST_OF_FAVORITES_TAG, charactersListViewModel.listOfAllFavorites)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_FAVORITE)
    }

    override fun onDestroy() {
        super.onDestroy()
        charactersListViewModel.onDestroy()
    }

    private fun init() {
        hideContent()
        initLiveDatas()
        initRecyclerView()
    }

    private fun initLiveDatas() {
        charactersListViewModel.mutableLiveDataFavorites.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response.statusEnum) {
                    Response.StatusEnum.SUCCESS -> {
                        response.data?.run {
                            when (isNotEmpty()) {
                                true -> {
                                    if (charactersListViewModel.isQuerySearch) clearList()
                                    addResults(this)
                                    showContent()
                                    hideNoFavorites()
                                }
                                else -> {
                                    showNoFavorites()
                                    hideContent()
                                }
                            }
                            if (charactersListViewModel.firstTime) charactersListViewModel.firstTime = false
                            activity?.searchViewToolbar?.loading(false)

                        }
                    }
                    Response.StatusEnum.ERROR -> {
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
        if (activity?.rvCharactersFavoriteList?.layoutManager == null) activity?.rvCharactersFavoriteList?.layoutManager =
            layoutManager
        if (activity?.rvCharactersFavoriteList?.adapter == null) activity?.rvCharactersFavoriteList?.adapter =
            charactersAdapter

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

    private fun addResults(character: List<Character>) {
        charactersAdapter.clearResults()
        charactersAdapter.addResults(character)
    }

    private fun showFeedbackToUser(message: String, shortTime: Boolean) =
        Toast.makeText(context, message, if (shortTime) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()

    private fun showContent() {
        activity?.rvCharactersFavoriteList?.visibility = View.VISIBLE
    }

    private fun hideContent() {
        activity?.rvCharactersFavoriteList?.visibility = View.GONE
    }

    private fun showNoFavorites() {
        grpNoFavorites.visibility = View.VISIBLE
    }

    private fun hideNoFavorites() {
        grpNoFavorites.visibility = View.GONE
    }

    companion object {
        const val CHARACTER_TAG = "CHARACTER"
        const val LIST_OF_FAVORITES_TAG = "LIST_OF_FAVORITES"
        private const val REQUEST_CODE_UPDATE_FAVORITE = 200

        fun newInstance(): CharacterFavoriteListFragment {
            return CharacterFavoriteListFragment()
        }
    }

}