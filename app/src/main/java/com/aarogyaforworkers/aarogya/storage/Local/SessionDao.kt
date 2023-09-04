package com.aarogyaforworkers.aarogya.storage.Local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.aarogyaforworkers.aarogya.storage.Local.Models.LocalSession

@Dao
interface SessionDao {

    @Insert
    fun saveNewSession(session: LocalSession)

    @Query("SELECT * FROM LocalSessions")
    fun getAllSessions() : List<LocalSession>

    @Query("SELECT * FROM LocalSessions WHERE sessionId = :sessionId")
    fun getSessionById(sessionId: String): LocalSession?

    @Query("DELETE FROM LocalSessions WHERE sessionId = :sessionId")
    fun deleteSessionById(sessionId: String)

    @Update
    fun updateSession(localSession: LocalSession)

}