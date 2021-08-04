package com.local.foodshop.models.temp

data class Opt(
        val price: Int = 0,
        var selected: Boolean = false,
        val title: String
)

fun List<Opt>.findSelectedOrZero(): Int {
    forEachIndexed{index, item ->
        if(item.selected)
            return index
    }
    return 0
}
