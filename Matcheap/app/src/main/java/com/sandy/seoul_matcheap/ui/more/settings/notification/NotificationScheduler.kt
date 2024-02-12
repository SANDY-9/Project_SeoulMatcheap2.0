package com.sandy.seoul_matcheap.ui.more.settings.notification

import com.sandy.seoul_matcheap.ui.more.settings.Time

/**
 * @author SANDY
 * @email nnal0256@naver.com
 * @created 2023-08-25
 * @desc
 */
interface NotificationScheduler {
    fun schedule(time: Time)
    fun cancel()
    fun setNotificationSchedule(register: Boolean, time: Time)
}