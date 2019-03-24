package oliveira.fabio.marvelapp.util.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import oliveira.fabio.marvelapp.R

fun View.openKeyboard() =
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )

fun View.hideKeyboard() =
    (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
        windowToken,
        0
    )

fun AppCompatImageView.loadImageByGlide(image: Any) {
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()

    Glide.with(context).load(image)
        .apply(
            RequestOptions.centerCropTransform().priority(Priority.IMMEDIATE).placeholder(circularProgressDrawable).error(
                R.color.colorAccent
            ).diskCacheStrategy(
                DiskCacheStrategy.ALL
            )
        )
        .transition(
            DrawableTransitionOptions.withCrossFade()
        ).into(this)
}