package com.aarogyaforworkers.aarogya.composeScreens

import Commons.ConnectionPageTags
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.aarogyaforworkers.aarogya.Destination
import com.aarogyaforworkers.aarogya.Omron.OmronRepository
import com.aarogyaforworkers.aarogya.PC300.PC300Repository
import com.aarogyaforworkers.aarogya.checkBluetooth
import com.aarogyaforworkers.aarogya.composeScreens.Models.Device
import com.aarogyaforworkers.aarogya.isBluetoothEnabled

var isFromUserHome = false

@SuppressLint("MissingPermission")
@Composable
fun DevicesConnectionScreen(navHostController: NavHostController, pC300Repository: PC300Repository, omronRepository: OmronRepository){
    val context = LocalContext.current
    var bleEnabled by remember { mutableStateOf(isBluetoothEnabled()) }
    if(!bleEnabled) checkBluetooth(context)
    var isDisconnecting by remember { mutableStateOf(false) }
    val oDevice = omronRepository.connectedOmronDevice.value
    val omronDevice = Device("Omron", oDevice?.localName?.takeLast(14) ?:  "", oDevice?.address ?:  "", omronRepository.connectedOmronDevice.value != null)
    val pDevice = pC300Repository.connectedPC300Device.value
    val pc300Deice = Device("PC300", pDevice?.name ?:  "", pDevice?.address ?:  "", pC300Repository.connectedPC300Device.value != null)

    Column(modifier = Modifier.fillMaxSize().testTag(ConnectionPageTags.shared.connectionScreen)) {
        Spacer(modifier = Modifier.height(10.dp))
        BackBtn { navHostController.navigate(if(isFromUserHome) Destination.UserHome.routes else Destination.Home.routes) }
        Spacer(modifier = Modifier.height(30.dp))
        // action card for PC300 connect disconnect
        ConnectionCard(device = pc300Deice, ConnectionPageTags.shared.pc300Card) {isConnectedPc300->
            when(isConnectedPc300){
                true -> { // Disconnect
                    isDisconnecting = true
                    pC300Repository.clearPC300()
                    pC300Repository.disConnectPC300Device()
                }

                false -> {
                    isPc300 = true
                    isDisconnecting = false
                    pC300Repository.scanPC300Device()
                    navHostController.navigate(Destination.DeviceList.routes)
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        // action card for Omron connect disconnect
        ConnectionCard(device = omronDevice, ConnectionPageTags.shared.omronCard) {isConnectedOmron->
            when(isConnectedOmron){
                true -> {
                    navHostController.navigate(Destination.DeviceList.routes)
                }

                false -> {
                    isPc300 = false
                    omronRepository.resetOmronDevice()
                    omronRepository.startScan()
                    navHostController.navigate(Destination.DeviceList.routes)
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
    }
}
