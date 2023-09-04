package com.aarogyaforworkers.aarogya.Session

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogya.S3.S3Repository

class SessionStatusRepo {

    private val status = SessionStatus(false, false, false)

    private val isCurrentSessionStatus = mutableStateOf(status)

    var sessionStatus : State<SessionStatus> = isCurrentSessionStatus

    fun updateSessionState(status: SessionStatus){
        isCurrentSessionStatus.value = status
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: SessionStatusRepo? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SessionStatusRepo().also { instance = it }
            }
    }
}