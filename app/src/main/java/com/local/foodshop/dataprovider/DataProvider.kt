package com.local.foodshop

import android.util.Log
import androidx.lifecycle.LiveData
import com.local.foodproject.data.AppDatabase
import com.local.foodshop.api.ApiFactory
import com.local.foodshop.api.ServerApi
import com.local.foodshop.database.FoodItemBasketEntity
import com.local.foodshop.models.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.flowable.FlowableFlatMapSingle
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class DataProvider (
    val api : ServerApi,
    val db : AppDatabase

){
    val catalogList: BehaviorSubject<List<FoodCatalogResponse>> = BehaviorSubject.create()
    val catalogItems : BehaviorSubject<List<FoodItemResponse>> = BehaviorSubject.create()

    val compositeDisposable : CompositeDisposable = CompositeDisposable()

    private val timerBehavior: BehaviorSubject<Int> = BehaviorSubject.create()

    fun insertToBasket(item : FoodItemBasketEntity): Observable<Long> {
        return Observable.just(item)
            .flatMapSingle { db.getBacketDao().insert(item) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    }
    fun getBasketItems() : LiveData<List<FoodItemBasketEntity>>
    {
        return db.getBacketDao().getBasketItems()
    }


    fun incQuantityById(ids: Int) = db.getBacketDao().incQuantityById(ids)
    fun decQuantityById(ids: Int) = db.getBacketDao().decQuantityById(ids)
    fun selectQuantityById(ids: Int) = db.getBacketDao().selectQuantityById(ids)
    fun selectItemsQuantity() = db.getBacketDao().selectItemsQuantity()

    fun deletebyId(ids: Int) = db.getBacketDao().deletebyId(ids)

    fun getFoodListSizes(ids:String) = api.getFoodListSizes(ids)

    fun getItemOptions(id:Int) = api.getItemOptions(id)
    fun postStringAndSave(str: String) = api.postStringAndSave(str)

    fun getCatalogItemList(id: Int) : Observable<List<FoodItemResponse>>{
        api.getFoodList(id)
            .subscribeOn(Schedulers.io())

            .subscribe(
                { catalogItems.onNext(it)},
                { catalogItems.onNext(listOf()) }
            ).addTo(compositeDisposable)
        return catalogItems
    }

    fun getCatalog() : Observable<List<FoodCatalogResponse>> {
        api.getFoodCatalog()
            .subscribeOn(Schedulers.io())
            .subscribe(
                {

                    Log.e("TAG",it.toString())
                    catalogList.onNext(it)
                },{
                    catalogList.onNext(listOf(FoodCatalogResponse(0,"error: " + it.message)))
                }
            ).addTo(compositeDisposable)
        return catalogList
    }

}