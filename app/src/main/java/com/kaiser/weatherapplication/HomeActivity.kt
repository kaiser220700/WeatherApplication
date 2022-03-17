package com.kaiser.weatherapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToLong

class HomeActivity : AppCompatActivity() {
    private var tvDegree: TextView? = null
    private var tvFeelsLike: TextView? = null
    private var ivIcon: ImageView? = null
    private var tvPOP: TextView? = null
    private var tvStatus: TextView? = null
    private var tvHumidity: TextView? = null
    private var tvUV: TextView? = null
    private var tvWindy: TextView? = null
    private var tvStatusHourlyForecast: TextView? = null
    private var tvDateHourlyForecast: TextView? = null
    private var rvHourlyForecast: RecyclerView? = null
    private var tvStatusDailyForecast: TextView? = null
    private var tvDateDailyForecast: TextView? = null
    private var lvDailyForecast: ListView? = null
    private val arrayHourlyForecast: ArrayList<HourlyForecast> = ArrayList()
    private val arrayDailyForecast: ArrayList<DailyForecast> = ArrayList()
    private lateinit var prefHome: SharedPreferences
    private var llStatus: LinearLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        tvDegree = findViewById(R.id.home_temp)
        tvFeelsLike = findViewById(R.id.home_feels_like)
        ivIcon = findViewById(R.id.home_icon)
        tvPOP = findViewById(R.id.home_pop)
        tvStatus = findViewById(R.id.home_status)
        tvHumidity = findViewById(R.id.home_humidity)
        tvUV = findViewById(R.id.home_uv)
        tvWindy = findViewById(R.id.home_windy)
        tvStatusHourlyForecast = findViewById(R.id.home_hourly_forecast_status)
        tvDateHourlyForecast = findViewById(R.id.home_hourly_forecast_date)
        rvHourlyForecast = findViewById(R.id.home_hourly_forecast_list)
        tvStatusDailyForecast = findViewById(R.id.home_daily_forecast_status)
        tvDateDailyForecast = findViewById(R.id.home_daily_forecast_date)
        lvDailyForecast = findViewById(R.id.home_daily_forecast_list)
        llStatus = findViewById(R.id.home_layout_status)

        setActionBar()

        prefHome = getSharedPreferences("Current Location", Context.MODE_PRIVATE)

        val lat = prefHome.getString("current lat", " ")
        val long = prefHome.getString("current long", " ")

        getJsonData(lat,long)

        prefHome = getSharedPreferences("City", Context.MODE_PRIVATE)

        if (intent.getBooleanExtra("check", false)) {
            val newLat = prefHome.getString("lat", " ")
            val newLong = prefHome.getString("long", " ")

            getJsonData(newLat,newLong)
        }

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
        //set temp
        var temp = current.getString("temp")
        var convert: Long = temp.toDouble().roundToLong()
        tvDegree?.text = "$convert°C"
        //set feels like
        temp = current.getString("feels_like")
       convert = temp.toDouble().roundToLong()
        tvFeelsLike?.text = "Feels like: $convert°C"
        //set image status
        var icon: String =
            current.getJSONArray("weather")
                .getJSONObject(0).getString("icon")
        Glide.with(this)
            .load("https://openweathermap.org/img/wn/$icon@2x.png")
            .into(ivIcon!!)
        //set status description
        tvStatus?.text =
            current.getJSONArray("weather")
                .getJSONObject(0).getString("description")
        //set humidity
        tvHumidity?.text = current.getString("humidity") + "%"
        //set uv
        tvUV?.text = current.getString("uvi")
        // set wind speed
        tvWindy?.text = current.getString("wind_speed") + "m/s"

//        set forecast for hour
//        set status for hourly forecast
        tvStatusHourlyForecast?.text =
            response.getJSONObject("current").getJSONArray("weather")
                .getJSONObject(0).getString("description")
//        set time for hourly forecast
        val time: String = response.getJSONObject("current").getString("dt")
        val convertDay: Long = time.toLong()
        val date = Date(convertDay * 1000L)
        val dateFormat = SimpleDateFormat("MMMM, d yyyy")
        val getDay: String = dateFormat.format(date)
        tvDateHourlyForecast?.text = getDay
        val hour: JSONArray = response.getJSONArray("hourly")
        //set forecast chance of rain for current
        tvPOP?.text = "Chance of rain " + hour.getJSONObject(0).getString("pop") + "%"
        //execute  forecast for 24 hour in day
        for (i in 0..23) {
            val list: JSONObject = hour.getJSONObject(i)
            val dateHForecast: String = list.getString("dt")
            val convertTime: Long = dateHForecast.toLong()
            val convertDate = Date(convertTime * 1000L)
            val setFormat = SimpleDateFormat("h a")
            val getTime: String = setFormat.format(convertDate)
            temp = list.getString("temp")
            val tempFormat: Long = temp.toDouble().roundToLong()
            val getTemp = "$tempFormat°C"
            icon = list.getJSONArray("weather").getJSONObject(0).getString("icon")
            arrayHourlyForecast.add(HourlyForecast(getTime, icon, getTemp))
            rvHourlyForecast?.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvHourlyForecast?.adapter = HourlyForecastCustomAdapter(arrayHourlyForecast)
        }
        //set forecast for day of week
        tvStatusDailyForecast?.text =
            response.getJSONObject("current").getJSONArray("weather")
                .getJSONObject(0).getString("description")
        // set time daily forecast
        tvDateDailyForecast?.text = getDay
        val day: JSONArray = response.getJSONArray("daily")
        //execute forecast day in week
        for (i in 0..6) {
            val list: JSONObject = day.getJSONObject(i)
            icon = list.getJSONArray("weather").getJSONObject(0).getString("icon")
            val convertTime: Long = time.toLong()
            val convertDate = Date(convertTime * 1000L)
            val setFormat = SimpleDateFormat("EEEE")
            val getTime: String = setFormat.format(convertDate)
            temp = list.getJSONObject("temp").getString("min")
            var tempFormat: Long = temp.toDouble().roundToLong()
            val getMinTemp = "$tempFormat°C"
            temp = list.getJSONObject("temp").getString("max")
            tempFormat = temp.toDouble().roundToLong()
            val getMaxTemp = "$tempFormat°C"
            arrayDailyForecast.add(DailyForecast(icon, getTime, getMinTemp, getMaxTemp))
            lvDailyForecast?.adapter = DailyForecastCustomAdapter(this, arrayDailyForecast)
        }

