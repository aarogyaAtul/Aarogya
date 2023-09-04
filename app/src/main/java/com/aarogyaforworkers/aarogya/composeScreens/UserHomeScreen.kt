package com.aarogyaforworkers.aarogya.composeScreens

import Commons.UserHomePageTags
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aarogyaforworkers.aarogya.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogya.Commons.*
import com.aarogyaforworkers.aarogya.CsvGenerator.CsvRepository
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.Location.LocationRepository
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.Omron.OmronRepository
import com.aarogyaforworkers.aarogya.PC300.PC300Repository
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.S3.S3Repository
import com.aarogyaforworkers.aarogya.SubUser.*
import com.aarogyaforworkers.aarogya.checkBluetooth
import com.aarogyaforworkers.aarogya.composeScreens.ECGPainter.draw.BackGround
import com.aarogyaforworkers.aarogya.composeScreens.ECGPainter.recvdata.StaticReceive
import com.aarogyaforworkers.aarogya.isBluetoothEnabled
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import kotlinx.coroutines.delay
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.util.Calendar


var isShown = false

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserHomeScreen(navHostController: NavHostController, repository : AdminDBRepository, pc300Repository: PC300Repository, locationRepository: LocationRepository, subUserDBRepository: SubUserDBRepository, s3Repository: S3Repository, csvRepository: CsvRepository) {

    isItFromHistoryPage = false

    Disableback()

    CheckInternet(context = LocalContext.current)

    val context = LocalContext.current

    MainActivity.playerRepo.setPlayers(context)

    val bleEnabled by remember { mutableStateOf(isBluetoothEnabled()) }

    if(!bleEnabled) checkBluetooth(context)

//    val currentLocalSession = MainActivity.localDBRepo.getAllSessions()

    pc300Repository.isOnSessionPage = true

    MainActivity.adminDBRepo.updateLastSavedUnits(context)

    isOnUserHomeScreen = true

    MainActivity.subUserRepo.startFetchingFromOmronDevice()

    val showProgress = MainActivity.subUserRepo.showProgress.value

    val currentUserSessionState = MainActivity.subUserRepo.currentSessionState.value

    // check the state and reset it
    when{
        // update UI for when session Saved
        currentUserSessionState.isSave -> {
            Log.d("TAG", "UserHomeScreen: is saved")
            MainActivity.subUserRepo.updateIsBufferThere(false)
            MainActivity.subUserRepo.updateProgressState(false)
            MainActivity.subUserRepo.updateDefaultSessionState()
            if(ifIsExitAndSave){
                if(MainActivity.subUserRepo.lastSavedSession != null){
                    selectedSession =  MainActivity.subUserRepo.lastSavedSession!!
                    isFromUserHomePage = true
                    MainActivity.subUserRepo.calculateAvgSession(MainActivity.subUserRepo.sessions.value, MainActivity.subUserRepo.sessions.value.size)
                    isItFromHistoryPage = false
                    navHostController.navigate(Destination.SessionSummary.routes)
                }else{
                    navHostController.navigate(Destination.Home.routes)
                }
            }
        }
        // update UI for update when session updated
        currentUserSessionState.isUpdate -> {
            Log.d("TAG", "UserHomeScreen: is updated")
            MainActivity.subUserRepo.updateIsBufferThere(false)
            MainActivity.subUserRepo.updateProgressState(false)
            MainActivity.subUserRepo.updateDefaultSessionState()
            if(ifIsExitAndSave){
                if(MainActivity.subUserRepo.lastSavedSession != null){
                    selectedSession =  MainActivity.subUserRepo.lastSavedSession!!
                    isFromUserHomePage = true
                    isItFromHistoryPage = false
                    MainActivity.subUserRepo.calculateAvgSession(MainActivity.subUserRepo.sessions.value, MainActivity.subUserRepo.sessions.value.size)
                    navHostController.navigate(Destination.SessionSummary.routes)
                }else{
                    navHostController.navigate(Destination.Home.routes)
                }
            }
        }
        // update UI for reset when session reseted
        currentUserSessionState.isReset -> {
            MainActivity.pc300Repo.clearSessionValues()
            MainActivity.subUserRepo.updateIsBufferThere(false)
            MainActivity.subUserRepo.createNewSession()
            MainActivity.subUserRepo.updateProgressState(false)
            MainActivity.subUserRepo.updateDefaultSessionState()
            MainActivity.subUserRepo.resetStates()
            MainActivity.subUserRepo.isResetQuestion.value = true
        }
        // update UI for restart when session restarted
        currentUserSessionState.isRestart -> {
            Log.d("TAG", "UserHomeScreen: is restarted")
            MainActivity.pc300Repo.clearSessionValues()
            MainActivity.subUserRepo.updateIsBufferThere(false)
            MainActivity.subUserRepo.createNewSession()
            MainActivity.subUserRepo.updateProgressState(false)
            MainActivity.subUserRepo.updateDefaultSessionState()
            MainActivity.subUserRepo.resetStates()
            MainActivity.subUserRepo.isResetQuestion.value = true
        }

        currentUserSessionState.isFailed -> {
            isSaving = false
            Toast.makeText(context, "Failed, Please retry again", Toast.LENGTH_LONG).show()
        }
    }

//    MainActivity.localDBRepo.saveSession(MainActivity.localDBRepo.getCurrentLocalSession(false, false))

//    val savedSession = MainActivity.localDBRepo.getSession()

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
            .testTag(UserHomePageTags.shared.userHomeScreen)
    ) {

        Column(modifier = Modifier.alpha(if(showProgress) 0.07f else 1.0f)) {
            UserHome(repository.getSelectedSubUserProfile().copy(), MainActivity.subUserRepo.isResetQuestion.value, navHostController, repository, pc300Repository, locationRepository, subUserDBRepository, {MainActivity.subUserRepo.isResetQuestion.value = false})
        }

        if(showProgress) showProgress()

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserHome(user : SubUserProfile, isResetQuestion : Boolean, navHostController: NavHostController, adminDBRepository: AdminDBRepository, pc300Repository: PC300Repository, locationRepository: LocationRepository, subUserDBRepository: SubUserDBRepository, onResetChange : () -> Unit){

    subUserDBRepository.selectedUserId = user.user_id

    userHometimeStamp = System.currentTimeMillis().toString()

    val context = LocalContext.current

    if(user.medical_history.contains(",")){
        subUserDBRepository.parseUserMedicalHistory(user)
    }

    var isShowAlert by remember {
        mutableStateOf(false)
    }

    var isShowEcgAlert by remember {
        mutableStateOf(false)
    }

    var isSaveSessionAlert by remember {
        mutableStateOf(false)
    }

    if(isShowEcgAlert) EcgAlert(title = "ECG Result", subTitle = MainActivity.pc300Repo.getEcgResultMsgBasedOnCode(context)) {
       isShowEcgAlert = false
    }

    if(MainActivity.pc300Repo.showEcgRealtimeAlert.value) RealtimeEcgAlertView()

    TopBarWithBackEditBtn(
        user,
        onBackBtnPressed = {
            if(MainActivity.subUserRepo.bufferThere.value){
                isShowAlert = true
                ifIsExitAndSave = true
            }else{
                if(MainActivity.subUserRepo.lastSavedSession != null){
                    selectedSession =  MainActivity.subUserRepo.lastSavedSession!!
                    isFromUserHomePage = true
                    isItFromHistoryPage = false
                    MainActivity.subUserRepo.calculateAvgSession(MainActivity.subUserRepo.sessions.value, MainActivity.subUserRepo.sessions.value.size)
                    navHostController.navigate(Destination.SessionSummary.routes)
                }else{
                    navHostController.navigate(Destination.Home.routes)
                }
            }
            isOnUserHomeScreen = false },
        onEditBtnClicked = {
            isEditUser = true
            isSetUpDone = false
            userProfileToEdit = MainActivity.adminDBRepo.getSelectedUserProfileToEdit()
            isSubUserProfileSetUp = false
            navHostController.navigate(Destination.AddNewUser.routes)
        },
        onConnectionBtnClicked = {isFromUserHome = true
            navHostController.navigate(Destination.DeviceConnection.routes)}
    ){
        if(MainActivity.pc300Repo.isSessionStarted.value || MainActivity.omronRepo.isSessionStarted.value){
            isShowAlert = true
        }else{
            MainActivity.pc300Repo.isOnSessionPage = false
            isSubUserProfileSetUp = false
            navHostController.navigate(Destination.Home.routes)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp)) {

        AlertView(showAlert = isShowAlert,
            title = "Unsaved Data",
            subTitle = "Do you want to save user session data?",
            subTitle1 = "",
            onYesClick = {
                ifIsExitAndSave = true
                MainActivity.subUserRepo.updateProgressState(true)
                MainActivity.subUserRepo.saveOrUpdateSession()
                isShowAlert = false
            },
            onNoClick = {
                isShowAlert = false
                navHostController.navigate(Destination.Home.routes)
            }
        ) {
            isShowAlert = false
        }

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {

//                ProfileCard(user)
                Spacer(modifier = Modifier.height(4.dp))

                Divider(
                    thickness = 4.dp,
                    color = Color.LightGray,
                    modifier = Modifier
                        .padding(16.dp),
                )

                Spacer(modifier = Modifier.height(8.dp))

                CardWithHeadingAndContent1(navHostController,title = "Chief Complaint")

                Spacer(modifier = Modifier.height(6.dp))

                CardWithHeadingAndContent1(navHostController,"History of Present Illness (HPI)")

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp), // Adjust spacing as needed
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                )
                {
                    CardWithHeadingAndContentForHistory1(navHostController, "Family History")
                }
                Box(
                    modifier = Modifier.weight(1f)
                )
                {
                    CardWithHeadingAndContentForHistory1(navHostController, "Social History")
                }
            }

                Spacer(modifier = Modifier.height(6.dp))

                CardWithHeadingAndContent1(navHostController,"Past Medical & Surgical History")

                Spacer(modifier = Modifier.height(6.dp))

                CardWithHeadingAndContent1(navHostController,"Medication")

                Spacer(modifier = Modifier.height(6.dp))

                Divider(thickness = 4.dp, color = Color.LightGray, modifier = Modifier
                    .padding(16.dp))

                Spacer(modifier = Modifier.height(8.dp))

                VisitSummaryCards()


//                TakeImage()

//                UserPhoneVerify(user)
//
//                Spacer(modifier = Modifier.height(10.dp))
//
//                SessionActionRow(
//                    navHostController = navHostController,
//                    context,
//                    showAlert = { isShowAlert = MainActivity.subUserRepo.bufferThere.value },
//                    showSaveAlert = {
//                        isShowAlert = if (!sessionAllreadySaved) {
//                            true
//                        } else {
//                            MainActivity.subUserRepo.bufferThere.value
//                        }
//                    })
//
//                Spacer(modifier = Modifier.height(15.dp))
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Column {
//
//                        BloodPressure(pc300Repository, LocalContext.current)
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        HeartRate(pc300Repository)
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        SPO2(pc300Repository)
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Temperature(pc300Repository)
//                    }
//                    Spacer(modifier = Modifier.width(10.dp))
//                    Column {
//
//                        ECG(pc300Repository = pc300Repository, context = LocalContext.current) {
//                            isShowEcgAlert = true
//                        }
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        Weight(MainActivity.omronRepo)
//
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        BodyFat(MainActivity.omronRepo)
//                        Spacer(modifier = Modifier.height(10.dp))
//
//                        GLU(MainActivity.pc300Repo)
//
////                        BMI(MainActivity.omronRepo)
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(20.dp))
//
////                Box(modifier = Modifier.height(300.dp)) {
////                    // ECG Realtime -
////                    ECGGrid(14, 6, strokeSize = 0.2.dp, cellSize = (786/14).dp)
////                    Realtime()
////                }
//
//                Spacer(modifier = Modifier.height(30.dp))
//                UserQuestionOne(pc300Repository, isResetQuestion) { onResetChange() }
//                Spacer(modifier = Modifier.height(10.dp))
//
//                if (user.medical_history.contains(",")) {
//                    if (subUserDBRepository.subUserMedicalHistory.value.toString().length > 10) {
//                        if (subUserDBRepository.subUserMedicalHistory.value[0].answer == 1) {
//                            UserQuestionTwo(pc300Repository, isResetQuestion) { onResetChange() }
//                            Spacer(modifier = Modifier.height(10.dp))
//                        }
//                        if (subUserDBRepository.subUserMedicalHistory.value[4].answer == 1) {
//                            var medicationName = "other"
//                            when (subUserDBRepository.subUserMedicalHistory.value[4].subAnswer) {
//                                1 -> medicationName = "B.P"
//                                2 -> medicationName = "Asthma"
//                                3 -> medicationName = "Diabetes"
//                                4 -> medicationName =
//                                    subUserDBRepository.subUserMedicalHistory.value[4].subAnsOther
//                            }
//                            UserQuestionThree(
//                                pc300Repository,
//                                medicationName,
//                                isResetQuestion
//                            ) { onResetChange() }
//                            Spacer(modifier = Modifier.height(10.dp))
//                        }
//                    }
//                } else {
//                    UserQuestionTwo(pc300Repository, isResetQuestion, { onResetChange() })
//                    Spacer(modifier = Modifier.height(10.dp))
//                    UserQuestionThree(pc300Repository, "B.P.", isResetQuestion) { onResetChange() }
//                    Spacer(modifier = Modifier.height(10.dp))
//                }
            }
        }
    }
}



