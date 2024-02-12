import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup

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