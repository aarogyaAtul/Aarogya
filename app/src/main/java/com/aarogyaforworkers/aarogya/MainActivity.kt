@file:OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvMaterial3Api::class)

package com.aarogyaforworkers.aarogya
import android.Manifest
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.aarogyaforworkers.aarogya.AdminDB.AdminDBRepository
import com.aarogyaforworkers.aarogya.Auth.AuthRepository
import com.aarogyaforworkers.aarogya.Camera.CameraRepository
import com.aarogyaforworkers.aarogya.Commons.selectedEcg
import com.aarogyaforworkers.aarogya.CsvGenerator.CsvRepository
import com.aarogyaforworkers.aarogya.Location.LocationRepository
import com.aarogyaforworkers.aarogya.MediaPlayer.PlayerRepo
import com.aarogyaforworkers.aarogya.Omron.OmronRepository
import com.aarogyaforworkers.aarogya.PC300.PC300Repository
import com.aarogyaforworkers.aarogya.S3.S3Repository
import com.aarogyaforworkers.aarogya.Session.SessionStatusRepo
import com.aarogyaforworkers.aarogya.SubUser.SubUserDBRepository
import com.aarogyaforworkers.aarogya.composeScreens.AddNewUserScreen
import com.aarogyaforworkers.aarogya.composeScreens.AdminProfileScreen
import com.aarogyaforworkers.aarogya.composeScreens.CameraScreen
import com.aarogyaforworkers.aarogya.composeScreens.ConfirmAdminSignInScreen
import com.aarogyaforworkers.aarogya.composeScreens.DevicesConnectionScreen
import com.aarogyaforworkers.aarogya.composeScreens.EditTextScreen
import com.aarogyaforworkers.aarogya.composeScreens.ForgotPasswordScreen
import com.aarogyaforworkers.aarogya.composeScreens.GraphScreen
import com.aarogyaforworkers.aarogya.composeScreens.HomeScreen
import com.aarogyaforworkers.aarogya.composeScreens.LoginScreen
import com.aarogyaforworkers.aarogya.composeScreens.NearByDeviceListScreen
import com.aarogyaforworkers.aarogya.composeScreens.PasswordResetScreen
import com.aarogyaforworkers.aarogya.composeScreens.RadioButtonHistoryScreen
import com.aarogyaforworkers.aarogya.composeScreens.SessionSummaryScreen
import com.aarogyaforworkers.aarogya.composeScreens.SplashScreen
import com.aarogyaforworkers.aarogya.composeScreens.UserHomeScreen
import com.aarogyaforworkers.aarogya.composeScreens.UserSessionHistoryScreen
import com.aarogyaforworkers.aarogya.storage.Local.LocalSessionDBManager
import com.aarogyaforworkers.aarogya.ui.theme.AarogyaTheme
import com.google.firebase.analytics.FirebaseAnalytics

private const val ENABLE_BLUETOOTH_REQUEST_CODE = 1

private const val REQUEST_PERMISSIONS = 1003

sealed class Destination(val routes : String){
    object Splash: Destination("Splash")
    object Home: Destination("Home")
    object Login : Destination("Login")
    object ForgotPasswordScreen : Destination("ForgotPassword")
    object UserHome : Destination("UserHome")
    object PasswordReset : Destination("PasswordReset")
    object ConfirmAdminSignIn : Destination("ConfirmAdminSignIn")
    object AddNewUser : Destination("AddNewUser")
    object AdminProfile : Destination("AdminProfile")
    object Camera : Destination("Camera")
    object DeviceList : Destination("DeviceList")
    object SessionHistory : Destination("Session")
    object Graphs : Destination("Graphs")
    object DeviceConnection : Destination("DeviceConnection")
    object SessionSummary : Destination("SessionSummary")
    object EditTextScreen : Destination("EditTextScreen/{title}")
    object RadioButtonHistoryScreen : Destination("RadioButtonHistoryScreen/{title}")
}

class MainActivity : ComponentActivity(){

