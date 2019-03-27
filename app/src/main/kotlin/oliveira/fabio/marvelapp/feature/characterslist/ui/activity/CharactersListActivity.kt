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
import kotlinx.android.synthetic.main.fragment_regular_list.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.activity.CharacterDetailsActivity
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.FavoriteListFragment
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.RegularListFragment
import oliveira.fabio.marvelapp.feature.characterslist.ui.adapter.CharactersAdapter
import oliveira.fabio.marvelapp.feature.characterslist.ui.adapter.TabAdapter
import oliveira.fabio.marvelapp.feature.characterslist.ui.listener.InfiniteScrollListener
import oliveira.fabio.marvelapp.feature.characterslist.viewmodel.CharactersListViewModel
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.util.Response
import oliveira.fabio.marvelapp.util.extensions.doRotateAnimation
import oliveira.fabio.marvelapp.util.extensions.hideKeyboard
import org.koin.android.viewmodel.ext.android.viewModel

class CharactersListActivity : AppCompatActivity(), TextWatcher {

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isNotEmpty()) {
//            unselectTabs()
//            charactersListViewModel.isQuerySearch = true
//            charactersListViewModel.getCharactersList(s.toString())
            searchViewToolbar.loading(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters_list)

        val adapter = TabAdapter(supportFragmentManager)
        adapter.addFragment(RegularListFragment(), "Tab 1")
        adapter.addFragment(FavoriteListFragment(), "Tab 2")
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

//        savedInstanceState?.let {
//            if (savedInstanceState.getInt(CURRENT_TAB) != null) tabLayout.getTabAt(savedInstanceState.getInt(CURRENT_TAB))?.select()
//            setupTabLayout()
//            initLiveDatas()
//            initRecyclerView()
//            initSearchViewListener()
//        } ?: run {
//            init()
//        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_TAB, tabLayout.selectedTabPosition)
    }



//    private fun setupTabLayout() {
//        for (i in 0 until tabLayout.tabCount) {
//            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
//            tab.requestLayout()
//            tab.setOnClickListener {
//                when (tab.contentDescription) {
//                    resources.getString(R.string.characters_list_regular_list) -> {
//                        tabClickAction(tab)
//                        charactersListViewModel.getCharactersList()
//                    }
//                    resources.getString(R.string.characters_list_favorite_list) -> {
//                        tabClickAction(tab)
//                        charactersListViewModel.isFavoriteList = true
//                        charactersListViewModel.getFavoritesList()
//                    }
//                }
//
//            }
//        }
//    }

//    private fun tabClickAction(tab: View) {
//        tab.isSelected = true
//        changeTabLayoutSelectedTabColor(R.color.colorAccent)
//        charactersListViewModel.isQuerySearch = false
//        charactersListViewModel.offset = 0
//        charactersListViewModel.firstTime
//        clearList()
//        hideContent()
//        showLoading()
//        initRecyclerView()
//        searchViewToolbar.closeSearch()
//        infiniteScrollListener.clear()
//    }

    private fun initSearchViewListener() = searchViewToolbar.setTextWatcherListener(this)


//    private fun unselectTabs() {
//        changeTabLayoutSelectedTabColor(R.color.colorPrimary)
//        for (i in 0 until tabLayout.tabCount) {
//            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
//            tab.isSelected = false
//        }
//    }

//    private fun changeTabLayoutSelectedTabColor(color: Int) =
//        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, color))

    companion object {
        const val CHARACTER_TAG = "CHARACTER"
        const val LIST_OF_FAVORITES_TAG = "LIST_OF_FAVORITES"
        private const val CURRENT_TAB = "CURRENT_TAB"
        private const val REQUEST_CODE_UPDATE_FAVORITE = 200
    }

}
