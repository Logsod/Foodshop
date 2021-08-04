package com.local.foodproject.views

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.local.foodshop.R
import com.local.foodshop.adapters.RecyclerViewClickListener
import com.local.foodshop.databinding.RecyclerviewFoodCatalogBinding
import com.local.foodshop.models.FoodItemBasket
import com.local.foodshop.models.FoodItemResponse
import com.local.foodshop.models.ModelFoodItem


class FoodAdapter(
    private var food: List<ModelFoodItem> = listOf(),
    private val listener: RecyclerViewClickListener
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
        FoodViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recyclerview_food_catalog,
                parent,
                false
            )
        )

    override fun getItemCount(): Int {
        return food.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {

        holder.recyclerviewFoodCatalogBinding.fooditem = food[position]
        holder.recyclerviewFoodCatalogBinding.addToBasketButton.setOnClickListener{
            listener.onRecyclerViewItemClick(holder.recyclerviewFoodCatalogBinding.addToBasketButton, food[position], position)
        }
//        holder.recyclerviewFoodCatalogBinding.incButton.setOnClickListener{
//            listener.onRecyclerViewItemClick(holder.recyclerviewFoodCatalogBinding.incButton,food[position],position)
//        }
        holder.recyclerviewFoodCatalogBinding.foodImage.setOnClickListener{
            listener.onRecyclerViewItemClick(holder.recyclerviewFoodCatalogBinding.foodImage,food[position],position)
        }
//        holder.recyclerviewFoodCatalogBinding.decButton.setOnClickListener{
//            food[position].id?.let {
//                if(food[position].id >= 0)
//                    listener.onRecyclerViewItemClick(holder.recyclerviewFoodCatalogBinding.decButton,food[position],position)
//            }
//        }
    }


    inner class FoodViewHolder(
        val recyclerviewFoodCatalogBinding: RecyclerviewFoodCatalogBinding
    ) : RecyclerView.ViewHolder(recyclerviewFoodCatalogBinding.root)

    fun update(data :List<ModelFoodItem>)
    {
        food = data
        notifyDataSetChanged()
    }

    fun setQuantityById(id: Int, quantity: Int)
    {
        food.find{it.id == id}?.basket_quantity = quantity
    }
}
