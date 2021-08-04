package com.local.foodshop

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.facebook.stetho.Stetho
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.local.foodproject.data.AppDatabase
import com.local.foodshop.adapters.SectionsPagerAdapter
import com.local.foodshop.api.ApiFactory
import com.local.foodshop.api.ServerApi
import com.local.foodshop.database.FoodItemBasketEntity
import com.local.foodshop.databinding.ActivityMainBinding
import com.local.foodshop.models.Basket
import com.local.foodshop.models.FoodCatalogActivityBinding
import com.local.foodshop.models.FoodItemInBasket
import com.local.foodshop.models.temp.Opt
import com.local.foodshop.models.temp.findSelectedOrZero
import com.local.foodshop.viewmodel.AppViewModel
import com.local.foodshop.viewmodel.ViewModelFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.io.File

class MainActivity : AppCompatActivity() {

    private var compositeDisposable = CompositeDisposable()
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AppViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //binding.activitybinding = FoodCatalogActivityBinding(100)
        //setContentView(R.layout.activity_main)

        val sectionPagerAdapter =
                SectionsPagerAdapter(
                        this,
                        supportFragmentManager
                )
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        val api: ServerApi = ApiFactory().serverApi
        val db = AppDatabase(this)
        viewModel = ViewModelProvider(this, ViewModelFactory(api, db)).get(AppViewModel::class.java)

        viewModel.getBasketItems().observe(this, Observer {
            var newTotal: Long = 0
            it.forEach {
                newTotal += it.price!! * it.quantity!!
            }
            binding.activitybinding = FoodCatalogActivityBinding(newTotal)
        })


        val listDisposable = viewModel.getCatalog()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.e("TAG",it.toString())
                    sectionPagerAdapter.refresh(it)
                }

        if (listDisposable != null) {
            compositeDisposable.add(listDisposable)
        }


        binding.idBacket.setOnClickListener{
            //intent = Intent(this,ActivityCheckout::class.java)
            intent = Intent(this,BasketActivity::class.java)
            //intent = Intent(this,ActivityMap::class.java)
            startActivity(intent)
            //Toast.makeText(this,"backet",Toast.LENGTH_LONG).show()
        }




        Stetho.initializeWithDefaults(this);

    }

    override fun onResume() {
        super.onResume()
          binding.activitybinding = FoodCatalogActivityBinding(Basket.instance.calcAllTotal())
    }
    fun updateTotalPrice() {
//        var newTotal: Long = 0
        val items = Basket.instance.items
//
//        items.forEachIndexed { index, item ->
//            val selectedOption = item.itemOption?.findSelectedOrZero()
//            val option = item.itemOption!![selectedOption!!].options
//            option.forEach { option ->
//                if (
//                        option.type == FoodDialogOptionFragment.ITEM_OPTION_TYPE.TITLED_CHECKBOX.value ||
//                        option.type == FoodDialogOptionFragment.ITEM_OPTION_TYPE.TITLED_GROUP.value
//                ) {
//                    option.opts.forEach {
//                        if (it.selected) {
//                            newTotal += it.price.toLong()
//                            //Toast.makeText(this, "index:$index[$it]",Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                }
//            }
//            // newTotal += item.modelFoodItem?.price?.toLong() ?: 0
//        }
        binding.activitybinding = FoodCatalogActivityBinding(Basket.instance.calcAllTotal())
        val json: String = Gson().toJson(items)
        //Toast.makeText(this, json,Toast.LENGTH_SHORT).show()
        val dis = viewModel.postStringAndSave(json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            Toast.makeText(this, "posted", Toast.LENGTH_SHORT).show()
                        },
                        {
                            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                        }
                )
    }
}
