package com.aarogyaforworkers.aarogya

import Commons.HomePageTags
import Commons.LoginTags
import Commons.UserHomePageTags
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LoggedInUserTestFlow {


    @Test
    fun startLoggedIUserTestFlow(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>) {
        val latch = CountDownLatch(1)
        withRule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()

        // Wait for admin to be logged in
        withRule.waitUntil(40000) { MainActivity.adminDBRepo.getLoggedInUser().first_name.isNotEmpty() }

        latch.await(10, TimeUnit.SECONDS)
        // Check admin name is coming or not
        val adminProfile = withRule.onNodeWithTag(HomePageTags.shared.getSavedAdminTag())
        adminProfile.assertIsDisplayed()

        val searchView = withRule.onNodeWithTag(HomePageTags.shared.searchView)
        searchView.assertIsDisplayed()
        searchView.performClick()
        searchView.performTextInput("RAVI")

        // Wait for user search results
        withRule.waitUntil(40000) { MainActivity.adminDBRepo.subUserSearchProfileListState.value.isNotEmpty() }

        latch.await(10, TimeUnit.SECONDS)

        val user = withRule.onNodeWithText(HomePageTags.shared.getSavedUserTag())
        user.assertIsDisplayed()
        user.performClick()

        // Check if we reached the userHomeScreen or not
        val userHomeScreen = withRule.onNodeWithTag(UserHomePageTags.shared.userHomeScreen)
        userHomeScreen.assertIsDisplayed()

        val backBtn = withRule.onNodeWithContentDescription(UserHomePageTags.shared.backBtn)
        backBtn.assertIsDisplayed()
        backBtn.performClick()

        withRule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()
        signOut(withRule)
    }

    @Test
    fun searchOpenProfile_ConnectPC300Device_COllectSaveData_SignOut_Flow(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        withRule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()

        // Wait for admin to be logged in
        withRule.waitUntil(40000) { MainActivity.adminDBRepo.getLoggedInUser().first_name.isNotEmpty() }

        latch.await(20, TimeUnit.SECONDS)
        // Check admin name is coming or not
        val adminProfile = withRule.onNodeWithTag(HomePageTags.shared.getSavedAdminTag())
        adminProfile.assertIsDisplayed()

        val searchView = withRule.onNodeWithTag(HomePageTags.shared.searchView)
        searchView.assertIsDisplayed()
        searchView.performClick()
        searchView.performTextInput("RAVI")

        // Wait for user search results
        withRule.waitUntil(40000) { MainActivity.adminDBRepo.subUserSearchProfileListState.value.isNotEmpty() }

        latch.await(10, TimeUnit.SECONDS)

        val user = withRule.onNodeWithText(HomePageTags.shared.getSavedUserTag())
        user.assertIsDisplayed()
        user.performClick()

        // Check if we reached the userHomeScreen or not
        val userHomeScreen = withRule.onNodeWithTag(UserHomePageTags.shared.userHomeScreen)
        userHomeScreen.assertIsDisplayed()
        // connect device PC300
        UserDataCollectionTestFlow.getInstance().ConnectPC300(withRule)
        latch.await(5, TimeUnit.SECONDS)
        //check alert is visible or not

//        val alertView = withRule.onNodeWithText("Unsaved Data")
//        alertView.assertIsDisplayed()
//        latch.await(5, TimeUnit.SECONDS)
//        val alertBtn = withRule.onNodeWithText("Yes")
//        alertBtn.assertIsDisplayed()
//        alertBtn.performClick()
//        latch.await(5, TimeUnit.SECONDS)


        // Back SignOut
//        val backBtn = withRule.onNodeWithContentDescription(UserHomePageTags.shared.backBtn)
//        backBtn.assertIsDisplayed()
//        backBtn.performClick()
//
        withRule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()
//        signOut(withRule)
    }

    @Test
    fun searchOpenProfile_editProfile_SaveProfile_ConfirmSave(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val latch = CountDownLatch(1)
        withRule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()

        // Wait for admin to be logged in
        withRule.waitUntil(40000) { MainActivity.adminDBRepo.getLoggedInUser().first_name.isNotEmpty() }

        latch.await(20, TimeUnit.SECONDS)
        // Check admin name is coming or not
        val adminProfile = withRule.onNodeWithTag(HomePageTags.shared.getSavedAdminTag())
        adminProfile.assertIsDisplayed()

        val searchView = withRule.onNodeWithTag(HomePageTags.shared.searchView)
        searchView.assertIsDisplayed()
        searchView.performClick()
        searchView.performTextInput("RAVI")

        // Wait for user search results
        withRule.waitUntil(40000) { MainActivity.adminDBRepo.subUserSearchProfileListState.value.isNotEmpty() }

        latch.await(10, TimeUnit.SECONDS)

        val user = withRule.onNodeWithText(HomePageTags.shared.getSavedUserTag())
        user.assertIsDisplayed()
        user.performClick()


        // Check if we reached the userHomeScreen or not
        val userHomeScreen = withRule.onNodeWithTag(UserHomePageTags.shared.userHomeScreen)
        userHomeScreen.assertIsDisplayed()

        // Edit profile

        val editBtn = withRule.onNodeWithTag(UserHomePageTags.shared.editBtn)
        editBtn.assertIsDisplayed()
        editBtn.performClick()

        latch.await(10, TimeUnit.SECONDS)

        // verify edit profile screen is visible or not
        AddEditUserTestFlow.getInstance().ConfirmScreen(withRule)

        AddEditUserTestFlow.getInstance().editUserName(withRule)

        AddEditUserTestFlow.getInstance().checkItIsSavedOrNot(withRule)

        AddEditUserTestFlow.getInstance().goBackToUserHomeScreenAndVerifyUpdates(withRule)

        UserDataCollectionTestFlow.getInstance().goBack(withRule)

        signOut(withRule)

    }





    @Test
    fun signOut(withRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>){
        val logoutbtn  = withRule.onNodeWithTag(HomePageTags.shared.logoutBtn)
        logoutbtn.assertIsDisplayed()
        logoutbtn.performClick()
        val latch = CountDownLatch(1)
        latch.await(10, TimeUnit.SECONDS)
        val signOutBtn = withRule.onNodeWithText("SignOut")
        signOutBtn.assertIsDisplayed()
        signOutBtn.performClick()
        latch.await(10, TimeUnit.SECONDS)
        withRule.onNodeWithTag(LoginTags.shared.loginScreen).assertIsDisplayed()
        latch.await(10, TimeUnit.SECONDS)
    }

    companion object{
        @Volatile private var instance: LoggedInUserTestFlow? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance
                    ?: LoggedInUserTestFlow().also { instance = it }
            }

    }
}
