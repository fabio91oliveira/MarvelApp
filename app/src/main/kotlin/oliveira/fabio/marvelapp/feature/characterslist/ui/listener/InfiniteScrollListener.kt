package oliveira.fabio.marvelapp.feature.characterslist.ui.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class InfiniteScrollListener(
    private val layoutManager: RecyclerView.LayoutManager,
    private var currentTotalResults: Int
) :
    RecyclerView.OnScrollListener() {

    private val visibleThreshold = 5
    private var previousTotalItemCount = 0
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var isLoading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val linearLayoutManager = (layoutManager as LinearLayoutManager)
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
                currentTotalResults += 20
                onLoadMore(currentTotalResults)
                isLoading = true
            }
        }
    }

    abstract fun onLoadMore(totalLatestResult: Int)
}