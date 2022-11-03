package com.example.resturantsearch.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.resturantsearch.getJsonDataFromAsset
import com.example.resturantsearch.models.MenuResponse
import com.example.resturantsearch.models.RestaurantInfo
import com.example.resturantsearch.models.ResturantResponse
import com.example.resturantsearch.util.Resource
import com.google.gson.Gson
import java.io.IOException

class RestaurantRepository(private val context: Context) {
    private val TAG = "RestaurantRepository"

    fun readJsonFromLocal(jsonPath:String, dataClass:Any):Resource<Any> {
        return try {
            val json = getJsonDataFromAsset(context, jsonPath)
            Resource.Success((Gson().fromJson(json, dataClass::class.java)))
        }catch (e:Exception){
            Resource.Error(e.message.toString())
        }
    }
    fun fetchDetailsFromJson(restaurantResponse:ResturantResponse, menuResponse: MenuResponse):Resource<ArrayList<RestaurantInfo>> {
        lateinit var restaurants:Resource<ArrayList<RestaurantInfo>>
        Log.i(TAG, "Restaurants $restaurantResponse")
        Log.i(TAG, "Menu $menuResponse")
        val restaurant = ArrayList<RestaurantInfo>()
        try {
            restaurantResponse.restaurants?.mapNotNull { restId ->
                val menu = menuResponse.menus?.find { it?.restaurantId == restId?.id }
                if (menu == null) null
                else {
                    val myList = menu.categories?.flatMap { it?.menuItems!! }
                    Log.d(TAG, "Flat Menu: ${myList}}")
                    val restaurantInfo = RestaurantInfo(restId!!, myList!!)
                    restaurant.add(restaurantInfo)
                    restaurants = Resource.Success(restaurant)
                }
            }
        } catch (e:Exception){
            restaurants = Resource.Error(e.message.toString())
        }
        return restaurants
    }
}