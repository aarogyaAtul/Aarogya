package com.aarogyaforworkers.aarogya.composeScreens

import Commons.HomePageTags
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogya.Auth.AuthRepository
import com.aarogyaforworkers.aarogya.Commons.*
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.Location.LocationRepository
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.PC300.PC300Repository
import com.aarogyaforworkers.aarogya.SubUser.SessionStates
import com.aarogyaforworkers.aarogya.checkBluetooth
import com.aarogyaforworkers.aarogya.isBluetoothEnabled
import com.aarogyaforworkers.awsapi.models.SubUserProfile

var lastUpdatedSignOutValue = false
var isAdminHomeScreenSetUp = false
var subUserSelected = false


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(navHostController: NavHostController, authRepository: AuthRepository, adminRepository : AdminDBRepository, pc300Repository: PC300Repository, locationRepository: LocationRepository) {

    Disableback()

    CheckInternet(context = LocalContext.current)

    isOnUserHomeScreen = false

    val context = LocalContext.current

    val bleEnabled by remember { mutableStateOf(isBluetoothEnabled()) }

    if(!bleEnabled) checkBluetooth(context)

    pc300Repository.isOnSessionPage = false

    MainActivity.shared.initializeOmronPC300(LocalContext.current)

    MainActivity.csvRepository.setUpNewContext(LocalContext.current)

//    RealtimeEcgAlertView()

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .testTag(HomePageTags.shared.homeScreen)
    ) {
        Column {
            Spacer(modifier = Modifier.height(10.dp))
            ProfileView(navHostController)
            Spacer(modifier = Modifier.height(15.dp))
            ActionBtnView(navHostController)
            Spacer(modifier = Modifier.height(15.dp))
            UserSearchView(navHostController)
            locationRepository.getLocation(LocalContext.current)
            subUserSelected = false
        }
    }

    if(authRepository.userSignOutState.value && isAdminHomeScreenSetUp){
        isLoginScreenSetUp = false
        isAllreadyOnHome = false
        isAdminHomeScreenSetUp = false
        if(lastUpdatedSignOutValue != authRepository.userSignOutState.value){
            MainActivity.adminDBRepo.resetLoggedInUser()
            navHostController.navigate(Destination.Login.routes)
            lastUpdatedSignOutValue = authRepository.userSignOutState.value
        }
        authRepository.updateSignInState(false)
    }

    if(!isAdminHomeScreenSetUp) isAdminHomeScreenSetUp = true

    if(adminRepository.getLoggedInUser().admin_id.isEmpty()){
        adminRepository.getProfile(authRepository.getAdminUID())
    }
}

/*
 * A Composable function that renders the user's profile view.
 * Takes a NavHostController as a parameter to navigate to other destinations.
 */
@Composable
fun ProfileView(navHostController: NavHostController){

    val profile = MainActivity.adminDBRepo.adminProfileState.value

    var showSignOutAlert by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        UserImageView(imageUrl = profile.profile_pic_url, size = 65.dp) { navHostController.navigate(Destination.AdminProfile.routes) }

        Spacer(modifier = Modifier.width(15.dp))

        Box(modifier = Modifier
            .weight(1f)
            .testTag(HomePageTags.shared.getAdminTag(profile))){ TitleView(title = "Hey, "+MainActivity.adminDBRepo.adminProfileState.value.first_name + " ") }

        ConnectionBtnView(isConnected = MainActivity.pc300Repo.connectionStatus.value, 36.dp) {
            isFromUserHome = false
            navHostController.navigate(Destination.DeviceConnection.routes) }

        Spacer(modifier = Modifier.width(25.dp))

        SignOutBtnView { showSignOutAlert = true }

        Spacer(modifier = Modifier.width(5.dp))

        // Conditionally show the sign-out alert
        SignOutAlertView(showAlert = showSignOutAlert, onSignOutClick = {
            isLastUpdatedValue = false
            isAllreadyOnHome = false
            lastUpdatedSignOutValue = false
            MainActivity.authRepo.signOut() }) {
            // on Cancel
            showSignOutAlert = false
        }
    }
}

