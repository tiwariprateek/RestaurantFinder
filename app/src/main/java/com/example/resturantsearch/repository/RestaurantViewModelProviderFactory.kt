package com.example.resturantsearch.repository

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.resturantsearch.ui.RestaurantViewModel



class RestaurantViewModelProviderFactory(val app:Application,
                                   val restaurantRepository: RestaurantRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RestaurantViewModel(app,restaurantRepository) as T
    }
}