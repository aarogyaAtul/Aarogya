package com.aarogyaforworkers.awsauth

import android.content.Context
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.storage.s3.AWSS3StoragePlugin

class AmplifyManager {

    companion object{
        val shared = AmplifyManager()
        var isConfigured = false
    }

    fun configureManager(context: Context?) {
        if(isConfigured) return
        try {
            // Add these lines to add the AWSApiPlugin plugins
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(context!!)
            isConfigured = true
            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }

        Amplify.Hub.subscribe(HubChannel.AUTH) { event ->
            when (event.name) {
                InitializationStatus.SUCCEEDED.toString() ->
                    Log.i("AuthQuickstart", "Auth successfully initialized")
                InitializationStatus.FAILED.toString() ->
                    Log.i("AuthQuickstart", "Auth failed to succeed")
                else -> when (AuthChannelEventName.valueOf(event.name)) {
                    AuthChannelEventName.SIGNED_IN ->
                        Log.i("AuthQuickstart", "Auth just became signed in")
                    AuthChannelEventName.SIGNED_OUT ->
                        Log.i("AuthQuickstart", "Auth just became signed out")
                    AuthChannelEventName.SESSION_EXPIRED ->
                        Log.i("AuthQuickstart", "Auth session just expired")
                    AuthChannelEventName.USER_DELETED ->
                        Log.i("AuthQuickstart", "User has been deleted")
                    else ->
                        Log.w("AuthQuickstart", "Unhandled Auth Event: ${event.name}")
                }
            }
        }
    }
}