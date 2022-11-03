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
import com.example.resturantsearch.models.*
import com.example.resturantsearch.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    lateinit var restaurantAdapter: RestaurantAdapter
    private lateinit var restaurantResponse: ResturantResponse
    private var restaurants = ArrayList<RestaurantInfo>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: RestaurantViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= ViewModelProvider(this).get(RestaurantViewModel::class.java)
        setupRecyclerView()

        viewModel.restaurants.observe(this) {response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    restaurants = response.data!!
                    restaurantAdapter.differ.submitList(restaurants.map { it -> it.restaurantResponse })
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    Log.e(TAG, "onCreate: restaurants error ${response.message}")
                    hideProgressBar()
                    Toast.makeText(this,"An error occured ", Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.searchResult.observe(this){searchResult ->
            Log.d(TAG, "onCreate: Search result changes $searchResult")
            if (searchResult!=null) {
                restaurantAdapter.differ.submitList(searchResult)
                if (searchResult.isEmpty()) {
                    binding.restaurantRv.visibility = View.GONE
                    binding.notFoundText.visibility = View.VISIBLE
                } else {
                    binding.restaurantRv.visibility = View.VISIBLE
                    binding.notFoundText.visibility = View.GONE
                }
            }
            else{
                restaurantAdapter.differ.submitList(restaurants.map { it.restaurantResponse })
                binding.restaurantRv.visibility = View.GONE
                binding.notFoundText.visibility = View.VISIBLE
            }

        }

        binding.restaurantSearch.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrBlank()) {
                    viewModel.filterSearch(newText, restaurants)
                }
                else {
                    restaurantAdapter.differ.submitList(restaurants.map { it.restaurantResponse })
                    binding.restaurantRv.visibility = View.VISIBLE
                    binding.notFoundText.visibility = View.GONE
                }
                return false
            }

        })

    }


    private fun hideProgressBar() {
        binding.progress.visibility= View.INVISIBLE
        isLoading=false
    }
    private fun showProgressBar(){
        binding.progress.visibility= View.VISIBLE
        isLoading=true
    }
    var isLoading = false



    private fun setupRecyclerView(){
        restaurantAdapter= RestaurantAdapter()
        binding.restaurantRv.apply {
            adapter=restaurantAdapter
            layoutManager= LinearLayoutManager(this@MainActivity)
        }
    }
}