package com.example.resturantsearch.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.resturantsearch.adapter.RestaurantAdapter
import com.example.resturantsearch.databinding.ActivityMainBinding
import com.example.resturantsearch.getJsonDataFromAsset
import com.example.resturantsearch.models.*
import com.example.resturantsearch.repository.RestaurantRepository
import com.example.resturantsearch.repository.RestaurantViewModelProviderFactory
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var restaurantResponse: ResturantResponse
    private lateinit var menuResponse: MenuResponse
    private lateinit var restaurantInfo: RestaurantInfo
    private var restaurants = ArrayList<RestaurantInfo>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: RestaurantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val restaurantRepository=RestaurantRepository(application)
        val factory = RestaurantViewModelProviderFactory(application,restaurantRepository)

        viewModel= ViewModelProvider(this,factory).get(RestaurantViewModel::class.java)
        viewModel.getResturants()
        setupRecyclerView()

        viewModel.restaurants.observe(this) {
            restaurants = it
        }
        viewModel.restaurantResponse.observe(this){
            restaurantResponse = it
            restaurantAdapter.differ.submitList(it.restaurants)
        }


        binding.restaurantSearch.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.getRestaurantInfo()
                val searchResult = ArrayList<RestaurantsItem>()
                if(!newText.isNullOrBlank()) {
                    val searchText = newText.lowercase(Locale.getDefault())
                    restaurants.forEach {
                        if (it.restaurantResponse.name?.lowercase(Locale.getDefault())?.contains(searchText)!! or
                            it.restaurantResponse.cuisineType?.lowercase(Locale.getDefault())?.contains(searchText)!!) {
                            searchResult.add(it.restaurantResponse)
                            restaurantAdapter.differ.submitList(searchResult)
                            Log.d(TAG, "Found name or cuisine ")
                        }
                        else {
                            Log.d(TAG, "Not Found name or cuisine ")
                            it.menuItemsItem.forEach { dish ->
                                if (dish?.name?.lowercase(Locale.getDefault())?.contains(searchText)!!) {
                                    searchResult.add(it.restaurantResponse)
                                    restaurantAdapter.differ.submitList(searchResult)
                                    Log.d(TAG, "Found dish")
                                }
                                else{
                                    Log.d(TAG, "Not Found dish")
                                }
                            }
                        }
                    }
                }
                else restaurantAdapter.differ.submitList(restaurantResponse.restaurants)

                return false
            }

        })

    }


    private fun setupRecyclerView(){
        restaurantAdapter= RestaurantAdapter()
        binding.restaurantRv.apply {
            adapter=restaurantAdapter
            layoutManager= LinearLayoutManager(this@MainActivity)
        }
    }
}