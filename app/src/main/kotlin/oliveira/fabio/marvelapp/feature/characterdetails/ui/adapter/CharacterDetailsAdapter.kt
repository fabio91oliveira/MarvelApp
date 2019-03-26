package oliveira.fabio.marvelapp.feature.characterdetails.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_header.*
import kotlinx.android.synthetic.main.item_sub_item.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.model.vo.Item

class CharacterDetailsAdapter : RecyclerView.Adapter<CharacterDetailsViewHolder>() {

    private var list = mutableListOf<Item>()

    override fun getItemViewType(position: Int) = list[position].viewType
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(viewHolder: CharacterDetailsViewHolder, position: Int) =
        viewHolder.bind(list[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterDetailsViewHolder = when (viewType) {
        Item.HEADER_ITEM -> HeaderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_header,
                parent,
                false
            )
        )
        else -> SubItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_sub_item, parent, false)
        )
    }

    fun addItems(list: List<Item>) {
        this.list = list.toMutableList()
        notifyDataSetChanged()
    }
}

class HeaderViewHolder(override val containerView: View) :
    CharacterDetailsViewHolder(containerView) {
    override fun bind(item: Item) {
        txtHeaderTitle.text = item.getHeader().title
    }

}

class SubItemViewHolder(override val containerView: View) : CharacterDetailsViewHolder(containerView) {
    override fun bind(item: Item) {
        txtSubItemTitle.text = item.getSubItem().title
        txtSubItemDescription.text = item.getSubItem().description
    }

}

abstract class CharacterDetailsViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {
    abstract fun bind(item: Item)
}