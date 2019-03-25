package oliveira.fabio.marvelapp.feature.characterdetails.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_character_details.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.adapter.CharacterDetailsAdapter
import oliveira.fabio.marvelapp.feature.characterdetails.viewmodel.CharacterDetailsViewModel
import oliveira.fabio.marvelapp.feature.characterslist.ui.activity.CharactersListActivity
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.util.Response
import oliveira.fabio.marvelapp.util.extensions.loadImageByGlide
import org.koin.android.viewmodel.ext.android.viewModel


class CharacterDetailsActivity : AppCompatActivity() {

    private val characterDetailsViewModel: CharacterDetailsViewModel by viewModel()
    private val characterDetailsAdapter by lazy { CharacterDetailsAdapter() }
    private val character by lazy { intent?.extras?.getSerializable(CharactersListActivity.CHARACTER_TAG) as Character }
    private val listOfAllFavorites by lazy { intent?.extras?.getSerializable(CharactersListActivity.LIST_OF_FAVORITES_TAG) as ArrayList<Character> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        savedInstanceState?.let {
            setupToolbar()
            setValues()
            initRecyclerView()
            initLiveDatas()
        } ?: run {
            init()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        characterDetailsViewModel.onDestroy()
    }

    private fun init() {
        setupToolbar()
        setValues()
        initRecyclerView()
        characterDetailsViewModel.listOfAllFavorites.addAll(listOfAllFavorites)
        characterDetailsViewModel.getDatasByCharacterId(character.id.toInt())
        initLiveDatas()
    }

    private fun initRecyclerView() {
        rvCharacterInfoList.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        rvCharacterInfoList.adapter = characterDetailsAdapter
        if (characterDetailsViewModel.lastResultsInfos.isNotEmpty()) {
            characterDetailsAdapter.addItems(characterDetailsViewModel.lastResultsInfos)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.navigationIcon?.setColorFilter(resources.getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setValues() {
        character.apply {
            txtCharacterResourceURI.text = handleStrings(resourceURI)
            txtCharacterName.text = handleStrings(name)
            txtCharacterDescription.text = handleStrings(description)
            chkFavorite.isChecked = isFavorite
            chkFavorite.setOnClickListener {
                character.isFavorite = !character.isFavorite
                characterDetailsViewModel.addRemoveFavorite(character)
                val intent = Intent()
                intent.putExtra(CharactersListActivity.CHARACTER_TAG, character)
                setResult(Activity.RESULT_OK, intent)
            }
            imgCharacter.loadImageByGlide(urlImage)
        }
    }

    private fun initLiveDatas() {
        characterDetailsViewModel.mutableLiveDataEventsList.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response.statusEnum) {
                    Response.StatusEnum.SUCCESS -> {
                        response.data?.let {
                            characterDetailsAdapter.addItems(it)
                        }
                    }
                    Response.StatusEnum.ERROR -> {

                    }
                }
            }
        })
    }

    private fun handleStrings(message: String) = if (message.isEmpty()) handledMessage else message

    companion object {
        private const val handledMessage = "There is no information about this attribute from the API"
    }
}