package com.example.resturantsearch.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.resturantsearch.models.RestaurantInfo
import com.example.resturantsearch.models.RestaurantsItem
import com.example.resturantsearch.models.ResturantResponse
import com.example.resturantsearch.repository.RestaurantRepository
import com.example.resturantsearch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    val app: Application,
    val repository: RestaurantRepository):AndroidViewModel(app) {
    private val TAG = "RestaurantViewModel"

    private val _restaurants = MutableLiveData<Resource<ArrayList<RestaurantInfo>>>()
    val restaurants:LiveData<Resource<ArrayList<RestaurantInfo>>>
        get() = _restaurants

    private val _restaurantResponse = MutableLiveData<Resource<ResturantResponse>>()
    val restaurantResponse:LiveData<Resource<ResturantResponse>>
        get() = _restaurantResponse

    private val _searchResult = MutableLiveData<ArrayList<RestaurantsItem>?>()
    val searchResult: MutableLiveData<ArrayList<RestaurantsItem>?>
        get() = _searchResult

    private val _searchResultFlag = MutableLiveData<Boolean>()
    val searchResultFlag:LiveData<Boolean>
            get() = _searchResultFlag

    init {
        getRestaurants()
    }


    fun getRestaurantInfo() = viewModelScope.launch {
            safeRestaurantInfoCall()
        }


    private fun getRestaurants() = viewModelScope.launch {
        safeRestaurantCall()
    }

    private suspend fun safeRestaurantCall(){
        _restaurantResponse.postValue(Resource.Loading())
        try {
            val response = repository.getInitialRestaurantList()
            if (!response.restaurants.isNullOrEmpty()){
                _restaurantResponse.postValue(Resource.Success(response))
            }
            else
                _restaurantResponse.postValue(Resource.Error("Not able to parse json properly"))

        }catch (t:Throwable){
            _restaurantResponse.postValue(Resource.Error("Not able to parse json properly"))
        }

    }

    private suspend fun safeRestaurantInfoCall(){
        _restaurants.postValue(Resource.Loading())
        try {
            val response = repository.readJson()
            if (response.isNotEmpty()){
                _restaurants.postValue(Resource.Success(response))
            }
            else
                _restaurants.postValue(Resource.Error("Not able to parse json properly"))

        }catch (t:Throwable){
            _restaurants.postValue(Resource.Error("Not able to parse json properly"))
        }

    }

    fun filterSearch(newText:String, restaurants: ArrayList<RestaurantInfo>) {
        Log.d(TAG, "filterSearch: called")
        val result = ArrayList<RestaurantsItem>()
        viewModelScope.launch(IO) {
            if (newText.isNotBlank()) {
                val searchText = newText.lowercase(Locale.getDefault())
                restaurants.forEach {
                    if (it.restaurantResponse.name?.lowercase(Locale.getDefault())
                            ?.contains(searchText)!! or
                        it.restaurantResponse.cuisineType?.lowercase(Locale.getDefault())
                            ?.contains(searchText)!!
                    ) {
                        result.add(it.restaurantResponse)
                        _searchResult.postValue(result)
                    } else {
                        run breaking@{
                            it.menuItemsItem.forEach { dish ->
                                if (dish?.name?.lowercase(Locale.getDefault())
                                        ?.contains(searchText)!!
                                ) {
                                    result.add(it.restaurantResponse)
                                    _searchResult.postValue(result)
                                    return@breaking
                                }
                            }
                        }
                    }
                    if(result.isEmpty()){
                        _searchResult.postValue(null)
                    }
                }
            }
            else{
                _searchResult.postValue(null)
            }
        }
    }



}