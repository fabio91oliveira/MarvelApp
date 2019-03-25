package oliveira.fabio.marvelapp.model.vo

data class SubItem(
    var title: String,
    var description: String
) : Item(Item.SUB_ITEM)