package oliveira.fabio.marvelapp.feature.characterslist.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_character.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.model.response.CharactersResponse
import oliveira.fabio.marvelapp.util.extensions.loadImageByGlide


class CharactersAdapter2(recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var results: MutableList<CharactersResponse.Data.Result?> = mutableListOf()
    private val visibleThreshold = 5
    private var previousTotalItemCount = 0
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var isLoading = true
    private var hasLoadingFooter = false
    private var currentTotalResults = 0
    private var onLoadMoreListener: OnLoadMoreListener? = null

    init {
        recyclerView
            .addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager)
                    visibleItemCount = recyclerView.childCount
                    totalItemCount = linearLayoutManager.itemCount
                    firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()

                    if (isLoading) {
                        if (totalItemCount > previousTotalItemCount) {
                            isLoading = false
                            previousTotalItemCount = totalItemCount
                        }
                    }

                    if (firstVisibleItem != -1) {
                        if (isLoading.not() && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                            currentTotalResults += totalItemCount
                            onLoadMoreListener?.onLoadMore(currentTotalResults)
                            isLoading = true
                        }
                    }

                    if (!hasLoadingFooter) {
                        if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == results.size - 1) {
                            results.add(null)
//                            notifyItemInserted(results.size - 1)
                            hasLoadingFooter = true
                        }
                    }

                }
            })
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(results[position])
        }
    }


    override fun getItemCount() = results.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_character, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        if (results[position] == null && hasLoadingFooter) {
            Log.d("hhahahahaha", position.toString())
        }
        return super.getItemViewType(position)
    }


    fun addResults(results: List<CharactersResponse.Data.Result>) {
        this.results.addAll(results)
        hasLoadingFooter = false
        notifyDataSetChanged()
    }

    fun clearResults() = results.clear()

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener, offset: Int) {
        this.onLoadMoreListener = onLoadMoreListener
        this.currentTotalResults = offset
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

    interface OnLoadMoreListener {
        fun onLoadMore(totalLatestResult: Int)
    }
}