package com.example.gps

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    val REQUEST_CODE = 1000

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this@MainActivity, "Permition granted", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(this@MainActivity, "Permition denied", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        else {
            buildLocationRequest()
            buildLocationCallBack()

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            btn_star_update.setOnClickListener{
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE
                    )
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )

                btn_star_update.isEnabled = false
                btn_stop_update.isEnabled = true
            }

            btn_stop_update.setOnClickListener{
                if (ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED &&

                    ActivityCompat.checkSelfPermission(
                        this@MainActivity,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE
                    )
                }
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)

                btn_star_update.isEnabled = true
                btn_stop_update.isEnabled = false

            }
        }

    }

    private fun buildLocationCallBack() {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addressLocation: List<Address> = arrayListOf()

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                var location = p0!!.locations.get(p0.locations.size - 1)

                text_location.text =
                    location.latitude.toString() + "/" + location.longitude.toString()
                addressLocation = geocoder.getFromLocation(location.latitude, location.longitude,1)

                val city = addressLocation[0].locality
                val state = addressLocation[0].locality
                val country = addressLocation[0].countryName
                val postalCode= addressLocation[0].postalCode

                Toast.makeText(this@MainActivity, "$city, $state, $country, $postalCode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }
}