@Composable
fun CardWithHeadingAndContent1(navHostController: NavHostController,title:String) {
    Column(
        horizontalAlignment=Alignment.Start
    ) {
        BoldTextView(title = title, fontSize = 18,modifier=Modifier.padding(horizontal=8.dp))
//        Text(
//            text = title,
//            style = MaterialTheme.typography.headlineMedium,
//            fontSize = 18.sp,
//            color = Color.Black,
//            modifier = Modifier
//                    .padding(horizontal = 8.dp)
//        )
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    navHostController.navigate(
                        route = Destination.EditTextScreen.routes + "/$title"
                    )
                },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xffdae3f3),
//            color = Color(0xFFBFEFFF),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2, // Set the maximum number of lines
                    overflow = TextOverflow.Ellipsis
                )
//            MediumTextView(title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
//                        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
//                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
//                         fontSize = 16)
            }
        }
    }
}

//@Composable
//fun CardWithHeadingAndContent(navHostController: NavHostController,title:String) {
//    Surface(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth()
//            .clickable {
//                navHostController.navigate(
//                    route = Destination.EditTextScreen.routes + "/$title"
//                )
//            },
//        shape = RoundedCornerShape(16.dp),
//        color = Color(0xFFBFEFFF),
//        shadowElevation = 4.dp
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(8.dp)
//                .fillMaxSize()
//        ) {
//            Text(
//                text = title,
//                style = MaterialTheme.typography.headlineMedium,
//                fontSize = 18.sp,
//                color = Color.Black
//            )
////            BoldTextView(title,18)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
//                style = MaterialTheme.typography.bodyMedium,
//                fontSize = 14.sp,
//                color = MaterialTheme.colorScheme.onBackground ,
//                maxLines = 2, // Set the maximum number of lines
//                overflow = TextOverflow.Ellipsis
//            )
////            MediumTextView(title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
////                        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
////                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
////                         fontSize = 16)
//        }
//    }
//}

