package com.aarogyaforworkers.awsauth

import android.content.Context
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthConfirmResetPasswordOptions
import com.amplifyframework.auth.cognito.options.AWSCognitoAuthSignInOptions
import com.amplifyframework.core.Amplify.Auth

class AuthManager {

    companion object {
        val shared = AuthManager()
    }

    var username = ""

    var callback : AuthCallbacks? = null

    fun initializeManager(context: Context){
        AmplifyManager.shared.configureManager(context)
    }

    fun setAuthCallbacks(callbacks: AuthCallbacks){
        callback = callbacks
    }

    /**
     * SIGN IN User with registered Email and password and update Failure and Success Case
     * */
    fun signInWithEmailPassword(email: String, password: String){
        val options = AWSCognitoAuthSignInOptions.builder()
            .authFlowType(com.amplifyframework.auth.cognito.options.AuthFlowType.USER_SRP_AUTH)
            .build()

        Auth.signIn(email, password,  options, {

            isUserSignedIn()

        },{
            when(it.message){

                "User not found in the system." -> {
                    // invalid Username-email
                    callback?.onInvalidUserName()
                }

                "Failed since user is not authorized." -> {
                    // Invalid Password
                    callback?.onInvalidPassword()
                }

                "There is already a user signed in." -> {
                    isUserSignedIn()
//                    callback?.onUserAllReadySignedIn()
                }

                else -> {
                    callback?.onSignInFailed(it.message.toString())
                }
            }
        })
    }

    fun sendSignInOTPToEmail(email: String){
        username = email
        val options = AWSCognitoAuthSignInOptions.builder()
            .authFlowType(com.amplifyframework.auth.cognito.options.AuthFlowType.CUSTOM_AUTH_WITHOUT_SRP)
            .build()
        Auth.signIn(email,null,  options, {
            callback?.onSignInOTPSent()
        },{
            when(it.message){

                "User not found in the system." -> {
                    // invalid Username-email
                    callback?.onInvalidUserName()
                }

                "Failed since user is not authorized." -> {
                    // Invalid Password
                    callback?.onInvalidOTP()
                }

                "There is already a user signed in." -> {
                    callback?.onUserAllReadySignedIn()
                }

                else -> {
                    callback?.onSignInFailed(it.message.toString())
                }
            }
        })
    }

    fun confirmOtpSignIn(otp: String){
        Auth.confirmSignIn(otp, {
//            callback?.onSignInSuccess()
            isUserSignedIn()
        }, {
            when(it.message){

                "User not found in the system." -> {
                    // invalid Username-email
                    callback?.onInvalidUserName()
                }

                "Failed since user is not authorized." -> {
                    // Invalid Password
                    callback?.onInvalidOTP()
                }

                "There is already a user signed in." -> {
                    callback?.onUserAllReadySignedIn()
                }

                else -> {
                    callback?.onSignInFailed(it.message.toString())
                }
            }
        })
    }

    fun isUserSignedIn(){
        Auth.getCurrentUser({
            callback?.onAdminUserIdUpdate(it.userId)
            callback?.onSignInSuccess()
        }, {
            callback?.onSignInFailed(it.message.toString())
        })
    }

    // user forgot password
    fun forgotPassword(email: String){
        Auth.resetPassword(email, {
            callback?.onForgotPasswordConfirmationOTPSent()
        },{
            when(it.message) {

                "User not found in the system." -> {
                    // invalid Username-email
                    callback?.onForgorPasswordUserNotFound()
                }
                else -> {
                    callback?.onForgotPasswordConfirmationOTPFailed(it.message.toString())
                }
            }
        })
    }

    /**
     * enter the received OTP and confirm user
     * after validation user new password will be updated
     * */
    fun confirmResetPassword(email: String, password: String, otp: String){
        val options = AWSCognitoAuthConfirmResetPasswordOptions.builder()
            .build()
        Auth.confirmResetPassword(email, password, otp, options, {
            callback?.onSuccessFullPasswordReset()
        },{
            when(it.message.toString()){
                "Confirmation code entered is not correct." -> {
                    callback?.onWrongPasswordResetOTP()
                }
                else -> {
                    callback?.onPasswordResetFailure(it.message.toString())
                }
            }
        })
    }

    fun signOut(){
        Auth.getCurrentUser({
            Auth.signOut{
                callback!!.onSignOutSuccess()
                val isNull = callback
            }
        },{
            callback?.onSignOutSuccess()
        })
    }

}