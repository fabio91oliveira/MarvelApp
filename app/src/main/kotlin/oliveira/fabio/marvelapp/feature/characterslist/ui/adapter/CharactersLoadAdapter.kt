//package oliveira.fabio.marvelapp.feature.characterslist.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import kotlinx.android.extensions.LayoutContainer
//import kotlinx.android.synthetic.main.item_character.*
//import kotlinx.android.synthetic.main.view_progress_spinner.*
//import oliveira.fabio.marvelapp.R
//import oliveira.fabio.marvelapp.model.response.CharactersResponse
//import oliveira.fabio.marvelapp.util.extensions.loadImageByGlide
//
//
//class CharactersLoadAdapter(recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private var results: MutableList<CharactersResponse.Data.Result?> = mutableListOf()
//    private val VIEW_ITEM = 1
//    private val VIEW_PROG = 0
//
//    private val visibleThreshold = 5
//    private var previousTotalItemCount = 0
//    private var firstVisibleItem = 0
//    private var visibleItemCount = 0
//    private var totalItemCount = 0
//    private var isLoading = true
//    private var currentTotalResults = 0
//    private var onLoadMoreListener: OnLoadMoreListenera? = null
//
//    private var isLoadingAdded = false
//
//    fun setListener(onLoadMoreListener: OnLoadMoreListenera, offset: Int) {
//        this.onLoadMoreListener = onLoadMoreListener
//        this.currentTotalResults = offset
//    }
//
//    init {
//        recyclerView
//            .addOnScrollListener(object : RecyclerView.OnScrollListener() {
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager)
//                    visibleItemCount = recyclerView.childCount
//                    totalItemCount = linearLayoutManager.itemCount
//                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
//
//                    if (isLoading) {
//                        if (totalItemCount > previousTotalItemCount) {
//                            isLoading = false
//                            previousTotalItemCount = totalItemCount
//                        }
//                    }
//
//                    if (firstVisibleItem != -1) {
//                        if (isLoading.not() && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
//                            currentTotalResults += totalItemCount
//                            onLoadMoreListener?.onLoadMore(currentTotalResults)
//                            isLoading = true
//                        }
//                    }
//                }
//            })
//    }
//
//    override fun getItemCount() = results.size
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is ItemViewHolder) {
//            holder.bind(results[position])
//        } else if (holder is ProgressViewHolder) {
//            holder.bind()
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val vh: RecyclerView.ViewHolder
//        if (viewType == VIEW_ITEM) {
//            val v = LayoutInflater.from(parent.context).inflate(
//                R.layout.item_character, parent, false
//            )
//            vh = ItemViewHolder(v)
//        } else {
//            val v = LayoutInflater.from(parent.context).inflate(
//                R.layout.view_progress_spinner, parent, false
//            )
//            vh = ProgressViewHolder(v)
//        }
//        return vh
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return if (results[position] != null) VIEW_ITEM else VIEW_PROG
//    }
//
//
//    fun addResults(results: List<CharactersResponse.Data.Result>) {
//        this.results.addAll(results)
//        notifyDataSetChanged()
//    }
//
//    fun addLoadingFooter() {
//        isLoadingAdded = true
//        results.add(null)
//        notifyItemInserted(results.size - 1)
//    }
//
//    fun removeLoadingFooter() {
//        isLoadingAdded = false
//
//        val position = results.size - 1
//        val result = results[position]
//
//        if (result != null) {
//            results.removeAt(position)
//            notifyItemRemoved(position)
//        }
//    }
//
//
//    fun clearResults() = results.clear()
//
//    inner class ItemViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
//        LayoutContainer {
//
//        fun bind(result: CharactersResponse.Data.Result?) {
//            result?.apply {
//                txtCharacterName.text = name
//                chkFavorite.isChecked = isFavorite
//                imgCharacter.loadImageByGlide(thumbnail.getFullImageUrl())
//            }
//        }
//    }
//
//    inner class ProgressViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
//        LayoutContainer {
//
//        fun bind() {
//            progressbar.isIndeterminate = true
//        }
//    }
//}