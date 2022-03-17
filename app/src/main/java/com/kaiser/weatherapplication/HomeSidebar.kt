package com.kaiser.weatherapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import org.json.JSONObject

class HomeSidebar : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private lateinit var mFusedLocation: FusedLocationProviderClient
    private var myRequestCode = 1010
    private var tvCurrentLocation: TextView? = null
    private var ivIntentHome: ImageView? = null
    private var llAddLocation: LinearLayout? = null
    private var tvSettings: TextView? = null
    private var lvLocation: ListView? = null
    private var arrayLocation: ArrayList<NewLocation> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_sidebar)
        supportActionBar?.hide()


        tvCurrentLocation = findViewById(R.id.home_sidebar_current_location)
        ivIntentHome = findViewById(R.id.home_sidebar_intent_home)
        llAddLocation = findViewById(R.id.home_sidebar_add_location)
        tvSettings = findViewById(R.id.home_sidebar_settings)
        lvLocation = findViewById(R.id.home_sidebar_list_location)

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()

        actionIntentActivity()


        val check =  intent.getBooleanExtra("check", false)
        if(check) {
            val city = intent.getStringExtra("City")
            val country = intent.getStringExtra("Country")
            arrayLocation.add(NewLocation("$city, $country"))
            lvLocation?.adapter = NewLocationCustomAdapter(this, arrayLocation)
        }

        var checkClick: Boolean
        lvLocation?.setOnItemClickListener { _, _, _, _ ->
            checkClick = true
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("check", checkClick)
            startActivity(intent)
        }


    }

    private fun actionIntentActivity() {
        ivIntentHome?.setOnClickListener{
            val intent = Intent(this,HomeActivity::class.java)
            startActivity(intent)
        }
        llAddLocation?.setOnClickListener{
            val intent = Intent(this,AddCity::class.java)
            startActivity(intent)
        }
        tvSettings?.setOnClickListener{
            val intent = Intent(this,Settings::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLastLocation() {
        if (checkPermission()) {
            if (locationEnable()) {
                mFusedLocation.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    @Suppress("DEPRECATION")
                    if (location == null) {
                        newLocation()
                        recreate()
                    } else {
                        val lat = location.latitude.toString()
                        val long = location.longitude.toString()
                        preferences = getSharedPreferences("Current Location", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = preferences.edit()
                        editor.putString("current lat", lat)
                        editor.putString("current long", long)
                        editor.apply()

                        getJsonData(lat,long)
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on your GPS location", Toast.LENGTH_LONG).show()
            }
        } else {
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun newLocation() {
        @Suppress("DEPRECATION")
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocation.requestLocationUpdates(mLocationRequest,
            locationCallBack,
            Looper.myLooper()!!)
    }

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
        }
    }

    private fun locationEnable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION), myRequestCode)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == myRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    private fun getJsonData(lat:String?, long:String?) {
        val apiKEY = "07477f5316bccf0a26594fcf75e042c1"
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/onecall?lat=${lat}&lon=${long}&units=metric&appid=${apiKEY}"
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response ->
                setValues(response)
            },
            {
                Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()})

        queue.add(jsonRequest)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setValues(response: JSONObject) {
        val currentLocation:String = response.getString("timezone")

        tvCurrentLocation?.text = currentLocation
    }
}