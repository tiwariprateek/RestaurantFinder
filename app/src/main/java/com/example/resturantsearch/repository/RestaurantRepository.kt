package com.example.resturantsearch.repository

import android.content.Context
import android.util.Log
import com.example.resturantsearch.getJsonDataFromAsset
import com.example.resturantsearch.models.MenuResponse
import com.example.resturantsearch.models.RestaurantInfo
import com.example.resturantsearch.models.ResturantResponse
import com.google.gson.Gson

class RestaurantRepository(val context: Context) {
    private val TAG = "RestaurantRepository"

    suspend fun getInitialRestaurantList():ResturantResponse {
        val restaurantJson = getJsonDataFromAsset(context, "restaurants.json")
        return Gson().fromJson(restaurantJson, ResturantResponse::class.java)
    }
    suspend fun readJson():ArrayList<RestaurantInfo> {
        val restaurants = ArrayList<RestaurantInfo>()
        val menuJson = getJsonDataFromAsset(context, "menus.json")
        val restaurantResponse = getInitialRestaurantList()
        val menuResponse = Gson().fromJson(menuJson, MenuResponse::class.java)
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