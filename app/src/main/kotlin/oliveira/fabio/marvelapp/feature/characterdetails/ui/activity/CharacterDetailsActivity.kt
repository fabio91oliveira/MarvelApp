package oliveira.fabio.marvelapp.feature.characterdetails.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_character_details.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.feature.characterdetails.ui.adapter.CharacterDetailsAdapter
import oliveira.fabio.marvelapp.feature.characterdetails.viewmodel.CharacterDetailsViewModel
import oliveira.fabio.marvelapp.feature.characterslist.ui.fragment.CharacterRegularListFragment
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.util.Response
import oliveira.fabio.marvelapp.util.extensions.loadImageByGlide
import org.koin.android.viewmodel.ext.android.viewModel


class CharacterDetailsActivity : AppCompatActivity() {

    private val characterDetailsViewModel: CharacterDetailsViewModel by viewModel()
    private val characterDetailsAdapter by lazy { CharacterDetailsAdapter() }
    private val character by lazy { intent?.extras?.getSerializable(CharacterRegularListFragment.CHARACTER_TAG) as Character }
    private val listOfAllFavorites by lazy { intent?.extras?.getSerializable(CharacterRegularListFragment.LIST_OF_FAVORITES_TAG) as ArrayList<Character> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)

        savedInstanceState?.let {
            setupToolbar()
            setValues()
            initRecyclerView()
            initLiveDatas()
            showLoadingMoreInfo(false)
            if (characterDetailsViewModel.isLoadedWithNoResults) hideMoreInfo()
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
        initLiveDatas()
        showLoadingMoreInfo(true)
        characterDetailsViewModel.listOfAllFavorites.addAll(listOfAllFavorites)
        characterDetailsViewModel.getDatasByCharacterId(character.id.toInt())
    }

    private fun initRecyclerView() {
        rvCharacterInfoList.layoutManager =
            LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        rvCharacterInfoList.adapter = characterDetailsAdapter
        if (characterDetailsViewModel.lastResultsInfo.isNotEmpty()) {
            characterDetailsAdapter.addItems(characterDetailsViewModel.lastResultsInfo)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.navigationIcon?.setColorFilter(
            ContextCompat.getColor(this, R.color.colorWhite),
            PorterDuff.Mode.SRC_ATOP
        )
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setValues() {
        character.apply {
            collapsingToolbar.title = handleStrings(name)
            txtCharacterResourceURI.text = handleStrings(resourceURI)
            txtCharacterDescription.text = handleStrings(description)
            chkFavorite.isChecked = character.isFavorite
            chkFavorite.setOnClickListener {
                character.isFavorite = !character.isFavorite
                characterDetailsViewModel.addRemoveFavorite(character)
                val intent = Intent()
                intent.putExtra(CharacterRegularListFragment.CHARACTER_TAG, character)
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
                            if (it.isNotEmpty()) {
                                characterDetailsAdapter.addItems(it)
                                showLoadingMoreInfo(false)
                            } else {
                                showLoadingMoreInfo(false)
                                hideMoreInfo()
                                characterDetailsViewModel.isLoadedWithNoResults = true
                            }
                        }
                    }
                    Response.StatusEnum.ERROR -> {
                        showFeedbackToUser(resources.getString(R.string.character_details_error))
                        showLoadingMoreInfo(false)
                        hideMoreInfo()
                        characterDetailsViewModel.isLoadedWithNoResults = true
                    }
                }
            }
        })
    }

    private fun showFeedbackToUser(message: String) =
        Snackbar.make(container, message, Snackbar.LENGTH_LONG).apply {
            setAction(
                resources.getString(
                    oliveira.fabio.marvelapp.R.string.snack_bar_hide
                ), null
            )
            view.setBackgroundColor(ContextCompat.getColor(this@CharacterDetailsActivity, R.color.colorPrimaryDark))
        }.show()

    private fun showLoadingMoreInfo(isLoading: Boolean) {
        if (isLoading) {
            progressBar.animate().setDuration(200).alpha(1f).start()
        } else {
            progressBar.animate().setDuration(200).alpha(0f).start()
        }
    }

    private fun hideMoreInfo() {
        txtCharacterMoreInformationTitle.visibility = View.INVISIBLE
    }

    private fun handleStrings(message: String) = if (message.isEmpty()) handledMessage else message

    companion object {
        private const val handledMessage = "There is no information about this attribute from the API"
    }
}