@Composable
fun CardWithHeadingAndContentForHistory1(navHostController: NavHostController,title:String) {
    Column(
        horizontalAlignment=Alignment.Start
    ) {
        BoldTextView(title = title, fontSize = 18,modifier=Modifier.padding(horizontal=8.dp))
//        Text(
//            text = title,
//            style = MaterialTheme.typography.headlineMedium,
//            fontSize = 18.sp,
//            color = Color.Black,
//            modifier = Modifier
//                .padding(horizontal = 8.dp)
//        )
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable {
                    navHostController.navigate(
                        route = Destination.RadioButtonHistoryScreen.routes + "/$title"
                    )
                },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xffdae3f3),
//            color = Color(0xFFBFEFFF),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
//            BoldTextView(title,18)
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                    fontFamily = FontFamily(Font(R.font.roboto_regular)),
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2, // Set the maximum number of lines
                    overflow = TextOverflow.Ellipsis
                )
//            MediumTextView(title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
//                        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
//                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
//                         fontSize = 16)
            }
        }
    }
}

//@Composable
//fun CardWithHeadingAndContentForHistory(navHostController: NavHostController,title:String) {
//    Surface(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth()
//            .clickable {
//                navHostController.navigate(
//                    route = Destination.RadioButtonHistoryScreen.routes + "/$title"
//                )
//            },
//        shape = RoundedCornerShape(16.dp),
//        color = Color(0xFFBFEFFF),
//        shadowElevation = 4.dp
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(8.dp)
//                .fillMaxSize()
//        ) {
//            Text(
//                text = title,
//                style = MaterialTheme.typography.headlineMedium,
//                fontSize = 18.sp,
//                color = Color.Black
//            )
////            BoldTextView(title,18)
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
//                style = MaterialTheme.typography.bodyMedium,
//                fontSize = 14.sp,
//                color = MaterialTheme.colorScheme.onBackground ,
//                maxLines = 2, // Set the maximum number of lines
//                overflow = TextOverflow.Ellipsis
//            )
////            MediumTextView(title = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
////                        "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
////                        "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
////                         fontSize = 16)
//        }
//    }
//}


