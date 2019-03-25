package oliveira.fabio.marvelapp.model.vo

open class Item(var viewType: Int) {
    fun getHeader() = this as HeaderItem
    fun getSubItem() = this as SubItem

    companion object {
        const val HEADER_ITEM = 1
        const val SUB_ITEM = 2
    }
}