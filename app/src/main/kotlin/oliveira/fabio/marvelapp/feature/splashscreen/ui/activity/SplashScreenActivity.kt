package oliveira.fabio.marvelapp.feature.splashscreen.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import oliveira.fabio.marvelapp.feature.characterslist.ui.activity.CharactersListActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, CharactersListActivity::class.java)
        startActivity(intent)
        finish()
    }
}