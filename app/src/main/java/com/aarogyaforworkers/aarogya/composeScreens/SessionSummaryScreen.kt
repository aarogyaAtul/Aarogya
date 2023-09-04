package com.aarogyaforworkers.aarogya.composeScreens

import Commons.SessionSummaryPageTags
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color.*
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.aarogyaforworkers.aarogya.Commons.*
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.Whatsapp.WhatsAppManager
import com.aarogyaforworkers.awsapi.models.Session
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.Locale

var isSharingStarted = false

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun SessionSummaryScreen(navHostController: NavHostController){

    val session = selectedSession

    val context = LocalContext.current

    var isSharing by remember { mutableStateOf(false) }

    val captureController = rememberCaptureController()

    val avgSession = MainActivity.subUserRepo.lastAvgSession

    val user = MainActivity.adminDBRepo.getSelectedSubUserProfile()

    var showConfirmOTPAlert by remember { mutableStateOf(false) }

    var showAddPhoneAlert by remember { mutableStateOf(false) }

    if(showAddPhoneAlert) ShowAddPhoneNoAlert(user.phone, showOtpAlert = {
        showAddPhoneAlert = false
        showConfirmOTPAlert = true
        MainActivity.subUserRepo.selectedPhoneNoForVerification.value = it
        MainActivity.adminDBRepo.sendSubUserVerificationCode(it)
    }) {
        showAddPhoneAlert = false
    }

    if(showConfirmOTPAlert) ShowConfirmOtpAlert(userphone = MainActivity.subUserRepo.selectedPhoneNoForVerification.value, onConfrimOtp = {
        if(it){
            showConfirmOTPAlert = false
            user.isUserVerified = "true"
            user.phone = MainActivity.subUserRepo.selectedPhoneNoForVerification.value
            MainActivity.adminDBRepo.adminUpdateSubUser(user)
            isSharing = true
            captureController.capture()
        }
    } ) {
        val handler = Handler(Looper.getMainLooper())
        handler.post(Runnable {
            user.isUserVerified = "false"
            showConfirmOTPAlert = false
        })
    }
    when{
        MainActivity.s3Repo.sessionSummaryUploaded.value == true -> {
            when(isFromUserHomePage){
                true -> {
                    navHostController.navigate(Destination.Home.routes)
                }
                false -> {
                    navHostController.navigate(Destination.SessionHistory.routes)
                }
            }
            Toast.makeText(context, "Session summary shared successfully", Toast.LENGTH_LONG).show()
            MainActivity.s3Repo.updateSessionSummaryUploadStatus(null, "")
            isSharing = false
        }
        MainActivity.s3Repo.sessionSummaryUploaded.value == false -> {
            Toast.makeText(context, "Failed to share try again", Toast.LENGTH_LONG).show()
            MainActivity.s3Repo.updateSessionSummaryUploadStatus(null, "")
            isSharing = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        Column(modifier = Modifier
            .fillMaxSize()
            .testTag(SessionSummaryPageTags.shared.summaryScreen)
            .alpha(if (isSharing) 0.07f else 1.0f)) {

            TopBarWithCancelBtn {
                when(isFromUserHomePage){
                    true -> {
                        navHostController.navigate(Destination.Home.routes)
                    }
                    false -> {
                        navHostController.navigate(Destination.SessionHistory.routes)
                    }
                }
            }
            Capturable(
                controller = captureController,
                onCaptured = { bitmap, error ->
                    // This is captured bitmap of a content inside Capturable Composable.
                    if (bitmap != null) {
                        // Bitmap is captured successfully. Do something with it!
                        val image = bitmapToByteArray(bitmap.asAndroidBitmap())
                        isSharingStarted = true
                        isSharing = true
//                        202754:3519:d9fc1b:919340413756
                        var reqId = ""
                        Log.d("TAG", "SessionSummaryScreen: sessionId ${session.sessionId}")
                        val ses = session.sessionId.split(":").toMutableList()
//                        when(ses.size){
//                            4 -> {
//                                reqId  = ses[0]+":"+ses[1]+":"+ses[2]+":"+ses[3]
//                                MainActivity.s3Repo.startUploadingSessionSummary(image, reqId)
//                            }
//
//                            5 -> {
//                                reqId  = ses[0]+":"+ses[1]+":"+ses[2]+":"+ses[3]+":"+ses[4]
//                                Toast.makeText(context, "session can not be shared", Toast.LENGTH_LONG).show()
//                            }
//                            else -> {
//                                reqId = session.sessionId+":"+MainActivity.adminDBRepo.getSelectedSubUserProfile().phone
//                                MainActivity.s3Repo.startUploadingSessionSummary(image, reqId)
//                            }
//                        }

                        reqId = session.sessionId+":"+MainActivity.adminDBRepo.getSelectedSubUserProfile().phone
                        MainActivity.s3Repo.startUploadingSessionSummary(image, reqId)

//                        if(ses.size == 4 || ses.size == 5){
//                            ses[3] = MainActivity.adminDBRepo.getSelectedSubUserProfile().phone
//                            reqId  = ses[0]+":"+ses[1]+":"+ses[2]+":"+ses[3]
//                        }else{
//                            reqId = session.sessionId+":"+MainActivity.adminDBRepo.getSelectedSubUserProfile().phone
//                        }

                    }

                    if (error != null) {
                        // Error occurred. Handle it!
                        Toast.makeText(context, "Failed to share try again", Toast.LENGTH_LONG).show()
                        isSharing = false
                    }
                }
            ) {
                // Composable content to be captured.
                // Here, `MovieTicketContent()` will be get captured
                SessionCard(session = session, avgSession = avgSession)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(5.dp))
                ActionButton(action = {
                    when(isFromUserHomePage){
                        true -> {
                            navHostController.navigate(Destination.Home.routes)
                        }
                        false -> {
                            navHostController.navigate(Destination.SessionHistory.routes)
                        }
                    }
                     }, buttonName = "Cancel")
                ActionButton(action = {
                    if(user.isUserVerified == "false" || user.isUserVerified == "False"){
                        showAddPhoneAlert = true
                    }else{
                        isSharing = true
                        isSessionShared = false
                        captureController.capture()
                    } }, buttonName = "Share")
                Spacer(modifier = Modifier.width(5.dp))
            }
        }
        if(isSharing) showProgress()
    }
}

