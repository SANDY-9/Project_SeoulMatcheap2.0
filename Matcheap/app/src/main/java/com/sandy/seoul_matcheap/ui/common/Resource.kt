package com.sandy.seoul_matcheap.ui.common

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import com.sandy.seoul_matcheap.R
import com.sandy.seoul_matcheap.util.constants.*

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-02-14
 * @desc
 */
object Resource {
    val colorMatcheapBlue = Color.parseColor("#1042C7")
    val colorMatcheapLightYellow = Color.parseColor("#4DFAE913")
    val colorMatcheapGray = Color.parseColor("#707070")
    val colorMatcheapLightGray = Color.parseColor("#C3C3C3")
    val colorMatcheapTransparentBlue = Color.parseColor("#261042C7")
    val matCheapBlack = Color.parseColor("#222222")
    val matCheapBlue = ColorStateList.valueOf(colorMatcheapBlue)
    val matCheapGray = ColorStateList.valueOf(colorMatcheapGray)
    val matCheapLightGray = ColorStateList.valueOf(colorMatcheapLightGray)
    val matCheapWhite = ColorStateList.valueOf(Color.WHITE)

/*    fun getPlaceHolderDrawableResource(code: String) = when (code) {
        HANSIK_ -> R.drawable.menu_hansik_holder
        CHINA_ -> R.drawable.placeholder_china
        JAPAN_ -> R.drawable.placeholder_japan
        OTHER_ -> R.drawable.placeholder_other
        WASH_ -> R.drawable.placeholder_wash
        BEAUTY_ -> R.drawable.menu_beauty_holder
        HOTEL_ -> R.drawable.placeholder_hotel
        else -> R.drawable.placeholder_etc
    }*/

    fun getStoreIconDrawableResource(isChecked: Boolean, sort: String) = when(sort) {
        HANSIK_ -> if(isChecked) R.drawable.menu_hansik else R.drawable.menu_hansik_off
        CHINA_ -> if(isChecked) R.drawable.menu_china else R.drawable.menu_china_off
        JAPAN_ -> if(isChecked) R.drawable.menu_japan else R.drawable.menu_japan_off
        OTHER_ -> if(isChecked) R.drawable.menu_other else R.drawable.menu_other_off
        WASH_ -> if(isChecked) R.drawable.menu_wash else R.drawable.menu_wash_off
        BEAUTY_ -> if(isChecked) R.drawable.menu_beauty else R.drawable.menu_beauty_off
        HOTEL_ -> if(isChecked) R.drawable.menu_hotel else R.drawable.menu_hotel_off
        else -> if(isChecked) R.drawable.menu_etc else R.drawable.menu_etc_off
    }

}