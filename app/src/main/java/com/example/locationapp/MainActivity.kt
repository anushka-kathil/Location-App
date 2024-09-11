package com.example.locationapp

import android.content.Context
import android.os.Bundle
// import manifest for coarse location
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LocationViewModel = viewModel()
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils, viewModel, context = context)
}



@Composable
fun LocationDisplay(
    locationUtils :LocationUtils,
    viewModel: LocationViewModel,
    context: Context
){
     val location = viewModel.location.value

    // we unpack our address using let keyword
    val address = location?.let{
        locationUtils.reverseGeocodeLocation(location)  // give me that address and set this address to display
        // {under column }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions ->
            if(permissions[Manifest.permission.ACCESS_COARSE_LOCATION]== true
                &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
                // I HAVE ACCESS TO LOCATION

                locationUtils.requestLocationUpdates(viewModel=viewModel)    
            }else{
                // ASK FOR PERMISSION
                // below function lets user to know why we want to have that permission
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
              // don't open it on another screen open it here in mainactivity
                   context as MainActivity,
                   Manifest.permission.ACCESS_FINE_LOCATION
                ) ||  ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if(rationaleRequired){
                    Toast.makeText(context,
                        "Location Permission is required for this feature to work", Toast.LENGTH_LONG)
                        .show()
                }
                // if permission is denied app could not access location you have to do it manually by going to settings
                else{
                    Toast.makeText(context,
                        "Location Permission is required. Please enable it in the Android Settings", Toast.LENGTH_LONG)
                        .show()

                }


            }
        })
        




    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        if(location!=null)
        {
            Text(text = "Address: ${location.latitude} ${location.longitude} \n $address")
        }  else{
                 Text(text = "Location not available")     
        }


        Button(onClick = {
            if(locationUtils.hasLocationPermission(context)){
              // Permission already granted update the location
              locationUtils.requestLocationUpdates(viewModel)
            }else{
                // Requires location permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {    
            Text(text = "Get Location")
        }
    }
}
//
//@Preview
//@Composable
//fun FirstPreview(){
////    MyApp()
//    LocationDisplay(
//
//
//    )
//}
