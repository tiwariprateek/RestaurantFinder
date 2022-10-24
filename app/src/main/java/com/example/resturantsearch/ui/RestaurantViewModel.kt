package com.example.resturantsearch.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.resturantsearch.models.MenuResponse
import com.example.resturantsearch.models.RestaurantInfo
import com.example.resturantsearch.models.ResturantResponse
import com.example.resturantsearch.repository.RestaurantRepository
import kotlinx.coroutines.launch

class RestaurantViewModel(
    val app: Application,
    val repository: RestaurantRepository):AndroidViewModel(app) {

    private val _restaurants = MutableLiveData<ArrayList<RestaurantInfo>>()
    val restaurants:LiveData<ArrayList<RestaurantInfo>>
        get() = _restaurants

    private val _restaurantResponse = MutableLiveData<ResturantResponse>()
    val restaurantResponse:LiveData<ResturantResponse>
        get() = _restaurantResponse


    fun getRestaurantInfo() = viewModelScope.launch{
        _restaurants.postValue(repository.readJson())
    }

    fun getResturants() = viewModelScope.launch {
        _restaurantResponse.postValue(repository.getInitialRestaurantList())
    }


}