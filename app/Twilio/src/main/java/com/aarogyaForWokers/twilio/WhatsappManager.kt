package com.aarogyaForWokers.twilio

import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message

class WhatsappManager {

    companion object{
        val shared = WhatsappManager()
    }

    // Replace these values with your own recipient and sender numbers and message body
    private val recipient = "whatsapp:+919340413756"
    private val sender = "whatsapp:+15077105321"
    private val messageBody = "Ravi, appointment is coming up on July 21 at 3PM"

    // Initialize the Twilio API client with your Twilio account SID and auth token
    val accountSid = "ACff27e924d692ca08ac83991f75fc9445"
    val authToken = "11568edad8a8ce37dde0b90de48f0608"

    fun init(){
        Twilio.init(accountSid, authToken)
    }

    fun send(){
        val message = Message.creator(
            com.twilio.type.PhoneNumber(recipient),
            com.twilio.type.PhoneNumber(sender),
            messageBody
        ).create()
        // Send the message using the Message.creator method
    }

}