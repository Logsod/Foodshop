package com.local.foodshop.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single

@Dao
interface FoodItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(foodItem : FoodItemBasketEntity) : Single<Long>

    @Query("SELECT * FROM basket WHERE 1")
    fun getBasketItems() : LiveData<List<FoodItemBasketEntity>>

    @Query("SELECT * FROM basket WHERE item_id = :ids")
    fun selectById(ids : Int) : LiveData<FoodItemBasketEntity>

    @Query("DELETE FROM basket WHERE item_id = :ids")
    fun deletebyId(ids: Int) : Single<Int>

    @Query("UPDATE basket SET quantity = quantity + 1 WHERE item_id = :ids")
    fun incQuantityById(ids: Int): Single<Int>

    @Query("UPDATE basket SET quantity = quantity - 1 WHERE item_id = :ids")
    fun decQuantityById(ids: Int): Single<Int>

    @Query("SELECT quantity FROM basket WHERE item_id = :ids")
    fun selectQuantityById(ids: Int) : Single<Int>

    @Query("SELECT * FROM basket WHERE 1")
    fun selectItemsQuantity() : Single<List<FoodItemBasketEntity>>

}