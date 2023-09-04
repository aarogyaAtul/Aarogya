package com.aarogyaforworkers.aarogya.composeScreens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.PC300.gethMm
import com.aarogyaforworkers.aarogya.composeScreens.ECGPainter.recvdata.StaticReceive
import com.aarogyaforworkers.aarogya.ui.theme.defDark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignOutAlertView(showAlert : Boolean, onSignOutClick : () -> Unit, onCancelClick : () -> Unit){
    if(showAlert){
        AlertDialog(onDismissRequest = { onCancelClick() },title = {
            TitleViewWithCancelBtn(title = "SIGN OUT") {
           onCancelClick()
        } }, text = { Text(
            text = "Are you sure you want to SignOut?")
        }, modifier = Modifier.fillMaxWidth(), confirmButton = {
            Button(onClick = {
                onSignOutClick()
            }) {
                Text(text = "SignOut")
            }
        }, dismissButton = {
            Button(onClick = { onCancelClick() }) {
                Text(text = "Cancel")
            }
        })
    }
}


@Composable
fun ShowSaveSessionAlertView(showAlert : Boolean, onSaveClicked : () -> Unit, onCancelClick : () -> Unit){
    if(showAlert){
        AlertDialog(onDismissRequest = { onCancelClick() }, title = {
            TitleViewWithCancelBtn(title = "Save") {
                onCancelClick()
            } }, text = { Text(
            text = "Do you want to save user session data?")
        }, modifier = Modifier.fillMaxWidth(), confirmButton = {
            Button(onClick = {
                onSaveClicked()
            }) {
                Text(text = "Save")
            }
        }, dismissButton = {
            Button(onClick = { onCancelClick() }) {
                Text(text = "Cancel")
            }
        })
    }
}


@Composable
fun ShwowCustomAlert(title : String, subTitle: String,  onOkayCLiked : () -> Unit){
    AlertDialog(onDismissRequest = {onOkayCLiked() }, title = {
        TitleViewWithCancelBtn(title = title) {
            onOkayCLiked()
        }
    }, text = {
        RegularTextView(title = subTitle)
              },
        confirmButton = { Button(onClick = {
            onOkayCLiked()},) { BoldTextView(title = "OK", textColor = Color.White) } })
}


@Composable
fun ShowAddPhoneNoAlert(userphone: String, showOtpAlert : (String) -> Unit, onDismiss: () -> Unit){
    var enablePhone = true
    var phone by remember { mutableStateOf("") }
    if(userphone.isNotEmpty()){
        phone = userphone
    }
    var isPhoneValid by remember { mutableStateOf(true) }
    var isPhoneEmpty by remember { mutableStateOf(false) }

    AlertDialog(onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { if(isPhoneValid && phone.isNotEmpty()) { showOtpAlert(phone) } },
                enabled = isPhoneValid && phone.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = defDark.copy(alpha = 0.7f),
                    containerColor = defDark)
            ) {
                BoldTextView(title = "Send OTP", textColor = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                BoldTextView(title = "Cancel", textColor = Color.White)
            }
        },
        text = { Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TitleViewWithCancelBtn(title = "Add Phone No.") {
                onDismiss()
            }
            Spacer(modifier = Modifier.height(10.dp))
            AuthTextField(
                textInput = phone,
                onChangeInput = {
                    phone = it.take(10)
                    isPhoneValid = phone.length == 10 && phone.isNotEmpty()
                    isPhoneEmpty = phone.isEmpty()
                },
                labelText = "Enter Phone No.",
                keyboard = KeyboardType.Phone,
                error = (!isPhoneValid || isPhoneEmpty),
                TestTag = "tagPhone",
                enable = enablePhone
            )
            //#Change16-may
            PhoneErrorView(isPhoneValid = isPhoneValid, isPhoneEmpty = isPhoneEmpty)
        }
        }
    )
}

