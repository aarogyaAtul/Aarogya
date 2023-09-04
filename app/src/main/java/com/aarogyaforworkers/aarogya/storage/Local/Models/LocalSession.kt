package com.aarogyaforworkers.aarogya.storage.Local.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocalSessions")
data class LocalSession( val date : String, val time : String, @PrimaryKey var sessionId : String, val deviceId : String, var userId : String, val adminId : String, val sys : String, val dia : String, val heartRate : String, val spO2 : String,
                   val weight : String, val bodyFat : String, val temp : String, var ecgFileName : String, val questionerAnswers : String, var remarks : String, val location : String, val isAdminNeedToSync : Boolean, val isNeededToShared : Boolean, val isNeededToUpdate : Boolean)


