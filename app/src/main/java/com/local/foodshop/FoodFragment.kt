package com.local.foodshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.local.foodproject.data.AppDatabase
import com.local.foodproject.views.FoodAdapter
import com.local.foodshop.adapters.RecyclerViewClickListener
import com.local.foodshop.api.ApiFactory
import com.local.foodshop.api.ServerApi
import com.local.foodshop.database.FoodItemBasketEntity
import com.local.foodshop.models.*
import com.local.foodshop.viewmodel.AppViewModel
import com.local.foodshop.viewmodel.ViewModelFactory

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.food_fragment.*


class AuthorDiffUtilCallback(
    private val oldList: List<FoodItemBasket>,
    private val newList: List<FoodItemBasket>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}

class FoodFragment : Fragment(), RecyclerViewClickListener {

    var group: ViewGroup? = null
    private lateinit var viewModel: AppViewModel
    val adapter = FoodAdapter(listOf(), this)
    private var compositeDisposable = CompositeDisposable()
    private var cat_id: Int = 0
    lateinit var foodItemResponse: List<ModelFoodItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        group = container
        return inflater.inflate(R.layout.food_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var disposable: Disposable? = null
        cat_id = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1
        val api: ServerApi = ApiFactory().serverApi
        val db = AppDatabase(this.context!!)
        viewModel = ViewModelProvider(this, ViewModelFactory(api, db)).get(AppViewModel::class.java)

        foodItemsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        foodItemsRecyclerView.setHasFixedSize(true)
        foodItemsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.HORIZONTAL
            )
        )
        foodItemsRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        (foodItemsRecyclerView.getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations =
            false
        foodItemsRecyclerView.adapter = adapter

        disposable = viewModel.getCatalogItemList(cat_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response -> response.toMovieModelList() }
            .subscribe { response ->
                foodItemResponse = response
                val ids: MutableList<Int> = mutableListOf()
                foodItemResponse.forEach {
                    ids.add(it.id)
                }
                fetchSizesOnServer(ids.joinToString { id -> "$id" })
                adapter.update(response)
                updateListByDbBasket()

            }
        compositeDisposable.add(disposable)


    }

    fun updateListByDbBasket() {

        val disposable = viewModel.selectItemsQuantity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.forEach { db ->
                    adapter.setQuantityById(db.item_id!!, db.quantity!!)
                    //adapter.food.find{it.id == db.item_id}?.basket_quantity = db.quantity!!
                    //adapter.basketQuantity.put(it.item_id!!, FoodItemBasket(it.item_id,it.price,it.quantity))
                    //adapter.basketQuantity.put(it.item_id!!, it.quantity!!)
                }
                adapter.notifyDataSetChanged()
            }, {}
            )

        if (disposable != null) {
            compositeDisposable.add(disposable)
        }
    }

    fun fetchSizesOnServer(ids: String) {
        var disposable = viewModel.getFoodListSizes(ids)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    //Toast.makeText(this.context,it.toString(),Toast.LENGTH_LONG).show()
                },
                {}
            )

        if (disposable != null) {
            compositeDisposable.add(disposable)
        }
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): FoodFragment {
            return FoodFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onRecyclerViewItemClick(view: View, foodItem: ModelFoodItem, position: Int) {
        when (view.id) {
            R.id.add_to_basket_button, R.id.food_image -> {
                val fm = (activity as MainActivity).supportFragmentManager
                val editNameDialogFragment: FoodDialogOptionFragment =
                    FoodDialogOptionFragment(viewModel, foodItem.copy())
                //editNameDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE,0)
                editNameDialogFragment.show(fm, "fragment_edit_name")
            }

//            R.id.add_to_basket_button -> {
//                viewModel.insertToBasket(FoodItemBasketEntity(foodItem.id,cat_id,foodItem.price,1))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                    {
//                        adapter.setQuantityById(foodItem.id!!,1)
//
//                        //adapter.food.find { it.id == foodItem.id }?.basket_quantity = 1
//                        adapter.notifyItemChanged(position)
//                    },
//                    {}
//                ).addTo(CompositeDisposable())
//            }
//            R.id.inc_button -> {
//                viewModel.incQuantityById(foodItem.id)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                        {
//                            updateQuantity(foodItem.id,position)
//                        },
//                        {}
//                    )
//
//            }
//            R.id.dec_button -> {
//                viewModel.decQuantityById(foodItem.id)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(
//                        {
//                            updateQuantity(foodItem.id,position)
//                        },
//                        {}
//                    )
//
//            }

        }
    }

    fun updateQuantity(foodItemId: Int, position: Int) {
        viewModel.selectQuantityById(foodItemId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val quantity = it
                    adapter.setQuantityById(foodItemId, quantity)

                    adapter.notifyItemChanged(position)
                    if (quantity.toInt() == 0) {
                        viewModel.deletebyId(foodItemId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                {}, {}
                            )
                    }
                },
                {}
            ).let { (compositeDisposable) }
    }

}
