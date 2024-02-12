import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sandy.seoul_matcheap.util.constants.DEFAULT_POSITION

/**
 * 화면을 로딩할 때 보여주는 화면
 * @param progressView 보여주는 로딩화면
 * @param duration progressView를 보여주는 시간. default setting duration = 500L
 */
fun ViewGroup.showProgressView(duration: Long = 1000L) {
    visibility = View.VISIBLE
    Handler(Looper.getMainLooper()).postDelayed({
        visibility = View.GONE
    }, duration)
}

fun Activity.dropDownSoftKeyboard(inputMethodManager: InputMethodManager?) = currentFocus?.let{
    inputMethodManager?.hideSoftInputFromWindow(
        it.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
    it.clearFocus()
}

fun TextView.connectRecyclerView(recyclerView: RecyclerView, MIN_SCROLL: Int = 0, DEFAULT_POSITION: Int = 0) {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            visibility = if(y > MIN_SCROLL) View.VISIBLE else View.GONE
        }
    }
    recyclerView.addOnScrollListener(listener)
    setOnClickListener { recyclerView.smoothScrollToPosition(DEFAULT_POSITION) }
}