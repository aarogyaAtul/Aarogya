package com.aarogyaforworkers.aarogya.Auth
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.aarogyaforworkers.aarogya.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.awsapi.APIManager
import com.aarogyaforworkers.awsauth.AuthManager

class AuthRepository {

    private var lastOTPSentEmail = ""

    private var isNoEmailFound = mutableStateOf(false)

    var noEmailFound = isNoEmailFound

    private var isWrongPassword = mutableStateOf(false)

    var wrongPassword = isWrongPassword

    private var isWrongOTP = mutableStateOf(false)

    var wrongOTP = isWrongOTP

    private var isWrongUsername = mutableStateOf(false)

    var wrongUsername = isWrongUsername

    private var isAllReadyLoggedIn = mutableStateOf(false)

    var allReadyLoggedIn = isAllReadyLoggedIn

    private var isSignedIn = mutableStateOf(false)

    private var isEmailLinkedWithPhone = mutableStateOf("")

    private var isSignedOut = mutableStateOf(false)

    private var isPasswordReset = mutableStateOf(false)

    private var isSignInOTPSent = mutableStateOf(false)

    private var isForgotPasswordOTPSent = mutableStateOf(false)

    private var isAdminUID = mutableStateOf("")

    var userSignInState : State<Boolean> = isSignedIn

    var userSignOutState : State<Boolean> = isSignedOut

    var emailLinkedWithPhone : State<String> = isEmailLinkedWithPhone

    var forgotPasswordOTPState : State<Boolean> = isForgotPasswordOTPSent

    var signInOTPSent : State<Boolean> = isSignInOTPSent

    var passwordResetState : State<Boolean> = isPasswordReset


    // This function initializes the Amplify library by initializing the AuthManager with the given context and setting up callbacks.
    fun initializeAmplify(context: Context){
        AuthManager.shared.initializeManager(context)
        AdminDBRepository().initializeAPIManager()
        setUpCallbacks()
    }

    // This function updates the value of the isNoEmailFound LiveData with the given Boolean value.
    fun updateEmailNotFound(value : Boolean){
        isNoEmailFound.value = value
    }

    // This function updates the value of the isWrongPassword LiveData with the given Boolean value.
    fun updateWrongPassword(value: Boolean){
        isWrongPassword.value = value
    }

    // This function updates the value of the isWrongOTP LiveData with the given Boolean value.
    fun updateWrongOTP(value: Boolean){
        isWrongOTP.value = value
    }

    // This function updates the value of the isWrongUsername LiveData with the given Boolean value.
    fun updateWrongUserName(value: Boolean){
        isWrongUsername.value = value
    }

    // This function updates the value of the isAllReadyLoggedIn LiveData with the given Boolean value.
    fun updateIsAllReadyLoggedIn(value: Boolean){
        isAllReadyLoggedIn.value = value
    }

    // This function sets up the AuthCallbackResponse callbacks in the AuthManager.
    private fun setUpCallbacks(){
        AuthManager.shared.setAuthCallbacks(AuthCallbackResponse())
    }

    // This function calls the AuthManager to sign in with email and password.
    fun signInWithEmailPassword(email:String, password:String){
        AuthManager.shared.signInWithEmailPassword(email, password)
    }

    // This function calls the APIManager to get the admin profile by phone and password.
    fun signInWithPhonePassword(phone: String, password: String){
        APIManager.shared.getAdminProfileByPhone(phone, password)
    }

    // This function calls the AuthManager to send a sign-in OTP to the given email.
    fun getSignInEmailOTP(email: String){
        AuthManager.shared.sendSignInOTPToEmail(email)
    }

    // This function calls the AuthManager to confirm sign-in with the given OTP.
    fun confirmSignInWithOTP(otp: String){
        AuthManager.shared.confirmOtpSignIn(otp)
    }

    // This function signs out the current user using the AuthManager.
    fun signOut() = AuthManager.shared.signOut()

    // This function updates the value of the isSignedIn LiveData with the given Boolean value. If the value is true, it also updates the value of the isSignedOut LiveData to false.
    fun updateSignInState(isSigned : Boolean){
        isSignedIn.value = isSigned
        if(isSigned) updateSignOutState(false)
    }

    // This function updates the value of the isSignedOut LiveData with the given Boolean value. If the value is true, it also updates the value of the isSignedIn LiveData to false.
    fun updateSignOutState(isSignOut : Boolean){
        isSignedOut.value = isSignOut
        if(isSignOut) updateSignInState(false)
    }

    // This function updates the value of the isForgotPasswordOTPSent LiveData with the given Boolean value.
    fun updateForgotPasswordOTPState(isOTPSent : Boolean){
        isForgotPasswordOTPSent.value = isOTPSent
    }

    // This function updates the value of the isPasswordReset LiveData with the given Boolean value.
    fun updatePasswordResetState(isReset : Boolean){
        isPasswordReset.value = isReset
    }

    // This function updates the value of the isSignInOTPSent LiveData with the given Boolean value.
    fun updateSignInOTPState(isOTPSent: Boolean){
        isSignInOTPSent.value = isOTPSent
    }

    // This function updates the value of the isAdminUID LiveData with the given String value.
    fun updateAdminUID(uid : String){
        isAdminUID.value = uid
    }

    // This function returns the value of the isAdminUID LiveData.
    fun getAdminUID() = isAdminUID.value

    // This function calls the isUserSignedIn() function of the AuthManager and returns the result.
    fun isUserSignedIn() = AuthManager.shared.isUserSignedIn()

    // This function calls the forgotPassword() function of the AuthManager with the given email parameter.
    fun forgotPassword(email: String) = AuthManager.shared.forgotPassword(email)

    // This function calls the confirmResetPassword() function of the AuthManager with the given otp, email, and newPassword parameters.
    fun confirmAndResetPassword(otp : String, email: String, newPassword: String) = AuthManager.shared.confirmResetPassword(email, newPassword, otp)

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: AuthRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
    }

}