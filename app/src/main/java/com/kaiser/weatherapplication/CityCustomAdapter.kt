package com.kaiser.weatherapplication

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class CityCustomAdapter(var context : Context, private var arrayCity : ArrayList<City>) : BaseAdapter(){
    class ViewHolder(row : View) {
        var ivStatus: ImageView = row.findViewById(R.id.item_add_city_status)
        var tvCity: TextView = row.findViewById(R.id.item_add_city_city)
        var tvCountry: TextView = row.findViewById(R.id.item_add_city_country)
        var tvTemp: TextView = row.findViewById(R.id.item_add_city_temp)

    }
    override fun getCount(): Int {
        return arrayCity.size
    }

    override fun getItem(p0: Int): Any {
        return arrayCity[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View?
        val viewholder: ViewHolder
        if (p1 == null){
            val layoutinflater : LayoutInflater = LayoutInflater.from(context)
            view  = layoutinflater.inflate(R.layout.item_add_city,null)
            viewholder = ViewHolder(view)
            view.tag = viewholder
        }else{
            view = p1
            viewholder = p1.tag as ViewHolder
        }
        val city : City = getItem(p0) as City
        Glide.with(context)
            .load("https://openweathermap.org/img/wn/"+city.image+"@2x.png")
            .into(viewholder.ivStatus)
        viewholder.tvCity.text = city.city
        viewholder.tvCountry.text = city.country
        viewholder.tvTemp.text = city.temp

        return view as View
    }

}

