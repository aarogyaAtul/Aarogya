package com.aarogyaforworkers.aarogya.Whatsapp

import android.util.Log
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.rest.messaging.v1.Service
import com.twilio.type.PhoneNumber

class WhatsAppManager {

    fun sendWhatsAppMessage() {
        val ACCOUNT_SID = "ACff27e924d692ca08ac83991f75fc9445"
        val AUTH_TOKEN = "11568edad8a8ce37dde0b90de48f0608"
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN)
        val message = Message.creator(
            PhoneNumber("whatsapp:+919340413756"),
            PhoneNumber("whatsapp:+13203773220"),
            "as shipped and should be delivered"
        ).create()
        Log.d("TAG", "sendWhatsAppMessage: $message")
    }

    companion object{
        val shared = WhatsAppManager()
    }

}

