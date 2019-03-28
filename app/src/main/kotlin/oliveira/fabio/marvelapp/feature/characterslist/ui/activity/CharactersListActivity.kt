package oliveira.fabio.marvelapp.feature.characterslist.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_characters_list.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.CharacterFavoriteListFragment
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.CharacterRegularListFragment
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.InfoFragment


class CharactersListActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var characterRegularListFragment = CharacterRegularListFragment.newInstance()
    private var characterFavoriteListFragment = CharacterFavoriteListFragment.newInstance()
    private var infoFragment = InfoFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(oliveira.fabio.marvelapp.R.layout.activity_characters_list)
        navigation.setOnNavigationItemSelectedListener(this)

        savedInstanceState?.let {
            characterRegularListFragment =
                supportFragmentManager.findFragmentByTag(TAG_1) as CharacterRegularListFragment
            characterFavoriteListFragment =
                supportFragmentManager.findFragmentByTag(TAG_2) as CharacterFavoriteListFragment
            infoFragment = supportFragmentManager.findFragmentByTag(TAG_3) as InfoFragment
        } ?: run {
            supportFragmentManager.beginTransaction().add(R.id.mainContainer, infoFragment, TAG_3)
                .hide(infoFragment).commit()
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
                    .hide(infoFragment).show(characterRegularListFragment).commit()
                characterRegularListFragment.onResume()
                return true
            }

            R.id.action_favorites_list -> {
                supportFragmentManager.beginTransaction().addToBackStack(null).hide(characterRegularListFragment)
                    .hide(infoFragment).show(characterFavoriteListFragment).commit()
                characterFavoriteListFragment.onResume()
                return true
            }

            R.id.action_info -> {
                supportFragmentManager.beginTransaction().addToBackStack(null).hide(characterRegularListFragment)
                    .hide(characterFavoriteListFragment).show(infoFragment).commit()
                characterFavoriteListFragment.onResume()
                return true
            }

        }
        return true
    }

    override fun onBackPressed() {
        when (searchViewToolbar.isVisible()) {
            true -> searchViewToolbar.closeSearch()
            false -> finish()
        }
    }

    companion object {
        private const val TAG_1 = "1"
        private const val TAG_2 = "2"
        private const val TAG_3 = "3"
    }
}
