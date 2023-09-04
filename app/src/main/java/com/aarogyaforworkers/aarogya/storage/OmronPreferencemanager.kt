package com.aarogyaforworkers.aarogya.storage

import android.content.Context
import com.aarogyaforworkers.aarogya.Omron.Const
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.huray.omronsdk.ble.enumerate.OHQUserDataKey

class OmronPreferencemanager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("omron", Context.MODE_PRIVATE)

    private val lastSeqKey = "lastSeqKey"
    private val lastUserIncDecIndex = "lastUserIncDecIndex"
    private val lastSyncedUser = "lastSyncedUser"

    private val user = Const.demoUser

    fun saveLastSeqKey(key: Long){
        sharedPreferences.edit().putLong(lastSeqKey, key).apply()
    }

    fun saveLastIncDecKey(key: Long){
        sharedPreferences.edit().putLong(lastUserIncDecIndex, key).apply()
    }

    fun saveLastSyncedUser(user: Map<OHQUserDataKey, Any>) {
        sharedPreferences.edit().putString(lastSyncedUser, user.toString()).apply()
    }


    fun getLastSeqkey() : Long {
        return sharedPreferences.getLong(lastSeqKey, 0)
    }

    fun getLastIncDeckey() : Long{
        return sharedPreferences.getLong(lastUserIncDecIndex, 0)
    }

    fun getLastSyncedUser(): Map<OHQUserDataKey, Any> {
        val userString = sharedPreferences.getString(lastSyncedUser, "${Const.demoUser}")
        val type = object : TypeToken<Map<OHQUserDataKey, Any>>() {}.type
        return Gson().fromJson(userString, type)
    }




    companion object {

        @Volatile
        private var instance: OmronPreferencemanager? = null


        // Get instance of SettingPreferenceManager to access the functions
        fun getInstance(context: Context): OmronPreferencemanager =
            instance ?: synchronized(this) {
                instance ?: OmronPreferencemanager(context).also { instance = it }
            }
    }

}

