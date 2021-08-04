package com.local.foodproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.local.foodshop.database.FoodItemBasketEntity
import com.local.foodshop.database.FoodItemDao

@Database(
    entities = [FoodItemBasketEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getBacketDao() : FoodItemDao
    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: buildDatabase(context).also {
                        instance = it
                    }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "basket.db"
            ).build()
    }
}