@Composable
fun VisitSummaryCard(
    date: String,
    place: String,
    details: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color(0xffdae3f3))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RegularTextView(date, fontSize = 14)
                RegularTextView(title = place, fontSize = 14)
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }

        }
    }
    if (expanded) {
        Spacer(modifier = Modifier.height(16.dp))
        VisitDetails()
//                Text(text = details, fontSize = 16.sp)
    }
}

    @Composable
    fun VisitDetails(){
        Column(Modifier.padding(horizontal = 8.dp)) {
            VitalBox()
        }
    }

    @Composable
    fun VitalBox(){
        Row() {
            Column() {
                BoldTextView(title = "Vitals", fontSize = 18)

                val sessionsList = MainActivity.subUserRepo.sessions.value.reversed()
                if (sessionsList.isNotEmpty()) {

                    val sess = sessionsList.first()
                    val tempInC = sess.temp.substringBefore("°C").toDoubleOrNull()
                    val sysValue = sess.sys?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
                    val diaValue = sess.dia?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
                    val hrValue = sess.heartRate?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
                    val spo2Value = sess.spO2?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
//            val bmiValue = sess.weight?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()
//            val bodyFatValue = sess.bodyFat?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()
//            val tempValue = sess.temp?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()

                    val sys = sysValue?.toString() ?: ""
                    val dia = diaValue?.toString() ?: ""
                    val hr = hrValue?.toString() ?: ""
                    val spo2 = spo2Value?.toString() ?: ""
//            val bmi = bmiValue?.toString() ?: ""
//            val bodyFat = bodyFatValue?.toString() ?: ""
//            val temp = tempValue?.toString() ?: ""

                    Column(
                        Modifier
                            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SessionBox(
                                title = "BP",
                                value = if((sys.isNullOrEmpty() && dia.isNullOrEmpty())) "${sys}${dia}" else "${sys}/${dia}",
                                iconId = R.drawable.bp,
                                unit = "mmHg"
                            )

                            SessionBox(
                                title = "HR",
                                value = hr,
                                iconId = R.drawable.hr,
                                unit = "bpm")

                            SessionBox(
                                title = "SpO2",
                                value = spo2,
                                iconId = R.drawable.userspo,
                                unit = "%"
                            )


                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SessionBox(
                                title = "Temp",
                                value = MainActivity.adminDBRepo.getTempBasedOnUnit(tempInC),
                                iconId = R.drawable.temp,
                                unit = "°F")

                            val selectedUser = MainActivity.adminDBRepo.getSelectedSubUserProfile()
                            SessionBox(
                                title = "Weight",
                                value = sess.weight,
                                //MainActivity.doctorAddPatientRepo.updateKgToLbs(sess.weight),
                                iconId = R.drawable.weightuser,
                                unit = "lbs"
                            )

                            val result = sess.ecgFileLink.split("_")
                            if (result.size == 6) {
                                SessionBox(
                                    title = "ECG",
                                    value = result.last(),
                                    iconId = R.drawable.ecg,
                                    unit = ""
                                )
                            } else {
                                SessionBox(title = "ECG",
                                    value = "",
                                    iconId = R.drawable.ecg,
                                    unit = "")
                            }
                        }
                    }
                }else{
                    EmptyBox()
                }
            }
        }
    }

    @Composable
    fun EmptyBox(){
        Column(
            Modifier
                .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SessionBox(title = "BP", value = "", iconId = R.drawable.bp, unit = "")
                SessionBox(title = "HR", value = "", iconId = R.drawable.hr, unit = "")
                SessionBox(title = "SpO2", value = "", iconId = R.drawable.userspo, unit = "")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SessionBox(title = "Temp", value = "", iconId = R.drawable.temp, unit = "")
                SessionBox(title = "Weight", value = "", iconId = R.drawable.weightuser, unit = "")
                SessionBox(title = "ECG", value = "", iconId = R.drawable.ecg, unit = "")
            }
        }
    }

@Composable
fun VisitSummaryCards() {
    Column(horizontalAlignment = Alignment.CenterHorizontally )
    {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                BoldTextView(title = "Visits Summary",fontSize=18)
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.AddCircleOutline,
                        contentDescription = "Add Button"
                    )
                }
            }

            VisitSummaryCard(date = "MM/DD/YYYY HH:MM", place = "Banglaore", details = "THis is a Visit Summary Card")
    }
}

//
//@Composable()
//@Preview(showSystemUi = true)
//fun VisitSummaryPreview()
//{
//    VisitSummaryCard(date = "MM/DD/YYYY HH:MM", place = "Banglaore", details = "THis is a Visit Summary Card")
//}

fun gethPx(data: Int, zoomSpo2: Float, height: Float): Float {
    return height - zoomSpo2 * data
}

fun gethMm(data: Int, zoomECGforMm: Float, gain: Int, height: Float): Float {
    var d = 0f
    if (StaticReceive.is128) { //wave Y nMax = 255
        val da = data - 128
        d = height / 2 - zoomECGforMm * (da * gain)
        return BackGround.fMMgetPxfory(d)
    } else { //wave Y nMax = 4095
        val da = data - 2048
        d = height - (da * gain + 2048) / 4096f * height
    }
    return d
}

