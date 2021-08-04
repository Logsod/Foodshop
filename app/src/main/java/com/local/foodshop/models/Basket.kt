package com.local.foodshop.models

import android.content.Context
import android.content.SharedPreferences
import com.local.foodshop.FoodDialogOptionFragment
import com.local.foodshop.models.temp.ItemOptions
import com.local.foodshop.models.temp.findSelectedOrZero


data class FoodItemInBasket(
        val modelFoodItem : ModelFoodItem,
        val itemOption: List<ItemOptions>,
        val currentSelectedOptions : Int = 0



)

class Basket() {

    val items : MutableList<FoodItemInBasket> = mutableListOf()
    enum class ITEM_OPTION_TYPE(val value:Int){
        OPTION_TITLE(0),
        TITLED_CHECKBOX(1),
        TITLED_GROUP(2)
    }
    private object HOLDER {
        val INSTANCE = Basket()

    }

    companion object{
        val instance : Basket by lazy { HOLDER.INSTANCE}
    }

    fun calcItemTotal(item : FoodItemInBasket) : Long{
        var itemTotal : Long = 0
        val foodItemInBasket = item
        val selectedOptionGroup = foodItemInBasket.itemOption.findSelectedOrZero()
        val option = foodItemInBasket.itemOption[selectedOptionGroup].options
        itemTotal += foodItemInBasket.modelFoodItem.price.toLong()
        option?.forEach { option ->
            if (
                option.type == ITEM_OPTION_TYPE.TITLED_CHECKBOX.value ||
                option.type == ITEM_OPTION_TYPE.TITLED_GROUP.value
            ) {
                option.opts?.forEach {
                    if (it.selected) {
                        itemTotal += it.price.toLong()
                        //Toast.makeText(this, "index:$index[$it]",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        itemTotal *= foodItemInBasket.modelFoodItem.basket_quantity
        return itemTotal
    }
    fun calcAllTotal() : Long{
        var newTotal: Long = 0
        var itemTotal : Long = 0
        items.forEach{ items_in_basket->
            itemTotal = calcItemTotal(items_in_basket)
            newTotal += itemTotal
        }
        return newTotal
    }

}
