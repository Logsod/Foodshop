package com.local.foodshop.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.local.foodshop.FoodFragment
import com.local.foodshop.models.FoodCatalogResponse

class SectionsPagerAdapter(private val context: Context, fragmentManager: FragmentManager):
        FragmentPagerAdapter(fragmentManager,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var catalog : List<FoodCatalogResponse> = listOf()
    var mActualTitleListSize = 6
    override fun getItem(position: Int): Fragment {

        return FoodFragment.newInstance((catalog[position].id) )
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if(catalog.isEmpty()) return ""
        return catalog[position].title
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return catalog.size;
        //return 6
    }
    fun refresh(_catalog : List<FoodCatalogResponse>)
    {
        catalog = _catalog
        notifyDataSetChanged()
    }
}