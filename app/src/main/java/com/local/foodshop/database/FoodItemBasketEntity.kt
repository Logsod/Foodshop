package com.local.foodshop.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "basket")
data class FoodItemBasketEntity(
    @PrimaryKey(autoGenerate = false)
    val item_id: Int? = null,
//        val id: Long,
    val cat_id: Int? = null,
    val price: Int? = null,
    val quantity: Int? = null
)