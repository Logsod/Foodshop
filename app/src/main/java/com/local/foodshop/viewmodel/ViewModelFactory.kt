package com.local.foodshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.local.foodproject.data.AppDatabase
import com.local.foodshop.api.ServerApi


class ViewModelFactory(private val serverApi: ServerApi, private val db: AppDatabase) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            return AppViewModel(serverApi,db) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}