/*
 * A Composable function that renders the action button view.
 * Takes a NavHostController as a parameter to navigate to other destinations.
 */
@Composable
fun ActionBtnView(navHostController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f)) {
            ActionBtn(title = "Create New User") {
                isEditUser = false
                lastCreateUserValue = false
                lastUserRegisteredState = true
                lastUserNotRegisteredState = false
                isEditUser = false
                userProfileToEdit = null
                isSetUpDone = false
                isUpdatingProfile = false
                MainActivity.adminDBRepo.setSubUserProfilePicture(null)
                isCurrentUserVerifiedPhone = ""
                newUserProfile = SubUserProfile("","","","","","","","","","","","", "")
                isCameraCliked = false
                isCheckingUserBeforeSendingOTP = false
                isUserAllreadyRegistered = false
                allReadyRegisteredPhone = ""
                isSavingOrUpdating = false
                isAllreadyOtpSent = false
                MainActivity.adminDBRepo.resetStates()
                newUserProfile = SubUserProfile("","","","","","","","","","","","", "")
                MainActivity.adminDBRepo.resetMedicalAnswers()
                navHostController.navigate(Destination.AddNewUser.routes)
            }
        }
        Spacer(modifier = Modifier.width(15.dp))

        Box(modifier = Modifier.weight(1f)) {
            ActionBtn(title = "Guest User Data") {
                isGuest = true
                navHostController.navigate(Destination.SessionHistory.routes)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun UserSearchView(navHostController: NavHostController) {

    var searchText by remember { mutableStateOf("") }

    var isEmptyResult by remember { mutableStateOf(false) }

    var isSearching by remember { mutableStateOf(false) }

    var searchResults by remember { mutableStateOf(listOf<SubUserProfile>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        SearchView(searchText = searchText, isSearching = isSearching, onValueChange = {
            searchText = it
            isEmptyResult = it.isEmpty()
            if(!isEmptyResult) isSearching = true
            searchResults = if (it.isNotEmpty()) {
                performSearch(it)
            } else {
                isSearching = false
                emptyList()
            }
        })
        if(searchText.isNotEmpty()){
            searchResults = performSearch(searchText.replace(" ", ""))
            if(searchResults.isNotEmpty() || searchResults.isEmpty()) isSearching = false
            SearchResultView(searchResults = searchResults, onResultFound = {
                isSearching = false
            }, onSelectingPatient = {
                if(!subUserSelected){
                    MainActivity.pc300Repo.clearSessionValues()
                    isSetRequestSent = false
                    lastFailed = false
                    isReadyForWeight = false
                    // if different user goes then reset omron sync status
                    if(MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id != it.user_id){
                        MainActivity.omronRepo.isReadyForFetch = false
                        MainActivity.subUserRepo.isResetQuestion.value = true
                    }

                    MainActivity.pc300Repo.isShowEcgRealtimeAlert.value = false
                    isShown = false
                    MainActivity.adminDBRepo.setNewSubUserprofile(it.copy())
                    MainActivity.adminDBRepo.setNewSubUserprofileCopy(it.copy())
                    MainActivity.subUserRepo.isResetQuestion.value = true
                    MainActivity.subUserRepo.updateSessionState(SessionStates(false, false, false, false, false))
                    MainActivity.subUserRepo.resetStates()
                    ifIsExitAndSave = false
                    MainActivity.subUserRepo.lastSavedSession = null
                    MainActivity.subUserRepo.createNewSession()
//                    MainActivity.localDBRepo.createNewSession()
                    navHostController.navigate(Destination.UserHome.routes)
                    isOnUserHomeScreen = true
                }
            }) {
                navHostController.navigate(Destination.AddNewUser.routes)
            }
        }
    }
}











