package com.aarogyaforworkers.aarogya

import Commons.ConnectionPageTags
import Commons.HomePageTags
import Commons.SessionSummaryPageTags
import Commons.UserHomePageTags
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.aarogyaforworkers.aarogya.Commons.isSessionShared
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class UserDataCollectionTestFlow {

    @Test
    fun ConnectPC300(withRule : AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        val connectionBtn = withRule.onNodeWithContentDescription(UserHomePageTags.shared.connectionBtn)
        connectionBtn.assertIsDisplayed()
        connectionBtn.performClick()
        val connectionScreen = withRule.onNodeWithTag(ConnectionPageTags.shared.connectionScreen)
        connectionScreen.assertIsDisplayed()
        val pc300btn = withRule.onNodeWithTag(ConnectionPageTags.shared.pc300Card)
        pc300btn.assertIsDisplayed()
        pc300btn.performClick()
        latch.await(10, TimeUnit.SECONDS)
        val bleScanScreen = withRule.onNodeWithTag(ConnectionPageTags.shared.bleScanningScreen)
        bleScanScreen.assertIsDisplayed()
        val pc300 = withRule.onNodeWithText("PC_300SNT")
        withRule.waitUntil(80000) {
            MainActivity.pc300Repo.deviceList.value != null
        }
        withRule.waitUntil(80000) {
            MainActivity.pc300Repo.deviceList.value!!.isNotEmpty()
        }
        latch.await(10, TimeUnit.SECONDS)
        pc300.assertIsDisplayed()
        pc300.performClick()
        latch.await(5, TimeUnit.SECONDS)
        connectionScreen.assertIsDisplayed()
        // back to user home screen - >
        val backBtn = withRule.onNodeWithContentDescription(UserHomePageTags.shared.backBtn)
        backBtn.assertIsDisplayed()
        backBtn.performClick()
        latch.await(5, TimeUnit.SECONDS)
        connectionBtn.assertIsDisplayed()
        TestDataCollection(withRule)
    }

    @Test
    fun TestDataCollection(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        latch.await(10, TimeUnit.SECONDS)
        withRule.waitUntil(60000) { MainActivity.subUserRepo.bufferThere.value }
        latch.await(10, TimeUnit.SECONDS)
        SaveCollectedData(withRule)
    }

    @Test
    fun SaveCollectedData(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        val savebtn = withRule.onNodeWithTag(UserHomePageTags.shared.saveBtn)
        savebtn.assertIsDisplayed()
        savebtn.performClick()
        withRule.waitUntil(30000) { !MainActivity.subUserRepo.bufferThere.value }
        latch.await(10, TimeUnit.SECONDS)
        exitUserProfileAfterSave(withRule)
    }

    @Test
    fun exitUserProfileAfterSave(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        val backBtn = withRule.onNodeWithContentDescription(UserHomePageTags.shared.backBtn)
        backBtn.assertIsDisplayed()
        backBtn.performClick()
        latch.await(5, TimeUnit.SECONDS)
        val sessionSummaryScreen = withRule.onNodeWithTag(SessionSummaryPageTags.shared.summaryScreen)
        sessionSummaryScreen.assertIsDisplayed()
        latch.await(5, TimeUnit.SECONDS)
        val shareBtn = withRule.onNodeWithText("Share")
        shareBtn.assertIsDisplayed()
        shareBtn.performClick()
        withRule.waitUntil(40000) { MainActivity.s3Repo.sessionSummaryUploaded.value == true }
        withRule.waitUntil(40000) { isSessionShared }
        latch.await(10, TimeUnit.SECONDS)
    }

    @Test
    fun goBack(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val backBtn = withRule.onNodeWithContentDescription(UserHomePageTags.shared.backBtn)
        backBtn.assertIsDisplayed()
        backBtn.performClick()
        withRule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()
    }

    companion object{
        @Volatile private var instance: UserDataCollectionTestFlow? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: UserDataCollectionTestFlow().also { instance = it }
            }
    }

}