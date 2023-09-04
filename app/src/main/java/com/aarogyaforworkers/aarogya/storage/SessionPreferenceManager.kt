package com.aarogyaforworkers.aarogya.storage

import android.content.Context
import com.aarogyaforworkers.awsapi.models.Session
import com.google.gson.Gson

class SessionPreferenceManager private constructor(context: Context) {

//    private val sharedPreferences = context.getSharedPreferences("guestSession", Context.MODE_PRIVATE)
//
//    private val gson = Gson()
//
//    fun saveSessions(sessions: List<Session>) {
//        val sessionsJson = gson.toJson(sessions)
//        sharedPreferences.edit().putString("sessions", sessionsJson).apply()
//    }
//
//    fun getSessions(): List<Session> {
//        val sessionsJson = sharedPreferences.getString("sessions", null)
//        return gson.fromJson(sessionsJson, Array<Session>::class.java).toList()
//    }
//
//    companion object {
//
//        @Volatile
//        private var instance: SessionPreferenceManager? = null
//
//        fun getInstance(context: Context): SessionPreferenceManager =
//            instance ?: synchronized(this) {
//                instance ?: SessionPreferenceManager(context).also { instance = it }
//            }
//    }
}