@Composable
fun sendMessage(sendingMessage : Boolean, url : String, onSuccess : () -> Unit, onFailed : () -> Unit){
    // send the message in a separate coroutine to avoid blocking the UI
    LaunchedEffect(sendingMessage) {
        if (sendingMessage) {
            try {
                val accountSid = "ACff27e924d692ca08ac83991f75fc9445"
                val authToken = "11568edad8a8ce37dde0b90de48f0608"
                val recipientNumber = "+919340413756"
                val senderNumber = "+13203773220"
                val message = ""
                val credentials = Credentials.basic(accountSid, authToken)
                // replace with the URL of the image file you want to send
                val client = OkHttpClient()
                val requestBody = FormBody.Builder()
                    .add("To", "whatsapp:$recipientNumber")
                    .add("From", "whatsapp:$senderNumber")
                    .add("Body", message)
                    .add("MediaUrl", url)
                    .build()

                val request = Request.Builder()
                    .url("https://api.twilio.com/2010-04-01/Accounts/$accountSid/Messages.json")
                    .addHeader("Authorization", credentials)
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        // Handle failure to make the request
                        onFailed()
                    }
                    override fun onResponse(call: Call, response: Response) {
                        // Handle the response from the API
                        Log.d("TAG", "onResponse: ")
                        if(response.isSuccessful){
                            onSuccess()
                        }else{
                            onFailed()
                        }
                    }
                })
            } catch (e: Exception) {
                // handle any errors
                e.printStackTrace()
                onFailed()
            }
        }
    }
}

