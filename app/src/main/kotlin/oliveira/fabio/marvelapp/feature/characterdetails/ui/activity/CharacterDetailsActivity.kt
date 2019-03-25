package oliveira.fabio.marvelapp.feature.characterdetails.ui.activity

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_character_details.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.adapter.CharacterListEventsAdapter
import oliveira.fabio.marvelapp.feature.characterdetails.viewmodel.CharacterDetailsViewModel
import oliveira.fabio.marvelapp.feature.characterslist.ui.activity.CharactersListActivity
import oliveira.fabio.marvelapp.model.response.Character
import oliveira.fabio.marvelapp.model.response.Response
import oliveira.fabio.marvelapp.util.extensions.loadImageByGlide
import org.koin.android.viewmodel.ext.android.viewModel

class CharacterDetailsActivity : AppCompatActivity() {

    private val characterDetailsViewModel: CharacterDetailsViewModel by viewModel()
    private var characterListEventsAdapter: CharacterListEventsAdapter? = null
    private val character by lazy { intent?.extras?.getSerializable(CharactersListActivity.CHARACTER_TAG) as Character }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        characterDetailsViewModel.onDestroy()
    }

    private fun init() {
        setupToolbar()
        setValues()
        characterDetailsViewModel.getDatasByCharacterId(character.id.toInt())
        initLiveDatas()
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
            imgCharacter.loadImageByGlide(urlImage)
        }
    }

    private fun initLiveDatas() {
        characterDetailsViewModel.mutableLiveDataEventsList.observe(this, Observer { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response.statusEnum) {
                    Response.StatusEnum.SUCCESS -> {
                        response.data?.let {
                            val titleList = ArrayList(it.keys)
                            characterListEventsAdapter =
                                CharacterListEventsAdapter(this@CharacterDetailsActivity, titleList, it)
                            expandableListView.setAdapter(characterListEventsAdapter)


                            for (i in 0 until expandableListView.expandableListAdapter.groupCount) {
                                expandableListView.expandGroup(i)
                            }
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