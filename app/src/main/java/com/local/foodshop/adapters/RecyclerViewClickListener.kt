package com.local.foodshop.adapters

import android.view.View
import com.local.foodshop.models.FoodItemInBasket
import com.local.foodshop.models.FoodItemResponse
import com.local.foodshop.models.ModelFoodItem

interface RecyclerViewClickListener {
    fun onRecyclerViewItemClick(view: View, foodItem: ModelFoodItem, position: Int)

}
interface BasketRecyclerViewClickListener {
    fun onRecyclerViewItemClick(view: View, foodItem: FoodItemInBasket, position: Int)

}
