package com.kaiser.weatherapplication

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class NewLocationCustomAdapter (var context : Context, private var arrayLocation : ArrayList<NewLocation>) : BaseAdapter(){
    class ViewHolder(row : View) {
        var tvDisplayLocation: TextView =
            row.findViewById(R.id.item_home_sidebar_new_location_display_loaction)
    }
    override fun getCount(): Int {
        return arrayLocation.size
    }

    override fun getItem(p0: Int): Any {
        return arrayLocation[p0]
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
            view  = layoutinflater.inflate(R.layout.item_home_sidebar_new_location,null)
            viewholder = ViewHolder(view)
            view.tag = viewholder
        }else{
            view = p1
            viewholder = p1.tag as ViewHolder
        }
        val location : NewLocation = getItem(p0) as NewLocation

        viewholder.tvDisplayLocation.text = location.location

        return view as View
    }
}