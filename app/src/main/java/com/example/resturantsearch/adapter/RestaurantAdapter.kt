package com.example.resturantsearch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.resturantsearch.databinding.RestaurantRowBinding
import com.example.resturantsearch.models.RestaurantsItem

class RestaurantAdapter(): RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    private val differCallback=object : DiffUtil.ItemCallback<RestaurantsItem>(){
        override fun areItemsTheSame(oldItem: RestaurantsItem, newItem: RestaurantsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RestaurantsItem, newItem: RestaurantsItem): Boolean {
            return oldItem == newItem
        }
    }
    val differ= AsyncListDiffer(this,differCallback)

    private var onItemClickListener:((RestaurantsItem)->Unit)?=null
    fun setOnItemClickListener(listener:(RestaurantsItem) -> Unit){
        onItemClickListener=listener
    }

    inner class RestaurantViewHolder(val binding: RestaurantRowBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RestaurantViewHolder {
        val binding = RestaurantRowBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return RestaurantViewHolder(binding)
    }


    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant=differ.currentList[position]
        holder.binding.apply {
            Glide.with(this.restaurantImage).load(restaurant.photograph).into(this.restaurantImage)
            restaurantName.text = restaurant.name
            restaurantAddress.text = restaurant.address
            restaurantCuisine.text = restaurant.cuisineType
        }

    }

    override fun getItemCount() = differ.currentList.size
}