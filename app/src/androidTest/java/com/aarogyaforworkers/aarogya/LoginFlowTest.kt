package com.aarogyaforworkers.aarogya

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import Commons.LoginTags
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LoginFlowTest {

    /*
    update this test code to update the test flow
    0 - test all possible login test each one by one
    1 - single login with email-pin
    2 - single login with phone-pin
    3 - single login with email-otp
    4 - single login with pin-otp
    * **/

    private var testCode = 1
    private var isSignOutNeededAfterLogin = false
    private var isToResetAndLogin = false

    fun startLoginTestFlow(withRule : AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        when(testCode){
            0 -> {
                isSignOutNeededAfterLogin = true
                loginWithPhonePIN(withRule)
            }

            1 -> {
                loginWithEmailPIN()
            }

            2 -> {
                loginWithPhonePIN(withRule)
            }

            3 -> {
                loginWithEmailOTP()
            }

            4 -> {
                loginWithPhoneOTP()
            }
        }
    }

    fun startLoginWithPhonePinFlow2(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        val phone = withRule.onNodeWithTag(LoginTags.shared.phoneTextField).performClick()
        phone.performTextInput("9340413756")
        latch.await(2, TimeUnit.SECONDS)
        val pin = withRule.onNodeWithTag(LoginTags.shared.otpPinTextField).performClick()
        pin.performTextInput("1111111")
        latch.await(2, TimeUnit.SECONDS)
        val loginBtn = withRule.onNodeWithTag(LoginTags.shared.loginContinueBtn)
        loginBtn.performClick()
        latch.await(2, TimeUnit.SECONDS)
        withRule.waitUntil(30000) {
            MainActivity.authRepo.userSignInState.value
        }
        latch.await(5, TimeUnit.SECONDS)
        LoggedInUserTestFlow.getInstance().startLoggedIUserTestFlow(withRule)
    }

    fun startLoginWithPhonePinFlow3(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        val phone = withRule.onNodeWithTag(LoginTags.shared.phoneTextField).performClick()
        phone.performTextInput("9340413756")
        latch.await(2, TimeUnit.SECONDS)
        val pin = withRule.onNodeWithTag(LoginTags.shared.otpPinTextField).performClick()
        pin.performTextInput("1111111")
        latch.await(2, TimeUnit.SECONDS)
        val loginBtn = withRule.onNodeWithTag(LoginTags.shared.loginContinueBtn)
        loginBtn.performClick()
        latch.await(2, TimeUnit.SECONDS)
        withRule.waitUntil(30000) {
            MainActivity.authRepo.userSignInState.value
        }
        latch.await(5, TimeUnit.SECONDS)
        LoggedInUserTestFlow.getInstance().searchOpenProfile_ConnectPC300Device_COllectSaveData_SignOut_Flow(withRule)
    }

    fun startLoginWithPhonePinFlow4(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        val phone = withRule.onNodeWithTag(LoginTags.shared.phoneTextField).performClick()
        phone.performTextInput("9340413756")
        latch.await(2, TimeUnit.SECONDS)
        val pin = withRule.onNodeWithTag(LoginTags.shared.otpPinTextField).performClick()
        pin.performTextInput("1111111")
        latch.await(2, TimeUnit.SECONDS)
        val loginBtn = withRule.onNodeWithTag(LoginTags.shared.loginContinueBtn)
        loginBtn.performClick()
        latch.await(2, TimeUnit.SECONDS)
        withRule.waitUntil(30000) {
            MainActivity.authRepo.userSignInState.value
        }
        latch.await(5, TimeUnit.SECONDS)
        LoggedInUserTestFlow.getInstance().searchOpenProfile_editProfile_SaveProfile_ConfirmSave(withRule)
    }

    @Test
    fun loginWithEmailPIN(){
        when(isToResetAndLogin){
            true -> {
                resetPIN()
                isToResetAndLogin = false
                loginWithEmailPIN()
            }

            false -> {

            }
        }
    }

    @Test
    fun loginWithPhonePIN(withRule : AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){

        // to reset
        when(isToResetAndLogin){
            true -> {
                resetPIN()
                isToResetAndLogin = false
                loginWithPhonePIN(withRule)
            }

            false -> {
                val latch = CountDownLatch(1)
                val phone = withRule.onNodeWithTag(LoginTags.shared.phoneTextField).performClick()
                phone.performTextInput("9340413756")
                latch.await(2, TimeUnit.SECONDS)
                val pin = withRule.onNodeWithTag(LoginTags.shared.otpPinTextField).performClick()
                pin.performTextInput("1111111")
                latch.await(2, TimeUnit.SECONDS)
                val loginBtn = withRule.onNodeWithTag(LoginTags.shared.loginContinueBtn)
                loginBtn.performClick()
                latch.await(2, TimeUnit.SECONDS)
                withRule.waitUntil(30000) {
                    MainActivity.authRepo.userSignInState.value
                }
                latch.await(5, TimeUnit.SECONDS)
                // checking to signOut or not
                when(isSignOutNeededAfterLogin){

                    true -> {
                        testCode = 1
                        LoggedInUserTestFlow.getInstance().signOut(withRule)
                    }

                    false -> {
                        // continue to Home
                        LoggedInUserTestFlow.getInstance().startLoggedIUserTestFlow(withRule)
                    }
                }
                latch.await(15, TimeUnit.SECONDS)

                if(isSignOutNeededAfterLogin) startLoginTestFlow(withRule)
            }
        }
    }

    /**
     * The otp for test will be always 111111
     * */
    @Test
    fun loginWithEmailOTP(){

    }

    /**
     * The otp for test will be always 111111
     * */
    @Test
    fun loginWithPhoneOTP(){

    }

    @Test
    fun signOutUser(){

    }

    @Test
    fun resetPIN(){

    }

    @Test
    fun clickOnForgetPIN(){

    }

    companion object{
        @Volatile private var instance: LoginFlowTest? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: LoginFlowTest().also { instance = it }
            }
    }

}