    companion object{
        val shared = MainActivity()
        var authRepo : AuthRepository = AuthRepository.getInstance()
        var adminDBRepo : AdminDBRepository = AdminDBRepository.getInstance()
        var cameraRepo : CameraRepository = CameraRepository()
        var locationRepo : LocationRepository = LocationRepository.getInstance()
        var pc300Repo : PC300Repository = PC300Repository.getInstance()
        var omronRepo : OmronRepository = OmronRepository.getInstance()
        var subUserRepo : SubUserDBRepository = SubUserDBRepository.getInstance()
        var csvRepository : CsvRepository = CsvRepository.getInstance()
        var s3Repo : S3Repository = S3Repository()
        var sessionStatusRepo : SessionStatusRepo = SessionStatusRepo()
        var playerRepo : PlayerRepo = PlayerRepo.getInstance()
        var localDBRepo : LocalSessionDBManager = LocalSessionDBManager.getInstance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val PERMISSIONS = arrayOf(
        Manifest.permission.INTERNET,
        BLUETOOTH_SCAN,
        BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE
    )
    private val PERMISSION_REQUEST_CODE = 123

    private var permissionIndex = 0

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    fun initializeOmronPC300(context: Context){
        pc300Repo.initializePC300(context)
        adminDBRepo.initializeAPIManager()
        omronRepo.register(context)
        csvRepository.setUpContext(this)
    }

    // Call this function to start the permission request process
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissionsForLatest() {
        permissionIndex = 0
        requestNextPermission()
        csvRepository.setUpContext(this)
        csvRepository.checkECGDirectory(this)
    }


    private fun requestPermissionsForOlder() {
        requestPermissions(arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("tag", "requestLocationPermission: ")
        }
    }



    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestNextPermission() {
        if (permissionIndex < PERMISSIONS.size) {
            val permission = PERMISSIONS[permissionIndex]
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            } else {
                permissionIndex++
                requestNextPermission()
            }
        }else{
            csvRepository.checkECGDirectory(this)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun checkDirectory(){
        MainActivity.csvRepository.checkECGDirectory(this)
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionIndex++
                requestNextPermission()
            } else {
                // Permission denied
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AarogyaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        requestPermissionsForLatest()
                    }else{
                        requestPermissionsForOlder()
                    }
                    val navController = rememberNavController()
                    NavigationAppHost(navController = navController)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initializeRepository()
        localDBRepo.setDBDao(this)
        FirebaseAnalytics.getInstance(this);
    }

    fun initializeRepository() = authRepo.initializeAmplify(this)

}

// Function to check if Bluetooth is enabled
fun isBluetoothEnabled(): Boolean {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    return bluetoothAdapter?.isEnabled ?: false
}

@SuppressLint("MissingPermission")
fun turnOn(context: Context) {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        val activity = context as? Activity
        activity?.startActivityForResult(enableBtIntent, 0)
    }
}

fun checkBluetooth(context: Context){
    if(!isBluetoothEnabled()) turnOn(context)
}



@ExperimentalTvMaterial3Api
@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalMaterial3Api
@Composable
fun NavigationAppHost(navController: NavHostController){
      NavHost(navController = navController, startDestination = Destination.Splash.routes){
          composable (Destination.Splash.routes) { SplashScreen (navController, MainActivity.authRepo) }
          composable (Destination.Home.routes) { HomeScreen (navController, MainActivity.authRepo, MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo) }
          composable(Destination.DeviceConnection.routes) { DevicesConnectionScreen(navController, MainActivity.pc300Repo, MainActivity.omronRepo)}
          composable(Destination.Login.routes) { LoginScreen(navController, MainActivity.authRepo) }
          composable(Destination.ForgotPasswordScreen.routes) { ForgotPasswordScreen(navController, MainActivity.authRepo)}
          composable(Destination.UserHome.routes) { UserHomeScreen(navController, MainActivity.adminDBRepo, MainActivity.pc300Repo, MainActivity.locationRepo, MainActivity.subUserRepo, MainActivity.s3Repo, MainActivity.csvRepository)}
          composable(Destination.ConfirmAdminSignIn.routes) { ConfirmAdminSignInScreen(navController)}
          composable(Destination.PasswordReset.routes) { PasswordResetScreen(navController,MainActivity.authRepo)}
          composable(Destination.AddNewUser.routes) { AddNewUserScreen(navController, MainActivity.adminDBRepo, MainActivity.cameraRepo, MainActivity.locationRepo, MainActivity.subUserRepo)}
          composable(Destination.AdminProfile.routes) { AdminProfileScreen(navController, MainActivity.adminDBRepo, MainActivity.locationRepo) }
          composable(Destination.Camera.routes) { CameraScreen(MainActivity.cameraRepo, navController)}
          composable(Destination.SessionSummary.routes) { SessionSummaryScreen(navController) }
          composable(Destination.Graphs.routes) { GraphScreen(navHostController = navController, selectedEcg) }
          composable(Destination.DeviceList.routes) { NearByDeviceListScreen(navHostController = navController, MainActivity.pc300Repo, MainActivity.omronRepo)}
          composable(Destination.SessionHistory.routes) { UserSessionHistoryScreen(navHostController = navController, subUserDBRepository = MainActivity.subUserRepo, MainActivity.adminDBRepo)}
          composable(Destination.EditTextScreen.routes + "/{title}") { navBackStack ->
              val title = navBackStack.arguments?.getString("title")
              if (title != null) {
                  EditTextScreen(navHostController = navController,title = title)
              }
          }
          composable(Destination.RadioButtonHistoryScreen.routes + "/{title}") { navBackStack ->
              val title = navBackStack.arguments?.getString("title")
              if (title != null) {
                  RadioButtonHistoryScreen(navHostController = navController,title = title)
              }
          }
      }
}






