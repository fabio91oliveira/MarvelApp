package oliveira.fabio.marvelapp.feature.characterslist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_character.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.model.persistence.Character
import oliveira.fabio.marvelapp.util.extensions.loadImageByGlide


class CharactersAdapter(private val onClickCharacterListener: OnClickCharacterListener) :
    RecyclerView.Adapter<CharactersAdapter.ItemViewHolder>() {

    private var results: MutableList<Character> = mutableListOf()

    override fun getItemCount() = results.size
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(results[position])
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_character, parent, false)
    )

    fun addResults(results: List<Character>) {
        this.results.addAll(results)
        notifyDataSetChanged()
    }

    fun validateCharacterFavorite(character: Character) {
        results.forEach {
            if (it.id == character.id) {
                it.isFavorite = character.isFavorite
            }
        }
        notifyDataSetChanged()
    }

    fun clearResults() {
        results.clear()
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(character: Character) {
            character.apply {
                txtCharacterName.text = name
                chkFavorite.isChecked = isFavorite
                imgCharacter.loadImageByGlide(urlImage)
                containerView.setOnClickListener { onClickCharacterListener.onCharacterClick(this) }
                chkFavorite.setOnClickListener {
                    character.isFavorite = chkFavorite.isChecked
                    onClickCharacterListener.onFavoriteButtonClick(character)
                }
            }
        }
    }

    interface OnClickCharacterListener {
        fun onCharacterClick(character: Character)
        fun onFavoriteButtonClick(character: Character)
    }
}