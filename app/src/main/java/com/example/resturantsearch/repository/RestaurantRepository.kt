package com.example.resturantsearch.repository

import android.content.Context
import android.util.Log
import com.example.resturantsearch.getJsonDataFromAsset
import com.example.resturantsearch.models.MenuResponse
import com.example.resturantsearch.models.RestaurantInfo
import com.example.resturantsearch.models.ResturantResponse
import com.google.gson.Gson

class RestaurantRepository(private val context: Context) {
    private val TAG = "RestaurantRepository"

    fun readJsonFromLocal(jsonPath:String, dataClass:Any):Any {
        val restaurantJson = getJsonDataFromAsset(context, jsonPath)
        return Gson().fromJson(restaurantJson, dataClass::class.java)
    }
    fun fetchDetailsFromJson(restaurantResponse:ResturantResponse, menuResponse: MenuResponse):ArrayList<RestaurantInfo> {
        val restaurants = ArrayList<RestaurantInfo>()
        Log.i(TAG, "Restaurants $restaurantResponse")
        Log.i(TAG, "Menu $menuResponse")

        restaurantResponse.restaurants?.mapNotNull { restId ->
            val menu = menuResponse.menus?.find { it?.restaurantId == restId?.id }
            if (menu == null) null
            else {
                val myList = menu.categories?.flatMap { it?.menuItems!! }
                Log.d(TAG, "Flat Menu: ${myList}}")
                var restaurantInfo = RestaurantInfo(restId!!, myList!!)
                restaurants.add(restaurantInfo)
                Log.d(TAG, "Menu: $menu")
            }
        }
        return restaurants
    }
}