fun getAge(user: SubUserProfile) : String{
    val dob = user.dob.split("/")
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH) + 1 // add 1 since month is 0-based
    try {
        return if(dob.size == 2){
            val givenMonth = dob[0].toInt()
            val givenYear = dob[1].toInt()
            var age = currentYear - givenYear
            if(currentMonth < givenMonth){
                age--
            }
            age.toString() + " yrs"
        }else{
            ""
        }
    }catch (e : NumberFormatException){
        return ""
    }
}

//@Composable
//fun ProfileCard(user: SubUserProfile){
//    Row(
//        modifier = Modifier
//            .fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//
//        Box(modifier = Modifier
//            .size(65.dp)
//            .clip(CircleShape)
//            .background(Color.LightGray)
//        ) {
//            UserImageView(imageUrl = user.profile_pic_url, size = 65.dp
//            ){}
//        }
//
//        Box(modifier = Modifier.fillMaxWidth()) {
//            Column {
//                Row {
//                    Box(modifier = Modifier
//                        .weight(1f)
//                        .padding(horizontal = 16.dp)
//                    ) {
//                        Column {
//                            LabelWithoutIconView(title = formatTitle(user.frist_name, user.last_name))
//                            Spacer(modifier = Modifier.height(5.dp))
//                            LabelWithIconView(title = user.gender, icon = if(checkIsMale(user.gender)) Icons.Default.Male else Icons.Default.Female)
//                        }
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Box(
//                        modifier = Modifier
//                            .weight(1f)
//                            .padding(horizontal = 16.dp)
//                    ) {
//                        Column {
//                            LabelWithIconView(title = getAge(user), icon = Icons.Default.Cake)
//                            Spacer(modifier = Modifier.height(5.dp))
//                            if(user.height.isNotEmpty()) LabelWithIconView(title = MainActivity.adminDBRepo.getHeightBasedOnUnitSet(user.height.toDouble()), icon = Icons.Default.Height) else LabelWithIconView(title = MainActivity.adminDBRepo.getHeightBasedOnUnitSet(0.0), icon = Icons.Default.Height)
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(5.dp))
//                Row() {
//                    Box(modifier = Modifier
//                        .weight(1f)
//                        .padding(horizontal = 16.dp)
//                    ) {
//                        Column {
//                            LabelWithIconView(title = user.location, icon = Icons.Default.LocationOn)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun UserPhoneVerify(user: SubUserProfile){
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        if (user.phone.isNotEmpty()){
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "phone",
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))

            when{
                user.phone.startsWith("+91") -> {
                    Box(Modifier.weight(1f)) {
                        RegularTextView(title = user.phone)
                    }
                }
                user.phone.startsWith("91") ->{
                    Box(Modifier.weight(1f)) {
                        RegularTextView(title = "+${user.phone}")
                    }
                }
                else -> {
                    Box(Modifier.weight(1f)) {
                        RegularTextView(title = "+91${user.phone}")
                    }
                }
            }

            when{
                (user.isUserVerified == "true" || user.isUserVerified == "True") -> RegularTextView(title = "Verified", textColor = Color.Green)
                (user.isUserVerified == "false" || user.isUserVerified == "False") -> RegularTextView(title =  "Not-Verified", textColor = Color.Red)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SessionActionRow(navHostController: NavHostController, context: Context , showAlert : (Boolean) -> Unit, showSaveAlert : (Boolean) -> Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {

        ActionBtn(btnName = "Restart", size = 22.dp, icon = Icons.Default.Replay) {
            isSaving = false
            isUplodingCLicked = true
            ifIsExitAndSave = false
            MainActivity.subUserRepo.updateProgressState(true)
            MainActivity.subUserRepo.restartSession()
        } // Restart session
        ActionBtn(btnName = "Reset",size = 22.dp, icon = Icons.Default.Undo) {
            ifIsExitAndSave = false
            isSessionSaved = true
            MainActivity.subUserRepo.updateProgressState(true)
            MainActivity.subUserRepo.resetSession()
        } // Reset session
        ActionBtn(btnName = "Save",size = 22.dp, icon = Icons.Default.Save) {
            // if data taken save
            if(MainActivity.subUserRepo.bufferThere.value){
                ifIsExitAndSave = false
                MainActivity.subUserRepo.updateProgressState(true)
                MainActivity.subUserRepo.saveOrUpdateSession()
            }
        } // Save session
        ActionBtn(btnName = "History",size = 22.dp, icon = Icons.Default.History) {
            isGuest = false
            isFromUserHomePage = false
            navHostController.navigate(Destination.SessionHistory.routes)
        } // Session history
    }
}

fun saveOrUpdate(){
    if(!isSessionSaved){
        isSaving = true
        isUplodingCLicked = true
        if(MainActivity.pc300Repo.isEcgDataTaken && MainActivity.csvRepository.getSessionFile() != null) {
            MainActivity.s3Repo.startUploadingFile(MainActivity.csvRepository.getSessionFile()!!)
        }else{
            MainActivity.subUserRepo.createAndUploadSession(MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo)
        }
    }else{
        if(MainActivity.subUserRepo.bufferThere.value){
            isSaving = true
            isUplodingCLicked = true
            if(MainActivity.pc300Repo.isEcgDataTaken && MainActivity.csvRepository.getSessionFile() != null) {
                MainActivity.s3Repo.startUploadingFile(MainActivity.csvRepository.getSessionFile()!!)
            }else{
                MainActivity.subUserRepo.createAndUploadSession(MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo)
            }
        }
    }
}

private val bpHandler = Handler(Looper.getMainLooper())
private val ecgHandler = Handler(Looper.getMainLooper())

fun startBPBeepSound(player: MediaPlayer, isStop: Boolean) {
    if (isStop) {
        player.stop()
        player.release()
        bpHandler.removeCallbacksAndMessages(null)
    } else {
        player.start()
        bpHandler.postDelayed({
            startBPBeepSound(player, isStop)
        }, 1000)
    }
}

fun startECGBeepSound(player: MediaPlayer, isStop: Boolean) {
    if (isStop) {
        player.stop()
        player.release()
        ecgHandler.removeCallbacksAndMessages(null)
    } else {
        player.start()
        ecgHandler.postDelayed({
            startECGBeepSound(player, isStop)
        }, 1000)
    }
}

@Composable
fun updateColorState(): Color {
    var isColor1 by remember { mutableStateOf(true) }
    val colorState = if (isColor1) Color.LightGray else Color.Yellow

    LaunchedEffect(1000) {
        while (true) {
            delay(1000.toLong())
            isColor1 = !isColor1
        }
    }
    return colorState
}

@Composable
fun BloodPressure(pc300Repository: PC300Repository, context: Context){

    val isState = pc300Repository.bloodPressure.value

    val colorHandler = Handler(Looper.getMainLooper())

    val colorState = updateColorState()

    val isError by remember { mutableStateOf(pc300Repository.bloodPressure.value.contains("0/0")) }

    Card(modifier = Modifier.size(width = 160.dp, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {
                    isState.isNullOrEmpty() -> Color.LightGray
                    isState.contains("0/0") -> Color(0xFFBB3F3F)
                    isState.contains("/") -> Color(0xFF90EE90)
                    isState.contains("e") -> Color(0xFFBB3F3F)
                    else -> colorState
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "Blood Pressure", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bp),
                        contentDescription ="BPIcon",Modifier.size(15.dp) )
                }
                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {

                    Log.d("TAG", "BloodPressure: value $isState")
                    when{
                        isState.isNullOrEmpty()-> {
                            Text(text = "")
                        }

                        isState.contains("0/0 mmHg") || isState == "e" -> {
                            MainActivity.playerRepo.stopBpSound()
                            BoldTextView(title = "Error: Do it again", fontSize = 14)
                        }

                        isState.contains("/") -> {
                            MainActivity.playerRepo.stopBpSound()
                            colorHandler.removeCallbacksAndMessages(null)
                            BoldTextView(title = pc300Repository.bloodPressure.value, fontSize = 14)
                        }

                        else -> {
                            MainActivity.playerRepo.startBpSound()
                            BoldTextView(title = pc300Repository.bloodPressure.value, fontSize = 14)
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun HeartRate(pc300Repository: PC300Repository){
    Card(modifier = Modifier.size(width = 160.dp, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when (pc300Repository.heartRate.value.isEmpty()) {
                    true -> Color.LightGray
                    false -> Color(0xFF90EE90)
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "Heart Rate", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.hr),
                        contentDescription ="HRIcon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = pc300Repository.heartRate.value, fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun SPO2(pc300Repository: PC300Repository){

    val isTaken by remember { mutableStateOf(pc300Repository.spO2.value.isNotEmpty()) }

    Card(modifier = Modifier.size(width = 160.dp, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when (pc300Repository.spO2.value.isEmpty()) {
                    true -> Color.LightGray
                    false -> Color(0xFF90EE90)
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "SpO2", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.userspo),
                        contentDescription ="Spo2Icon",Modifier.size(15.dp) )
                }
                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = pc300Repository.spO2.value, fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun Weight(omronRepository: OmronRepository){

    var isRegistered by remember { mutableStateOf(true) }

    var isSynced by remember { mutableStateOf(true) }

    if(!isRegistered) StartRegistration()

    if(isRegistered && !isSynced) StartSyncing()

    Card(modifier = Modifier
        .size(width = 160.dp, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {
                    omronRepository.deviceStat.value == "Syncing" -> Color.Yellow

                    omronRepository.latestUserWeightInfo.value != null -> Color(0xFF90EE90)

                    else -> {
                        Color.LightGray
                    }
                }

            )){

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "Weight", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.weightuser),
                        contentDescription ="weightIcon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(omronRepository.latestUserWeightInfo.value != null) MainActivity.adminDBRepo.getWeightBasedOnUnitSet(omronRepository.latestUserWeightInfo.value!!.weight.toDoubleOrNull()) else omronRepository.deviceStat.value, fontSize = 14)
                }
            }
        }

    }
}

@Composable
fun Temperature(pc300Repository: PC300Repository){

    val tempInC = pc300Repository.temp.value.substringBefore("°C").toDoubleOrNull()

    Card(modifier = Modifier.size(width = 160.dp, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when (pc300Repository.temp.value.isEmpty()) {
                    true -> Color.LightGray
                    false -> Color(0xFF90EE90)
                }
            )
        ){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "Temp", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.temp),
                        contentDescription ="tempIcon",Modifier.size(15.dp) )

                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = MainActivity.adminDBRepo.getTempBasedOnUnitSet(tempInC), fontSize = 14)
                }
            }
        }
    }
}




