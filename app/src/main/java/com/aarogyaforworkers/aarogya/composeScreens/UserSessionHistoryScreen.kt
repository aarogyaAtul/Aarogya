package com.aarogyaforworkers.aarogya.composeScreens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogya.Commons.*
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.R
import com.aarogyaforworkers.aarogya.SubUser.SubUserDBRepository
import com.aarogyaforworkers.aarogya.SubUser.isItFromHistoryPage
import com.aarogyaforworkers.aarogya.ui.theme.defCardDark
import com.aarogyaforworkers.awsapi.models.Session
import kotlinx.coroutines.delay
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

@Composable
fun UserSessionHistoryScreen(navHostController: NavHostController, subUserDBRepository: SubUserDBRepository, adminDBRepository: AdminDBRepository) {

    val context = LocalContext.current

    isItFromHistoryPage = true

    CheckInternet(context = LocalContext.current)

    var lastSession = MainActivity.subUserRepo.getSession()

    var isUpdating by remember { mutableStateOf(false) }

    var isDeleting by remember { mutableStateOf(false) }

    var isDownloading by remember { mutableStateOf(false) }

    SideEffect {
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    if(isDeletingSession && adminDBRepository.guestSessionDeleted.value){
        LaunchedEffect(isDeletingSession) {
            if (isDeletingSession) {
                delay(500)
                isDeleting = false
                isDeletingSession = false
                adminDBRepository.updateIsGuestSessionDeleted(false)
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_LONG).show()
            }
        }
    }

    val admin = adminDBRepository.getLoggedInUser()

    val subUser = adminDBRepository.getSelectedSubUserProfile()

    val adminId = admin.first_name.take(4).toUpperCase().padEnd(4, '0') + admin.phone.takeLast(4).padEnd(4, '0')

    val subUserId = subUser.user_id

    if(isGuest) subUserDBRepository.getSessionsByUserID(adminId) else subUserDBRepository.getSessionsByUserID(subUserId)

    var progress by remember { mutableStateOf(true) }

    val sessionsList = subUserDBRepository.sessions.value

    if(subUserDBRepository.sessions.value.isNotEmpty()){ progress = false }

    if(isUpdating && subUserDBRepository.sessionUpdated.value || isUpdating && !subUserDBRepository.sessionUpdated.value){
        isUpdating = false
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { if(isGuest){
                navHostController.navigate(Destination.Home.routes)
            }else{
                navHostController.navigate(Destination.UserHome.routes)
            } })
            { Icon(imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon),
                contentDescription = "Back Btn",
                Modifier.size(25.dp))}
            if(sessionsList.isNotEmpty() && isGuest){
                TextButton(onClick = {
                    isDeleting = true
                    isDeletingSession = true
                    adminDBRepository.deleteAllSessionForGuest(adminId) }) {
                    Text(text = "Delete All",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold)
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            item {
                // Table header
                Row()
                {
                    TableHeaderCell("Date")
                    TableHeaderCell("Time")
                    TableHeaderCell("Device ID")
                    TableHeaderCell("User ID")
                    if(!isGuest) TableHeaderCell("Admin ID")
                    TableHeaderCell("SYS")
                    TableHeaderCell("DIA")
                    TableHeaderCell("Heart rate")
                    TableHeaderCell("SpO2")
//                    TableHeaderCell("BMI")
                    TableHeaderCell(text = "Glucose")
                    TableHeaderCell("Body fat")
                    TableHeaderCell("Temperature")
                    TableHeaderCell("ECG")
                    TableHeaderCell("QNA")
                    TableHeaderCell("Remarks")
                    TableHeaderCell("Location")
                    when(isGuest){
                        true -> TableHeaderCell("Delete")
                        false -> TableHeaderCell("Share")
                    }
                }
            }
            if(MainActivity.subUserRepo.bufferThere.value){
                sessionsList.add(MainActivity.subUserRepo.getSession())
            }
            if(sessionsList.size >= 1 && sessionsList.last().userId.isNotEmpty()){
                itemsIndexed(sessionsList.toList()) { index, session ->
                    Row(modifier = Modifier.background(
                        when{
                            index + 1 == sessionsList.size -> if(MainActivity.subUserRepo.bufferThere.value) defCardDark else Color.White
                            index % 2 == 0 -> Color(0xFFF1EFEF)
                            else -> {
                                Color.White
                            }
                        }))
                    {
                        TableCell(session.date)
                        if(session.time.length > 5) TableCell(session.time.dropLast(3)) else TableCell(text = session.time)
                        TableCell(session.deviceId.replace(":", ""))
                        if(session.userId.contains("/")){
                            val id = session.userId.split("/")
                            val userId = id[0]+id[1]
                            TableCell(userId)
                        }else{
                            TableCell(session.userId)
                        }
                        if(!isGuest)TableCell(session.adminId)
                        when{
                            session.sys.isNotEmpty() -> TableCell(session.sys + " mmHg")
                            session.sys.isEmpty() -> TableCell(session.sys)
                        }
                        when{
                            session.dia.isNotEmpty() -> TableCell(session.dia + " mmHg")
                            session.dia.isEmpty() -> TableCell(session.dia)
                        }
                        TableCell(session.heartRate)
                        TableCell(session.spO2)
                        TableCell(session.weight)
                        when(session.bodyFat.isEmpty()){
                            true -> TableCell(session.bodyFat)
                            false -> TableCell("${session.bodyFat} %")
                        }
                        val tempInC = session.temp.substringBefore("°C").toDoubleOrNull()
                        TableCell(adminDBRepository.getTempBasedOnUnitSet(tempInC))
                        TableEcgCell(session.ecgFileLink, navHostController){
                            isDownloading = true
                            isAllreadyDownloading = false
                        }
                        if (isDownloading && !isAllreadyDownloading) {
                            downLoadData(url = csvUrl){
                                isDownloading = false
                                Handler(Looper.getMainLooper()).post {
                                    isClosing = false
                                    navHostController.navigate(Destination.Graphs.routes)
                                }
                            }
                            isAllreadyDownloading = true
                        }
                        TableCell(session.questionerAnswers)
                        Box(modifier = Modifier
                            .border(BorderStroke(1.dp, Color(0XFF6a6a6a)))
                            .padding(5.dp)
                            .size(width = 150.dp, height = 30.dp),
                            contentAlignment = Alignment.Center){
                            when(session.remarks){

                                "t" , "T" -> {
                                    IconButton(onClick = {
                                        isUpdating = true
                                        session.remarks = "f"
                                        session.sessionId = session.sessionId
                                        session.userId = MainActivity.adminDBRepo.getSelectedSubUserProfile().user_id
                                        subUserDBRepository.updateSession(session)
                                    }, modifier = Modifier.fillMaxSize()) {
                                        Text(text = "OK")
                                    }
                                }

                                "f", "F" -> {
                                    IconButton(onClick = {
                                        session.remarks = "t"
                                        isUpdating = true
                                        session.userId = subUserDBRepository.selectedUserId
                                        subUserDBRepository.updateSession(session)
                                    },modifier = Modifier.fillMaxSize()) {
                                        Text(text = "NOT OK")
                                    }
                                }

                                else -> {
                                    IconButton(onClick = {
                                        session.remarks = "f"
                                        isUpdating = true
                                        session.userId = subUserDBRepository.selectedUserId
                                        subUserDBRepository.updateSession(session)
                                    },modifier = Modifier.fillMaxSize()) {
                                        Text(text = "Ok")
                                    }
                                }
                            }
                        }
                        var location : List<String> = emptyList()
                        if(session.location.isNotEmpty()){
                            location = session.location.split(",")
                        }
                        if(location.isNotEmpty()){
                            TableCell(location[0])
                        }else{
                            TableCell(text = "")
                        }
                        when(isGuest){
                            true -> {
                                Box(modifier = Modifier
                                    .border(BorderStroke(1.dp, Color(0XFF6a6a6a)))
                                    .padding(5.dp)
                                    .size(width = 150.dp, height = 30.dp),
                                    contentAlignment = Alignment.Center){
                                    IconButton(onClick = {
                                        isDeleting = true
                                        isDeletingSession = true
                                        subUserDBRepository.sessions.value.removeAt(index)
                                        MainActivity.adminDBRepo.deleteSessionBySessionId(session.sessionId)
                                    }) {
                                        if(session.sessionId != ""){
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = R.drawable.delete_icon),
                                                contentDescription = "Delete"
                                            )
                                        }
                                    }
                                }
                            }
                            false -> {
                                TableShareCell(adminDBRepository, session, navHostController, index)
                            }
                        }
                    }
                }
            }
        }
    }

    if(isUpdating && !isGuest || isDeleting && isGuest || isDownloading) showProgress()

}

