package com.aarogyaforworkers.aarogya

import Commons.AddEditUserPageTags
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AddEditUserTestFlow {

    var genderOption = listOf("Male", "Female","Other")

    @Test
    fun ConfirmScreen(withRule : AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val screen = withRule.onNodeWithTag(AddEditUserPageTags.shared.addEditUserScreen)
        val latch = CountDownLatch(1)
        latch.await(10, TimeUnit.SECONDS)
        screen.assertIsDisplayed()
    }

    fun editUserName(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val name = withRule.onNodeWithTag(AddEditUserPageTags.shared.firstName + 1)
        name.assertIsDisplayed()
        name.performClick()
        name.performTextClearance()
        name.performTextInput("Ravijk")
        withRule.waitUntil(30000) {
            MainActivity.subUserRepo.changeInProfile.value
        }
        val saveBtn = withRule.onNodeWithTag(AddEditUserPageTags.shared.saveBtn)
        saveBtn.assertIsDisplayed()
        saveBtn.performClick()
    }

    fun checkItIsSavedOrNot(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        withRule.waitUntil(30000) {
            !MainActivity.subUserRepo.changeInProfile.value
        }
        latch.await(10, TimeUnit.SECONDS)
    }

    fun goBackToUserHomeScreenAndVerifyUpdates(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        val backBtn = withRule.onNodeWithContentDescription("BackBtn")
        backBtn.assertIsDisplayed()
        backBtn.performClick()
        latch.await(10, TimeUnit.SECONDS)
        val userFirstName = withRule.onNodeWithText("Rajul")
        userFirstName.assertIsDisplayed()
    }

    companion object{
        @Volatile private var instance: AddEditUserTestFlow? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: AddEditUserTestFlow().also { instance = it }
            }
    }

}