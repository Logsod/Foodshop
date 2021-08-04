package com.local.foodshop.models.temp

import android.content.ClipData
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class ItemOptions(
        val options: List<Option>,
        val showtitle: Boolean,
        var selected: Boolean = false,
        var price: Int = 0,
        var image: String = "",
        val title : String)
{
    fun deepCopy() : ItemOptions {
        return Gson().fromJson(Gson().toJson(this), this.javaClass)
    }
}



//usage///

//data class ItemOptions(
//        @SerializedName("options")  val options: List<Option>,
//        @SerializedName("showtitle") val showtitle: Boolean,
//        @SerializedName("selected") var selected: Boolean = false,
//        @SerializedName("title") val title : String) : JSONConvertable
//{
//    fun deepCopy() : ItemOptions {
//        return Gson().fromJson(Gson().toJson(this), this.javaClass)
//    }
//}
//
//
//interface JSONConvertable {
//    fun toJSON(): String = Gson().toJson(this)
//}
//
//inline fun <reified T: JSONConvertable> String.toObject(): T = Gson().fromJson(this, T::class.java)

fun List<ItemOptions>.deleteUnselected() : List<ItemOptions>{
    return this.toMutableList().filter { it.selected }
}

fun List<ItemOptions>.resetSelected(): Unit {
    for (element in this) element.selected=false
}

fun List<ItemOptions>.findSelectedOrZero(): Int {
    forEachIndexed{index, item ->
        if(item.selected)
            return index
    }
    return 0
}