@Composable
fun ECG(pc300Repository: PC300Repository, context: Context, onClickEcgResult : () -> Unit){

    val isState = pc300Repository.ecg.value

    val colorHandler = Handler(Looper.getMainLooper())

    val colorState = updateColorState()


    Card(modifier = Modifier.size(width = 160.dp, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                if (isState == 1 || isState == 2) {
                    MainActivity.pc300Repo.isShowEcgRealtimeAlert.value =
                        !MainActivity.pc300Repo.isShowEcgRealtimeAlert.value
                }
            }
            .background(
                when (isState) {
                    0 -> colorState
                    1 -> colorState
                    2 -> Color(0xFF90EE90)
                    3, 4 -> Color(0xFFBB3F3F)
                    else -> {
                        Color.LightGray
                    }
                }
            )) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {

                    BoldTextView(title = "ECG", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ecg),
                        contentDescription ="ECGIcon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    when (isState){

                        0,1-> {
                            MainActivity.playerRepo.startEcgSound()
                            if(!isShown) {
                                isShown = true
                                isWriting = true
                                MainActivity.pc300Repo.isShowEcgRealtimeAlert.value = true
                            }
                            BoldTextView(title = "Measuring", fontSize = 14)
                        }

                        2-> {
                            if(isWriting){
                                isWriting = false
                            }
                            MainActivity.playerRepo.stopEcgSound()
                            isShown = false
                            colorHandler.removeCallbacksAndMessages(null)
                            BoldTextView(title = "Done (${MainActivity.pc300Repo.ecgResultCode.value})", fontSize = 14)
                        }

                        3-> {
                            MainActivity.playerRepo.stopEcgSound()
                            isShown = false
                            BoldTextView(title = "Error: Do it again", fontSize = 14)
                        }

                        4-> {
                            MainActivity.playerRepo.stopEcgSound()
                            colorHandler.removeCallbacksAndMessages(null)
                            BoldTextView(title = "Stopped", fontSize = 14)
                        }
                        else -> {
                            Text(text = "")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BodyFat(omronRepository: OmronRepository){
    Card(modifier = Modifier
        .size(width = 160.dp, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {
                    omronRepository.deviceStat.value == "Syncing" -> Color.Yellow

                    omronRepository.latestUserWeightInfo.value != null -> Color(0xFF90EE90)

                    else -> {
                        Color.LightGray
                    }
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    BoldTextView(title = "Body Fat", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bodyfat),
                        contentDescription ="Body Fat Icon",Modifier.size(15.dp) )
                }
                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(omronRepository.latestUserWeightInfo.value != null) omronRepository.latestUserWeightInfo.value!!.bodyFat + " %" else omronRepository.deviceStat.value, fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun BMI(omronRepository: OmronRepository){

    Card(modifier = Modifier
        .size(width = 160.dp, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {
                    omronRepository.deviceStat.value == "Syncing" -> Color.Yellow

                    omronRepository.latestUserWeightInfo.value != null -> Color(0xFF90EE90)

                    else -> {
                        Color.LightGray
                    }
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {

                    BoldTextView(title = "BMI", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bmi),
                        contentDescription ="BMI Icon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(omronRepository.latestUserWeightInfo.value != null) omronRepository.latestUserWeightInfo.value!!.bmi else omronRepository.deviceStat.value, fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun GLU(repo: PC300Repository){

    Card(modifier = Modifier
        .size(width = 160.dp, height = 70.dp),
        shape = RoundedCornerShape(15.dp))
    {

        Box(modifier = Modifier
            .fillMaxSize()
            .background(
                when {

                    repo.glu.value.isNotEmpty() -> Color(0xFF90EE90)

                    else -> {
                        Color.LightGray
                    }
                }
            )){
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally)
            {
                Row(verticalAlignment = Alignment.CenterVertically)
                {

                    BoldTextView(title = "GLU", fontSize = 16)

                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.bmi),
                        contentDescription ="BMI Icon",Modifier.size(15.dp) )
                }

                Row(modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    BoldTextView(title = if(repo.glu.value != null) repo.glu.value else "", fontSize = 14)
                }
            }
        }
    }
}


@Composable
fun UserQuestionOne(pc300Repository: PC300Repository, isReset : Boolean, changeResetState: () -> Unit) {

    var isSelected1A = MainActivity.subUserRepo.isSelected1A

    var isSelected1B = MainActivity.subUserRepo.isSelected1B

    var isSelected1C = MainActivity.subUserRepo.isSelected1C

    var isAnswerExpanded by remember { mutableStateOf(false) }

    if(isReset) {
        isSelected1A.value = false
        isSelected1B.value = false
        isSelected1C.value = false
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BoldTextView(title = "How are you feeling?", fontSize = 14)

        Icon(
            imageVector = when (isAnswerExpanded){
                true -> ImageVector.vectorResource(id = R.drawable.dropdown_up_icon)
                false ->ImageVector.vectorResource(id = R.drawable.dropdown_icon)
            },
            contentDescription = if (isAnswerExpanded) "Collapse answer" else "Expand answer",
            tint = Color.Black,
            modifier = Modifier.clickable {
                MainActivity.subUserRepo.isResetQuestion.value = false
                isAnswerExpanded = !isAnswerExpanded }
        )
    }
    if (isAnswerExpanded) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                Modifier
                    .clickable {
                        isSelected1A.value = true
                        isSelected1B.value = false
                        isSelected1C.value = false
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(1, "1")
                    }
                    .size(width = 90.dp, height = 30.dp)
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(if (isSelected1A.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center) {
                    RegularTextView(title = "Awesome", fontSize = 14)
                }
            }

            Spacer(modifier = Modifier.width(30.dp))

            Card(
                Modifier
                    .clickable {
                        isSelected1A.value = false
                        isSelected1B.value = true
                        isSelected1C.value = false
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(1, "2")
                    }
                    .size(width = 90.dp, height = 30.dp))
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected1B.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    RegularTextView(title = "Well", fontSize = 14)
                }
            }

            Spacer(modifier = Modifier.width(30.dp))


            Card(
                Modifier
                    .clickable {
                        isSelected1A.value = false
                        isSelected1B.value = false
                        isSelected1C.value = true
                        pc300Repository.updateOnGoingSessionQuestionAnswers(1, "3")
                    }
                    .size(width = 90.dp, height = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected1C.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    RegularTextView(title = "Not well", fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun UserQuestionTwo(pc300Repository: PC300Repository, isReset: Boolean, changeResetState : () -> Unit){

    var isSelected2A = MainActivity.subUserRepo.isSelected2A

    var isSelected2B = MainActivity.subUserRepo.isSelected2B

    var isAnswerExpanded by remember { mutableStateOf(false) }

    if(isReset) {
        isSelected2A.value = false
        isSelected2B.value = false
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BoldTextView(title = "Have you drunk alcohol in the last 24 hours?", fontSize = 14)

        Icon(
            imageVector = when (isAnswerExpanded){
                true -> ImageVector.vectorResource(id = R.drawable.dropdown_up_icon)
                false ->ImageVector.vectorResource(id = R.drawable.dropdown_icon)
            },
            contentDescription = if (isAnswerExpanded) "Collapse answer" else "Expand answer",
            tint = Color.Black,
            modifier = Modifier.clickable {
                MainActivity.subUserRepo.isResetQuestion.value = false
                isAnswerExpanded = !isAnswerExpanded }
        )
    }
    if (isAnswerExpanded) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Card(
                Modifier
                    .clickable {
                        isSelected2A.value = true
                        isSelected2B.value = false
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(2, "1")
                    }
                    .size(60.dp, height = 30.dp)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected2A.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center,

                    ) {
                    RegularTextView(title = "Yes", fontSize = 14)
                }
            }

            Spacer(modifier = Modifier.width(100.dp))

            Card(
                Modifier
                    .clickable {
                        isSelected2A.value = false
                        isSelected2B.value = true
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(2, "2")
                    }
                    .size(width = 60.dp, height = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected2B.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center,
                ) {
                    RegularTextView(title = "No", fontSize = 14)
                }
            }
        }
    }
}

@Composable
fun UserQuestionThree(pc300Repository: PC300Repository, medicationName : String, isReset: Boolean, changeResetState : () -> Unit){

    var isSelected3A = MainActivity.subUserRepo.isSelected3A

    var isSelected3B = MainActivity.subUserRepo.isSelected3B

    var isAnswerExpanded by remember { mutableStateOf(false) }

    if(isReset) {
        isSelected3A.value = false
        isSelected3B.value = false
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoldTextView(title = "Did you take your $medicationName medicine?", fontSize = 14)
        Icon(
            imageVector = when (isAnswerExpanded){
                true -> ImageVector.vectorResource(id = R.drawable.dropdown_up_icon)
                false ->ImageVector.vectorResource(id = R.drawable.dropdown_icon)
            },
            contentDescription = if (isAnswerExpanded) "Collapse answer" else "Expand answer",
            tint = Color.Black,
            modifier = Modifier.clickable {
                MainActivity.subUserRepo.isResetQuestion.value = false
                isAnswerExpanded = !isAnswerExpanded }
        )
    }
    if (isAnswerExpanded) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            Card(
                Modifier
                    .clickable {
                        isSelected3A.value = true
                        isSelected3B.value = false
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(3, "1")
                    }
                    .size(60.dp, height = 30.dp)
            ){
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected3A.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center,

                    ) {
                    RegularTextView(title = "Yes", fontSize = 14)
                }
            }

            Spacer(modifier = Modifier.width(100.dp))

            Card(
                Modifier
                    .clickable {
                        isSelected3A.value = false
                        isSelected3B.value = true
                        changeResetState()
                        pc300Repository.updateOnGoingSessionQuestionAnswers(3, "2")
                    }
                    .size(width = 60.dp, height = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isSelected3B.value) Color(0xFF397EF5) else Color.LightGray),
                    contentAlignment = Alignment.Center,
                ) {
                    RegularTextView(title = "No", fontSize = 14)
                }
            }
        }
    }
}


