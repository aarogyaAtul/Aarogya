package com.aarogyaforworkers.aarogya.composeScreens

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.aarogyaforworkers.aarogya.Auth.AuthRepository
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.MainActivity
import com.aarogyaforworkers.aarogya.R


var lastForgotPasswordOTPState = false

@ExperimentalMaterial3Api
@Composable
fun ForgotPasswordScreen(navHostController: NavHostController, repository: AuthRepository) {

    var isLoading by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("")}

    var isEmailEmpty by remember { mutableStateOf(false) }

    var isEmailValid by remember { mutableStateOf(true) }

    if(repository.noEmailFound.value) {
        isLoading = false
        showUserNotFoundAlert(withTitle = "No user found with given email")
    }

    when(val newForgotPasswordOTPState = repository.forgotPasswordOTPState.value){
        true -> {
            PasswordResetScreenObjects.shared.email = ForgotPasswordObjects.shared.email
            if(lastForgotPasswordOTPState != newForgotPasswordOTPState){
                isLoading = false
                navHostController.navigate(Destination.PasswordReset.routes)
            }
            lastForgotPasswordOTPState = newForgotPasswordOTPState
        }
        false -> {
            lastForgotPasswordOTPState= newForgotPasswordOTPState
            BackBtn(navHostController)

            Box() {
                if(isLoading) showProgress()

                Column(
                    Modifier
                        .alpha(if (isLoading) 0.08f else 1.0f)
                        .fillMaxSize()) {
                    Row(Modifier.height(200.dp), verticalAlignment = Alignment.CenterVertically) {
                        AppLogo()
                    }
                    Column(Modifier.padding(horizontal = 15.dp), verticalArrangement = Arrangement.Center) {
                        AuthTextField(
                            textInput = email,
                            onChangeInput = {
                                email = it
                                ForgotPasswordObjects.shared.email = it
                                isEmailValid = isValidEmail(it)
                                isEmailEmpty = email.isEmpty()
                            },
                            labelText = "Email",
                            keyboard = KeyboardType.Email,
                            error = (!isEmailValid || isEmailEmpty),
                            TestTag = "tagEmail"
                        )

                        when{
                            (!isEmailValid && !isEmailEmpty)-> ErrorMessage(errorMessage = "Please enter email in format abc@xyz.pqr", errorTestTag = "tagEmailInvalid")
                            (isEmailEmpty)-> ErrorMessage(errorMessage = "Enter email", errorTestTag = "tagEmailEmpty")
                        }
                        Spacer(modifier = Modifier.height(15.dp))

                        AuthBtn(btnName = "Continue",
                            enable = isEmailValid && email.isNotEmpty(),
                            btnColor = Color(0XFF397EF5)) {
                            isLoading = true
                            MainActivity.authRepo.updatePasswordResetState(false)
                            repository.forgotPassword(email)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BackBtn(navHostController: NavHostController){
    IconButton(onClick = { navHostController.navigate(Destination.Login.routes) }, modifier = Modifier.size(36.dp)) {
        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon), contentDescription = "Logout")
    }
}

class ForgotPasswordObjects{
    var email = ""
    companion object{
        val shared = ForgotPasswordObjects()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ForgotPasswordScreenView(){
    ForgotPasswordScreen(navHostController = rememberNavController(), repository = AuthRepository())
}

//var lastForgotPasswordOTPState = false
//
//@ExperimentalMaterial3Api
//@Composable
//fun ForgotPasswordScreen(navHostController: NavHostController, repository: AuthRepository) {
//
//    var isLoading by remember { mutableStateOf(false) }
//
//    var email by remember { mutableStateOf("")}
//
//    var isEmailEmpty by remember { mutableStateOf(false) }
//
//    var isEmailValid by remember { mutableStateOf(true) }
//
//    if(repository.noEmailFound.value) {
//        isLoading = false
//        showUserNotFoundAlert(withTitle = "No user found with given email")
//    }
//
//    when(val newForgotPasswordOTPState = repository.forgotPasswordOTPState.value){
//        true -> {
//            PasswordResetScreenObjects.shared.email = ForgotPasswordObjects.shared.email
//            if(lastForgotPasswordOTPState != newForgotPasswordOTPState){
//                isLoading = false
//                navHostController.navigate(Destination.PasswordReset.routes)
//            }
//            lastForgotPasswordOTPState = newForgotPasswordOTPState
//        }
//        false -> {
//            lastForgotPasswordOTPState= newForgotPasswordOTPState
//            BackBtn(navHostController)
//            Column(
//                Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 40.dp, vertical = 30.dp).testTag("ForgotPasswordScreen"),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ){
//                //#change12May
//                Spacer(modifier = Modifier.height(40.dp))
//                AppLogo()
//                Spacer(modifier = Modifier.height(50.dp))
//                OutlinedTextField(
//                    value = email,
//                    onValueChange = {
//                        email = it
//                        ForgotPasswordObjects.shared.email = it
//                        isEmailValid = isValidEmail(it)
//                        isEmailEmpty = email.isEmpty()
//                    },
//                    label = { Text("Email")},
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .width(55.dp).testTag("forgotEmail"),
//                    keyboardOptions = KeyboardOptions.Default.copy(
//                        keyboardType = KeyboardType.Email,
//                        imeAction = ImeAction.Done ),
//                    isError = (!isEmailValid || isEmailEmpty),
//                    singleLine = true
//                )
//                Box(modifier = Modifier.fillMaxWidth()){
//                    Text(text =
//                    when{
//                        (!isEmailValid && !isEmailEmpty) -> "Please enter email in format abc@xyz.pqr"
//                        isEmailEmpty -> "Enter Email"
//                        else -> ""
//                    },
//                        color = Color.Red,
//                        fontStyle = FontStyle.Italic,
//                        fontSize = 12.sp,)
//                }
//                Spacer(modifier = Modifier.height(15.dp))
//                Button(onClick = {
//                    if (email.isEmpty()) {
//                        isEmailEmpty = true
//                    } else {
//                        isLoading = true
//                        repository.forgotPassword(email)
//                    } },
//                    colors = ButtonDefaults.buttonColors(Color(0xFF2C3E50)),
//                    shape = RoundedCornerShape(5.dp),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(55.dp).testTag("forgotclick")) {
//                    Text("Continue",
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White)
//                }
//            }
//            if(isLoading) showProgress()
//        }
//    }
//
//}
//
//@Composable
//fun BackBtn(navHostController: NavHostController){
//    IconButton(onClick = { navHostController.navigate(Destination.Login.routes) }, modifier = Modifier.size(36.dp)) {
//        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.back_btn_icon), contentDescription = "Logout")
//    }
//}
//
//class ForgotPasswordObjects{
//    var email = ""
//    companion object{
//        val shared = ForgotPasswordObjects()
//    }
//}