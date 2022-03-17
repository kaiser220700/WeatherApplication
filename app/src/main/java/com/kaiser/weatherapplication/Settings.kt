package com.kaiser.weatherapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class Settings : AppCompatActivity() {
    private var llTemperatureChange: LinearLayout? = null
    private var tvTemperatureUnit: TextView? = null
    private lateinit var prefDegree: SharedPreferences
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setActionBar()

        llTemperatureChange = findViewById(R.id.settings_current_temperature_unit_change)
        tvTemperatureUnit = findViewById(R.id.settings_temperature_unit)

        prefDegree = getSharedPreferences("Degree", Context.MODE_PRIVATE)

        val check = prefDegree.getBoolean("Check", false)

        if(check) tvTemperatureUnit?.text = "Degree Fahrenheit °F"
        else tvTemperatureUnit?.text = "Degree Celsius °C"

        llTemperatureChange?.setOnClickListener{
            registerForContextMenu(llTemperatureChange)

        }

    }
    @SuppressLint("ResourceAsColor")
    private fun setActionBar() {
        supportActionBar?.setBackgroundDrawable(ColorDrawable(R.color.white))
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if  (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo?) {
        menuInflater.inflate(R.menu.menu_temperature_unit, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }
    @SuppressLint("SetTextI18n")
    override fun onContextItemSelected(item: MenuItem): Boolean {
        prefDegree = getSharedPreferences("Degree", Context.MODE_PRIVATE)
        val check: Boolean
        val editor: SharedPreferences.Editor = prefDegree.edit()
        return when (item.itemId) {
            R.id.menu_temperature_unit_c -> {
                tvTemperatureUnit?.text = "Degree Celsius °C"
                editor.putString("Celsius","Celsius").apply()
                check = false
                editor.putBoolean("Check", check).apply()
                Toast.makeText(this, "Successful Changed", Toast.LENGTH_LONG).show()
                return true
            }

            R.id.menu_temperature_unit_f -> {
                tvTemperatureUnit?.text = "Degree Fahrenheit °F"
                editor.putString("Fahrenheit","Fahrenheit").apply()
                check = true
                editor.putBoolean("Check", check).apply()
                Toast.makeText(this, "Successful Changed", Toast.LENGTH_LONG).show()
                return true
            }


            else -> super.onContextItemSelected(item)
        }
    }
}