package com.example.adsdesk.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
object CityHelper {
    fun getAllCounties(context: Context): ArrayList<String>{
        var tempArray = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val countriesName = jsonObject.names()
            if (countriesName != null) {
                for (i in 0 until countriesName.length()) {
                    tempArray.add(countriesName.getString(i))
                }
            }
        }catch (e: IOException){

        }
        return tempArray
    }

    fun getAllCities(country: String, context: Context): ArrayList<String>{
        var tempArray = ArrayList<String>()
        try {
            val inputStream: InputStream = context.assets.open("countriesToCities.json")
            val size: Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val cityNames = jsonObject.getJSONArray(country)
                for (i in 0 until cityNames.length()) {
                    tempArray.add(cityNames.getString(i))
                }

        }catch (e: IOException){

        }
        return tempArray
    }

    fun filterListData(list: ArrayList<String>, searchText: String?): ArrayList<String>{
        val tempList = ArrayList<String>()
        tempList.clear()
        if (searchText == null){
            tempList.add("No result")
            return tempList
        }
        for (selection: String in list){
            if (selection.lowercase().startsWith(searchText.lowercase())){
                tempList.add(selection)
            }
        }
        if (tempList.size == 0) tempList.add("No result")
        return tempList
    }
}