package com.aarogyaforworkers.aarogya.composeScreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.Auth.AuthRepository
import com.aarogyaforworkers.aarogya.Commons.*
import com.aarogyaforworkers.aarogya.Destination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Timer
import java.util.TimerTask


@Composable
fun SplashScreen(navHostController: NavHostController, repository: AuthRepository) {
    val timer = Timer()
    startTimer(timer = timer, repository = repository, navHostController)
    val updatedValue = repository.userSignInState.value
    splashLogo()
    when(repository.userSignInState.value){
        true -> {
            if(lastUpdatedSignInValue != updatedValue){
                if(isSplashScreenSetup) stopTimer(timer)
                lastUpdatedSignInValue = updatedValue
                navigateToHome(navHostController = navHostController)
            }
            if(!isSplashScreenSetup) isSplashScreenSetup = true
        }
        false -> {
            if(lastUpdatedSignInValue != updatedValue){
                if(isSplashScreenSetup) stopTimer(timer)
                lastUpdatedSignInValue = updatedValue
                navigateToLogin(navHostController = navHostController)
            }
            if(!isSplashScreenSetup) isSplashScreenSetup = true
        }
    }
}

fun stopTimer(timer: Timer){
    isTimerStopped = true
    timer.cancel()
}

fun startTimer(timer: Timer, repository: AuthRepository, navHostController: NavHostController) {
    var splashTime = 10
    timer.apply {
        scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if(splashTime - 1 == 0){
                    CoroutineScope(Dispatchers.Main).launch {
                        if (!isTimerStopped) {
                            navHostController.navigate(Destination.Login.routes)
                        }
                        withContext(Dispatchers.Default) {
                            if(!isTimerStopped)stopTimer(timer)
                        }
                    }
                } else if(splashTime == 6){
                    splashTime -= 1
                    repository.isUserSignedIn()
                }else{
                    splashTime -= 1
                }

            }
        }, 0, 1000)
    }
}
