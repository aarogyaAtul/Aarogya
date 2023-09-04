package com.aarogyaforworkers.aarogya.Omron

import com.aarogyaforworkers.aarogya.MainActivity
import net.huray.omronsdk.ble.enumerate.OHQGender
import net.huray.omronsdk.ble.enumerate.OHQUserDataKey
import java.math.BigDecimal
import java.util.HashMap

object Const {
    const val EXTRA_DEVICE_TYPE = "EXTRA_DEVICE_TYPE"
    const val PREF_NAME = "PREF_NAME.Pref"
    const val PREF_HEM_9200T_ADDRESS = "PREF_HEM_9200T_ADDRESS"
    const val PREF_HEM_7155T_ADDRESS = "PREF_HEM_7155T_ADDRESS"
    const val PREF_HEM_7142T_ADDRESS = "PREF_HEM_7142T_ADDRESS"
    const val PREF_HBF_222T_ADDRESS = "PREF_HBF_222T_ADDRESS"
    const val PREF_HBF_222T_USER_INDEX = "PREF_HBF_222T_USER_INDEX"
    const val PREF_HBF_222T_SEQUENCE_NUMBER = "PREF_HBF_222T_SEQUENCE_NUMBER"
    const val PREF_HBF_222T_DB_CHANGE_KEY = "PREF_HBF_222T_DB_CHANGE_KEY"

    // For Test Only
    val demoUser: Map<OHQUserDataKey, Any>
        get() {
            val userData: MutableMap<OHQUserDataKey, Any> = HashMap()
            userData[OHQUserDataKey.DateOfBirthKey] = "2004-04-04"
            userData[OHQUserDataKey.HeightKey] = BigDecimal("175")
            userData[OHQUserDataKey.GenderKey] = OHQGender.Female
            return userData
        }
}