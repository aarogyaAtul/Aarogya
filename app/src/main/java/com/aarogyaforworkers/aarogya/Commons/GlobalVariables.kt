package com.aarogyaforworkers.aarogya.Commons

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import com.aarogyaforworkers.awsapi.models.Session
import com.aarogyaforworkers.awsapi.models.SubUserProfile
import java.io.ByteArrayOutputStream


var lastCreateUserValue = false
var lastUserRegisteredState = true
var lastUserNotRegisteredState = false
var isEditUser = false
var userProfileToEdit : SubUserProfile? = null
var editingUserProfile : SubUserProfile? = null
var isSetUpDone = false
var isUpdatingProfile = false
var isCurrentUserVerifiedPhone = ""
var newUserProfile : SubUserProfile = SubUserProfile("","","","","","","","","","","","", "")
var isCameraCliked = false
var isCheckingUserBeforeSendingOTP = false
var isUserAllreadyRegistered = false
var allReadyRegisteredPhone = ""
var isSavingOrUpdating = false
var isAllreadyOtpSent = false

fun updateMedicalHistory(medicalHistory: String){
    if(userProfileToEdit != null){
        userProfileToEdit!!.medical_history = medicalHistory
    }
}

fun updatePhoneVerifiedStatus(){
    if(userProfileToEdit != null){
        userProfileToEdit!!.isUserVerified = "true"
    }
}

fun updatePhone(phone : String){
    if(userProfileToEdit != null) userProfileToEdit!!.phone = phone
}

fun updateFirstLastName(firstName : String, lastName : String){
    if(userProfileToEdit != null) {
        userProfileToEdit!!.frist_name = firstName
        userProfileToEdit!!.last_name = lastName
    }
}

fun updateGender(gender : String){
    if(userProfileToEdit != null) userProfileToEdit!!.gender = gender
}

fun updateHeight(height : String){
    if(userProfileToEdit != null) userProfileToEdit!!.height = height
}

fun updateDob(dob : String){
    if(userProfileToEdit != null) userProfileToEdit!!.dob = dob
}



// Admin Profile

var lastupdateStatus = false
var isAdminProfileUpdated = false
var timestamp = ""
var genderOption = listOf("Male", "Female","Other")
var isShowAlert = false


//Subuserhome -
var isRegisring = false
var isSyncing = false
var isReadyForWeight = false
var lastIndex = 6
var isBPPlaying = false
var lastFailed = false
var isECGPlaying = false
var isSetRequestSent = false
var isSubUserProfileSetUp = false
var isOnUserHomeScreen = true
var isAdminNeededToSync = false
var isSaving = false
var isUplodingCLicked = false
var isSessionSaved = false
var isDeleting = false
var ifIsExitAndSave = false
var sessionAllreadySaved = false
var userHometimeStamp = System.currentTimeMillis().toString()

// SessionSummaryreport -

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val whiteBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(whiteBitmap)
    canvas.drawPaint(Paint().apply { Color.White })
    val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.DITHER_FLAG)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)
    val stream = ByteArrayOutputStream()
    whiteBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

var lastSharingStatus = false

var isFromUserHomePage = true

var selectedSession : Session = Session("", "", "", "","","","","", "", "", "","","","", "", "", "")

// SplashScreen

var lastUpdatedSignInValue = false
var isSplashScreenSetup = false
var isTimerStopped = false

// SessonHistory

var isGuest = false
var isDeletingSession = false
var csvUrl = ""
var selectedEcg = ArrayList<Float>()
var selectedECGResult = 55
var isAllreadyDownloading = false
var isSessionShared = false