package com.aarogyaforworkers.aarogya.composeScreens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.Auth.AuthRepository
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


var lastPasswordResetState = false

@Composable
fun PasswordResetScreen(navHostController: NavHostController, repository: AuthRepository) {

    var isOTPValid by remember { mutableStateOf(true) }
    var isOTPEmpty by remember { mutableStateOf(false) }
    var isNewPINValid by remember { mutableStateOf(true) }
    var isNewPINEmpty by remember { mutableStateOf(false) }
    var isConfirmPINValid by remember { mutableStateOf(true) }
    var isConfirmPINEmpty by remember { mutableStateOf(false) }

    var context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    var otp by remember { mutableStateOf("") }

    var newPassword by remember { mutableStateOf("") }

    var confirmPassword by remember { mutableStateOf("") }

    if(repository.wrongOTP.value) {
        isLoading = false
        showUserNotFoundAlert("Please enter correct OTP")
    }

    when(val newPasswordResetState = repository.passwordResetState.value){
        true -> {
            if(lastPasswordResetState != newPasswordResetState){
                repository.signOut()
                isLoading = false
                Toast.makeText(context, "PIN Reset successfully Login Now", Toast.LENGTH_LONG).show()
                navHostController.navigate(Destination.Login.routes)
                lastPasswordResetState = newPasswordResetState
            }
        }
        false -> {
            lastPasswordResetState = newPasswordResetState
            PasswordResetBackBtn(navHostController = navHostController)
            val userLoggedIn = repository.userSignInState.value


            Box() {
                if (isLoading) showProgress()

                Column(
                    Modifier
                        .alpha(if (isLoading) 0.08f else 1.0f)
                        .fillMaxSize()) {

                    Row(Modifier.height(200.dp), verticalAlignment = Alignment.CenterVertically) {
                        AppLogo()
                    }
                    Column(
                        Modifier.padding(horizontal = 15.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        if(!userLoggedIn){
                            Spacer(modifier = Modifier.height(10.dp))
                            RegularTextView(title = "OTP is sent successfully, Please enter OTP and reset PIN")
//                            Text(text = "OTP is sent successfully, Please enter OTP and reset PIN")
                            Spacer(modifier = Modifier.height(5.dp))

                            AuthTextField(
                                textInput = otp,
                                onChangeInput = {
                                    otp = it.take(6)
                                    isOTPValid = otp.length == 6
                                    isOTPEmpty = otp.isEmpty()
                                },
                                labelText = "Enter OTP",
                                keyboard = KeyboardType.NumberPassword,
                                error = !isOTPValid || isOTPEmpty,
                                TestTag = "tagOTP"
                            )

                            when{
                                (!isOTPValid && !isOTPEmpty)-> ErrorMessage(errorMessage = "Please enter a 6 digit OTP.", errorTestTag = "tagOtpInvalid")
                                isOTPEmpty-> ErrorMessage(errorMessage = "Enter OTP", errorTestTag = "tagOtpEmpty")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        AuthTextField(
                            textInput = newPassword,
                            onChangeInput = {newPassword = it.take(6)
                                isNewPINValid = newPassword.length == 6
                                isNewPINEmpty = newPassword.isEmpty()
                                PasswordResetScreenObjects.shared.newPassword = it},
                            labelText = "Enter new PIN",
                            keyboard = KeyboardType.NumberPassword,
                            error = !isNewPINValid || isNewPINEmpty,
                            TestTag = "tagNewPIN"
                        )

                        when{
                            (!isNewPINValid && !isNewPINEmpty)-> ErrorMessage(errorMessage = "Please enter your 6 digit PIN code.", errorTestTag = "tagNewPinInvalid")
                            isNewPINEmpty -> ErrorMessage(errorMessage = "Enter PIN", errorTestTag = "tagNewPinEmpty")
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        AuthTextField(
                            textInput = confirmPassword,
                            onChangeInput = {
                                confirmPassword = it.take(6)
                                isConfirmPINValid = confirmPassword == newPassword
                                isConfirmPINEmpty = confirmPassword.isEmpty()
                                PasswordResetScreenObjects.shared.confirmPassword = it
                            },
                            labelText = "Enter confirm PIN",
                            keyboard = KeyboardType.NumberPassword,
                            error = !isConfirmPINValid || isConfirmPINEmpty,
                            TestTag = "tagConfirmPIN"
                        )

                        when{
                            (!isConfirmPINValid && !isConfirmPINEmpty)-> ErrorMessage(errorMessage = "Confirm PIN do not match.", errorTestTag = "tagCnfPinInvalid")
                            isConfirmPINEmpty -> ErrorMessage(errorMessage = "Enter email", errorTestTag = "tagCnfPinEmpty")
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        if(!userLoggedIn) ResendBtn {
                            repository.forgotPassword(PasswordResetScreenObjects.shared.email)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        AuthBtn(btnName = "Reset PIN",
                            enable = listOf(otp.isNotEmpty(), newPassword.isNotEmpty(), confirmPassword.isNotEmpty(), isOTPValid, isNewPINValid, isConfirmPINValid).all { it },
                            btnColor = Color(0XFF397EF5)) {
                            isLoading = true
                            repository.confirmAndResetPassword(otp, PasswordResetScreenObjects.shared.email, confirmPassword)
                        }

                    }
                }
            }
            if(isLoading) showProgress()
        }
    }
}


@Composable
fun ResendBtn(actionBtn: () -> Unit) {
    val countdown = remember { mutableStateOf(30) }
    val countdownRunning = remember { mutableStateOf(true) } // Track if countdown is running

    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd) {
        TextButton(onClick = {
            if (!countdownRunning.value) { // Check if countdown is not running
                actionBtn()
                countdown.value = 30 // Set initial countdown value
                countdownRunning.value = true // Start the countdown
            }
        },
            enabled = countdown.value <= 0
        ) {
            BoldTextView(title = if(countdown.value > 0) "Resend OTP in ${countdown.value}" else "Resend OTP",
                textColor = Color(0xFF397EF5))
        }
    }

    LaunchedEffect(countdown.value) { // Change Unit to countdown.value
        while (countdown.value > 0) {
            delay(1000)
            if (countdownRunning.value) { // Only decrement countdown if it is running
                countdown.value--
            }
        }
        countdownRunning.value = false // Reset the countdown running state after countdown finishes
    }
}

@Composable
fun AuthBtn(btnName: String, enable: Boolean, btnColor: Color, actionBtn: () -> Unit){
    Row(Modifier.fillMaxWidth()) {
        Button(onClick =  actionBtn,
            enabled = enable,
            shape = RoundedCornerShape(5.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = btnColor.copy(alpha = 0.7f),
                containerColor = btnColor
            ),
            modifier = Modifier.fillMaxWidth().height(55.dp)
        ) {
            BoldTextView(title = btnName, textColor = Color.White)
        }
    }
}




@Composable
fun PasswordResetBackBtn(navHostController: NavHostController){
    IconButton(onClick = {
        MainActivity.authRepo.updateForgotPasswordOTPState(false)
        navHostController.navigate(Destination.ForgotPasswordScreen.routes) }, modifier = Modifier.size(36.dp)) {
        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon), contentDescription = "Logout")
    }
}

class PasswordResetScreenObjects{
    val otp = ""
    var email = ""
    var newPassword = ""
    var confirmPassword = ""
    companion object{
        val shared = PasswordResetScreenObjects()
    }
}




//var lastPasswordResetState = false
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PasswordResetScreen(navHostController: NavHostController, repository: AuthRepository) {
//
//    var isOTPValid by remember { mutableStateOf(true) }
//    var isOTPEmpty by remember { mutableStateOf(false) }
//    var isNewPINValid by remember { mutableStateOf(true) }
//    var isNewPINEmpty by remember { mutableStateOf(false) }
//    var isConfirmPINValid by remember { mutableStateOf(true) }
//    var isConfirmPINEmpty by remember { mutableStateOf(false) }
//
//    var context = LocalContext.current
//
//    var isLoading by remember { mutableStateOf(false) }
//
//    var otp by remember { mutableStateOf("") }
//
//    var newPassword by remember { mutableStateOf("") }
//
//    var confirmPassword by remember { mutableStateOf("") }
//
//    if(repository.wrongOTP.value) {
//        isLoading = false
//        showUserNotFoundAlert("Please enter correct OTP")
//    }
//
//    when(val newPasswordResetState = repository.passwordResetState.value){
//        true -> {
//            if(lastPasswordResetState != newPasswordResetState){
//                repository.signOut()
//                isLoading = false
//                Toast.makeText(context, "PIN Reset successfully Login Now", Toast.LENGTH_LONG).show()
//                navHostController.navigate(Destination.Login.routes)
//                lastPasswordResetState = newPasswordResetState
//            }
//        }
//        false -> {
//            lastPasswordResetState = newPasswordResetState
//            PasswordResetBackBtn(navHostController = navHostController)
//            val userLoggedIn = repository.userSignInState.value
//            Column(modifier = Modifier
//                .padding(horizontal = 40.dp, vertical = 60.dp).testTag("resetScreen"),
//                horizontalAlignment = Alignment.CenterHorizontally) {
//                if(!userLoggedIn){
//                    //#change12May
//                    Spacer(modifier = Modifier.height(40.dp))
//                    AppLogo()
//                    //#change12May
//                    Spacer(modifier = Modifier.height(20.dp))
//                    Text(text = "OTP is sent successfully, Please enter OTP and reset PIN")
//                    Spacer(modifier = Modifier.height(5.dp))
//                    OutlinedTextField(value = otp, onValueChange = {
//                        otp = it.take(6)
//                        isOTPValid = otp.length == 6
//                        isOTPEmpty = otp.isEmpty() }, label = { Text(text = "OTP", style = TextStyle(fontSize = 16.sp))},
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .width(55.dp),
//                        isError = !isOTPValid || isOTPEmpty,
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            keyboardType = KeyboardType.Number,
//                            imeAction = ImeAction.Done))
//                    Box(modifier = Modifier.fillMaxWidth()){
//                        Text(text =
//                        when{
//                            (!isOTPValid && !isOTPEmpty) -> "Please enter a 6 digit OTP."
//                            isOTPEmpty -> "Enter OTP"
//                            else -> ""
//                        },
//                            color = Color.Red,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp,)
//                    }
//                }
//                Spacer(modifier = Modifier.height(10.dp))
//                OutlinedTextField(value = newPassword, onValueChange = {
//                    newPassword = it.take(6)
//                    isNewPINValid = newPassword.length == 6
//                    isNewPINEmpty = newPassword.isEmpty()
//                    PasswordResetScreenObjects.shared.newPassword = it}, label = { Text(text = "New PIN", style = TextStyle(fontSize = 16.sp))},
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .width(55.dp),
//                    isError = !isNewPINValid || isNewPINEmpty,
//                    keyboardOptions = KeyboardOptions.Default.copy(
//                        keyboardType = KeyboardType.Number,
//                        imeAction = ImeAction.Done))
//                Box(modifier = Modifier.fillMaxWidth()){
//                    Text(text =
//                    when{
//                        //#change12May
//                        (!isNewPINValid && !isNewPINEmpty) -> "Please enter your 6 digit PIN code."
//                        isNewPINEmpty -> "Enter New PIN"
//                        else -> ""
//                    },
//                        color = Color.Red,
//                        fontStyle = FontStyle.Italic,
//                        fontSize = 12.sp,)
//                }
//
//                Spacer(modifier = Modifier.height(10.dp))
//                OutlinedTextField(value = confirmPassword, onValueChange = {
//                    confirmPassword = it.take(6)
//                    isConfirmPINValid = confirmPassword == newPassword
//                    isConfirmPINEmpty = confirmPassword.isEmpty()
//                    PasswordResetScreenObjects.shared.confirmPassword = it }, label = { Text(text = "Confirm PIN", style = TextStyle(fontSize = 16.sp))},
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .width(55.dp),
//                    isError = !isConfirmPINValid || isConfirmPINEmpty,
//                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done))
//                Box(modifier = Modifier.fillMaxWidth()){
//                    Text(text =
//                    when{
//                        //#change12May
//                        (!isConfirmPINValid && !isConfirmPINEmpty) -> "Confirm PIN do not match."
//                        isConfirmPINEmpty -> "Enter Confirm PIN"
//                        else -> ""
//                    },
//                        color = Color.Red,
//                        fontStyle = FontStyle.Italic,
//                        fontSize = 12.sp,)
//                }
//
//                Spacer(modifier = Modifier.height(10.dp))
//                if(!userLoggedIn) ResendBtn(repository)
//                Spacer(modifier = Modifier.height(10.dp))
//                Button(onClick = {
//                    if (isOTPEmpty || isNewPINEmpty || isConfirmPINEmpty){
//                        isOTPEmpty = true
//                        isNewPINEmpty = true
//                        isConfirmPINEmpty = true
//                    }else{
//                        isLoading = true
//                        repository.confirmAndResetPassword(otp, PasswordResetScreenObjects.shared.email, confirmPassword)
//                    } },
//                    colors = ButtonDefaults.buttonColors(Color(0XFF397EF5)),
//                    shape = RoundedCornerShape(5.dp),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(55.dp)) {
//                    Text("Reset PIN",
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White)
//                }
//            }
//            if(isLoading) showProgress()
//        }
//    }
//}
//
//
//@Composable
//fun ResendBtn(repository: AuthRepository) {
//    val countdown = remember { mutableStateOf(30) }
//    val countdownRunning = remember { mutableStateOf(true) } // Track if countdown is running
//
//    Box(modifier = Modifier.fillMaxWidth(),
//        contentAlignment = Alignment.CenterEnd) {
//        if (countdown.value > 0) {
//            Spacer(modifier = Modifier.height(10.dp))
//            Text(text = "Resend OTP in ${countdown.value}",
//                style = TextStyle(fontSize = 16.sp),
//                color = Color(0xFF397EF5),
//                fontWeight = FontWeight.Bold)
//        } else {
//            TextButton(onClick = {
//                if (!countdownRunning.value) { // Check if countdown is not running
//                    repository.forgotPassword(PasswordResetScreenObjects.shared.email)
//                    countdown.value = 30 // Set initial countdown value
//                    countdownRunning.value = true // Start the countdown
//                }
//            }) {
//                Text(text = "Resend OTP",
//                    style = TextStyle(fontSize = 16.sp),
//                    color = Color(0xFF397EF5),
//                    fontWeight = FontWeight.Bold)
//            }
//        }
//    }
//
//    LaunchedEffect(countdown.value) { // Change Unit to countdown.value
//        while (countdown.value > 0) {
//            delay(1000)
//            if (countdownRunning.value) { // Only decrement countdown if it is running
//                countdown.value--
//            }
//        }
//        countdownRunning.value = false // Reset the countdown running state after countdown finishes
//    }
//}
//
//@Composable
//fun PasswordResetBackBtn(navHostController: NavHostController){
//    IconButton(onClick = {
//        MainActivity.authRepo.updateForgotPasswordOTPState(false)
//        navHostController.navigate(Destination.ForgotPasswordScreen.routes) }, modifier = Modifier.size(36.dp)) {
//        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon), contentDescription = "Logout")
//    }
//}
//
//class PasswordResetScreenObjects{
//    val otp = ""
//    var email = ""
//    var newPassword = ""
//    var confirmPassword = ""
//    companion object{
//        val shared = PasswordResetScreenObjects()
//    }
//}