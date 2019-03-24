package oliveira.fabio.marvelapp.feature.characterslist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_character.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.model.response.CharactersResponse
import oliveira.fabio.marvelapp.util.extensions.loadImageByGlide


class CharactersAdapter : RecyclerView.Adapter<CharactersAdapter.ItemViewHolder>() {

    private var results: MutableList<CharactersResponse.Data.Result?> = mutableListOf()

    override fun getItemCount() = results.size
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(results[position])
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_character, parent, false)
    )


    fun addResults(results: List<CharactersResponse.Data.Result>) {
        this.results.addAll(results)
        notifyDataSetChanged()
    }

    fun clearResults() = results.clear()

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    inner class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(result: CharactersResponse.Data.Result?) {
            result?.apply {
                txtCharacterName.text = name
                chkFavorite.isChecked = isFavorite
                imgCharacter.loadImageByGlide(thumbnail.getFullImageUrl())
            }
        }
    }
}