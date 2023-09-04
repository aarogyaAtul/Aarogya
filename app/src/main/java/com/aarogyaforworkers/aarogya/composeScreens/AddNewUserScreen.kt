@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.aarogyaforworkers.aarogya.composeScreens

import Commons.AddEditUserPageTags
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.asImageBitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.aarogyaforworkers.aarogya.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogya.Camera.CameraRepository
import com.aarogyaforworkers.aarogya.Commons.isCurrentUserVerifiedPhone
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.Location.LocationRepository
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.SubUser.SessionStates
import com.aarogyaforworkers.aarogya.SubUser.SubUserDBRepository
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import java.io.ByteArrayOutputStream
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import com.aarogyaforworkers.aarogya.Commons.*

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun AddNewUserScreen(navHostController: NavHostController, adminDBRepository: AdminDBRepository, cameraRepository: CameraRepository, locationRepository: LocationRepository, subUserDBRepository: SubUserDBRepository) {
    Disableback()
    var isThereAnyChange = MainActivity.subUserRepo.changeInProfile.value
    var newUser by remember { mutableStateOf(SubUserProfile("", "", "", "", "", "", "", "", "", "", "", "", "")) }
    var genderOption = listOf("Male", "Female","Other")
    var isSaving by remember { mutableStateOf(false) }
    var isShowAlert by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(false) }
    var isShowAlertUserAllReadyPresent by remember { mutableStateOf(false) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var userphone by remember { mutableStateOf("") }
    var switchState by remember { mutableStateOf("c.m.") }
    var inch by remember { mutableStateOf("") }
    var ft by remember { mutableStateOf("") }
    var cm by remember { mutableStateOf("") }
    var showOTPDialog by remember { mutableStateOf(false) }
    var expandedMonth by remember { mutableStateOf(false) }
    var selectedMonthInt by remember { mutableStateOf("00") }
    var selectedMonth by remember { mutableStateOf("Month") }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 18 downTo currentYear - 100).toList()
    var expandedYear by remember { mutableStateOf(false) }
    var selectedYear by remember { mutableStateOf("Year") }
    var image = painterResource(R.drawable.profile_icon)
    var profileIconImage by remember { mutableStateOf(image) }
    // Errors
    var isFirstNameError by remember { mutableStateOf(false) }
    var isGenderError by remember { mutableStateOf(false) }
    var isHeightError by remember { mutableStateOf(false) }
    var isPhoneError by remember { mutableStateOf(false) }
    var isMonthError by remember { mutableStateOf(false) }
    var isYearError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isPhoneVerified by remember { mutableStateOf(false) }
    var isCaptured by remember { mutableStateOf(false) }

    val monthArray : List<String> = listOf<String>("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    var isPhoneValid by remember { mutableStateOf(true) }

    var capturedImageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    if(isEditUser && !isSetUpDone){
        firstName = userProfileToEdit?.frist_name.toString()
        lastName = userProfileToEdit?.last_name.toString()
        selectedGender= userProfileToEdit?.gender.toString()
        userphone = userProfileToEdit?.phone.toString()
        val status = userProfileToEdit?.isUserVerified.toString()
        isPhoneVerified = !(status == "false" || status == "False")
        cm = userProfileToEdit?.height.toString()
        if(isPhoneVerified) isCurrentUserVerifiedPhone = userProfileToEdit?.phone.toString()
        val convert = convertCmToFeetAndInch(cm.toDouble())
        ft = convert.first.toString()
        inch = convert.second.toString()
        val monthIndex = userProfileToEdit?.dob.toString().split("/")[0].toInt()
        selectedMonthInt = monthIndex.toString()
        selectedMonth = monthArray[monthIndex]
        selectedYear = userProfileToEdit?.dob.toString().split("/")[1]
        if(userProfileToEdit?.medical_history!!.contains(",")){
            subUserDBRepository.parseUserMedicalHistory(userProfileToEdit!!)
        }
    }

    when{
        subUserDBRepository.currentPhoneAllReadyRegistered.value == true && isCheckingUserBeforeSendingOTP -> {
            isSaving = false
            isShowAlertUserAllReadyPresent = true
            MainActivity.subUserRepo.updateCurrentPhoneRegistrationState(null)
        }
        subUserDBRepository.currentPhoneAllReadyRegistered.value == false && isCheckingUserBeforeSendingOTP -> {
            if(!isAllreadyOtpSent){
                if(!showOTPDialog) adminDBRepository.sendSubUserVerificationCode(userphone)
                showOTPDialog = true
                isSaving = false
                isAllreadyOtpSent = true
            }
        }
    }

    if(lastCreateUserValue != adminDBRepository.subUserProfileCreateUpdateState.value){
        if(adminDBRepository.subUserProfileCreateUpdateState.value) {
            MainActivity.adminDBRepo.setNewSubUserprofile(newUser.copy())
            MainActivity.adminDBRepo.setNewSubUserprofileCopy(newUser.copy())
            if(isEditUser) userProfileToEdit = newUser
            isSaving = false
            if(!isEditUser){
//                isOnUserHomeScreen = true
                MainActivity.subUserRepo.startFetchingAfterUserCreation()
                MainActivity.omronRepo.isReadyForFetch = false
                MainActivity.subUserRepo.isResetQuestion.value = true
                MainActivity.subUserRepo.isResetQuestion.value = true
                MainActivity.subUserRepo.updateSessionState(SessionStates(false, false, false, false, false))
                MainActivity.subUserRepo.resetStates()
                ifIsExitAndSave = false
                MainActivity.pc300Repo.clearSessionValues()
                MainActivity.subUserRepo.lastSavedSession = null
                MainActivity.subUserRepo.createNewSession()
                navHostController.navigate(Destination.UserHome.routes)
            }
            subUserDBRepository.updateChange(false)
        } else isSaving = false
        lastCreateUserValue = adminDBRepository.subUserProfileCreateUpdateState.value
    }

    locationRepository.getLocation(context)

    if(showOTPDialog) ShowConfirmOtpAlert(userphone = userphone, onConfrimOtp = {
        isCurrentUserVerifiedPhone = userphone
        isPhoneVerified = it
        if(isEditUser) userProfileToEdit?.isUserVerified = it.toString()
        if (isPhoneVerified) {
            showOTPDialog = false
            isSaving = false
        }
    }) {
        showOTPDialog = false
        isSaving = false
    }

    AlertView(
        showAlert = isShowAlert
        ,title = "Are you sure?",
        subTitle = "Do you really want to close it?",
        subTitle1 = "",
        onYesClick = {
            adminDBRepository.setSubUserProfilePicture(null)
            if(isEditUser){
                userProfileToEdit = MainActivity.adminDBRepo.subUserProfileToEditCopy.copy()
                MainActivity.adminDBRepo.setNewSubUserprofile(MainActivity.adminDBRepo.subUserProfileToEditCopy.copy())
                Thread.sleep(100)
                navHostController.navigate(Destination.UserHome.routes)
            }else{
                navHostController.navigate(Destination.Home.routes)
            }
            subUserDBRepository.updateChange(false)
            isShowAlert = false
        },
        onNoClick = {
            isShowAlert = false
        }
    ){
        isShowAlert = false
    }

    if(isShowAlertUserAllReadyPresent) ShwowCustomAlert(title = "User Already Registered", subTitle = "There is already a user linked with given phone") {
        isSaving = false
        isShowAlertUserAllReadyPresent = false
        isCheckingUserBeforeSendingOTP = false
        isUserAllreadyRegistered = true
        allReadyRegisteredPhone = userphone
    }

    val phonePattern = "^\\d{10}$".toRegex()

    fun isValidIndianPhoneNumber(phone: String): Boolean {
        return phonePattern.matches(phone) && phone.startsWith("7") || phone.startsWith("8") || phone.startsWith("9") && phone.isNotEmpty()
    }

    if(!showCamera){
        Box(
            modifier = Modifier
                .testTag(AddEditUserPageTags.shared.addEditUserScreen)
                .background(Color.White)
                .fillMaxSize()
        ) {
            Column {

                TopBarWithBackSaveBtn(onSaveVisible = subUserDBRepository.changeInProfile.value, onBackBtnPressed = {
                    when(isEditUser){
                        true -> {
                            timestamp = System.currentTimeMillis().toString()
                            if(isThereAnyChange){
                                isShowAlert = true
                                isSavingOrUpdating = true
                            }else{
                                navHostController.navigate(Destination.UserHome.routes)
                            }
                        }
                        false ->{
                            if(firstName.isEmpty() && lastName.isEmpty() && selectedGender.isEmpty() && selectedMonth == "Select Month" && selectedYear == "Select Year" && cm.isEmpty() && ft.isEmpty() && inch.isEmpty() && userphone.isEmpty() && !isThereAnyChange){
                                subUserDBRepository.updateChange(false)
                                navHostController.navigate(Destination.Home.routes)
                            }else{
                                if(isThereAnyChange){
                                    isShowAlert = true
                                    isSavingOrUpdating = true
                                }else{
                                    navHostController.navigate(Destination.Home.routes)
                                }
                            }
                        }
                    }
                }) {
                    isFirstNameError = firstName.isEmpty()
                    isGenderError = selectedGender.isEmpty()
                    isMonthError = selectedMonth == "Month"
                    isYearError = selectedYear == "Year"
                    isHeightError = ft.isEmpty() && switchState == "ft. in."
                    isHeightError = cm.isEmpty() && switchState == "c.m."
                    if(userphone.isNotEmpty()) isPhoneError = !isPhoneValid else isPhoneError = false
                    if(isValid(arrayListOf(isFirstNameError, isGenderError, isMonthError, isYearError, isHeightError, isPhoneError))) {
                        isSaving = true
                        adminDBRepository.resetStates()
                        isSavingOrUpdating = true
                        lastUserRegisteredState = true
                        lastUserNotRegisteredState = false
                        isSetUpDone = false
                        if(isEditUser) {
                            if(isCurrentUserVerifiedPhone != userProfileToEdit?.phone) isCurrentUserVerifiedPhone = ""
                        }
                        // Save or update ->
                        val locatiom = locationRepository.userLocation.value
                        var userHeight = cm
                        if(cm.isEmpty()){
                            val totalInch = ft.toDouble() * 12 + inch.toDouble()
                            val result = totalInch * 2.54
                            userHeight = result.toString()
                        }
                    val medicalAnswer = adminDBRepository.getMedicalHistory()
                    val patientId = firstName.take(4).padStart(4, '0') + selectedMonthInt.format("%02d")+ selectedYear + "/" + UUID.randomUUID().toString().take(6)
                    if(isEditUser) newUser = SubUserProfile(userProfileToEdit?.user_id.toString(), userphone, isPhoneVerified.toString(), firstName, lastName, "$selectedMonthInt/$selectedYear", selectedGender, userHeight, locatiom?.city + " " + locatiom?.postalCode, userProfileToEdit!!.profile_pic_url, medicalAnswer, "0", "")
                    if(!isEditUser) newUser = SubUserProfile(patientId, userphone, isPhoneVerified.toString(), firstName, lastName, "$selectedMonthInt/$selectedYear", selectedGender, userHeight, locatiom?.city + " " + locatiom?.postalCode, "Not-Given", medicalAnswer, "0", "")
                    if(isEditUser) adminDBRepository.adminUpdateSubUser(newUser) else adminDBRepository.adminCreateNewSubUser(newUser)
                        if(isEditUser) MainActivity.adminDBRepo.setNewSubUserprofileCopy(newUser)
                    lastUserNotRegisteredState = adminDBRepository.userNotRegisteredState.value
                    }
                }

                LazyColumn {
                    item{
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp)
                                .alpha(if (isSaving || isShowAlert) 0.07f else 1.0f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            Box {

                                if(isCaptured) capturedImageBitmap?.let { Image(bitmap = it, contentDescription = "profilePic", modifier = Modifier
                                    .size(100.dp)
//                                    .rotate(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 90f else 0f)
                                    .clip(CircleShape), contentScale = ContentScale.FillHeight,) }
                                else{
                                    if(isEditUser){
                                        if(userProfileToEdit!!.profile_pic_url.length > 20){
                                            LoadUserHomeImage(profileUrl = userProfileToEdit!!.profile_pic_url)
                                        }else{
                                            Image(
                                                painter = profileIconImage,
                                                contentDescription = "profilePic",
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(CircleShape))
                                        }
                                    }else{
                                        Image(
                                            painter = profileIconImage,
                                            contentDescription = "profilePic",
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(CircleShape))
                                    }
                                }
                                FloatingActionButton(
                                    onClick = {
                                        isCameraCliked = true
                                        showCamera = true },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.BottomEnd)
                                    //.padding(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.camera_icon),
                                        contentDescription = "photoUpload"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            InputView(
                                title = "Name*",
                                textIp = firstName,
                                textIp1 = lastName,
                                onChangeIp = {
                                    subUserDBRepository.updateChange(true)
                                    firstName = it.capitalize(Locale.ROOT) // capitalize the first character of the first name
                                    if (isEditUser) updateFirstLastName(firstName, lastName)
                                    if (it.isNotEmpty()) isFirstNameError = false
                                },
                                onChangeIp1 = {
                                    subUserDBRepository.updateChange(true)
                                    lastName = it.capitalize(Locale.ROOT) // capitalize the first character of the last name
                                    if (isEditUser) updateFirstLastName(firstName, lastName)
                                },
                                keyboard = KeyboardType.Text,
                                placeholderText = "First Name",
                                placeholderText1 = "Last Name",
                                tag = AddEditUserPageTags.shared.firstName,
                                isEdit = true
                            )
                            when{
                                isFirstNameError-> Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 75.dp)) {
                                    ErrorMessage(errorMessage = "Enter name", errorTestTag = "tagNameError")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.width(75.dp)){
                                    BoldTextView(title = "D.O.B.*")}
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(5.dp),
                                            colors = if(isMonthError) ButtonDefaults.buttonColors(Color.Red) else ButtonDefaults.buttonColors(Color.LightGray),
                                            onClick = {
                                                expandedMonth = true },
                                            content = {
                                                Text(text = "${selectedMonth}")
                                            }
                                        )
                                        DropdownMenu(
                                            expanded = expandedMonth,
                                            onDismissRequest = { expandedMonth = false },
                                            modifier = Modifier
                                                .width(IntrinsicSize.Min)
                                                .heightIn(max = 400.dp)
                                        ) {
                                            repeat(12) { index ->
                                                val month = monthArray[index]
                                                ListItem(
                                                    headlineText = { Text(month) },
                                                    modifier = Modifier.clickable(
                                                        onClick = {
                                                            subUserDBRepository.updateChange(true)
                                                            selectedMonthInt = String.format("%02d", index) // Add leading zero if necessary
                                                            selectedMonth = monthArray[index]
                                                            if(isEditUser) updateDob("$selectedMonthInt/$selectedYear")
                                                            expandedMonth = false
                                                            isMonthError = false
                                                        }
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Button(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(5.dp),
                                            colors = if(isYearError) ButtonDefaults.buttonColors(Color.Red) else ButtonDefaults.buttonColors(Color.LightGray),
                                            onClick = { expandedYear = true },
                                            content = {
                                                Text(text = "${selectedYear}")
                                            }
                                        )
                                        DropdownMenu(
                                            expanded = expandedYear,
                                            onDismissRequest = { expandedYear = false },
                                            modifier = Modifier
                                                .width(IntrinsicSize.Min)
                                                .heightIn(max = 400.dp)
                                        ) {
                                            years.forEach { year ->
                                                ListItem(
                                                    headlineText = { Text(text = year.toString()) },
                                                    modifier = Modifier.clickable {
                                                        selectedYear = year.toString()
                                                        if(isEditUser) updateDob("$selectedMonthInt/$selectedYear")
                                                        expandedYear = false
                                                        isYearError = false
                                                        subUserDBRepository.updateChange(true)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            when{
                                isMonthError || isYearError-> Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 75.dp)) {
                                    ErrorMessage(errorMessage = "Select D.O.B.", errorTestTag = "tagDobError")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.width(75.dp)){BoldTextView(title = "Gender*")}
                                Column {
                                    Row {
                                        genderOption.forEach { gender->
                                            Box(
                                                Modifier
                                                    .size(22.dp).testTag(gender),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                RadioButton(selected = selectedGender==gender,
                                                    onClick = {
                                                        subUserDBRepository.updateChange(true)
                                                        selectedGender=gender
                                                        if(isEditUser) updateGender(gender)
                                                        isGenderError = false},
                                                    colors = RadioButtonDefaults.colors(selectedColor = Color.Black),
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(5.dp))
                                            RegularTextView(title = gender)
                                            Spacer(modifier = Modifier.width(15.dp))
                                        }
                                    }
                                }
                            }
                            when{
                                isGenderError-> Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 75.dp)) {
                                    ErrorMessage(errorMessage = "Select Gender", errorTestTag = "tagGenderError")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    when (switchState) {
                                        "c.m." -> InputView(
                                            title = "Height*",
                                            textIp = cm,
                                            onChangeIp = {
                                                subUserDBRepository.updateChange(true)
                                                cm = it.take(3)
                                                if(isEditUser) updateHeight(cm)
                                                if(it.isEmpty()){
                                                    cm = ""
                                                    ft = ""
                                                    inch = ""
                                                }else{
                                                    try {
                                                        ft = convertCmToFeetAndInch(cm.toDouble()).first.toString()
                                                        inch = convertCmToFeetAndInch(cm.toDouble()).second.toInt().toString()
                                                    } catch (e: Exception) {
                                                        println("Error occurred: ${e.message}")
                                                    }
                                                }
                                                isHeightError = false},
                                            tag = "tagHeightView",
                                            keyboard = KeyboardType.Number,
                                            placeholderText = "cm",
                                            isEdit = true,
                                            isError = isHeightError
                                        )
                                        "ft. in." -> InputView(
                                            title = "Height*",
                                            textIp = ft,
                                            textIp1 = inch,
                                            onChangeIp = {
                                                subUserDBRepository.updateChange(true)
                                                ft = it.take(1)
                                                if(it.isEmpty()){
                                                    cm = if(inch.isEmpty()){
                                                        convertFeetAndInchToCm(0, 0.0).toString()
                                                    }else{
                                                        convertFeetAndInchToCm(0, inch.toDouble()).toString()
                                                    }
                                                    if(cm == "0.0") cm = ""
                                                    if(isEditUser) updateHeight(cm)
                                                }else{
                                                    try {
                                                        cm = convertFeetAndInchToCm(ft.toInt(), inch.toDouble()).toString()
                                                    } catch (e: Exception) {
                                                        println("Error occurred: ${e.message}")
                                                    }
                                                    if(isEditUser) updateHeight(cm)
                                                }
                                                isHeightError = false},
                                            onChangeIp1 = {
                                                subUserDBRepository.updateChange(true)
                                                inch = it.take(2)
                                                if(it.isEmpty()){
                                                    if(ft.isEmpty()){
                                                        cm = convertFeetAndInchToCm( 0, 0.0).toString()
                                                    }else{
                                                        cm = convertFeetAndInchToCm( ft.toInt(), 0.0).toString()
                                                    }
                                                    if(cm == "0.0") cm = ""
                                                    if(isEditUser) updateHeight(cm)
                                                }else{
                                                    try {
                                                        cm = convertFeetAndInchToCm(ft.toInt(), inch.toDouble()).toString()
                                                    } catch (e: Exception) {
                                                        println("Error occurred: ${e.message}")
                                                    }
                                                    if(isEditUser) updateHeight(cm)
                                                }
                                                try {
                                                    cm = convertFeetAndInchToCm(ft.toInt(), inch.toDouble()).toString()
                                                } catch (e: Exception) {
                                                    println("Error occurred: ${e.message}")
                                                }
                                                if(isEditUser) updateHeight(cm)
                                                isHeightError = false} ,
                                            tag = "tagHeight",
                                            keyboard = KeyboardType.Number ,
                                            placeholderText = "ft",
                                            placeholderText1 = "in",
                                            isEdit = true,
                                            isError = isHeightError
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                                Switch(
                                    checked = switchState == "ft. in.",
                                    onCheckedChange = { isChecked ->
                                        switchState = if (isChecked) "ft. in." else "c.m."
                                    }
                                )
                                Text(
                                    text = if (switchState == "ft. in.") "ft" else "cm",
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            when{
                                isHeightError-> Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 75.dp)) {
                                    ErrorMessage(errorMessage = "Enter height", errorTestTag = "tagHeightError")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    InputView(
                                        title = "Phone",
                                        textIp = userphone,
                                        onChangeIp ={
                                            subUserDBRepository.updateChange(true)
                                            userphone = it.take(10)
                                            isPhoneVerified = isCurrentUserVerifiedPhone == userphone
                                            isPhoneError = false
                                            userphone = userphone.filter { !it.isWhitespace() } // validate mobileNumber based on the new value
                                            isPhoneValid = userphone.length == 10
                                            updatePhone(userphone)
                                            if(userphone.isNotEmpty()){
                                                if(isEditUser){
                                                    if(userProfileToEdit!!.phone != userphone){
                                                        isPhoneVerified = false
                                                    }
                                                }
                                            } } ,
                                        tag = "tagPhoneView",
                                        keyboard = KeyboardType.Number,
                                        placeholderText = "Phone No.",
                                        isEdit = true,
                                        isError = !isPhoneValid && userphone.isNotEmpty()
                                    )
                                }
                                Box(contentAlignment = Alignment.CenterEnd) {
                                    if(isPhoneValid && userphone.isNotEmpty()){
                                        if(isEditUser) if(userProfileToEdit != null && isCurrentUserVerifiedPhone.isNotEmpty()) {
                                            isPhoneVerified = userphone == isCurrentUserVerifiedPhone
                                            Log.d("TAG", "AddNewUserScreen: $userphone")
                                            userProfileToEdit?.isUserVerified = isPhoneVerified.toString()
                                        }
                                        TextButton(onClick = {
                                            isCheckingUserBeforeSendingOTP = true
                                            isAllreadyOtpSent = false
                                            if(allReadyRegisteredPhone.isEmpty()){
                                                isSaving = true
                                                adminDBRepository.getSubUserByPhone(userphone)
                                            }else if(allReadyRegisteredPhone == userphone){
                                                isUserAllreadyRegistered = true
                                                isShowAlertUserAllReadyPresent = true
                                            }else{
                                                isSaving = true
                                                adminDBRepository.getSubUserByPhone(userphone)
                                            }
                                            subUserDBRepository.updateChange(true)

                                                             }
                                            , enabled = !isPhoneVerified
                                        ) {
                                            if(isPhoneVerified) {
                                                updatePhoneVerifiedStatus()
                                                Text(text = "Verified", fontSize = 16.sp, color = Color(0xFF397EF5))
                                            }else{
                                                Text(text = "Verify", fontSize = 16.sp, color = Color(0xFF397EF5))
                                            }
                                        }
                                    }
                                }
                            }
                            when{
                                !isPhoneValid && userphone.isNotEmpty()-> Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 75.dp)) {
                                    //#change12May
                                    ErrorMessage(errorMessage = "Please enter 10 digit phone number.", errorTestTag = "tagPhoneError")

                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val city = locationRepository.userLocation.value?.city
                                val postalCode = locationRepository.userLocation.value?.postalCode
                                InputView(
                                    title = "Location",
                                    textIp = "$city, $postalCode",
                                    onChangeIp = {},
                                    tag = "tagLocationView",
                                    keyboard = KeyboardType.Text,
                                    placeholderText = "Location",
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            MedicalQuestion(adminDBRepository, subUserDBRepository)
                        }
                    }
                }
            }
            if(isSaving) showProgress()
        }
    } else {
        val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
        var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
        var preview by remember { mutableStateOf<androidx.camera.core.Preview?>(null) }
        val executor = ContextCompat.getMainExecutor(context)
        val cameraProvider = cameraProviderFuture.get()
        val lifecycleOwner = LocalLifecycleOwner.current
        Box {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    cameraProviderFuture.addListener({
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .apply {
                                setAnalyzer(executor, FaceAnalyzer())
                            }
                        imageCapture = ImageCapture.Builder()
                            .setTargetRotation(previewView.display.rotation)
                            .build()

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            imageCapture,
                            preview
                        )
                    }, executor)
                    preview = androidx.camera.core.Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    previewView
                }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .align(Alignment.TopStart)
            ) {
                IconButton(
                    onClick = {
                        isSetUpDone = false
                        showCamera = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "back arrow"
                    )
                }
            }


            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.DarkGray, RoundedCornerShape(15.dp))
                    .padding(8.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Button(
                    onClick = {
                        val imgCapture = imageCapture ?: return@Button
                        imgCapture.takePicture(executor, @ExperimentalGetImage object : ImageCapture.OnImageCapturedCallback(){
                            override fun onCaptureSuccess(image: ImageProxy) {
                                super.onCaptureSuccess(image)
                                val buffer = image.planes[0].buffer
                                val bytes = ByteArray(buffer.remaining())
                                buffer.get(bytes)
                                val byteArrayOutputStream = ByteArrayOutputStream()
                                val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                val matrix = Matrix()
                                if (Build.VERSION.SDK_INT >= 30){
                                    matrix.postRotate(90f) // Rotate the image by 90 degrees
                                }
                                val rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                                capturedImageBitmap = rotatedBitmap.asImageBitmap()
                                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                                val imageArray = byteArrayOutputStream.toByteArray()
                                isUpdatingProfile = true
                                isSetUpDone = false
                                subUserDBRepository.updateChange(true)
                                adminDBRepository.setSubUserProfilePicture(imageArray)

                                Handler(Looper.getMainLooper()).postDelayed({
                                    image.close()
                                    showCamera = false
                                    isCaptured = true
                                }, 1000)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                super.onError(exception)
                                cameraRepository.onImageClickFailed(true)
                            }
                        })

                              },
                    modifier = Modifier
                        .size(70.dp)
                        .background(Color.LightGray, CircleShape)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .border(5.dp, Color.LightGray, CircleShape),
                    colors = ButtonDefaults.buttonColors(Color.LightGray),
                ) {

                }
            }
        }
    }

}

fun isValid(values: ArrayList<Boolean>) : Boolean{
    for(v in values){
        if(v) return false
    }
    return true
}

@Composable
fun QuestionsRow(
    text: String,
    options: List<String>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
    spacing: Dp = 80.dp
) {
    if(text.isNotEmpty()){
        Row {
            BoldTextView(title = text)
        }
    }else{
        Spacer(modifier = Modifier.height(5.dp))
    }
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        options.forEachIndexed { index, option ->
            Box(
                Modifier
                    .size(22.dp),
                contentAlignment = Alignment.Center
            ) {
                RadioButton(
                    selected = selectedOption == index,
                    onClick = { onOptionSelected(index) },
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            RegularTextView(title = option)
            if(index < options.size - 1){
                Spacer(modifier = Modifier.width(spacing))
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun MedicalQuestion(adminDBRepository: AdminDBRepository, subUserDBRepository: SubUserDBRepository) {

    var q1 by remember { mutableStateOf(6) }
    var q11 by remember { mutableStateOf(6) }
    var isShownQ11 by remember { mutableStateOf(false) }
    var q2 by remember { mutableStateOf(6) }
    var q21 by remember { mutableStateOf(6) }
    var isShownQ21 by remember { mutableStateOf(false) }
    var q3 by remember { mutableStateOf(6) }
    var q4 by remember { mutableStateOf(6) }
    var q5 by remember { mutableStateOf(6) }
    var q51 by remember { mutableStateOf(6) }
    var isShown51 by remember { mutableStateOf(false) }
    var q54 by remember { mutableStateOf("") } //textfield 5th others value
    var isShown54 by remember { mutableStateOf(false) }
    var q6 by remember { mutableStateOf(6) }
    var q61 by remember { mutableStateOf("") } // textfield 6th
    var isShown61 by remember { mutableStateOf(false) }
    var q7 by remember { mutableStateOf(6) }
    var q71 by remember { mutableStateOf("") } // textfield 7th
    var isShown71 by remember { mutableStateOf(false) }

    if(isEditUser && !isSetUpDone){
        if(userProfileToEdit?.medical_history!!.contains(",")){
            var answers = subUserDBRepository.subUserMedicalHistory.value
            answers.forEachIndexed { index, answer ->

                when(index + 1){

                    1 -> {
                        q1 = answer.answer - 1
                        q11 = answer.subAnswer - 1
                        isShownQ11 = answer.answer == 1
                        updateValueOfAnswer(0, (q1+1).toString(), (q11+1).toString(), adminDBRepository, subUserDBRepository)
                    }

                    2 -> {
                        q2 = answer.answer - 1
                        q21 = answer.subAnswer - 1
                        isShownQ21 = answer.answer == 1
                        updateValueOfAnswer(1, (q2+1).toString(), (q21+1).toString(), adminDBRepository, subUserDBRepository)
                    }

                    3 -> {
                        q3 = answer.answer - 1
                        updateValueOfAnswer(2, (q3+1).toString(), "5", adminDBRepository, subUserDBRepository)
                    }

                    4 -> {
                        q4 = answer.answer - 1
                        updateValueOfAnswer(3, (q4+1).toString(), "5", adminDBRepository, subUserDBRepository)
                    }

                    5 -> {
                        q5 = answer.answer - 1
                        q51 = answer.subAnswer - 1
                        isShown51 = answer.answer == 1
                        q54 = answer.subAnsOther
                        isShown54 = answer.subAnswer == 4
                        updateValueOfAnswer(4, (q5+1).toString(), "${q51+1}:$q54", adminDBRepository, subUserDBRepository)

                    }

                    6 -> {
                        q6 = answer.answer - 1
                        q61 = answer.subAnsOther
                        isShown61 = answer.answer == 1
                        updateValueOfAnswer(5, (q6+1).toString(), q61, adminDBRepository, subUserDBRepository)
                    }

                    7 -> {
                        q7 = answer.answer - 1
                        q71 = answer.subAnsOther
                        isShown71 = answer.answer == 1
                        updateValueOfAnswer(6, (q7+1).toString(), q71, adminDBRepository, subUserDBRepository)
                    }

                    8 -> {
                        isSetUpDone = true
                    }
                }
            }
        }
    }

    if(!isEditUser && isCameraCliked){
        if(newUserProfile?.medical_history!!.contains(",")){
            var answers = subUserDBRepository.subUserMedicalHistory.value
            answers.forEachIndexed { index, answer ->

                when(index + 1){

                    1 -> {
                        q1 = answer.answer - 1
                        q11 = answer.subAnswer - 1
                        isShownQ11 = answer.answer == 1
                        updateValueOfAnswer(0, (q1+1).toString(), (q11+1).toString(), adminDBRepository, subUserDBRepository)
                    }

                    2 -> {
                        q2 = answer.answer - 1
                        q21 = answer.subAnswer - 1
                        isShownQ21 = answer.answer == 1
                        updateValueOfAnswer(1, (q2+1).toString(), (q21+1).toString(), adminDBRepository, subUserDBRepository)
                    }

                    3 -> {
                        q3 = answer.answer - 1
                        updateValueOfAnswer(2, (q3+1).toString(), "5", adminDBRepository, subUserDBRepository)
                    }

                    4 -> {
                        q4 = answer.answer - 1
                        updateValueOfAnswer(3, (q4+1).toString(), "5", adminDBRepository, subUserDBRepository)
                    }

                    5 -> {
                        q5 = answer.answer - 1
                        q51 = answer.subAnswer - 1
                        isShown51 = answer.answer == 1
                        q54 = answer.subAnsOther
                        isShown54 = answer.subAnswer == 4
                        updateValueOfAnswer(4, (q5+1).toString(), "${q51+1}:$q54", adminDBRepository, subUserDBRepository)

                    }

                    6 -> {
                        q6 = answer.answer - 1
                        q61 = answer.subAnsOther
                        isShown61 = answer.answer == 1
                        updateValueOfAnswer(5, (q6+1).toString(), q61, adminDBRepository, subUserDBRepository)
                    }

                    7 -> {
                        q7 = answer.answer - 1
                        q71 = answer.subAnsOther
                        isShown71 = answer.answer == 1
                        updateValueOfAnswer(6, (q7+1).toString(), q71, adminDBRepository, subUserDBRepository)
                    }

                    8 -> {
                        isSetUpDone = true
                    }
                }
            }
        }
        isCameraCliked = false
    }

    val optionList = listOf("Yes", "No")
    val q1Q2List = listOf("Often", "Sometimes", "Not to say")
    val q5List = listOf("B.P", "Asthma", "Diabetes", "Others")


    Spacer(modifier = Modifier.height(10.dp))


    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(Color.LightGray)){Text(text = " Medical History ", Modifier.padding(5.dp), style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))}
    Spacer(modifier = Modifier.height(10.dp))


    Column(modifier = Modifier.fillMaxWidth()) {
        QuestionsRow(text = "Do you consume alcohol?", options = optionList , selectedOption = q1, onOptionSelected = {
            q1 = it
            isShownQ11 = q1 == 0
            if(q1 == 1) q11 = 6
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(0, (q1+1).toString(), "5", adminDBRepository, subUserDBRepository)
        } )
        if(isShownQ11) QuestionsRow(text = "", options = q1Q2List, selectedOption = q11, onOptionSelected = {
            q11 = it
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(0, (q1+1).toString(), (q11+1).toString(), adminDBRepository, subUserDBRepository)
        }, 25.dp)
        Spacer(modifier = Modifier.height(8.dp))
        QuestionsRow(text = "Do you Smoke?", options = optionList , selectedOption = q2, onOptionSelected = {
            q2 = it
            isShownQ21 = q2 == 0
            if(q2 == 1) q21 = 6
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(1, (q2+1).toString(), "5", adminDBRepository, subUserDBRepository)
        } )
        if(isShownQ21) QuestionsRow(text = "", options = q1Q2List, selectedOption = q21, onOptionSelected = {
            q21 = it
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(1, (q2+1).toString(), (q21+1).toString(), adminDBRepository, subUserDBRepository)
        }, 25.dp)
        Spacer(modifier = Modifier.height(8.dp))
        QuestionsRow(text = "Do you exercise?", options = optionList , selectedOption = q3, onOptionSelected = {
            q3 = it
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(2, (q3+1).toString(), "5", adminDBRepository, subUserDBRepository)
        } )
        Spacer(modifier = Modifier.height(8.dp))
        QuestionsRow(text = "Do you sleep well?", options = optionList , selectedOption = q4, onOptionSelected = {
            q4 = it
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(3, (q3+1).toString(), "5", adminDBRepository, subUserDBRepository)
        } )
        Spacer(modifier = Modifier.height(8.dp))
        QuestionsRow(text = "Do you take any medications?", options = optionList , selectedOption = q5, onOptionSelected = {
            q5 = it
            if(q54 == "5") q54 = ""
            if(q5 == 1) q51 = 6
            isShown51 = q5 == 0
            if(q5 == 1) isShown54 = false
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(4, (q5+1).toString(), "5", adminDBRepository, subUserDBRepository)
        } )
        if(isShown51) QuestionsRow(text = "", options = q5List, selectedOption = q51, onOptionSelected = {
            q51 = it
            isShown54 = q51 == 3
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(4, (q5+1).toString(), (q51+1).toString(), adminDBRepository, subUserDBRepository)
        }, 15.dp)
        if(isShown54) {
            Spacer(modifier = Modifier.height(5.dp))
            TextField(value = q54, onValueChange = {
                q54 = it
                subUserDBRepository.updateChange(true)
                updateValueOfAnswer(4, (q5+1).toString(), "${q51+1}:$q54", adminDBRepository, subUserDBRepository)
            }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        Spacer(modifier = Modifier.height(8.dp))
        QuestionsRow(text = "Do you have any allergies?", options = optionList , selectedOption = q6, onOptionSelected = {
            q6 = it
            isShown61 = q6 == 0
            if(q61 == "5") q61 = ""
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(5, (q6+1).toString(), "5", adminDBRepository, subUserDBRepository)
        } )
        if(isShown61) {
            Spacer(modifier = Modifier.height(5.dp))
            TextField(value = q61, onValueChange = {
                q61 = it
                subUserDBRepository.updateChange(true)
                updateValueOfAnswer(5, (q6+1).toString(), q61, adminDBRepository, subUserDBRepository)
            }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        }
        Spacer(modifier = Modifier.height(8.dp))
        QuestionsRow(text = "Do you have any medical history?", options = optionList , selectedOption = q7, onOptionSelected = {
            q7 = it
            isShown71 = q7 == 0
            if(q71 == "5") q71 = ""
            subUserDBRepository.updateChange(true)
            updateValueOfAnswer(6, (q7+1).toString(), "5", adminDBRepository, subUserDBRepository)
        } )
        if(isShown71) {
            Spacer(modifier = Modifier.height(5.dp))
            TextField(value = q71, onValueChange = {
                q71 = it
                subUserDBRepository.updateChange(true)
            }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            updateValueOfAnswer(6, (q7+1).toString(), q71, adminDBRepository, subUserDBRepository)
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

fun updateValueOfAnswer(index: Int, answer : String, subAnswers: String, adminDBRepository: AdminDBRepository, subUserDBRepository: SubUserDBRepository){

    when(index){

        0 -> {
            adminDBRepository.answer1.value = answer
            adminDBRepository.subAnswer1.value = subAnswers
        }
        1 -> {
            adminDBRepository.answer2.value = answer
            adminDBRepository.subAnswer2.value = subAnswers
        }
        2 -> {
            adminDBRepository.answer3.value = answer
            adminDBRepository.subAnswer3.value = subAnswers
        }
        3 -> {
            adminDBRepository.answer4.value = answer
            adminDBRepository.subAnswer4.value = subAnswers
        }
        4 -> {
            adminDBRepository.answer5.value = answer
            adminDBRepository.subAnswer5.value = subAnswers
        }
        5 -> {
            adminDBRepository.answer6.value = answer
            adminDBRepository.subAnswer6.value = subAnswers
        }
        6 -> {
            adminDBRepository.answer7.value = answer
            adminDBRepository.subAnswer7.value = subAnswers
        }

    }

    if(isEditUser) updateMedicalHistory(adminDBRepository.getMedicalHistory()) else {
        newUserProfile.medical_history = adminDBRepository.getMedicalHistory()
        subUserDBRepository.parseUserMedicalHistory(newUserProfile)
    }


}

fun convertCmToFeetAndInch(cm: Double): Pair<Int, Double> {
    // Step 1: Get input value in centimeters

    // Step 2: Convert to inches
    val inches = cm / 2.54

    // Step 3: Extract whole number of inches and remaining fractional inches
    val wholeInches = inches.toInt()
    val remainingInches = inches - wholeInches

    // Step 4: Convert to feet
    val feet = wholeInches / 12

    // Step 5: Get remaining inches after converting to feet
    val inchesAfterFeet = wholeInches % 12

    val roundedInches = String.format("%.2f", inchesAfterFeet + remainingInches).toDouble()

    return Pair(feet, roundedInches)
}

fun convertFeetAndInchToCm(feet: Int, inches: Double): Double {
    // Step 1: Convert feet to inches
    val totalInches = feet * 12

    // Step 2: Add inches
    val totalInchesWithRemaining = totalInches + inches

    // Step 3: Convert inches to centimeters
    val cm = totalInchesWithRemaining * 2.54

    return String.format("%.2f", cm).toDouble()
}





