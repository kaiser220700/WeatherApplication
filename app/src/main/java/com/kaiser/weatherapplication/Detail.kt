package com.kaiser.weatherapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class Detail : AppCompatActivity() {
    private var ivIcon: ImageView? = null
    private var tvStatus: TextView? = null
    private var tvDate: TextView? = null
    private var tvTempMorning: TextView? = null
    private var tvTempAfternoon: TextView? = null
    private var tvTempEvening: TextView? = null
    private var tvTempNight: TextView? = null
    private var tvFeelsLikeMorning: TextView? = null
    private var tvFeelsLikeAfternoon: TextView? = null
    private var tvFeelsLikeEvening: TextView? = null
    private var tvFeelsLikeNight: TextView? = null
    private var tvTemp: TextView? = null
    private var tvUnit: TextView? = null
    private var tvWindy: TextView? = null
    private var tvUV: TextView? = null
    private var tvHumidity: TextView? = null
    private lateinit var prefDetail: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        ivIcon = findViewById(R.id.detail_icon)
        tvStatus = findViewById(R.id.detail_status)
        tvDate = findViewById(R.id.detail_date)
        tvTempMorning = findViewById(R.id.detail_temp_morning)
        tvTempAfternoon = findViewById(R.id.detail_temp_afternoon)
        tvTempEvening = findViewById(R.id.detail_temp_evening)
        tvTempNight = findViewById(R.id.detail_temp_night)
        tvFeelsLikeMorning = findViewById(R.id.detail_feels_like_morning)
        tvFeelsLikeAfternoon = findViewById(R.id.detail_feels_like_afternoon)
        tvFeelsLikeEvening = findViewById(R.id.detail_feels_like_evening)
        tvFeelsLikeNight = findViewById(R.id.detail_feels_like_night)
        tvTemp = findViewById(R.id.detail_temp)
        tvUnit = findViewById(R.id.detail_unit)
        tvWindy = findViewById(R.id.detail_windy)
        tvUV = findViewById(R.id.detail_uv)
        tvHumidity = findViewById(R.id.detail_humidity)

        setActionBar()

        prefDetail = getSharedPreferences("Current Location", Context.MODE_PRIVATE)
        val lat = prefDetail.getString("current lat", " ")
        val long = prefDetail.getString("current long", " ")

        getJsonData(lat,long)

        prefDetail = getSharedPreferences("City", Context.MODE_PRIVATE)

    }

    private fun setActionBar() {
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if  (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
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
        supportActionBar?.title = response.getString("timezone")
        //Set on current
        val current: JSONObject = response.getJSONObject("current")
        //set image status
        val icon: String =
            current.getJSONArray("weather")
                .getJSONObject(0).getString("icon")
        Glide.with(this)
            .load("https://openweathermap.org/img/wn/$icon@2x.png")
            .into(ivIcon!!)
        //        set status for hourly forecast
        tvStatus?.text =
            response.getJSONObject("current").getJSONArray("weather")
                .getJSONObject(0).getString("description")
        val time: String = response.getJSONObject("current").getString("dt")
        val convertDay: Long = time.toLong()
        val date = Date(convertDay * 1000L)
        val dateFormat = SimpleDateFormat(" EEEE, dd MMMM yyyy")
        val getDay: String = dateFormat.format(date)
        tvDate?.text = getDay

        val day: JSONArray = response.getJSONArray("daily")
        val dayOpject: JSONObject = day.getJSONObject(0)
        // set temp day
        val tempMorning =  dayOpject.getJSONObject("temp").getString("morn")
        val convertTM: Long = tempMorning.toDouble().roundToLong()
        tvTempMorning?.text = "$convertTM°C"
        val tempAfternoon=  dayOpject.getJSONObject("temp").getString("day")
        val convertTA: Long = tempAfternoon.toDouble().roundToLong()
        tvTempAfternoon?.text = "$convertTA°C"
        // set temp night
        val tempEvening =  dayOpject.getJSONObject("temp").getString("eve")
        val convertTE: Long = tempEvening.toDouble().roundToLong()
        tvTempEvening?.text = "$convertTE°C"
        val tempNight =  dayOpject.getJSONObject("temp").getString("night")
        val convertTN: Long = tempNight.toDouble().roundToLong()
        tvTempNight?.text = "$convertTN°C"
        // set feels like day
        val feelsLikeMorning =  dayOpject.getJSONObject("feels_like").getString("morn")
        val convertFLM: Long = feelsLikeMorning.toDouble().roundToLong()
        tvFeelsLikeMorning?.text = "Feels Like: \n$convertFLM°C"
        val feelsLikeAfternoon =  dayOpject.getJSONObject("feels_like").getString("day")
        val convertFLA: Long = feelsLikeAfternoon.toDouble().roundToLong()
        tvFeelsLikeAfternoon?.text = "Feels Like: \n$convertFLA°C"
        // set feels like might
        val feelsLikeEvening =  dayOpject.getJSONObject("feels_like").getString("morn")
        val convertFLE: Long = feelsLikeEvening.toDouble().roundToLong()
        tvFeelsLikeEvening?.text = "Feels Like: \n$convertFLE°C"
        val feelsLikeNight =  dayOpject.getJSONObject("feels_like").getString("morn")
        val convertFLN: Long = feelsLikeNight.toDouble().roundToLong()
        tvFeelsLikeNight?.text = "Feels Like: \n$convertFLN°C"
        //set temp
        val temp = current.getString("temp")
        val convert: Long = temp.toDouble().roundToLong()
        tvTemp?.text = "$convert°C"
        // set unit
        tvUnit?.text = "Celsius"
        //set humidity
        tvHumidity?.text = current.getString("humidity") + "%"
        //set uv
        tvUV?.text = current.getString("uvi")
        // set wind speed
        tvWindy?.text = current.getString("wind_speed") + " m/s"


        prefDetail = getSharedPreferences("Degree", Context.MODE_PRIVATE)
        val f = prefDetail.getString("Fahrenheit", " ")
        val check = prefDetail.getBoolean("Check", false)

        if (check) {
            // set temp day
            val tempMorningF =  dayOpject.getJSONObject("temp").getString("morn")
            val convertTMF: Long = (tempMorningF.toDouble().roundToLong()* 1.8.toLong()) + 32
            tvTempMorning?.text = "$convertTMF°F"
            val tempAfternoonF=  dayOpject.getJSONObject("temp").getString("day")
            val convertTAF: Long = (tempAfternoonF.toDouble().roundToLong()* 1.8.toLong()) + 32
            tvTempAfternoon?.text = "$convertTAF°F"
            // set temp night
            val tempEveningF =  dayOpject.getJSONObject("temp").getString("eve")
            val convertTEF: Long = (tempEveningF.toDouble().roundToLong()* 1.8.toLong()) + 32
            tvTempEvening?.text = "$convertTEF°F"
            val tempNightF =  dayOpject.getJSONObject("temp").getString("night")
            val convertTNF: Long = (tempNightF.toDouble().roundToLong()* 1.8.toLong()) + 32
            tvTempNight?.text = "$convertTNF°F"
            // set feels like day
            val feelsLikeMorningF =  dayOpject.getJSONObject("feels_like").getString("morn")
            val convertFLMF: Long = (feelsLikeMorningF.toDouble().roundToLong()* 1.8.toLong()) + 32
            tvFeelsLikeMorning?.text = "Feels Like: \n$convertFLMF°F"
            val feelsLikeAfternoonF =  dayOpject.getJSONObject("feels_like").getString("day")
            val convertFLAF: Long = (feelsLikeAfternoonF.toDouble().roundToLong()* 1.8.toLong()) + 32
            tvFeelsLikeAfternoon?.text = "Feels Like: \n$convertFLAF°F"
            // set feels like might
            val feelsLikeEveningF =  dayOpject.getJSONObject("feels_like").getString("morn")
            val convertFLEF: Long = (feelsLikeEveningF.toDouble().roundToLong()* 1.8.toLong()) + 32
            tvFeelsLikeEvening?.text = "Feels Like: \n$convertFLEF°F"
            val feelsLikeNightF =  dayOpject.getJSONObject("feels_like").getString("morn")
            val convertFLNF: Long = (feelsLikeNightF.toDouble().roundToLong()* 1.8.toLong()) + 32
            tvFeelsLikeNight?.text = "Feels Like: \n$convertFLNF°F"
            //set temp
            val tempF = current.getString("temp")
            val convertF: Long = (tempF.toDouble().roundToLong() * 1.8.toLong()) + 32
            tvTemp?.text = "$convertF°F"
            // set unit
            tvUnit?.text = f
        }
    }
}