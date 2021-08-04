package com.local.foodshop.viewmodel

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.local.foodproject.data.AppDatabase
import com.local.foodshop.DataProvider
import com.local.foodshop.api.ServerApi
import com.local.foodshop.database.FoodItemBasketEntity
import com.local.foodshop.models.FoodCatalogResponse
import com.local.foodshop.models.FoodItemResponse
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

class AppViewModel(
    val api : ServerApi,
    val db : AppDatabase

) : ViewModel(){

    private val catalogItemsLiveData : MutableLiveData<List<FoodItemResponse>> = MutableLiveData()
    private val catalogLiveData : MutableLiveData<List<FoodCatalogResponse>> = MutableLiveData()

    val dataProvider: DataProvider = DataProvider(api,db)

    fun insertToBasket(item : FoodItemBasketEntity): Observable<Long> {
        return dataProvider.insertToBasket(item)
    }

    fun incQuantityById(ids:Int) = dataProvider.incQuantityById(ids)
    fun decQuantityById(ids:Int) = dataProvider.decQuantityById(ids)
    fun selectQuantityById(ids:Int) = dataProvider.selectQuantityById(ids)
    fun selectItemsQuantity() = dataProvider.selectItemsQuantity()

    fun deletebyId(ids:Int) = dataProvider.deletebyId(ids)

    var foodItemBasketEntity : LiveData<List<FoodItemBasketEntity>>? = null
    fun getBasketItems() : LiveData<List<FoodItemBasketEntity>> {
        if (foodItemBasketEntity == null) {
            foodItemBasketEntity = dataProvider.getBasketItems()
        }
        return foodItemBasketEntity as LiveData<List<FoodItemBasketEntity>>

    }
    fun getFoodListSizes(ids:String)  = dataProvider.getFoodListSizes(ids)
    fun getItemOptions(id:Int) = dataProvider.getItemOptions(id)
    fun postStringAndSave(str: String) = dataProvider.postStringAndSave(str)

    fun getCatalogItemList(id:Int): Observable<List<FoodItemResponse>> {
        return dataProvider.getCatalogItemList(id).doOnNext{ item->
            catalogItemsLiveData.postValue(item)
        }
    }

    fun getCatalog(): Observable<List<FoodCatalogResponse>> {
        return dataProvider.getCatalog().doOnNext{ item->
            catalogLiveData.postValue(item)
        }
    }
}