@Composable
fun TableHeaderCell(text: String) {
    Box(modifier = Modifier
        .background(Color.Gray)
        .border(BorderStroke(1.dp, Color(0XFF6a6a6a)))
        .padding(5.dp)
        .size(width = 150.dp, height = 30.dp),
        contentAlignment = Alignment.Center) {
        Text(
            text = text, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TableCell(text: String) {
    Box(modifier = Modifier
        .border(BorderStroke(1.dp, Color(0XFF6a6a6a)))
        .padding(5.dp)
        .size(width = 150.dp, height = 30.dp),
        contentAlignment = Alignment.Center) {
        Text(
            text = text,
        )
    }
}


@Composable
fun downLoadData(url : String, onCompletion : (Boolean) -> Unit){
    LaunchedEffect(url){
        downloadCsvDataFromUrl(url){ data ->
            if(data.isNotEmpty()) {
                selectedEcg = data as ArrayList<Float>
                onCompletion(true)
            }
        }
    }
}


@Composable
fun TableEcgCell(ecgVal : String, navHostController: NavHostController, onClick : (Boolean) -> Unit) {
    Log.e("TAG", "TableEcgCell: ecg link $ecgVal")
    Box(modifier = Modifier
        .border(BorderStroke(1.dp, Color(0XFF6a6a6a)))
        .padding(5.dp)
        .size(width = 150.dp, height = 30.dp),
        contentAlignment = Alignment.Center) {
        IconButton(onClick = {
            csvUrl = ecgVal
            if(ecgVal != "") if( ecgVal != "Not-Performed") {
                val result = ecgVal.split("_")
                if(result.size == 6){
                    selectedECGResult = result.last().toInt()
                }
            }
            onClick(false)
        }) {
            if(ecgVal != "") if( ecgVal != "Not-Performed") {
                val result = ecgVal.split("_")
                if(result.size == 6){
                    Text(text = result.last(), color = Color.Blue)
                }else{
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.outline_remove_red_eye_24),
                        contentDescription = "ECG"
                    )
                }
            }
        }
    }
}

@Composable
fun TableShareCell(adminDBRepository: AdminDBRepository, session: Session, navHostController: NavHostController, index : Int) {
    var showDialog by remember { mutableStateOf(false) }
    var showFailedDialog by remember { mutableStateOf(false) }
    var sendingMessage by remember { mutableStateOf(false) }
    var messageSent by remember { mutableStateOf(false) }
    val user = adminDBRepository.getSelectedSubUserProfile()

    Box(modifier = Modifier
        .border(BorderStroke(1.dp, Color(0XFF6a6a6a)))
        .padding(5.dp)
        .size(width = 150.dp, height = 30.dp),
        contentAlignment = Alignment.Center){
        IconButton(
            onClick = {
                MainActivity.subUserRepo.calculateAvgSession(MainActivity.subUserRepo.sessions.value, index)
                selectedSession = session
                isItFromHistoryPage = true
                navHostController.navigate(Destination.SessionSummary.routes)
            }
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.outline_share_24),
                contentDescription = "Share"
            )
        }
    }

    if (showDialog) {
        if (!sendingMessage && !messageSent && !showFailedDialog) {
            // show the dialog to confirm message sending
            AlertDialog(
                onDismissRequest = {  },
                title = { Text("Share with WhatsApp?") },
                confirmButton = {
                    Button(
                        onClick = {
                            sendingMessage = true
                            showFailedDialog = false
                            messageSent = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }else if (!sendingMessage && messageSent) {
            // show a message to confirm that the message has been sent
            AlertDialog(
                onDismissRequest = {
                    showDialog = false },
                title = { Text("Message Sent!") },
                text = { Text("The message has been sent to ${user.frist_name} on WhatsApp.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false }
                    ) {
                        Text("OK")
                    }
                },
            )
        } else if (!sendingMessage && !messageSent && showFailedDialog){
            AlertDialog(
                onDismissRequest = {
                    showDialog = false },
                title = { Text("Message Failed!") },
                text = { Text("Failed to send message to ${user.frist_name} on WhatsApp.") },
                confirmButton = {
                    Button(
                        onClick = {
                            sendingMessage = true
                            showFailedDialog = false
                            messageSent = false
                        }
                    ) {
                        Text("Retry")
                    }
                },dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        // send the message in a separate coroutine to avoid blocking the UI
        LaunchedEffect(sendingMessage) {
            if (sendingMessage) {
                try {
                    val accountSid = "ACff27e924d692ca08ac83991f75fc9445"
                    val authToken = "11568edad8a8ce37dde0b90de48f0608"
                    val recipientNumber = "+919340413756"
                    val senderNumber = "+13203773220"
//                    val senderNumber = "+14155238886"
                    val message = "Hello, ${user.frist_name}\n\nYour session details are as follows:\n\nBlood Pressure: ${session.dia+"/"+session.sys} \nHeart Rate: ${session.heartRate} \nHeight: ${user.height} \nTemperature: ${session.temp} \n" +
                            "SpO2: ${session.spO2}\n\nLet me know if you need any further assistance."

                    val mediaUrl = "https://aarogyaforworkers5c90f62fdef040a798f1911e2c5d81213923-dev.s3.ap-south-1.amazonaws.com/public/sub_users_Profile_Pictures/07db655d-3bb8-4f3f-85b4-79f49cbdd67b.jpg"
                    // replace with the URL of the image file you want to send

                    val client = OkHttpClient()

                    val requestBody = FormBody.Builder()
                        .add("To", "whatsapp:$recipientNumber")
                        .add("From", "whatsapp:$senderNumber")
                        .add("Body", message)
                        .add("MediaUrl", mediaUrl)
                        .build()

                    val request = Request.Builder()
                        .url("https://api.twilio.com/2010-04-01/Accounts/$accountSid/Messages.json")
                        .addHeader("Authorization", "Basic QUNmZjI3ZTkyNGQ2OTJjYTA4YWM4Mzk5MWY3NWZjOTQ0NToxMTU2OGVkYWQ4YThjZTM3ZGRlMGI5MGRlNDhmMDYwOA")
                        .post(requestBody)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: okhttp3.Call, e: IOException) {
                            // Handle failure to make the request
                            Log.d("TAG", "onFailure: ")
                            messageSent = false
                            sendingMessage = false
                            showFailedDialog = true
                        }
                        override fun onResponse(call: okhttp3.Call, response: Response) {
                            // Handle the response from the API
                            Log.d("TAG", "onResponse: ")
                            messageSent = response.isSuccessful
                            sendingMessage = false
                            showFailedDialog = false
                        }
                    })
                } catch (e: Exception) {
                    // handle any errors
                    e.printStackTrace()
                    messageSent = false
                    sendingMessage = false
                    showFailedDialog = true
                }
            }
        }
    }
}

