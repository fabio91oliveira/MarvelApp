package oliveira.fabio.marvelapp.feature.characterslist.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_characters_list.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterslist.ui.adapter.TabAdapter
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.CharacterFavoriteListFragment
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.CharacterRegularListFragment

class CharactersListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characters_list)

        val adapter = TabAdapter(supportFragmentManager)
        adapter.addFragment(
            CharacterRegularListFragment.newInstance(),
            resources.getString(R.string.characters_list_regular_list)
        )
        adapter.addFragment(
            CharacterFavoriteListFragment.newInstance(),
            resources.getString(R.string.characters_list_favorite_list)
        )
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onBackPressed() {
        when (searchViewToolbar.isVisible()) {
            true -> searchViewToolbar.closeSearch()
            false -> super.onBackPressed()
        }
    }
}
