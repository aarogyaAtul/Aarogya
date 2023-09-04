package com.aarogyaforworkers.aarogya.composeScreens

import Commons.LoginTags
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.*
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.Auth.AuthRepository
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.MainActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.aarogyaforworkers.aarogya.R


val listOfTab = listOf("Phone", "Email")

var isLoginScreenSetUp = false
var isLastUpdatedValue = false
var isAllreadyOnHome = false

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(navHostController: NavHostController, repository: AuthRepository){

    Disableback()

    CheckInternet(context = LocalContext.current)

    when(repository.userSignInState.value){
        true -> {
            //Change for Sonar Test
            if((isLastUpdatedValue != repository.userSignInState.value) && (isLoginScreenSetUp)) {
                isLastUpdatedValue = repository.userSignInState.value
                isAdminHomeScreenSetUp = false
                isAllreadyOnHome = false
                navigateToHome(navHostController = navHostController)
            }
            isLoginScreenSetUp = true
        }

        false -> {
            MainActivity.adminDBRepo.resetLoggedInUser()
            showLoginScreen(navHostController, repository)
            isLoginScreenSetUp = true
        }
    }
}

@Composable
fun showUserNotFoundAlert(withTitle : String){
    AlertDialog(
        onDismissRequest = { },
        text = {
            Column( Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(withTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = { Button(
            onClick = {
                MainActivity.authRepo.updateWrongOTP(false)
                MainActivity.authRepo.updateWrongPassword(false)
                MainActivity.authRepo.updateEmailNotFound(false)
                MainActivity.authRepo.updateWrongUserName(false)
            },
            modifier = Modifier.fillMaxWidth()
        ){Text("OK",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center)}
        },
        dismissButton = null
    )
}

@ExperimentalMaterial3Api
@Composable
fun showLoginScreen(navHostController: NavHostController, authRepository: AuthRepository){

    var isLoading by remember { mutableStateOf(false) }
    var otpVisiblePhone by remember { mutableStateOf(false) }
    var otpVisibleEmail by remember { mutableStateOf(false) }
    // Change for Sonar Test
    var pin by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    //Change for Sonar Test
    var email by remember { mutableStateOf("")}
    var emailError by remember { mutableStateOf("")}
    var pinError by remember { mutableStateOf("")}
    var otpError by remember { mutableStateOf("")}

    var phoneError by remember { mutableStateOf("")}
    var isEmailValid by remember { mutableStateOf(true) }
    var isPINValid by remember { mutableStateOf(true) }
    var isOTPValid by remember { mutableStateOf(true) }

    var tabIndex by remember { mutableStateOf(0) }
    //Change for Sonar Test
    var phone by remember { mutableStateOf("") }
    var isPhoneValid by remember { mutableStateOf(true) }

    //msg will come when user press continue button
    var userNotFoundAlert by remember { mutableStateOf(false) }
    var wrongPINAlert by remember { mutableStateOf(false) }
    var wrongOTPAlert by remember { mutableStateOf(false) }
    var isOTPTimerRunning by remember { mutableStateOf(false) }

    var isPhoneEmpty by remember { mutableStateOf(false) }
    var isEmailEmpty by remember { mutableStateOf(false) }
    var isPINEmpty by remember { mutableStateOf(false) }
    var isOTPEmpty by remember { mutableStateOf(false) }

    val countdown = remember { mutableStateOf(10) }

    val textFieldFocusManager = LocalFocusManager.current

    val countdownRunning = remember { mutableStateOf(true) } // Track if countdown is running

    var passwordVisible by remember { mutableStateOf(true) }

    when{
        authRepository.wrongUsername.value -> {
            showUserNotFoundAlert("Wrong Email")
            isLoading = false
        }

        authRepository.noEmailFound.value -> {
            showUserNotFoundAlert("There is no user linked with given Phone")
            isLoading = false
        }

        authRepository.wrongPassword.value -> {
            showUserNotFoundAlert("Please enter correct password")
            isLoading = false
        }

        authRepository.allReadyLoggedIn.value -> {
            navHostController.navigate(Destination.Home.routes)
        }

        authRepository.signInOTPSent.value -> {
            authRepository.updateSignInOTPState(false)
            isLoading = false
            when(tabIndex){
                0 -> {
                    otpVisiblePhone = true
                }
                1 -> {
                    otpVisibleEmail = true
                }
            }
        }

        authRepository.wrongOTP.value -> {
            isLoading = false
            showUserNotFoundAlert("Please enter correct OTP")
        }
    }

    if (wrongPINAlert) {
        AlertDialog(
            onDismissRequest = { wrongPINAlert = false },
            text = {
                Column( Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Your PIN is wrong.", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = { Button(
                onClick = { wrongPINAlert = false },
                modifier = Modifier.fillMaxWidth()
            ){Text("OK",fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)}
            },
            dismissButton = null
        )
    }

    if (wrongOTPAlert) {
        AlertDialog(
            onDismissRequest = { wrongOTPAlert = false },
            text = {
                Column( Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Wrong OTP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = { Button(
                onClick = { wrongOTPAlert = false },
                modifier = Modifier.fillMaxWidth()
            ){Text("OK",fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center)}
            },
            dismissButton = null
        )
    }

    Box() {
        if(isLoading) showProgress()
        Column(
            Modifier
                .alpha(if (isLoading) 0.08f else 1.0f)
                .fillMaxSize()
                .testTag("login")) {
            Row(Modifier.height(200.dp), verticalAlignment = Alignment.CenterVertically) {
                AppLogo()
            }
            Column(Modifier.padding(horizontal = 15.dp), verticalArrangement = Arrangement.Center) {
                Row() {
                    TabRow(tabIndex,
                        modifier = Modifier.fillMaxWidth(),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                color = Color(0XFF397EF5),
                                height = 2.dp,
                                modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex])
                            )
                        }
                    ) {
                        listOfTab.forEachIndexed { index, text ->
                            Tab(
                                selected = tabIndex == index,
                                onClick = {
                                    textFieldFocusManager.clearFocus()
                                    tabIndex = index},
                                modifier = Modifier
                                    .height(55.dp)
                                    .background(
                                        color = if (tabIndex == index) Color(0XFF397EF5) else Color.Transparent,
                                        shape = RoundedCornerShape(5.dp)
                                    ),
                                enabled = when{
                                    otpVisiblePhone -> false
                                    otpVisibleEmail -> false
                                    else -> true
                                }
                            ){
                                BoldTextView(title = text, textColor = if (tabIndex==index) Color.White else Color.Black)
                            }
                        }
                    }
                }
                Column() {
                    when (tabIndex){
                        0-> {
                            AuthTextField(
                                textInput = phone,
                                onChangeInput = { newValue ->
//                                    if (newValue.length == 10) {
                                    // remove spaces and update mobileNumber
                                    phone = newValue.take(10)
//                                        phone = newValue.filter { !it.isWhitespace() }
                                    // validate mobileNumber based on the new value
                                    isPhoneValid = phone.length == 10
                                    // update isPhoneEmpty
                                    isPhoneEmpty = phone.isEmpty()
//                                    }
                                },
                                labelText = "Phone",
                                keyboard = KeyboardType.NumberPassword,
                                error = (!isPhoneValid || isPhoneEmpty),
                                enable = !otpVisiblePhone,
                                TestTag = LoginTags.shared.phoneTextField
                            )

                            when{
                                (!isPhoneValid && !isPhoneEmpty)-> ErrorMessage(errorMessage = "Please enter 10 digit phone number", errorTestTag = "tagPhoneInvalid")
                                (isPhoneEmpty)-> ErrorMessage(errorMessage = "Enter Phone No.", errorTestTag = "tagPhoneEmpty")
                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            when{
                                otpVisiblePhone-> {
                                    AuthTextField(
                                        textInput = otp,
                                        onChangeInput ={ newValue->
                                            otp = newValue.take(6) // truncate to 6 characters
                                            isOTPValid = otp.length == 6
                                            isOTPEmpty = otp.isEmpty()
                                        },
                                        labelText = "OTP",
                                        keyboard = KeyboardType.Number,
                                        error = !isOTPValid || isOTPEmpty,
                                        TestTag = LoginTags.shared.otpPinTextField
                                    )
                                    when{
                                        (!isOTPValid && !isOTPEmpty) -> ErrorMessage(errorMessage = "Please enter a 6 digit OTP.", errorTestTag = "tagOTPInvalid")
                                        isOTPEmpty -> ErrorMessage(errorMessage = "Enter OTP", errorTestTag = "tagOTPEmpty")
                                    }
                                }

                                !otpVisiblePhone->{
                                    AuthTextField(
                                        textInput = pin,
                                        onChangeInput ={ newValue->
                                            pin = newValue.take(6) // truncate to 6 characters
                                            isPINValid = pin.length == 6
                                            isPINEmpty = pin.isEmpty()
                                        },
                                        labelText = "PIN",
                                        keyboard = KeyboardType.NumberPassword,
                                        error = !isPINValid || isPINEmpty,

                                        visualTransformation = if (passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                                        iconAction = { passwordVisible = !passwordVisible },
                                        iconImage = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        iconName = if (passwordVisible) "Show Password" else "Hide Password",
                                        TestTag = LoginTags.shared.otpPinTextField
                                    )
                                    when{
                                        (!isPINValid && !isPINEmpty) -> ErrorMessage(errorMessage = "Please enter your 6 digit PIN code.", errorTestTag = "tagPINInvalid")
                                        isPINEmpty -> ErrorMessage(errorMessage = "Enter PIN", errorTestTag = "tagPINEmpty")
                                    }
                                }
                            }
                        }

                        1-> {
                            AuthTextField(
                                textInput = email,
                                onChangeInput = {
                                    email = it
                                    isEmailValid = isValidEmail(it)
                                    isEmailEmpty = email.isEmpty()
                                },
                                labelText = "Email",
                                keyboard = KeyboardType.Email,
                                error = (!isEmailValid || isEmailEmpty),
                                enable = !otpVisibleEmail,
                                TestTag = "tagEmail"
                            )

                            when{
                                (!isEmailValid && !isEmailEmpty)-> ErrorMessage(errorMessage = "Please enter email in format abc@xyz.pqr", errorTestTag = "tagEmailInvalid")
                                (isEmailEmpty)-> ErrorMessage(errorMessage = "Enter email", errorTestTag = "tagEmailEmpty")
                            }

                            Spacer(modifier = Modifier.height(5.dp))

                            when{
                                otpVisibleEmail-> {
                                    AuthTextField(
                                        textInput = otp,
                                        onChangeInput ={ newValue->
                                            otp = newValue.take(6) // truncate to 6 characters
                                            isOTPValid = otp.length == 6
                                            isOTPEmpty = otp.isEmpty()
                                        },
                                        labelText = "OTP",
                                        keyboard = KeyboardType.Number,
                                        error = !isOTPValid || isOTPEmpty,
                                        TestTag = "tagOTP"
                                    )
                                    when{
                                        (!isOTPValid && !isOTPEmpty) -> ErrorMessage(errorMessage = "Please enter a 6 digit OTP", errorTestTag = "tagOTPInvalid")
                                        isOTPEmpty -> ErrorMessage(errorMessage = "Enter OTP", errorTestTag = "tagOTPEmpty")
                                    }
                                }

                                !otpVisibleEmail->{
                                    AuthTextField(
                                        textInput = pin,
                                        onChangeInput ={ newValue->
                                            pin = newValue.take(6) // truncate to 6 characters
                                            isPINValid = pin.length == 6
                                            isPINEmpty = pin.isEmpty()
                                        },
                                        labelText = "PIN",
                                        keyboard = KeyboardType.NumberPassword,
                                        error = !isPINValid || isPINEmpty,

                                        visualTransformation = if (passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                                        iconAction = { passwordVisible = !passwordVisible },
                                        iconImage = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        iconName = if (passwordVisible) "Show Password" else "Hide Password",
                                        TestTag = "tagPIN"
                                    )
                                    when{
                                        (!isPINValid && !isPINEmpty) -> ErrorMessage(errorMessage = "Please enter your 6 digit PIN code.", errorTestTag = "tagPINInvalid")
                                        isPINEmpty -> ErrorMessage(errorMessage = "Enter PIN", errorTestTag = "tagPINEmpty")
                                    }
                                }
                            }
                        }
                    }
                    when (tabIndex){

                        0 -> {
                            when(otpVisiblePhone) {
                                true -> {
                                    Row(Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        ResendBtn {
                                            MainActivity.adminDBRepo.getPhoneOTP(phone)
                                        }
//                                        isOTPTimerRunning = false
//                                        ResendBtn(actionBtn = MainActivity.adminDBRepo.getPhoneOTP(phone))
                                    }
                                }
                                false -> {


                                    Row(Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ForgotSetPIN {
                                            MainActivity.authRepo.updateForgotPasswordOTPState(false)

                                            authRepository.updateEmailNotFound(false)
                                            navHostController.navigate(Destination.ForgotPasswordScreen.routes)
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            when(otpVisibleEmail){
                                true -> {
                                    Row(Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ResendBtn {
                                            authRepository.getSignInEmailOTP(email)
                                        }

//                                        isOTPTimerRunning = false
//                                        ResendBtn(actionBtn = authRepository.getSignInEmailOTP(email))
                                    }
                                }

                                false -> {
                                    Row(Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        ForgotSetPIN {
                                            MainActivity.authRepo.updateForgotPasswordOTPState(false)
                                            navHostController.navigate(Destination.ForgotPasswordScreen.routes)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Button(onClick = {
                        when(tabIndex) {

                            0 -> { // PhoneLogin
                                when (otpVisiblePhone) {

                                    true -> {
                                        if (otp.isEmpty()) {
                                            isOTPEmpty = true
                                        }else if((isOTPValid)){
                                            isLoading = true
                                            authRepository.updateWrongOTP(false)
                                            authRepository.confirmSignInWithOTP(otp)
                                        }
                                    }
                                    false -> {
                                        if (phone.isEmpty() || pin.isEmpty()) {
                                            if (phone.isEmpty()) {
                                                isPhoneEmpty = true
                                            }
                                            if (pin.isEmpty()) {
                                                isPINEmpty = true
                                            }
                                        } else if (isPhoneValid && isPINValid) {
                                            isLoading = true
                                            authRepository.updateEmailNotFound(false)
                                            authRepository.updateWrongPassword(false)
                                            authRepository.updateWrongUserName(false)
                                            authRepository.updateIsAllReadyLoggedIn(false)
                                            authRepository.signInWithPhonePassword(phone, pin)
                                        }
                                    }
                                }
                            }

                            1 -> { // EmailLogin
                                when (otpVisibleEmail) {
                                    true -> {
                                        if (otp.isEmpty()) {
                                            isOTPEmpty = true
                                        } else if((isOTPValid)){
                                            isLoading = true
                                            authRepository.updateWrongOTP(false)
                                            authRepository.confirmSignInWithOTP(otp)
                                        }
                                    }
                                    false -> {
                                        if (email.isEmpty() || pin.isEmpty()) {
                                            if (email.isEmpty()) {
                                                isEmailEmpty = true
                                            }
                                            if (pin.isEmpty()) {
                                                isPINEmpty = true
                                            }
                                        } else if (isEmailValid && isPINValid) {
                                            isLoading = true
                                            authRepository.updateEmailNotFound(false)
                                            authRepository.updateWrongPassword(false)
                                            authRepository.updateWrongUserName(false)
                                            authRepository.updateIsAllReadyLoggedIn(false)
                                            authRepository.signInWithEmailPassword(email, pin)
                                        }
                                    }
                                }
                            }
                        }
                    },
                        colors = ButtonDefaults.buttonColors(Color(0xFF397EF5)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .alpha(
                                when (tabIndex) {
                                    0 -> if (isPhoneValid) 1.0f else 0.7f
                                    1 -> if (isEmailValid) 1.0f else 0.7f
                                    else -> {
                                        0.7f
                                    }
                                }
                            )
                            .testTag(LoginTags.shared.loginContinueBtn),
                        shape = RoundedCornerShape(5.dp))
                    {
                        BoldTextView(title="Continue", textColor = Color.White)
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    orLine()
                    Spacer(modifier = Modifier.height(15.dp))



                    Button(onClick = {

                        when(tabIndex){
                            0 -> {

                                when(otpVisiblePhone){
                                    true -> {
                                        isOTPEmpty = false
                                        isOTPValid = true
                                        otpVisiblePhone = !otpVisiblePhone
                                    }

                                    false -> {
                                        isLoading = true
                                        MainActivity.adminDBRepo.getPhoneOTP(phone)
                                        authRepository.updateSignInOTPState(false)
                                        authRepository.updateEmailNotFound(false)
                                        authRepository.updateWrongPassword(false)
                                        authRepository.updateWrongUserName(false)
                                        authRepository.updateIsAllReadyLoggedIn(false)
                                    }
                                }
                            }
                            1 -> {

                                when(otpVisibleEmail){
                                    true -> {
                                        isOTPEmpty = false
                                        isOTPValid = true
                                        otpVisibleEmail = !otpVisibleEmail
                                    }

                                    false -> {
                                        isLoading = true
                                        authRepository.getSignInEmailOTP(email)
                                        authRepository.updateSignInOTPState(false)
                                        authRepository.updateEmailNotFound(false)
                                        authRepository.updateWrongPassword(false)
                                        authRepository.updateWrongUserName(false)
                                        authRepository.updateIsAllReadyLoggedIn(false)
                                    }
                                }
                            }}
                    },
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color(0xFF2C3E50),
                            containerColor = Color(0xFF2C3E50)
                        ),
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .alpha(
                                when (tabIndex) {
                                    0 -> if (isPhoneValid && phone.isNotEmpty()) 1.0f else 0.7f
                                    1 -> if (isEmailValid && email.isNotEmpty()) 1.0f else 0.7f
                                    else -> {
                                        0.7f
                                    }
                                }
                            ),
                        enabled = when (tabIndex) {
                            0 -> isPhoneValid && phone.isNotEmpty()
                            1 -> isEmailValid && email.isNotEmpty()
                            else -> {false
                            }
                        }

                    ) {
                        BoldTextView(when(tabIndex){
                            0 -> if (otpVisiblePhone)"Continue with PIN" else "Continue with OTP"
                            1 -> if (otpVisibleEmail)"Continue with PIN" else "Continue with OTP"
                            else -> {
                                ""
                            }
                        },textColor = Color.White)
                    }

                }
            }
        }
    }
}

@Composable
fun navigateToHome(navHostController : NavHostController){
    navHostController.navigate(Destination.Home.routes)
}

@Composable
fun navigateToLogin(navHostController: NavHostController){
    navHostController.navigate(Destination.Login.routes)
}

@Composable
fun ReportAppLogo() {
    Row(modifier = Modifier
        .height(40.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.logo_app),
            contentDescription = "logo"
        )
    }
}

@Composable
fun AppLogo() {
    Row(modifier = Modifier
        .height(40.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.applogo),
            contentDescription = "logo"
        )
    }
}



fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun orLine(){
    Row(Modifier.fillMaxWidth(),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
        )
        Text(
            text = "or",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
        )
    }
}

@Composable
fun showProgress(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier
            .size(200.dp) // set the size of the progress circle
            .padding(16.dp)
            .zIndex(1f),
            color = Color.Gray, // set the color of the progress circle
            strokeWidth = 6.dp // set the thickness of the progress circle
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(navHostController = rememberNavController(), repository = AuthRepository())
}

@Composable
fun ForgotSetPIN(actionBtn: () -> Unit) {
    TextButton(onClick = actionBtn) {
        BoldTextView(title = "Set PIN", textColor = Color(0xFF397EF5))
    }
    TextButton(onClick = actionBtn) {
        BoldTextView(title = "Forgot PIN?", textColor = Color(0xFF397EF5))
    }
}


















//
//val listOfTab = listOf("Phone", "Email")
//
//var isLoginScreenSetUp = false
//var isLastUpdatedValue = false
//var isAllreadyOnHome = false
//
//@ExperimentalMaterial3Api
//@Composable
//fun LoginScreen(navHostController: NavHostController, repository: AuthRepository){
//
//    Disableback()
//
//    CheckInternet(context = LocalContext.current)
//
//    when(repository.userSignInState.value){
//        true -> {
//            //Change for Sonar Test
//            if((isLastUpdatedValue != repository.userSignInState.value) && (isLoginScreenSetUp)) {
//                isLastUpdatedValue = repository.userSignInState.value
//                isAdminHomeScreenSetUp = false
//                isAllreadyOnHome = false
//                navigateToHome(navHostController = navHostController)
//            }
//            isLoginScreenSetUp = true
//        }
//
//        false -> {
//            MainActivity.adminDBRepo.resetLoggedInUser()
//            showLoginScreen(navHostController, repository)
//            isLoginScreenSetUp = true
//        }
//    }
//}
//
//@Composable
//fun showUserNotFoundAlert(withTitle : String){
//    AlertDialog(
//        onDismissRequest = { },
//        text = {
//            Column( Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(withTitle,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold)
//            }
//        },
//        confirmButton = { Button(
//            onClick = {
//                MainActivity.authRepo.updateWrongOTP(false)
//                MainActivity.authRepo.updateWrongPassword(false)
//                MainActivity.authRepo.updateEmailNotFound(false)
//                MainActivity.authRepo.updateWrongUserName(false)
//                      },
//            modifier = Modifier.fillMaxWidth()
//        ){Text("OK",
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center)}
//        },
//        dismissButton = null
//    )
//}
//
//@ExperimentalMaterial3Api
//@Composable
//fun showLoginScreen(navHostController: NavHostController, authRepository: AuthRepository){
//
//    var isLoading by remember { mutableStateOf(false) }
//    var otpVisiblePhone by remember { mutableStateOf(false) }
//    var otpVisibleEmail by remember { mutableStateOf(false) }
//    // Change for Sonar Test
//    var pin by remember { mutableStateOf("") }
//    var otp by remember { mutableStateOf("") }
//
//    //Change for Sonar Test
//    var email by remember { mutableStateOf("")}
//    var emailError by remember { mutableStateOf("")}
//    var pinError by remember { mutableStateOf("")}
//    var otpError by remember { mutableStateOf("")}
//
//    var phoneError by remember { mutableStateOf("")}
//    var isEmailValid by remember { mutableStateOf(true) }
//    var isPINValid by remember { mutableStateOf(true) }
//    var isOTPValid by remember { mutableStateOf(true) }
//
//    var tabIndex by remember { mutableStateOf(0) }
//    //Change for Sonar Test
//    var phone by remember { mutableStateOf("") }
//    var isPhoneValid by remember { mutableStateOf(true) }
//
//    //msg will come when user press continue button
//    var userNotFoundAlert by remember { mutableStateOf(false) }
//    var wrongPINAlert by remember { mutableStateOf(false) }
//    var wrongOTPAlert by remember { mutableStateOf(false) }
//    var isOTPTimerRunning by remember { mutableStateOf(false) }
//
//    var isPhoneEmpty by remember { mutableStateOf(false) }
//    var isEmailEmpty by remember { mutableStateOf(false) }
//    var isPINEmpty by remember { mutableStateOf(false) }
//    var isOTPEmpty by remember { mutableStateOf(false) }
//
//    val countdown = remember { mutableStateOf(30) }
//
//    val textFieldFocusManager = LocalFocusManager.current
//
//    val countdownRunning = remember { mutableStateOf(true) } // Track if countdown is running
//
//    var passwordVisible by remember { mutableStateOf(true) }
//
//    when{
//        authRepository.wrongUsername.value -> {
//            showUserNotFoundAlert("Wrong Email")
//            isLoading = false
//        }
//
//        authRepository.noEmailFound.value -> {
//            showUserNotFoundAlert("There is no user linked with given Phone")
//            isLoading = false
//        }
//
//        authRepository.wrongPassword.value -> {
//            showUserNotFoundAlert("Please enter correct password")
//            isLoading = false
//        }
//
//        authRepository.allReadyLoggedIn.value -> {
//            navHostController.navigate(Destination.Home.routes)
//        }
//
//        authRepository.signInOTPSent.value -> {
//            authRepository.updateSignInOTPState(false)
//            isLoading = false
//            when(tabIndex){
//                0 -> {
//                    otpVisiblePhone = true
//                }
//                1 -> {
//                    otpVisibleEmail = true
//                }
//            }
//        }
//
//        authRepository.wrongOTP.value -> {
//            isLoading = false
//            showUserNotFoundAlert("Please enter correct OTP")
//        }
//    }
//
//    if (wrongPINAlert) {
//        AlertDialog(
//            onDismissRequest = { wrongPINAlert = false },
//            text = {
//                Column( Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Your PIN is wrong.",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold)
//                }
//            },
//            confirmButton = { Button(
//                onClick = { wrongPINAlert = false },
//                modifier = Modifier.fillMaxWidth()
//            ){Text("OK",fontSize = 16.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center)}
//            },
//            dismissButton = null
//        )
//    }
//
//    if (wrongOTPAlert) {
//        AlertDialog(
//            onDismissRequest = { wrongOTPAlert = false },
//            text = {
//                Column( Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text("Wrong OTP",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold)
//                }
//            },
//            confirmButton = { Button(
//                onClick = { wrongOTPAlert = false },
//                modifier = Modifier.fillMaxWidth()
//            ){Text("OK",fontSize = 16.sp,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center)}
//            },
//            dismissButton = null
//        )
//    }
//
//    Box {
//
//        if(isLoading) showProgress()
//
//        Column(
//            Modifier
//                .fillMaxSize()
//                .padding(horizontal = 40.dp, vertical = 30.dp)
//                .alpha(if (isLoading) 0.08f else 1.0f),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ){
//            if(authRepository.userSignInState.value) isLoading = false
//            //#change12May
//            Spacer(modifier = Modifier.height(30.dp))
//            AppLogo()
//            //#change12May
//            Spacer(modifier = Modifier.height(40.dp))
//            Box(modifier = Modifier.fillMaxWidth()) {
//                TabRow(tabIndex,
//                    modifier = Modifier.fillMaxWidth(),
//                    indicator = { tabPositions ->
//                        TabRowDefaults.Indicator(
//                            color = Color(0XFF397EF5),
//                            height = 2.dp,
//                            modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex])
//                        )
//                    }
//                ) {
//                    listOfTab.forEachIndexed { index, text ->
//                        Tab(
//                            selected = tabIndex == index,
//                            onClick = {
//                                textFieldFocusManager.clearFocus()
//                                tabIndex = index},
//                            modifier = Modifier
//                                .height(55.dp)
//                                .background(
//                                    color = if (tabIndex == index) Color(0XFF397EF5) else Color.Transparent,
//                                    shape = RoundedCornerShape(5.dp)
//                                ),
//                            enabled = when{
//                                otpVisiblePhone -> false
//                                otpVisibleEmail -> false
//                                else -> true
//                            }
//                        ){
//                            Text(text, color = if (tabIndex==index) Color.White else Color.Black)
//                        }
//                    }
//
//                }
//            }
//            Spacer(modifier = Modifier.height(15.dp))
//            when (tabIndex){
//                0-> {
//                    OutlinedTextField(
//                        value = phone,
//                        onValueChange = { newValue ->
//                            phone = newValue.take(10)
//
//                            isPhoneValid = phone.length == 10
//                            // update isPhoneEmpty
//                            isPhoneEmpty = phone.isEmpty()
//
////                            if (newValue.length <= 10) { // allow up to 13 digits
////                                // remove spaces and update mobileNumber
////                                phone = newValue.filter { !it.isWhitespace() }
////                                // validate mobileNumber based on the new value
////                                isPhoneValid = phone.length == 10
////                                // update isPhoneEmpty
////                                isPhoneEmpty = phone.isEmpty()
////                            }
//                        },
//                        label = { Text(text = "Phone") },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .width(55.dp)
//                            .testTag("loginPhone"),
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            keyboardType = KeyboardType.Phone,
//                            imeAction = ImeAction.Done
//                        ), isError = (!isPhoneValid || isPhoneEmpty),
//                        singleLine = true,
//                        enabled = !otpVisiblePhone
//                    )
//
//                    phoneError = when(isPhoneValid){
//                        true -> ""
//                        //#change12May
//                        false -> "Please enter 10 digit phone number."
//                    }
//
//                    if((!isPhoneValid && !isPhoneEmpty) ) Box(modifier = Modifier.fillMaxWidth()){
//                        Text(
//                            text = phoneError,
//                            color = Color.Red,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp,
//                        )
//                    }
//                    if(isPhoneEmpty) Box(modifier = Modifier.fillMaxWidth()){
//                        Text(
//                            text = "Enter Phone No.",
//                            color = Color.Red,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp,
//                        )
//                    }
//                }
//                1-> {
//                    OutlinedTextField(
//                        value = email,
//                        onValueChange = {
//                            email = it
//                            isEmailValid = isValidEmail(it)
//                            isEmailEmpty = email.isEmpty()
//                             },
//                        label = { Text("Email")},
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .width(55.dp),
//                        keyboardOptions = KeyboardOptions.Default.copy(
//                            keyboardType = KeyboardType.Email,
//                            imeAction = ImeAction.Done ),
//                        isError = (!isEmailValid || isEmailEmpty),
//                        singleLine = true,
//                        enabled = !otpVisibleEmail
//                    )
//
//                    when{
//                        !isEmailValid -> {
//                            //#change12May
//                            emailError ="Please enter email in format abc@xyz.pqr"
//                        }
//                    }
//
//                    if(!isEmailValid && !isEmailEmpty) Box(modifier = Modifier.fillMaxWidth()){
//                        Text(
//                            text = emailError,
//                            color = Color.Red,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp,
//                        )
//                    }
//                    if(isEmailEmpty) Box(modifier = Modifier.fillMaxWidth()){
//                        Text(
//                            text = "Enter Email",
//                            color = Color.Red,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp,
//                        )
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(5.dp))
//
//            OutlinedTextField(value =
//            when (tabIndex) {
//                0 -> if (otpVisiblePhone) otp else pin
//                1 -> if (otpVisibleEmail) otp else pin
//                else -> ""
//            },
//                onValueChange = { newValue ->
//                    when (tabIndex) {
//                        0 -> if (otpVisiblePhone) {
//                            otp = newValue.take(6) // truncate to 6 characters
//                            isOTPValid = otp.length == 6
//                            isOTPEmpty = otp.isEmpty()
//                        } else {
//                            pin = newValue.take(6) // truncate to 6 characters
//                            isPINValid = pin.length == 6
//                            isPINEmpty = pin.isEmpty()
//                        }
//                        1 -> if (otpVisibleEmail) {
//                            otp = newValue.take(6) // truncate to 6 characters
//                            isOTPValid = otp.length == 6
//                            isOTPEmpty = otp.isEmpty()
//                        } else {
//                            pin = newValue.take(6) // truncate to 6 characters
//                            isPINValid = pin.length == 6
//                            isPINEmpty = pin.isEmpty()
//                        }
//                        else -> {
//                            ""
//                        }
//                    }
//                },
//                label = {
//                    Text(
//                        text = when (tabIndex){
//                            0 -> if (otpVisiblePhone) "OTP" else "PIN"
//                            1 -> if (otpVisibleEmail) "OTP" else "PIN"
//                            else -> ""
//                        }
//                    )
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .testTag("pinOtp"),
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    keyboardType = KeyboardType.Number,
//                    imeAction = ImeAction.Done),
//                isError =
//                when (tabIndex) {
//                    0 -> if (otpVisiblePhone) {!isOTPValid || isOTPEmpty} else {!isPINValid || isPINEmpty}
//                    1 -> if (otpVisibleEmail) {!isOTPValid || isOTPEmpty} else {!isPINValid || isPINEmpty}
//                    else -> false
//                },
//                trailingIcon = {
//                    when (tabIndex) {
//                        0 -> if(!otpVisiblePhone) {IconButton(onClick = { passwordVisible = !passwordVisible })
//                        {Icon(imageVector = ImageVector.vectorResource(id =
//                        if (passwordVisible) R.drawable.on_visibility else R.drawable.off_visiblility),
//                            contentDescription = if (passwordVisible) "Show Password" else "Hide Password")
//                        }
//                        }
//                        1 -> if(!otpVisibleEmail) {IconButton(onClick = { passwordVisible = !passwordVisible })
//                        {Icon(imageVector = ImageVector.vectorResource(id =
//                        if (passwordVisible) R.drawable.on_visibility else R.drawable.off_visiblility),
//                            contentDescription = if (passwordVisible) "Show Password" else "Hide Password")
//                        }
//                        }
//                        else -> null
//                    }
//                },
//                visualTransformation = when (tabIndex) {
//                    0 -> if (passwordVisible && !otpVisiblePhone) PasswordVisualTransformation() else VisualTransformation.None
//                    1 -> if (passwordVisible && !otpVisibleEmail) PasswordVisualTransformation() else VisualTransformation.None
//                    else -> null
//                }?: VisualTransformation.None
//            )
//
//            otpError = when(isOTPValid){
//                true -> ""
//                //#change12May
//                false -> "Please enter a 6 digit OTP."
//            }
//
//            pinError = when(isPINValid){
//                true -> ""
//                //#change12May
//                false -> "Please enter your 6 digit PIN code."
//            }
//
//            Box(modifier = Modifier.fillMaxWidth()){
//                when (tabIndex) {
//                    0 -> if(otpVisiblePhone){
//                        Text(text = when{
//                            (!isOTPValid && !isOTPEmpty) -> otpError
//                            isOTPEmpty -> "Enter OTP"
//                            else -> ""
//                        },
//                            color = Color.Red,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp)
//                    }else{
//                        Text(text = when{
//                            (!isPINValid && !isPINEmpty) -> pinError
//                            isPINEmpty -> "Enter PIN"
//                            else -> ""
//                        },
//                            color = Color.Red,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp)
//                    }
//                    1 -> if(otpVisibleEmail){
//                        Text(text = when{
//                            (!isOTPValid && !isOTPEmpty) -> otpError
//                            isOTPEmpty -> "Enter OTP"
//                            else -> ""
//                        },
//                            color = Color.Red,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp)
//                    }else{
//                        Text(text = when{
//                            (!isPINValid && !isPINEmpty) -> pinError
//                            isPINEmpty -> "Enter PIN"
//                            else -> ""
//                        },
//                            color = Color.Red,
//                            fontStyle = FontStyle.Italic,
//                            fontSize = 12.sp
//                        )
//                    }
//                    else -> ""
//                }}
//
//            Box(Modifier.fillMaxWidth(), Alignment.CenterEnd) {
//                when(tabIndex){
//
//                    0 -> {
//                        when(otpVisiblePhone){
//                            true -> {
//                                if (countdown.value > 0) {
//                                    isOTPTimerRunning = true
//                                    Spacer(modifier = Modifier.height(20.dp))
//                                    Text(text = "Resend OTP in ${countdown.value}",
//                                        style = TextStyle(fontSize = 16.sp),
//                                        color = Color(0xFF397EF5),
//                                        fontWeight = FontWeight.Bold)
//                                } else {
//                                    isOTPTimerRunning = false
//                                    TextButton(onClick = {
//                                        MainActivity.adminDBRepo.getPhoneOTP(phone)
//                                        countdownRunning.value = true
//                                        countdown.value = 30 }) {
//                                        Text(text = "Resend OTP",
//                                            style = TextStyle(fontSize = 16.sp),
//                                            color = Color(0xFF397EF5),
//                                            fontWeight = FontWeight.Bold)
//                                    }
//                                }
//                                LaunchedEffect(countdown.value) { // Change Unit to countdown.value
//                                    while (countdown.value > 0) {
//                                        delay(1000)
//                                        if (countdownRunning.value) { // Only decrement countdown if it is running
//                                            countdown.value--
//                                        }
//                                    }
//                                    countdownRunning.value = false // Reset the countdown running state after countdown finishes
//                                }
//                            }
//                            false -> {
//                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                                    TextButton(onClick = {
//                                        authRepository.updateEmailNotFound(false)
//                                        navHostController.navigate(Destination.ForgotPasswordScreen.routes) }) {
//                                        Text("Set PIN",color = Color(0XFF397EF5))
//                                    }
//                                    TextButton(onClick = {
//                                        authRepository.updateEmailNotFound(false)
//                                        navHostController.navigate(Destination.ForgotPasswordScreen.routes) }) {
//                                        Text("Forgot PIN?",color = Color(0XFF397EF5))
//                                    }
//                                }
////                                TextButton(onClick = {
////                                    authRepository.updateEmailNotFound(false)
////                                    navHostController.navigate(Destination.ForgotPasswordScreen.routes) }) {
////                                    Text("Forgot PIN?",color = Color(0XFF397EF5))
////                                }
//                            }
//                        }
//                    }
//
//                    1 -> {
//                        when(otpVisibleEmail){
//                            true -> {
//                                if (countdown.value > 0) {
//                                    isOTPTimerRunning = true
//                                    Spacer(modifier = Modifier.height(10.dp))
//                                    Text(text = "Resend OTP in ${countdown.value}",
//                                        style = TextStyle(fontSize = 16.sp),
//                                        color = Color(0xFF397EF5),
//                                        fontWeight = FontWeight.Bold)
//                                } else {
//                                    isOTPTimerRunning = false
//                                    TextButton(onClick = {
//                                        authRepository.getSignInEmailOTP(email)
//                                        countdownRunning.value = true
//                                        countdown.value = 10 }) {
//                                        Text(text = "Resend OTP",
//                                            style = TextStyle(fontSize = 16.sp),
//                                            color = Color(0xFF397EF5),
//                                            fontWeight = FontWeight.Bold)
//                                    }
//                                }
//
//                                LaunchedEffect(countdown.value) { // Change Unit to countdown.value
//                                    while (countdown.value > 0) {
//                                        delay(1000)
//                                        if (countdownRunning.value) { // Only decrement countdown if it is running
//                                            countdown.value--
//                                        }
//                                    }
//                                    countdownRunning.value = false // Reset the countdown running state after countdown finishes
//                                }
//                            }
//                            false -> {
//                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                                    TextButton(onClick = {
//                                        authRepository.updateEmailNotFound(false)
//                                        navHostController.navigate(Destination.ForgotPasswordScreen.routes) }) {
//                                        Text("Set PIN",color = Color(0XFF397EF5))
//                                    }
//                                    TextButton(onClick = {
//                                        authRepository.updateEmailNotFound(false)
//                                        navHostController.navigate(Destination.ForgotPasswordScreen.routes) }) {
//                                        Text("Forgot PIN?",color = Color(0XFF397EF5))
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//            Button(onClick = {
//                when(tabIndex) {
//
//                    0 -> { // PhoneLogin
//                        when (otpVisiblePhone) {
//
//                            true -> {
//                                if (otp.isEmpty()) {
//                                    isOTPEmpty = true
//                                }else if((isPhoneValid && isOTPValid)){
//                                    isLoading = true
//                                    authRepository.updateWrongOTP(false)
//                                    authRepository.confirmSignInWithOTP(otp)
//                                }
//                            }
//                            false -> {
//                                if (phone.isEmpty() || pin.isEmpty()) {
//                                    if (phone.isEmpty()) {
//                                        isPhoneEmpty = true
//                                    }
//                                    if (pin.isEmpty()) {
//                                        isPINEmpty = true
//                                    }
//                                } else if (isPhoneValid && isPINValid) {
//                                    isLoading = true
//                                    authRepository.updateEmailNotFound(false)
//                                    authRepository.updateWrongPassword(false)
//                                    authRepository.updateWrongUserName(false)
//                                    authRepository.updateIsAllReadyLoggedIn(false)
//                                    authRepository.signInWithPhonePassword(phone, pin)
//                                }
//                            }
//                        }
//                    }
//
//                    1 -> { // EmailLogin
//                        when (otpVisibleEmail) {
//                            true -> {
//                                if (otp.isEmpty()) {
//                                    isOTPEmpty = true
//                                } else {
//                                    isLoading = true
//                                    authRepository.updateWrongOTP(false)
//                                    authRepository.confirmSignInWithOTP(otp)
//                                }
//                            }
//                            false -> {
//                                if (email.isEmpty() || pin.isEmpty()) {
//                                    if (email.isEmpty()) {
//                                        isEmailEmpty = true
//                                    }
//                                    if (pin.isEmpty()) {
//                                        isPINEmpty = true
//                                    }
//                                } else {
//                                    authRepository.updateEmailNotFound(false)
//                                    authRepository.updateWrongPassword(false)
//                                    authRepository.updateWrongUserName(false)
//                                    authRepository.updateIsAllReadyLoggedIn(false)
//                                    isLoading = true
//                                    authRepository.signInWithEmailPassword(email, pin)
//                                }
//                            }
//                        }
//                    }
//                }
//            },
//                colors = ButtonDefaults.buttonColors(Color(0xFF397EF5)),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(55.dp)
//                    .alpha(
//                        when (tabIndex) {
//                            0 -> if (isPhoneValid) 1.0f else 0.7f
//                            1 -> if (isEmailValid) 1.0f else 0.7f
//                            else -> {
//                                0.7f
//                            }
//                        }
//                    )
//                    .testTag("continue"),
//                shape = RoundedCornerShape(5.dp))
//            {
//                Text(text = "Continue",
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White)
//
//            }
//            Spacer(modifier = Modifier.height(15.dp))
//            orLine()
//            Spacer(modifier = Modifier.height(15.dp))
//
//            Button(onClick = {
//
//                when(tabIndex){
//                    0 -> {
//
//                        when(otpVisiblePhone){
//                            true -> {
//                                isOTPEmpty = false
//                                isOTPValid = true
//                                otpVisiblePhone = !otpVisiblePhone
//                            }
//
//                            false -> {
//                                isLoading = true
//                                authRepository.updateSignInOTPState(false)
//                                MainActivity.adminDBRepo.getPhoneOTP(phone)
//                                authRepository.updateEmailNotFound(false)
//                                authRepository.updateWrongPassword(false)
//                                authRepository.updateWrongUserName(false)
//                                authRepository.updateIsAllReadyLoggedIn(false)
//                            }
//                        }
//                    }
//                    1 -> {
//
//                        when(otpVisibleEmail){
//                            true -> {
//                                isOTPEmpty = false
//                                isOTPValid = true
//                                otpVisibleEmail = !otpVisibleEmail
//                            }
//
//                            false -> {
//                                isLoading = true
//                                authRepository.getSignInEmailOTP(email)
//                                authRepository.updateSignInOTPState(false)
//                                authRepository.updateEmailNotFound(false)
//                                authRepository.updateWrongPassword(false)
//                                authRepository.updateWrongUserName(false)
//                                authRepository.updateIsAllReadyLoggedIn(false)
//                            }
//                        }
//                    }}
//            },
//                colors = ButtonDefaults.buttonColors(Color(0xFF2C3E50)),
//                shape = RoundedCornerShape(5.dp),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(55.dp)
//                    .alpha(
//                        when (tabIndex) {
//                            0 -> if (isPhoneValid && phone.isNotEmpty()) 1.0f else 0.7f
//                            1 -> if (isEmailValid && email.isNotEmpty()) 1.0f else 0.7f
//                            else -> {
//                                0.7f
//                            }
//                        }
//                    ),
//                enabled = when (tabIndex) {
//                    0 -> isPhoneValid && phone.isNotEmpty()
//                    1 -> isEmailValid && email.isNotEmpty()
//                    else -> {false
//                    }
//                }
//
//            ) {
//                Text(when(tabIndex){
//                    0 -> if (otpVisiblePhone)"Continue with PIN" else "Continue with OTP"
//                    1 -> if (otpVisibleEmail)"Continue with PIN" else "Continue with OTP"
//                    else -> {
//                        ""
//                    }
//                },
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White)
//            }
//        }
//    }
//}
//
//@Composable
//fun navigateToHome(navHostController : NavHostController){
//    navHostController.navigate(Destination.Home.routes)
//}
//
//@Composable
//fun navigateToLogin(navHostController: NavHostController){
//    navHostController.navigate(Destination.Login.routes)
//}
//
//
//@Composable
//fun AppLogo() {
//    Row(modifier = Modifier
//        .height(40.dp)
//        .fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically) {
//        Image(
//            painter = painterResource(id = R.drawable.applogo),
//            contentDescription = "logo"
//        )
//    }
//
////    Box(
////        contentAlignment = Alignment.Center
////    ) {
////        Image(
////            painter = painterResource(id = R.drawable.app_logo),
////            contentDescription = "logo"
////        )
////    }
//}
//
//
//@Composable
//fun splashLogo(){
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        AppLogo()
//    }
//}
//
//
//
//fun isValidEmail(email: String): Boolean {
//    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
//}
//
//@Composable
//fun orLine(){
//    Row(Modifier.fillMaxWidth(),
//        Arrangement.SpaceBetween,
//        Alignment.CenterVertically) {
//        Divider(
//            modifier = Modifier
//                .weight(1f)
//                .height(1.dp)
//        )
//        Text(
//            text = "or",
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//        Divider(
//            modifier = Modifier
//                .weight(1f)
//                .height(1.dp)
//        )
//    }
//}
//
//@Composable
//fun showProgress(){
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        CircularProgressIndicator(modifier = Modifier
//            .size(200.dp) // set the size of the progress circle
//            .padding(16.dp)
//            .zIndex(1f),
//            color = Color.Gray, // set the color of the progress circle
//            strokeWidth = 6.dp // set the thickness of the progress circle
//        )
//    }
//}







