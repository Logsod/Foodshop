package com.local.foodshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.local.foodshop.FoodDialogOptionFragment
import com.local.foodshop.R
import com.local.foodshop.databinding.ActivityBasketRecyclerBottomBinding
import com.local.foodshop.databinding.ActivityBasketRecyclerviewBinding
import com.local.foodshop.models.Basket
import com.local.foodshop.models.BasketAdapterModel
import com.local.foodshop.models.temp.findSelectedOrZero


class BasketAdapter(
    private val listener: BasketRecyclerViewClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var basket_items_count: Int = 0
    var basket_item_last: Int = 0

    companion object {
        private const val TYPE_BASKET_ITEM = 0
        private const val TYPE_BASKET_RECYCLER_VIEW_BOTTOM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == TYPE_BASKET_RECYCLER_VIEW_BOTTOM)
            return BottomViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.activity_basket_recycler_bottom,
                    parent,
                    false
                )
            )
        return BasketViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.activity_basket_recyclerview,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        basket_items_count = Basket.instance.items.size
        basket_item_last = basket_items_count
        //return basket_items_count + 1
        return basket_items_count
    }

    override fun getItemViewType(position: Int): Int {
        if (position == basket_item_last)
            return TYPE_BASKET_RECYCLER_VIEW_BOTTOM

        return TYPE_BASKET_ITEM
    }

    fun calcAllTotal(): Long {
        var newTotal: Long = 0
        Basket.instance.items.forEach { items_in_basket ->
            val foodItemInBasket = items_in_basket
            val selectedOptionGroup = foodItemInBasket.itemOption.findSelectedOrZero()
            val option = foodItemInBasket.itemOption[selectedOptionGroup].options
            newTotal += foodItemInBasket.modelFoodItem.price.toLong()
            option?.forEach { option ->
                if (
                    option.type == FoodDialogOptionFragment.ITEM_OPTION_TYPE.TITLED_CHECKBOX.value ||
                    option.type == FoodDialogOptionFragment.ITEM_OPTION_TYPE.TITLED_GROUP.value
                ) {
                    option.opts?.forEach {
                        if (it.selected) {
                            newTotal += it.price.toLong()
                            //Toast.makeText(this, "index:$index[$it]",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        return newTotal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_BASKET_RECYCLER_VIEW_BOTTOM) {
            (holder as BottomViewHolder).recyclerBottomBinding.idTotalPrice.text =
                calcAllTotal().toString()
            return
        }

        val foodItemInBasket = Basket.instance.items[position]
        val inBasketQuantity = foodItemInBasket.modelFoodItem.basket_quantity
        val selectedOptionGroup = foodItemInBasket.itemOption.findSelectedOrZero()
        var newTotal: Long = Basket.instance.calcItemTotal(foodItemInBasket)
        val option = foodItemInBasket.itemOption[selectedOptionGroup].options

        //holder.recyclerviewBinding.idRemoveButton.text = position.toString()

        (holder as BasketViewHolder).recyclerviewBinding.idRemoveButton.setOnClickListener {
            if (position <= Basket.instance.items.size) {
                Basket.instance.items.removeAt(position)
                notifyDataSetChanged()
//                notifyItemRemoved(position)
//                notifyItemRangeChanged(position, Basket.instance.items.size)
            }
            listener.onRecyclerViewItemClick(holder.itemView, foodItemInBasket, position)
        }
        var main_desctiption: String = ""
        var checkbox_desctiption: String = ""
        var group_desctiption: String = ""


        val selectedCheckbox = ArrayList<String>()
        val selectedGroup = ArrayList<String>()
        //selectedCheckbox.add(main_desctiption)
        selectedGroup.add(foodItemInBasket.modelFoodItem.name)
        selectedGroup.add(foodItemInBasket.itemOption[selectedOptionGroup].title)
        option?.forEach { option ->
            if (option.type == FoodDialogOptionFragment.ITEM_OPTION_TYPE.TITLED_CHECKBOX.value) {
                //checkbox_desctiption += option.title + " "
                option.opts?.forEach {
                    if (it.selected) {
                        //checkbox_desctiption += it.title+","
                        selectedCheckbox.add(it.title)
                    }
                }
            }
            if (option.type == FoodDialogOptionFragment.ITEM_OPTION_TYPE.TITLED_GROUP.value) {
                group_desctiption = option.title + ":"
                option.opts?.forEach {
                    if (it.selected) {
                        group_desctiption += it.title
                    }
                }
                selectedGroup.add(group_desctiption)
            }
        }
        selectedGroup += selectedCheckbox
        selectedGroup += "Кол-во:" + inBasketQuantity.toString()
        main_desctiption = selectedGroup.joinToString(separator = ", ")
        //group_desctiption = selectedOpts.joinToString(separator = ",")
        (holder as BasketViewHolder).recyclerviewBinding.basketitem = BasketAdapterModel(
            Basket.instance.items[position].modelFoodItem.image,
            newTotal,
            main_desctiption// + group_desctiption + checkbox_desctiption
        )
    }


    inner class BasketViewHolder(val recyclerviewBinding: ActivityBasketRecyclerviewBinding) :
        RecyclerView.ViewHolder(recyclerviewBinding.root)

    inner class BottomViewHolder(val recyclerBottomBinding: ActivityBasketRecyclerBottomBinding) :
        RecyclerView.ViewHolder(recyclerBottomBinding.root)

}