//#Change16-may
@Composable
fun PhoneErrorView(isPhoneValid: Boolean, isPhoneEmpty: Boolean){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        ItalicTextView(
            title = when {
                (!isPhoneValid && !isPhoneEmpty)-> "Please enter 10 digit phone number."
                (isPhoneEmpty)-> "Enter Phone"
                else -> ""
            }, textColor = Color.Red
        )
    }
}


@ExperimentalMaterial3Api
@Composable
fun ShowConfirmOtpAlert(userphone : String, onConfrimOtp : (Boolean) -> Unit, onDismiss  : () -> Unit){

    var isOTPEmpty by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    var isOTPValid by remember { mutableStateOf(true) }
    var isOTPMatched by remember { mutableStateOf(true) }

    AlertDialog(onDismissRequest = {
        onDismiss()
    },
        confirmButton = {
            Button(onClick = {
                when(isOTPValid && otp.isNotEmpty()){ //change conditions
                    true-> {
                        isOTPMatched = MainActivity.adminDBRepo.checkVerificationCode(otp)
                        if(isOTPMatched) onConfrimOtp(isOTPMatched)
                    }
                    false -> !isOTPValid
                }
            }, enabled = isOTPValid && otp.isNotEmpty(),colors = ButtonDefaults.buttonColors(
                disabledContainerColor = defDark.copy(alpha = 0.7f),
                containerColor = defDark
            )
            ) {
                BoldTextView(title = "Verify", textColor = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                BoldTextView(title = "Cancel", textColor = Color.White)
            }
        },
        text = { Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            TitleViewWithCancelBtn(title = "Verify Phone No.") {
                onDismiss()
            }

            Spacer(modifier = Modifier.height(10.dp))

            AuthTextField(
                textInput = otp,
                onChangeInput = {
                    otp = it.take(6)
                    //#Change16-may
                    isOTPValid = otp.length == 6
                    isOTPEmpty = otp.isEmpty()
                    isOTPMatched = true
                },
                labelText = "Enter OTP",
                keyboard = KeyboardType.NumberPassword,
                error = !isOTPValid || !isOTPMatched ||isOTPEmpty,
                TestTag = "tagOTP"
            )
            OtpErrorView(isOTPValid = isOTPValid, isOTPEmpty= isOTPEmpty, isOTPMatched = isOTPMatched)
            ResendOtpView(
                userphone = userphone
            )
        }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpTextField(otp : String, isOTPValid : Boolean,onValueChange : (String) -> Unit){
    OutlinedTextField(
        value = otp,
        onValueChange = {
            onValueChange(it)
        },
        label = { Text("Enter OTP") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done
        ),
        isError = !isOTPValid,
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

//#Change16-may
@Composable
fun OtpErrorView(isOTPValid: Boolean, isOTPEmpty: Boolean, isOTPMatched : Boolean){
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        ItalicTextView(
            title = when {
                (!isOTPValid && !isOTPEmpty)-> "Please enter a 6 digit OTP"
                isOTPEmpty-> "Enter OTP"
                (!isOTPMatched)-> "OTP Not Matched"
                else -> ""
            }, textColor = Color.Red
        )
    }
}

@Composable
fun ResendOtpView(userphone: String){
    var isOTPTimerRunning by remember { mutableStateOf(false) }
    val countdown = remember { mutableStateOf(30) }
    val countdownRunning = remember { mutableStateOf(true) }

    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (countdown.value > 0) {
                isOTPTimerRunning = true
                //#Change
                BoldTextView(title = "Resend OTP in ${countdown.value}s", textColor = Color.Blue)//change text color
            } else {
                isOTPTimerRunning = false
                TextButton(
                    onClick = {
                        MainActivity.adminDBRepo.sendSubUserVerificationCode(userphone)
                        countdownRunning.value = true
                        countdown.value = 30
                    },
                    border = null,
                    contentPadding = PaddingValues(),
                    modifier = Modifier.height(20.dp)
                ) {
                    //#Change
                    BoldTextView(title = "Resend OTP", textColor = Color.Blue)//Change text color
                }
            }
            LaunchedEffect(countdown.value) { // Change Unit to countdown.value
                while (countdown.value > 0) {
                    delay(1000)
                    if (countdownRunning.value) { // Only decrement countdown if it is running
                        countdown.value--
                    }
                }
                countdownRunning.value =
                    false // Reset the countdown running state after countdown finishes
            }
        }
    }
}


@Composable
fun EcgAlert(title: String, subTitle: String, onOkClick : () -> Unit){
    AlertDialog(onDismissRequest = { },
        title = {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
                BoldTextView(title = title)
            }
        },
        text = {
            RegularTextView(title = subTitle)
        },
        confirmButton = {},
        dismissButton = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(onClick = { onOkClick() }) {
                    BoldTextView("OK", textColor = Color.White)
                }
            }
        }
    )
}


