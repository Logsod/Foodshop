package com.local.foodshop.models.temp

import com.local.foodshop.models.FoodItemResponse
import com.local.foodshop.models.ModelFoodItem

data class Option(
        var opts: List<Opt>,
        val type: Int,
        val title: String,
        val selected: Boolean = false
)

fun List<Opt>.resetSelected(): Unit {
    for (element in this) element.selected=false
}

fun List<Option>.findSelectedOrZero(): Int {
    forEachIndexed{index, item ->
        if(item.selected)
            return index
    }
    return 0
}
