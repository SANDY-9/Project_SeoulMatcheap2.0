package com.sandy.seoul_matcheap.ui.common

import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.ui.more.settings.Time
import com.sandy.seoul_matcheap.util.*
import com.sandy.seoul_matcheap.util.constants.Category
import com.sandy.seoul_matcheap.util.constants.DEFAULT_
import com.sandy.seoul_matcheap.util.helper.DataHelper
import java.util.*
import kotlin.math.*


/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2021-10-18
 * @desc
 */
object BindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["url", "code"])
    fun setImage(imageView: ImageView, url: String?, code: String?) {
        url?.let {
            val requestBuilder = Glide.with(imageView.context)
                .load(url)
                .sizeMultiplier(0.2f)
                .override(imageView.width, imageView.height)
                .centerCrop()
            Glide.with(imageView)
                .load(url)
                .thumbnail(requestBuilder)
                .override(imageView.width, imageView.height)
                .centerCrop()
                .placeholder(R.drawable.ic_title_black)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["lat", "lng", "curLat", "curLng"])
    fun inputDistance(textView: TextView, lat: Double?, lng: Double?, curLat: Double?, curLng: Double?) {
        if(lat != null && lng != null && curLat != null && curLng != null) {
            textView.text = DataHelper.convertDistance(lat, lng, curLat, curLng)
        }
    }

    @JvmStatic
    @BindingAdapter("Date")
    fun setDateTextview(textView: TextView, now: Boolean?) {
        now?.let {
            textView.text = DataHelper.getDate()
        }
    }

    @JvmStatic
    @BindingAdapter("Code")
    fun convertCodeName(textView: TextView, code: String?) {
        code?.let {
            textView.text = Category.from(it).codeName
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["isChecked", "sort"])
    fun setFilterTextEffect(textView: TextView, isChecked: Boolean = false, sort: String?) {
        sort?.let {
            textView.apply {
                setTextColor(if(isChecked) Resource.matCheapBlue else Resource.matCheapGray)
                setTypeface(null, if(isChecked) Typeface.BOLD else Typeface.NORMAL)

                val drawable = context.getDrawable(Resource.getStoreIconDrawableResource(isChecked, sort))
                setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("DoOrNot")
    fun convertDoOrNot(textView: TextView, str: String?) {
        str?.let {
            textView.text = when(str) {
                "Y" -> "가능"
                "N" -> "불가능"
                else -> "-"
            }
        }
    }

    private val colorSpan = ForegroundColorSpan(Resource.colorMatcheapBlue)
    private val styleSpan = StyleSpan(Typeface.BOLD)
    @JvmStatic
    @BindingAdapter(value = ["input", "auto"])
    fun setSpannableText(textView: TextView, input: String?, auto: String?) {
        input?.let {
            auto?.run {
                (textView.text as Spannable).apply {
                    val removedSpaceInput  = DataHelper.removeSpace(input)
                    val removedSpaceAuto = DataHelper.removeSpace(auto)
                    if(!contains(input) && !contains(removedSpaceInput) && !removedSpaceAuto.contains(removedSpaceInput)) return

                    val start = DataHelper.getStartIndex(input, auto, removedSpaceInput, removedSpaceAuto)
                    val end = DataHelper.getEndIndex(input, auto, removedSpaceInput, removedSpaceAuto, start)

                    setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                    setSpan(styleSpan, start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("CountText")
    fun setCountText(textView: TextView, num: Int?) {
        num?.let {
            textView.text = "$num 건"
        }
    }

    @JvmStatic
    @BindingAdapter("TemperatureText")
    fun setTemperatureText(textView: TextView, temp: String?) {
        temp?.let {
            textView.text = "$temp°"
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["temp", "wind"])
    fun getSensoryTemperature(textView: TextView, temp: String?, wind: String?) {
        if(temp != null && wind != null) {
            val t = temp.toDouble()
            val v = wind.toDouble()
            textView.text = "체감 ${DataHelper.calculateSensoryTemperature(t, v)}°"
        }
    }

    private const val FORECAST_CLEAR = "맑음"
    private const val FORECAST_CLOUD = "구름많음"
    private const val FORECAST_CLOUDY = "흐림"
    private const val FORECAST_RAIN = "비"
    private const val FORECAST_SHOWER = "소나기"
    private const val FORECAST_SNOW = "눈"
    private const val FORECAST_HOT = "폭염주의"
    private const val FORECAST_COLD = "한파주의"
    @JvmStatic
    @BindingAdapter(value = ["pty", "sky", "temp"])
    fun setForecastText(textView: TextView, pty: String?, sky: String?, temp: String?) {
        temp?.let {
            if(it.toInt() >= 33) {
                textView.text = FORECAST_HOT
                return
            }
            if(it.toInt() <= -12) {
                textView.text = FORECAST_COLD
                return
            }
        }
        textView.text = sky?.let {
            if(pty == DEFAULT_) when(sky) {
                "3" -> FORECAST_CLOUD
                "4" -> FORECAST_CLOUDY
                else -> FORECAST_CLEAR
            } else when(pty) {
                "1", "2", "5"  -> FORECAST_RAIN
                "4" -> FORECAST_SHOWER
                else -> FORECAST_SNOW
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["pty", "sky", "temp"])
    fun setForecastImage(animationView: LottieAnimationView, pty: String?, sky: String?, temp: String?) {
        temp?.let {
            if(it.toInt() >= 33) {
                animationView.setAnimation(R.raw.weather_sun)
                return
            }
            if(it.toInt() <= -12) {
                animationView.setAnimation(R.raw.weather_cold)
                return
            }
        }
        sky?.let {
            animationView.setAnimation(
                if(pty == DEFAULT_) when(sky) {
                    "3" -> R.raw.weather_cloud
                    "4" -> R.raw.weather_cloudy
                    else -> R.raw.weather_sun
                } else when(pty) {
                    "1", "2", "5" -> R.raw.weather_rain
                    "4" -> R.raw.weather_shower
                    else -> R.raw.weather_snow
                }
            )
        }
        animationView.playAnimation()
    }

    @JvmStatic
    @BindingAdapter(value = ["hour", "min"])
    fun setTimeText(textView: TextView, hour: Int?, min: Int?) {
        hour?.let {
            textView.text = "매일 ${if(hour < 12) "오전" else "오후"} ${it}시 ${min}분에 우리 동네 착한가격업소 추천 푸시 알림을 받을 수 있습니다."
        }
    }

    private const val DEFAULT_DESC = """착한가격업소란?
인건비, 재료비 등이 지속적으로 상승하고 있는 상황에도 원가절감 등 경영효율화 노력을 통해 저렴한 가격으로 서비스를 제공하고 있는 업소 가운데 행정자치부 기준에 의거 구청장이 지정한 업소입니다."""
    @JvmStatic
    @BindingAdapter("StoreDescription")
    fun setStoreDesc(textView: TextView, content: String?) {
        content?.let {
            textView.text = if(content == "-") DEFAULT_DESC else
                it.replace("\\n", System.getProperty("line.separator"))
        }
    }

    @JvmStatic
    @BindingAdapter("SavedTime")
    fun setSavedTimeText(button: Button, time: Time?) {
        time?.let {
            val hour = time.first
            val min = time.second
            button.text = "${if(hour < 12) "오전" else "오후"} ${hour}시 ${min}분"
        }
    }

    @JvmStatic
    @BindingAdapter("OpenTime")
    fun setOpenTime(textView: TextView, time: String?) {
        time?.let {
            textView.text = it.replace("\\n", System.getProperty("line.separator"))
        }
    }

    @JvmStatic
    @BindingAdapter("Bookmark")
    fun setOnBookmarkCheckedChange(imageView: ImageView, bookmark: Boolean?) {
        bookmark?.let {
            imageView.isSelected = it
        }
    }

    @JvmStatic
    @BindingAdapter("StoreCountTitle")
    fun setOnStoreCountTextTitle(textView: TextView, category: String?) {
        category?.let {
            textView.text = "$it 업체"
        }
    }

    @JvmStatic
    @BindingAdapter("StoreCount")
    fun setOnStoreCountText(textView: TextView, count: Int?) {
        count?.let {
            textView.text = "$it 개"
        }
    }

}