        prefHome = getSharedPreferences("Degree", Context.MODE_PRIVATE)
        val check = prefHome.getBoolean("Check", false)

        if (check) {
            arrayHourlyForecast.clear()
            arrayDailyForecast.clear()

            //set temp
            var tempF = current.getString("temp")
            var convertF: Long = (tempF.toDouble().roundToLong() * 1.8.toLong()) + 32
            tvDegree?.text = "$convertF°F"
            //set feels like
            tempF = current.getString("feels_like")
            convertF = (tempF.toDouble().roundToLong() * 1.8.toLong()) + 32
            tvFeelsLike?.text = "Feels like: $convertF°F"

            val hourF: JSONArray = response.getJSONArray("hourly")
            for (i in 0..23) {
                val list: JSONObject = hourF.getJSONObject(i)
                val dateHForecast: String = list.getString("dt")
                val convertTime: Long = dateHForecast.toLong()
                val convertDate = Date(convertTime * 1000L)
                val setFormat = SimpleDateFormat("h a")
                val getTime: String = setFormat.format(convertDate)
                temp = list.getString("temp")
                val tempFormat: Long = (temp.toDouble().roundToLong() * 1.8.toLong()) + 32
                val getTemp = "$tempFormat°F"
                icon = list.getJSONArray("weather").getJSONObject(0).getString("icon")
                arrayHourlyForecast.add(HourlyForecast(getTime, icon, getTemp))
                rvHourlyForecast?.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                rvHourlyForecast?.adapter = HourlyForecastCustomAdapter(arrayHourlyForecast)
            }

            val dayF: JSONArray = response.getJSONArray("daily")
            //execute forecast day in week
            for (i in 0..6) {
                val list: JSONObject = dayF.getJSONObject(i)
                icon = list.getJSONArray("weather").getJSONObject(0).getString("icon")
                val convertTime: Long = time.toLong()
                val convertDate = Date(convertTime * 1000L)
                val setFormat = SimpleDateFormat("EEEE")
                val getTime: String = setFormat.format(convertDate)
                temp = list.getJSONObject("temp").getString("min")
                var tempFormat: Long = (temp.toDouble().roundToLong() * 1.8.toLong()) + 32
                val getMinTemp = "$tempFormat°F"
                temp = list.getJSONObject("temp").getString("max")
                tempFormat = (temp.toDouble().roundToLong() * 1.8.toLong()) + 32
                val getMaxTemp = "$tempFormat°F"
                arrayDailyForecast.add(DailyForecast(icon, getTime, getMinTemp, getMaxTemp))
                lvDailyForecast?.adapter = DailyForecastCustomAdapter(this, arrayDailyForecast)
            }

        }

        llStatus?.setOnClickListener{
            val intent = Intent(this, Detail::class.java)
            startActivity(intent)
        }


    }


}