@Composable
fun StartRegistration(){

    var isRegistring by remember { mutableStateOf(false) }
    var isRegTitleMsg by remember { mutableStateOf("Start User registration") }
    var isRegSubTitleMsg by remember { mutableStateOf("Please, put device in pairing mode") }


    if(MainActivity.omronRepo.omronRegistrationFailed.value == true){
        isRegTitleMsg = "Failed, Pelase put device in pairing mode"
        isRegSubTitleMsg = "Device must be in pairing mode before starting"
    }

    Box(){
        AlertDialog(
            onDismissRequest = { },
            text = {
                Column( Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(isRegTitleMsg, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(isRegSubTitleMsg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                if(!isRegistring){
                    Button(
                        onClick = {
                            isRegistring = true
                            isRegTitleMsg = "Registring"
                            isRegSubTitleMsg = "Device must be in pair mode"
                            MainActivity.omronRepo.getLastSeqIncrementKeyForSelectedUser()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ){Text("Start Registration",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center)}
                }
            },
            dismissButton = null
        )
        if(isRegistring) showProgress()
    }
}

@Composable
fun StartSyncing(){

    var isSyncingTitleMsg by remember { mutableStateOf("Start Syncing profile") }
    var isSyncingSubTitleMsg by remember { mutableStateOf("Please, put device in pairing mode") }

    var isSyncing by remember { mutableStateOf(false) }

    if(MainActivity.omronRepo.omronRegistrationFailed.value == true){
        isSyncingTitleMsg = "Failed, Pelase pur device in pairing mode"
        isSyncingSubTitleMsg = "Device must be in pairing mode before starting"
    }

    Box(){
        AlertDialog(
            onDismissRequest = { },
            text = {
                Column( Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(isSyncingTitleMsg, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(isSyncingSubTitleMsg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                if(!isSyncing){
                    Button(onClick = {
                        isSyncingTitleMsg = "Syncing"
                        isSyncingSubTitleMsg = "Device must be in pair mode"
                        isSyncing = true
                        MainActivity.omronRepo.syncUserDataToDevice()
                    },
                        modifier = Modifier.fillMaxWidth()
                    ){Text("Start Syncing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center)}
                } }, dismissButton = null
        )
        if(isSyncing) showProgress()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun previwe()
{
//    Check(navHostController = rememberNavController())
    VisitSummaryCards()
}

