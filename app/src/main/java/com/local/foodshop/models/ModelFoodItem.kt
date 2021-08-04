package com.local.foodshop.models

import com.google.gson.Gson
import com.local.foodshop.models.temp.ItemOptions


data class ModelFoodItem(
        val description: String,
        val id: Int,
        val image: String,
        val name: String,
        val price: Int,
        var basket_quantity: Int

)
{
    fun deepCopy() : ModelFoodItem {
        return Gson().fromJson(Gson().toJson(this), this.javaClass)
    }
}

fun List<FoodItemResponse>.toMovieModelList(): List<ModelFoodItem> {
    return map { it ->
        ModelFoodItem(
            description = it.description,
            id = it.id,
            image = it.image,
            name = it.name,
            price = it.price,
            basket_quantity =  0
        )
    }
}