package com.local.foodshop.api

import com.local.foodshop.models.FoodCatalogResponse
import com.local.foodshop.models.FoodItemResponse
import com.local.foodshop.models.FooldItemSizesResponce
import com.local.foodshop.models.temp.ItemOptions
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

interface ServerApi {
    @GET("foodcatalog.php")
    fun getFoodCatalog() : Single<List<FoodCatalogResponse>>
    @GET("api.php")
    fun getFoodList(@Query("id") id :Int) : Single<List<FoodItemResponse>>

    @GET("getSizes.php")
    fun getFoodListSizes(@Query("ids") ids: String) : Single<List<FooldItemSizesResponce>>

    @GET("foodDialog.php")
    fun getItemOptions(@Query("id") id:Int) : Single<List<ItemOptions>>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("poststr.php")
    @FormUrlEncoded
    fun postStringAndSave(@Field("str") str: String) : Observable<Int>

}