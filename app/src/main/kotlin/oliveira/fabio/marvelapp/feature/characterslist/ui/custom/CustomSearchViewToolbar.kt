package oliveira.fabio.marvelapp.feature.characterslist.ui.custom

import android.animation.Animator
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.custom_search_view_toolbar.view.*
import oliveira.fabio.marvelapp.R
import oliveira.fabio.marvelapp.util.extensions.hideKeyboard
import oliveira.fabio.marvelapp.util.extensions.openKeyboard


class CustomSearchViewToolbar(
    context: Context,
    attrs: AttributeSet
) : ConstraintLayout(context, attrs) {

    private var isVisible = false
    private var onSearchButtonKeyboardPressed: OnSearchButtonKeyboardPressed? = null

    override fun onSaveInstanceState(): Parcelable? {
        return Bundle().apply {
            putParcelable("superState", super.onSaveInstanceState())
            putBoolean("isVisible", isVisible)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            isVisible = state.getBoolean("isVisible")
            if (isVisible) {
                searchEditText.setText("")
                searchOpenView.visibility = View.VISIBLE
                searchEditText.requestFocus()
                searchEditText.openKeyboard()
            }
        }
        super.onRestoreInstanceState(BaseSavedState.EMPTY_STATE)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_search_view_toolbar, this, true)
        initClickListeners()
    }

    private fun initClickListeners() {
        searchOpenButton.setOnClickListener { openSearch() }
        searchBackButton.setOnClickListener { closeSearch() }
        searchEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    v?.hideKeyboard()
                    container.requestFocus()
                    onSearchButtonKeyboardPressed?.search(v?.text.toString())
                    return true
                }
                return false
            }
        })
    }

    private fun openSearch() {
        searchEditText.setText("")
        searchOpenView.visibility = View.VISIBLE
        searchEditText.requestFocus()
        searchEditText.openKeyboard()
        isVisible = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val circularReveal = ViewAnimationUtils.createCircularReveal(
                searchOpenView,
                (searchOpenButton.right + searchOpenButton.left) / 2,
                (searchOpenButton.top + searchOpenButton.bottom) / 2,
                0f, width.toFloat()
            )
            circularReveal.duration = 300
            circularReveal.start()
        }
    }

    fun closeSearch() {
        isVisible = false
        searchEditText.hideKeyboard()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val circularConceal = ViewAnimationUtils.createCircularReveal(
                searchOpenView,
                (searchOpenButton.right + searchOpenButton.left) / 2,
                (searchOpenButton.top + searchOpenButton.bottom) / 2,
                width.toFloat(), 0f
            )
            circularConceal.duration = 300
            circularConceal.start()

            circularConceal.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) = Unit
                override fun onAnimationCancel(animation: Animator?) = Unit
                override fun onAnimationStart(animation: Animator?) = Unit
                override fun onAnimationEnd(animation: Animator?) {
                    searchOpenView.visibility = View.INVISIBLE
                    searchEditText.setText("")
                    circularConceal.removeAllListeners()
                }
            })
        } else {
            searchOpenView.visibility = View.INVISIBLE
            searchEditText.setText("")
        }
    }

    fun loading(isLoading: Boolean) {
        if (isLoading && !searchEditText.text.toString().isEmpty()) {
            searchProgressBar.animate().setDuration(200).alpha(1f).start()
        } else {
            searchProgressBar.animate().setDuration(200).alpha(0f).start()
        }
    }

    fun setTextWatcherListener(textWatcher: TextWatcher) = searchEditText.addTextChangedListener(textWatcher)

    fun isVisible() = isVisible

    interface OnSearchButtonKeyboardPressed {
        fun search(s: String)
    }
}