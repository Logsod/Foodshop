package com.local.foodshop.models


data class FoodItemBasket(
        var id: Long,
        var price: Long?,
        var quantity: Long?,
        var sizes: List<FoodItemResponse>? = null
) {
    constructor() : this(0,0,0, null)
}

