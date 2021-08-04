package com.local.foodshop.models

data class BasketAdapterModel(
        val image : String?,
        val total_price : Long,
        val main_description : String = ""
)