var xVal = 0f
var xList : ArrayList<Float> = arrayListOf()
var yList : ArrayList<Float> = arrayListOf()
var isWriting = false

fun resetGraphData(){
    xList = arrayListOf()
    yList = arrayListOf()
    xVal = 0f
}

@Composable
fun RealtimeEcgAlertView() {
    AlertDialog(
        onDismissRequest = { },
        title = {
          TitleViewWithCancelBtn(title = "Realtime ECG") {
              MainActivity.pc300Repo.isShowEcgRealtimeAlert.value = false
          }
        },
        text = {
            val cellSize = 10.dp
            val strokeWidth = with(LocalDensity.current) { 0.2.dp.toPx() }
            val lightPink = Color(android.graphics.Color.parseColor("#FFC0CB")).copy(alpha = 0.2f)

            Column() {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .background(color = Color.Transparent)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = lightPink)
                    ) {
                        val columns = (size.width / cellSize.toPx()).toInt()
                        val rows = (size.height / cellSize.toPx()).toInt()

                        for (i in 0 until columns) {
                            for (j in 0 until rows) {
                                val startX = i * cellSize.toPx()
                                val startY = j * cellSize.toPx()

                                drawRect(
                                    color = Color.Red,
                                    topLeft = Offset(startX, startY),
                                    size = Size(cellSize.toPx(), cellSize.toPx()),
                                    style = Stroke(strokeWidth)
                                )
                            }
                        }
                    }

                    val dataToDraw = MainActivity.pc300Repo.dataToDraw
                    var arrayCnt by remember { mutableStateOf(0) }
                    val configuration = LocalConfiguration.current
                    val density = LocalDensity.current.density
                    val stepX = density
                    val screenWidth = configuration.screenWidthDp
                    //For Phone
                    val screenHeight = 700
                    val displayBufferSize = 272
                    //For TaB
//                    val screenHeight = 500
//                    val displayBufferSize = 510

                    val yPX2MMUnit : Float = 25.4f / configuration.densityDpi
                    val heightMm : Float = screenHeight * yPX2MMUnit
                    val gridHeight : Float =  ((heightMm / 6).toFloat())
                    val zoomECGforMm = gridHeight / 114.3f

                    MainActivity.pc300Repo.setUpECGScreenConfiguration(LocalContext.current)

                    LaunchedEffect(Unit) {
                        while (true) {
                            try {
                                if (!StaticReceive.DRAWDATA.isNullOrEmpty()){
                                    val data = StaticReceive.DRAWDATA.removeAt(0).data.toFloat()
                                    withContext(Dispatchers.Main) {
                                        if (dataToDraw.size < displayBufferSize) {
                                            dataToDraw.add(data)
                                            Log.e("TAG", "RealtimeEcgAlertView: add data $data at index $arrayCnt")
                                        } else {
                                            xList.add(xVal)
                                            xVal += stepX
                                            val y = gethMm(
                                                data.toInt(),
                                                heightMm,
                                                zoomECGforMm,
                                                yPX2MMUnit
                                            )
                                            yList.add(y)
                                            dataToDraw[arrayCnt] = data
                                            Log.d("TAG", "RealtimeEcgAlertView: add new data $data at index $arrayCnt")
                                        }
                                        arrayCnt = (arrayCnt + 1) % displayBufferSize
                                    }
                                    if (StaticReceive.DRAWDATA.size > 25) {
                                        delay(6)
                                    } else {
                                        delay(9)
                                    }
                                } else {
                                    delay(500)
                                }
                            } catch(e : NullPointerException){
                                Log.e("TAG", "RealtimeEcgAlertView: ", )
                            }
                        }
                    }

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        if (dataToDraw.isNotEmpty()) {
                            val path = Path()
                            val paint = Paint()
                            paint.pathEffect = PathEffect.cornerPathEffect(10f)
                            paint.color = Color.Red
                            paint.style = PaintingStyle.Stroke
                            paint.strokeWidth = 1.dp.toPx()

                            path.moveTo(0f,
                                gethMm(
                                    dataToDraw[0].toInt(),
                                    heightMm,
                                    zoomECGforMm,
                                    yPX2MMUnit
                                )
                            )

                            Log.e("TAG", "RealtimeGraph: drawing new data")

                            try {
                                for (i in 0 until dataToDraw.size) {
                                    if(dataToDraw.isNotEmpty()) {
                                        path.lineTo((i * stepX),
                                            gethMm(dataToDraw[i].toInt(), heightMm, zoomECGforMm,
                                                yPX2MMUnit
                                            )
                                        )
                                    }
                                }
                            } catch (e : IndexOutOfBoundsException){
                                Log.d("TAG", "RealtimeEcgAlertView: ")
                            }

                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(width = 1.dp.toPx())
                            )

                            drawLine(
                                start = Offset((dataToDraw.size * stepX - 1f).toFloat(), 0f),
                                end = Offset((dataToDraw.size * stepX - 1f).toFloat(), size.height),
                                color = Color.Transparent,
                                strokeWidth = 5.dp.toPx()
                            )
                        }
                    }

                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                        .background(color = Color.Transparent),
                        contentAlignment = Alignment.Center){
                        when(MainActivity.pc300Repo.ecg.value){
                            2,3,4 -> {
                                writeDataToFile()
                                BoldTextView(title = "ECG Result - ${MainActivity.pc300Repo.getEcgResultMsgBasedOnCode(
                                    LocalContext.current)}", fontSize = 20)
                            }
                        }
                    }
                }
            }
        }, confirmButton = {}
    )
}

fun writeDataToFile(){
    if(isWriting) return
    isWriting = true
    GlobalScope.launch {
        if(xList.size == yList.size){
            try {
                xList.forEachIndexed { index, fl ->
                    val x = xList[index]
                    val y = yList[index]
                    MainActivity.csvRepository.writeDataToFile("$y")
                }
            } catch ( e : ConcurrentModificationException){
                Log.d("TAG", "writeDataToFile: ")
            }

        }
    }
}

@Composable
fun AlertView(showAlert : Boolean,title: String, subTitle: String, subTitle1: String, onYesClick : () -> Unit, onNoClick : () -> Unit, onCancelClick: () -> Unit){
    if(showAlert){
        AlertDialog(onDismissRequest = { },
            title = {
                TitleViewWithCancelBtn(title = title) {
                    onCancelClick()
                }
            },
            text = {
                RegularTextView(title = subTitle)
                Spacer(modifier = Modifier.height((5).dp))
                RegularTextView(title = subTitle1)
            },
            confirmButton = {
                Button(onClick = { onYesClick() }) {
                    BoldTextView(title = "YES", textColor = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { onNoClick() }) {
                    BoldTextView(title = "NO", textColor = Color.White)
                }
            }
        )
    }
}


