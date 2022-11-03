package com.example.resturantsearch.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.resturantsearch.models.MenuResponse
import com.example.resturantsearch.models.RestaurantInfo
import com.example.resturantsearch.models.RestaurantsItem
import com.example.resturantsearch.models.ResturantResponse
import com.example.resturantsearch.repository.RestaurantRepository
import com.example.resturantsearch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class RestaurantViewModel @Inject constructor(
    private val repository: RestaurantRepository):ViewModel() {

    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

    private val TAG = "RestaurantViewModel"

    private val _restaurants = MutableLiveData<Resource<ArrayList<RestaurantInfo>>>()
    val restaurants:LiveData<Resource<ArrayList<RestaurantInfo>>>
        get() = _restaurants


    private val _searchResult = MutableLiveData<ArrayList<RestaurantsItem>?>()
    val searchResult: MutableLiveData<ArrayList<RestaurantsItem>?>
        get() = _searchResult

    private val _searchResultFlag = MutableLiveData<Boolean>()
    val searchResultFlag:LiveData<Boolean>
            get() = _searchResultFlag

    init {
        getRestaurantInfo()
    }


    private fun getRestaurantInfo() = viewModelScope.launch(defaultDispatcher) {
        safeRestaurantInfoCall()
    }

    private fun safeRestaurantInfoCall(){
        _restaurants.postValue(Resource.Loading())
        try {
            viewModelScope.launch(defaultDispatcher) {
                val restaurantResponse = async {
                    repository.readJsonFromLocal(
                        "restaurants.json", ResturantResponse()
                    )
                }

                val menuResponse = async {
                    repository.readJsonFromLocal(
                        "menus.json", MenuResponse()
                    )
                }
                val menu = menuResponse.await()
                val restaurant = restaurantResponse.await()
                when (menu) {
                    is Resource.Success -> {
                        if (restaurant is Resource.Success) {
                            val response = repository.fetchDetailsFromJson(
                                restaurant.data as ResturantResponse, menu.data as MenuResponse
                            )
                            _restaurants.postValue(Resource.Success(response.data!!))
                        }
                        else _restaurants.postValue(Resource.Error("Exception"))
                    }
                    is Resource.Loading -> {
                        _restaurants.postValue(Resource.Loading())
                    }
                    is Resource.Error -> {
                        _restaurants.postValue(Resource.Error("Not able to parse json properly"))
                    }
                }
            }
        }
        catch (t:Throwable){
            _restaurants.postValue(Resource.Error(t.message.toString()))
        }
    }

    fun filterSearch(newText:String, restaurants: ArrayList<RestaurantInfo>) {
        Log.d(TAG, "filterSearch: called $newText $restaurants")
        val result = ArrayList<RestaurantsItem>()
        viewModelScope.launch(defaultDispatcher) {
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