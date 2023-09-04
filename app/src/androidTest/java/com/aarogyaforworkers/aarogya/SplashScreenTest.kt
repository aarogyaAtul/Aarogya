package com.aarogyaforworkers.aarogya

import Commons.HomePageTags
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.aarogyaforworkers.aarogya.AdminDB.AdminDBRepository
import Commons.LoginTags
import com.aarogyaforworkers.aarogya.Commons.*
import com.aarogyaforworkers.aarogya.composeScreens.AddNewUserScreen
import com.aarogyaforworkers.aarogya.composeScreens.AdminProfileScreen
import com.aarogyaforworkers.aarogya.composeScreens.CameraScreen
import com.aarogyaforworkers.aarogya.composeScreens.ConfirmAdminSignInScreen
import com.aarogyaforworkers.aarogya.composeScreens.DevicesConnectionScreen
import com.aarogyaforworkers.aarogya.composeScreens.ForgotPasswordScreen
import com.aarogyaforworkers.aarogya.composeScreens.GraphScreen
import com.aarogyaforworkers.aarogya.composeScreens.HomeScreen
import com.aarogyaforworkers.aarogya.composeScreens.LoginScreen
import com.aarogyaforworkers.aarogya.composeScreens.NearByDeviceListScreen
import com.aarogyaforworkers.aarogya.composeScreens.PasswordResetScreen
import com.aarogyaforworkers.aarogya.composeScreens.SessionSummaryScreen
import com.aarogyaforworkers.aarogya.composeScreens.SplashScreen
import com.aarogyaforworkers.aarogya.composeScreens.UserHomeScreen
import com.aarogyaforworkers.aarogya.composeScreens.UserSessionHistoryScreen
import com.aarogyaforworkers.aarogya.ui.theme.AarogyaTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SplashScreenTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalTvMaterial3Api::class)
    @Before
    fun setUp(){
        rule.activity.setContent {
            AarogyaTheme() {
                MainActivity.shared.initializeRepository()
                AdminDBRepository().initializeAPIManager()
                val navController = rememberNavController()
                AdminDBRepository().setnav(navController)
                NavHost(navController = navController, startDestination = Destination.Splash.routes){
                    composable (Destination.Splash.routes) { SplashScreen (navController, MainActivity.authRepo) }
                    composable (Destination.Home.routes) { HomeScreen (navController, MainActivity.authRepo, MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo) }
                    composable(Destination.DeviceConnection.routes) { DevicesConnectionScreen(navController, MainActivity.pc300Repo, MainActivity.omronRepo) }
                    composable(Destination.Login.routes) { LoginScreen(navController, MainActivity.authRepo) }
                    composable(Destination.ForgotPasswordScreen.routes) { ForgotPasswordScreen(navController, MainActivity.authRepo) }
                    composable(Destination.UserHome.routes) { UserHomeScreen(navController, MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo, MainActivity.subUserRepo, MainActivity.s3Repo, MainActivity.csvRepository) }
                    composable(Destination.ConfirmAdminSignIn.routes) { ConfirmAdminSignInScreen(navController) }
                    composable(Destination.PasswordReset.routes) { PasswordResetScreen(navController,MainActivity.authRepo) }
                    composable(Destination.AddNewUser.routes) { AddNewUserScreen(navController, MainActivity.adminDBRepo, MainActivity.cameraRepo, MainActivity.locationRepo, MainActivity.subUserRepo) }
                    composable(Destination.AdminProfile.routes) { AdminProfileScreen(navController, MainActivity.adminDBRepo, MainActivity.locationRepo) }
                    composable(Destination.Camera.routes) { CameraScreen(MainActivity.cameraRepo, navController) }
                    composable(Destination.SessionSummary.routes) { SessionSummaryScreen(navController) }
                    composable(Destination.Graphs.routes) { GraphScreen(navHostController = navController, selectedEcg) }
                    composable(Destination.DeviceList.routes) { NearByDeviceListScreen(navHostController = navController, MainActivity.pc300Repo, MainActivity.omronRepo) }
                    composable(Destination.SessionHistory.routes) { UserSessionHistoryScreen(navHostController = navController, subUserDBRepository = MainActivity.subUserRepo, MainActivity.adminDBRepo) }
                }
            }
        }
    }

    @Test
    fun checkIsLoggedInAndTestLoginFlow(){
        val latch = CountDownLatch(1)
        latch.await(3, TimeUnit.SECONDS)
        rule.onNodeWithTag(LoginTags.shared.splashScreen).assertIsDisplayed()
        // Wait for 10 seconds to ensure the homepage remains visible
        latch.await(15, TimeUnit.SECONDS)
        if(!MainActivity.authRepo.userSignInState.value) {
            LoginFlowTest.getInstance().startLoginTestFlow(rule)
        } else {
            rule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()
            latch.await(15, TimeUnit.SECONDS)
            LoggedInUserTestFlow.getInstance().startLoggedIUserTestFlow(rule)
        }
    }

    @Test
    fun login_Search_ViewUserProfile_SignOut_Flow(){
        val latch = CountDownLatch(1)
        latch.await(3, TimeUnit.SECONDS)
        rule.onNodeWithTag(LoginTags.shared.splashScreen).assertIsDisplayed()
        // Wait for 10 seconds to ensure the homepage remains visible
        latch.await(15, TimeUnit.SECONDS)
        if(!MainActivity.authRepo.userSignInState.value) {
            LoginFlowTest.getInstance().startLoginWithPhonePinFlow2(rule)
        } else {
            rule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()
            latch.await(15, TimeUnit.SECONDS)
            LoggedInUserTestFlow.getInstance().startLoggedIUserTestFlow(rule)
        }
    }

    @Test
    fun login_selectUser_ConnectDevice_CollectData_SaveData_SignOut_Flow(){
        val latch = CountDownLatch(1)
        latch.await(3, TimeUnit.SECONDS)
        rule.onNodeWithTag(LoginTags.shared.splashScreen).assertIsDisplayed()
        // Wait for 10 seconds to ensure the homepage remains visible
        latch.await(15, TimeUnit.SECONDS)
        if(!MainActivity.authRepo.userSignInState.value) {
            LoginFlowTest.getInstance().startLoginWithPhonePinFlow3(rule)
        } else {
            rule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()
            latch.await(15, TimeUnit.SECONDS)
            LoggedInUserTestFlow.getInstance().searchOpenProfile_ConnectPC300Device_COllectSaveData_SignOut_Flow(rule)
        }
    }

    @Test
    fun login_OpenProfile_EditUser_SaveUser(){
        val latch = CountDownLatch(1)
        latch.await(3, TimeUnit.SECONDS)
        rule.onNodeWithTag(LoginTags.shared.splashScreen).assertIsDisplayed()
        // Wait for 10 seconds to ensure the homepage remains visible
        latch.await(15, TimeUnit.SECONDS)
        if(!MainActivity.authRepo.userSignInState.value) {
            LoginFlowTest.getInstance().startLoginWithPhonePinFlow4(rule)
        } else {
            rule.onNodeWithTag(HomePageTags.shared.homeScreen).assertIsDisplayed()
            latch.await(15, TimeUnit.SECONDS)
            LoggedInUserTestFlow.getInstance().searchOpenProfile_editProfile_SaveProfile_ConfirmSave(rule)
        }
    }

    @Test
    fun login_saveSession_checkSessionHistory_ConfirmSessionSaved_Flow(){

    }

    companion object{
        val shared = SplashScreenTest()
    }
}