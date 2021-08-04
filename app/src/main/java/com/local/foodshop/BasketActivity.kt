package com.local.foodshop

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.local.foodshop.adapters.BasketAdapter
import com.local.foodshop.adapters.BasketRecyclerViewClickListener
import com.local.foodshop.adapters.RecyclerViewClickListener
import com.local.foodshop.models.Basket
import com.local.foodshop.models.FoodItemInBasket
import com.local.foodshop.models.ModelFoodItem
import kotlinx.android.synthetic.main.activity_basket.*


class BasketActivity : AppCompatActivity(), BasketRecyclerViewClickListener {

    val PREF_ADDR = "useraddress"
    private fun loadPreferences() : String {
        val settings = getSharedPreferences(
            PREF_ADDR,
            Context.MODE_PRIVATE
        )
        return settings.getString(PREF_ADDR, "").toString()
    }

    override fun onResume() {
        super.onResume()
        val address = loadPreferences()
//        val addrView : TextView = findViewById<TextView>(R.id.basket_addres)
//        if(address.length <= 2){
//            addrView.text = Html.fromHtml("<u>Введите адрес</u>")
//        }
//        else{
//            addrView.text = Html.fromHtml("<u>"+address+"</u>")
//        }

        findViewById<TextView>(R.id.id_sum)?.text = Basket.instance.calcAllTotal().toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_basket)
        super.onCreate(savedInstanceState)


        findViewById<Button>(R.id.id_basket_back)?.setOnClickListener {
            super.onBackPressed()
        }

        val basketAdapter = BasketAdapter(this)
        id_basket_items.layoutManager = GridLayoutManager(this, 1)
        id_basket_items.setHasFixedSize(true)
        id_basket_items.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.HORIZONTAL
            )
        )
        id_basket_items.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        (id_basket_items.getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations = false
        id_basket_items.adapter = basketAdapter
        id_basket_items.itemAnimator = null


//        findViewById<TextView>(R.id.basket_addres).setOnClickListener {
//            intent = Intent(this,ActivityAddress::class.java)
//            //intent = Intent(this,ActivityMap::class.java)
//            startActivity(intent)
//        }



    }

    override fun onRecyclerViewItemClick(view: View, foodItem: FoodItemInBasket, position: Int) {
        findViewById<TextView>(R.id.id_sum)?.text = Basket.instance.calcAllTotal().toString()
    }


}