package com.aarogyaforworkers.aarogya.storage.Local

import android.content.Context
import androidx.room.Room
import com.aarogyaforworkers.aarogya.Commons.isAdminNeededToSync
import com.aarogyaforworkers.aarogya.Commons.isOnUserHomeScreen
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.storage.Local.Models.LocalSession
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocalSessionDBManager {

    private val dbName = "LocalSessionsDb"

    private var db : LocalSessionsDB? = null

    private var dbDao : SessionDao? = null

    fun setDBDao(context: Context) {
        if (db == null) {
            db = Room.databaseBuilder(
                context.applicationContext,
                LocalSessionsDB::class.java,
                dbName
            ).build()
        }
        if(db != null){
            dbDao = db!!.sessionDao()
        }
    }

    fun createNewSession(){
        val s =  MainActivity.subUserRepo.getSession()
        val session =  LocalSession(s.date, s.time, s.sessionId+"1", s.deviceId, s.userId, s.adminId, s.sys, s.dia, s.heartRate, s.spO2, s.weight, s.bodyFat, s.temp, s.ecgFileLink, s.questionerAnswers, s.remarks, s.location,
            isAdminNeededToSync, false, false)
        saveSession(session)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getCurrentSessionBySessionId(){
        if(dbDao != null){
            GlobalScope.launch(Dispatchers.IO) {
                dbDao!!.getSessionById(MainActivity.subUserRepo.getSessionId())
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun saveSession(localSession : LocalSession){
        if(dbDao != null) {
            GlobalScope.launch(Dispatchers.IO) {
                dbDao!!.saveNewSession(localSession)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getAllSessions(){
        if(dbDao != null){
            GlobalScope.launch(Dispatchers.IO) {
                dbDao!!.getAllSessions()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getAndUpdateCurrentSession(){
        if(dbDao != null) {
            GlobalScope.launch(Dispatchers.IO) {
                val session = dbDao!!.getSessionById(MainActivity.subUserRepo.getSession().sessionId)
            }
        }
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: LocalSessionDBManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: LocalSessionDBManager().also { instance = it }
            }
    }

}