@ExperimentalTvMaterial3Api
@Composable
fun SessionCard(session: Session, avgSession: Session){
    val tempInC = session.temp.substringBefore("°C").toDoubleOrNull()

    val sysValue = session.sys?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
    val diaValue = session.dia?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
    val hrValue = session.heartRate?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
    val spo2Value = session.spO2?.replace(Regex("[^\\d.]"), "")?.toIntOrNull()
    val bmiValue = session.weight?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()
    val bodyFatValue = session.bodyFat?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()
    val tempValue = session.temp?.replace(Regex("[^\\d.]"), "")?.toDoubleOrNull()

    var monthArray : List<String> = listOf<String>("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")


    val sys = sysValue?.toString() ?: ""
    val dia = diaValue?.toString() ?: ""
    val hr = hrValue?.toString() ?: ""
    val spo2 = spo2Value?.toString() ?: ""
    val bmi = bmiValue?.toString() ?: ""
    val bodyFat = bodyFatValue?.toString() ?: ""
    val temp = tempValue?.toString() ?: ""

    val selectedUser = MainActivity.adminDBRepo.getSelectedSubUserProfile()

    Box(modifier = Modifier
        .background(Color.White),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .padding(start = 5.dp, end = 5.dp, top = 20.dp, bottom = 10.dp)
                .fillMaxWidth()
                .background(Color.White),
        ){
            ReportAppLogo()
            Spacer(modifier = Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                BoldTextView(title = "Aarogya Health Card", fontSize = 20)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row{
                BoldTextView(title = "Ref:")
                Spacer(modifier = Modifier.width(2.dp))
                RegularTextView(title = session.userId.take(6).toUpperCase(Locale.ROOT)+"/"+session.deviceId.replace(":","").takeLast(4)+"/"+session.adminId.takeLast(8).toUpperCase(Locale.ROOT))
            }
            Spacer(modifier = Modifier.height(5.dp))

            Row() {
                Column(Modifier.weight(1f)) {
                    Row() {
                        BoldTextView(title = "Date:")
                        Spacer(modifier = Modifier.width(2.dp))
                        if(session.date.isNotEmpty()){
                            val date = convertCustomDateFormat(session.date)
                            val time = convertTimeToAMPMFormat(session.time)
                            RegularTextView(title = "$date; $time")
                        }else{
                            RegularTextView(title = "")
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row() {
                        BoldTextView(title = "Name:")
                        Spacer(modifier = Modifier.width(2.dp))
                        RegularTextView(title = formatTitle(selectedUser.frist_name, selectedUser.last_name))
                    }
                }

                Column() {
                    Row() {

                        BoldTextView(title = "Place:")
                        Spacer(modifier = Modifier.width(2.dp))
                        val location = session.location.split(",")
                        if(location.isNotEmpty()){
                            RegularTextView(title = location[0])
                        }else{
                            RegularTextView(title = "")
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Row() {
                        BoldTextView(title = "Age:")
                        Spacer(modifier = Modifier.width(2.dp))
                        RegularTextView(title = getAge(selectedUser))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val bpAvg = if(avgSession.sys.isEmpty() || avgSession.dia.isEmpty())  "" else "${avgSession.sys}/${avgSession.dia}"

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                HeaderRow(title1 = "Vital", title2 = "Measured", title3 = "Trend*", title4 = "Reference")
                DataRow(title = "BP",
                    unit = "mmHg",
                    value = "$sys/$dia",
                    avg = bpAvg,
                    range = "120/80",
                    rowColor = Color.White)

                DataRow(title = "HR",
                    unit = "bpm",
                    value = hr,
                    avg = avgSession.heartRate,
                    range = "60.0 - 100.0",
                    validRange = 60.0..100.0,
                    rowColor = Color(0xfffae9db))

                DataRow(title = "SpO2",
                    unit = "%",
                    value = spo2,
                    avg = avgSession.spO2,
                    range = "95.0 - 100.0",
                    validRange = 95.0..100.0,
                    rowColor = Color.White)
                var avgTempss = ""
                if(avgSession.temp.isNotEmpty()){
                    avgTempss = MainActivity.adminDBRepo.getTempBasedOnUnit(avgSession.temp.toDouble())
                }
                DataRow(title = "Temp",
                    unit = MainActivity.adminDBRepo.getTempUnit(),
                    value = MainActivity.adminDBRepo.getTempBasedOnUnit(tempInC),
                    avg = avgTempss,
                    range = if(MainActivity.adminDBRepo.tempUnit.value == 0) "97.0 - 99.0" else "36.1 - 37.2", //implement as unit changes
                    validRange = if(MainActivity.adminDBRepo.tempUnit.value == 0) 97.0..99.0 else 36.1..37.2,
                    rowColor = Color(0xfffae9db))

                DataRow(title = "Weight",
                    unit = MainActivity.adminDBRepo.getWeightUnit(),
                    value = calculateWeightByBmiHeight(session.weight, selectedUser.height),
                    avg = calculateWeightByBmiHeight(avgSession.weight, selectedUser.height),
                    range = calculateMinRangeBYBmiHeight(avgSession.weight, selectedUser.height) +" - "+ calculateMaxRangeBYBmiHeight(avgSession.weight, selectedUser.height),
                    validRange = calculateMinRangeBYBmiHeight(avgSession.weight, selectedUser.height).toDouble()..calculateMaxRangeBYBmiHeight(avgSession.weight, selectedUser.height).toDouble(),
                    rowColor = Color.White)

                DataRow(title = "Body Fat",
                    unit = "%",
                    value = bodyFat,
                    avg = avgSession.bodyFat,
                    range = getRange(selectedUser.gender),
                    validRange = getValidRange(selectedUser.gender),
                    rowColor = Color(0xfffae9db) )
//                DataRow(title = "BMI",
//                    unit = "",
//                    value = bmi,
//                    avg = avgSession.weight,
//                    range = "18.5 - 24.9",
//                    validRange = 18.5..24.9,
//                    rowColor = Color.White)
                DataRow(title = "GLU",
                    unit = "mmol/L ",
                    value = bmi,
                    avg = avgSession.weight,
                    range = "3.9 - 5.5",
                    validRange = 3.9..5.5,
                    rowColor = Color.White)
            }
            Divider(modifier = Modifier
                .fillMaxWidth()
                .height(1.dp),color = Color.Black)

            Spacer(modifier = Modifier.height(5.dp))

            Row() {
                RegularTextView(title = "*Average of last three measurements", fontSize = 12)
            }
            Spacer(modifier = Modifier.height(15.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ItalicTextView(title = "Eat Right, Sleep Well & Exercise - 3 Mantras To Be Happy!", fontSize = 12)
            }
            Spacer(modifier = Modifier.height(15.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//#Changes
                RegularTextView(title = "hello@aarogyatech.com", fontSize = 12, textColor = Color.Blue, textDecoration = TextDecoration.Underline)//use underline
                RegularTextView(title = "https://www.aarogyatech.com", fontSize = 12, textColor = Color.Blue, TextDecoration.Underline)//use underline
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}



fun calculateMinRangeBYBmiHeight(bmi: String, height: String): String {
    return try {
        val bmiDouble = 18.5
        val heightDouble = height.toDouble() / 100.0 // convert height from cm to m
        val weight = bmiDouble * heightDouble * heightDouble
        val basedOnUnit = MainActivity.adminDBRepo.getWeightBasedOnUnits(weight).toFloat()
        "%.1f".format(basedOnUnit)
    } catch (e: NumberFormatException) {
        println("Invalid input: $bmi or $height cannot be converted to Double")
        ""
    }
}

fun calculateMaxRangeBYBmiHeight(bmi: String, height: String): String {
    return try {
        val bmiDouble = 24.9
        val heightDouble = height.toDouble() / 100.0 // convert height from cm to m
        val weight = bmiDouble * heightDouble * heightDouble
        val basedOnUnit = MainActivity.adminDBRepo.getWeightBasedOnUnits(weight).toFloat()
        "%.1f".format(basedOnUnit)
    } catch (e: NumberFormatException) {
        println("Invalid input: $bmi or $height cannot be converted to Double")
        ""
    }
}

fun calculateWeightByBmiHeight(bmi: String, height: String): String {
    return try {
        val bmiDouble = bmi.toDouble()
        val heightDouble = height.toDouble() / 100.0 // convert height from cm to m
        val weight = bmiDouble * heightDouble * heightDouble
        val basedOnUnit = MainActivity.adminDBRepo.getWeightBasedOnUnits(weight).toFloat()
        "%.1f".format(basedOnUnit)
    } catch (e: NumberFormatException) {
        println("Invalid input: $bmi or $height cannot be converted to Double")
        ""
    }
}

@Composable
fun ActionButton(action:() -> Unit, buttonName:String){
    Button(onClick = { action() }, colors = ButtonDefaults.buttonColors(Color(0xFF030C44))) {
        Text(text = buttonName)
    }
}


