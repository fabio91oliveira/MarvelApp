package oliveira.fabio.marvelapp.feature.characterslist.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_characters_list.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterslist.ui.adapter.TabAdapter
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.CharacterFavoriteListFragment
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.CharacterRegularListFragment


class CharactersListActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var characterRegularListFragment = CharacterRegularListFragment.newInstance()
    private var characterFavoriteListFragment = CharacterFavoriteListFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(oliveira.fabio.marvelapp.R.layout.activity_characters_list)
        navigation.setOnNavigationItemSelectedListener(this)

        savedInstanceState?.let {
            characterRegularListFragment =
                supportFragmentManager.findFragmentByTag(TAG_1) as CharacterRegularListFragment
            characterFavoriteListFragment =
                supportFragmentManager.findFragmentByTag(TAG_2) as CharacterFavoriteListFragment
        } ?: run {
            supportFragmentManager.beginTransaction().add(R.id.mainContainer, characterFavoriteListFragment, TAG_2)
                .hide(characterFavoriteListFragment).commit()
            supportFragmentManager.beginTransaction().add(R.id.mainContainer, characterRegularListFragment, TAG_1)
                .commit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_regular_list -> {
                supportFragmentManager.beginTransaction().addToBackStack(null).hide(characterFavoriteListFragment)
                    .show(characterRegularListFragment).commit()
                characterRegularListFragment.onResume()
                return true
            }

            R.id.action_favorites_list -> {
                supportFragmentManager.beginTransaction().addToBackStack(null).hide(characterRegularListFragment)
                    .show(characterFavoriteListFragment).commit()
                characterFavoriteListFragment.onResume()
                return true
            }

        }
        return true
    }

    override fun onBackPressed() {
        when (searchViewToolbar.isVisible()) {
            true -> searchViewToolbar.closeSearch()
            false -> super.onBackPressed()
        }
    }

    companion object {
        private const val TAG_1 = "1"
        private const val TAG_2 = "2"
    }
}
