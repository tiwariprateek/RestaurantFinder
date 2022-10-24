package com.example.resturantsearch.models

import com.google.gson.annotations.SerializedName

data class MenuResponse(
	val menus: List<MenusItem?>? = null
)

data class MenuItemsItem(
	val images: List<Any?>? = null,
	val price: String? = null,
	val name: String? = null,
	val description: String? = null,
	val id: String? = null
)

data class CategoriesItem(
	@SerializedName(value = "menu-items")
	val menuItems: List<MenuItemsItem?>? = null,
	val name: String? = null,
	val id: String? = null
)

data class MenusItem(
	val categories: List<CategoriesItem?>? = null,
	val restaurantId: Int? = null
)

