package com.local.foodshop.models


data class FooldItemSizesResponce(
        val id: Int,
        val list_id: Int,
        val name: String,
        val description: String,
        val price: Int
)

data class FoodCatalogResponse (
    val id: Int,
    val title: String
)

data class FoodItemResponse(
    val description: String,
    val size_list_id: Int,
    val id: Int,
    val image: String,
    val name: String,
    val price: Int
)


