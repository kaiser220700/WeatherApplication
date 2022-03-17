@file:Suppress("NonAsciiCharacters")

package com.kaiser.weatherapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import kotlin.math.roundToLong

class AddCity : AppCompatActivity() {
    private var edtEnter: EditText? = null
    private var lvCity: ListView? = null
    private val arrayCity: ArrayList<City> = ArrayList()
    private lateinit var prefCity: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_city)

        edtEnter = findViewById(R.id.add_city_enter_cities)
        lvCity = findViewById(R.id.add_city_list_cities)
        prefCity = getSharedPreferences("City", Context.MODE_PRIVATE)

        setActionBar()

        edtEnter?.setOnKeyListener { _, keyCode, event ->
            when {
                //Check if it is the Enter-Key,      Check if the Enter Key was pressed down
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {

                    getJsonData(edtEnter?.text.toString())

                    edtEnter?.text = null

                    //return true
                    return@setOnKeyListener true
                }
                else -> false
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setActionBar() {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(R.color.white))
        supportActionBar?.title = "Add City"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if  (item.itemId == android.R.id.home) {
                onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getJsonData(data: String?) {
        val apiKey = "07477f5316bccf0a26594fcf75e042c1"
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${data}&units=metric&appid=${apiKey}"
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            { response ->
                setValues(response)
            },
            {
                Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show()})
        queue.add(jsonRequest)
    }

    private fun setValues(response: JSONObject) {
        val icon: String =
            response.getJSONArray("weather").getJSONObject(0).getString("icon")
        val city: String = response.getString("name")
        val country: String = response.getJSONObject("sys").getString("country")
        val temp: String = response.getJSONObject("main").getString("temp")
        val convert: Long = temp.toDouble().roundToLong()
        val getTemp = "$convert°C"
        arrayCity.add(City(icon, city, country,getTemp))
        lvCity?.adapter = CityCustomAdapter(this, arrayCity)
        val lat = response.getJSONObject("coord").getString("lat")
        val long = response.getJSONObject("coord").getString("lon")
        var check: Boolean
        lvCity?.setOnItemClickListener { _, _, _, _ ->
            check = true
            val intent = Intent(this, HomeSidebar::class.java)
            intent.putExtra("City", city)
            intent.putExtra("Country", country)
            intent.putExtra("check", check)
            val editor: SharedPreferences.Editor = prefCity.edit()
            editor.putString("lat",lat).apply()
            editor.putString("long",long).apply()
            startActivity(intent)
        }
    }
}