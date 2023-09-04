package com.aarogyaforworkers.awsapi.models

data class Session(val date : String, val time : String, var sessionId : String, val deviceId : String, var userId : String, val adminId : String, val sys : String, val dia : String, val heartRate : String, val spO2 : String,
                   val weight : String, val bodyFat : String, val temp : String, var ecgFileLink : String, val questionerAnswers : String, var remarks : String, val location : String)
