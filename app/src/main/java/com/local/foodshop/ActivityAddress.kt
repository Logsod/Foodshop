package com.local.foodshop

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError


class ActivityAddress : AppCompatActivity() , SuggestSession.SuggestListener{

    private val MAPKIT_API_KEY: String = "db8beb9d-0f70-40a1-8802-8051beee89bb"
    var RESULT_NUMBER_LIMIT = 5
    lateinit var _contex : Context
    lateinit var searchManager: SearchManager
    lateinit var suggestSession: SuggestSession
    lateinit var suggestResultView: ListView
    lateinit var resultAdapter: ArrayAdapter<*>
    lateinit var suggestResult: MutableList<String>
    lateinit var queryEdit: EditText

    private val CENTER = Point(45.0313463, 41.9661977)

    private val BOX_SIZE = 0.2
    private val BOUNDING_BOX = BoundingBox(
        Point(CENTER.latitude - BOX_SIZE, CENTER.longitude - BOX_SIZE),
        Point(CENTER.latitude + BOX_SIZE, CENTER.longitude + BOX_SIZE)
    )
    private val SEARCH_OPTIONS = SuggestOptions().setSuggestTypes(
        SuggestType.GEO.value or
                SuggestType.BIZ.value or
                SuggestType.TRANSIT.value
    )

    val PREF_ADDR = "useraddress"
    fun saveAddress(addr : String) {
        val settings: SharedPreferences = getSharedPreferences(
            PREF_ADDR,
            Context.MODE_PRIVATE
        )
        val editor = settings.edit()

        // Edit and commit
        editor.putString(PREF_ADDR, addr)
        editor.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        MapKitFactory.setApiKey(MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        SearchFactory.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        suggestSession = searchManager.createSuggestSession()
        queryEdit = findViewById<EditText>(R.id.basket_addres_input) as EditText
        suggestResultView = findViewById<ListView>(R.id.suggest_result) as ListView

        findViewById<Button>(R.id.id_basket_back).setOnClickListener {
            super.onBackPressed()
        }
        findViewById<Button>(R.id.id_basket_apply_address).setOnClickListener {
            saveAddress(queryEdit.text.toString())
            super.onBackPressed()
        }

        suggestResultView.setOnItemClickListener { parent, view, position, id ->
            ;
            findViewById<TextView>(R.id.basket_addres_input).text =
                resultAdapter.getItem(position).toString()
            queryEdit.setText(resultAdapter.getItem(position).toString())
            queryEdit.setSelection(queryEdit.text.length)
        }
        suggestResult = ArrayList()
        resultAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            suggestResult
        )
        suggestResultView.setAdapter(resultAdapter)


        queryEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length >= 4) {
                    requestSuggest(editable.toString())
                }
            }
        })
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onResponse(suggest: List<SuggestItem>) {
        suggestResult.clear()
        for (i in 0 until Math.min(RESULT_NUMBER_LIMIT, suggest.size)) {
            suggestResult.add(suggest[i].displayText.toString())
        }
        resultAdapter.notifyDataSetChanged()
        suggestResultView.visibility = View.VISIBLE
    }

    override fun onError(error: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (error is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun requestSuggest(query: String) {
        suggestResultView.visibility = View.INVISIBLE
        suggestSession.suggest(query, BOUNDING_BOX, SEARCH_OPTIONS, this)
    }
}