package com.local.foodshop

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.local.foodshop.models.Basket
import com.local.foodshop.models.FoodItemInBasket
import com.local.foodshop.models.ModelFoodItem
import com.local.foodshop.models.temp.*
import com.local.foodshop.viewmodel.AppViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_elements_button.view.*
import kotlinx.android.synthetic.main.dialog_elements_checkbox.*
import kotlinx.android.synthetic.main.dialog_elements_checkbox.view.*
import kotlinx.android.synthetic.main.dialog_elements_counter.view.*
import kotlinx.android.synthetic.main.dialog_elements_head.view.*
import kotlinx.android.synthetic.main.dialog_elements_text.view.*
import kotlinx.android.synthetic.main.dialog_elements_text.view.id_text
import kotlinx.android.synthetic.main.popup_layout.*
import kotlinx.android.synthetic.main.test_layout.view.*

class DrawLine : View {
    var paint: Paint = Paint()
    private fun init() {
        paint.color = Color.GRAY
        paint.isAntiAlias = false
        paint.style = Paint.Style.STROKE
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawLine(0F, 0F, canvas.width.toFloat(), 0F, paint)
    }
}

class FoodDialogOptionFragment(
        private val viewModel: AppViewModel,
        private val modelFoodItem: ModelFoodItem
) : DialogFragment() {

    lateinit var itemOptionContainerLayout : LinearLayout
    lateinit var itemOptionArray: List<ItemOptions>
    var totalOptionPriceText : TextView? = null
    //var selectedOptionGroup: Int = 0

    enum class ITEM_OPTION_TYPE(val value:Int){
        OPTION_TITLE(0),
        TITLED_CHECKBOX(1),
        TITLED_GROUP(2)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        //setStyle(STYLE_NORMAL, android.R.style.Theme_Holo_Dialog_NoActionBar);
        itemOptionContainerLayout = LinearLayout(dialog.context)

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_layout)
        dialog.dismiss_button.setOnClickListener{ dismiss() }
        //!!dialog.dismiss_button.visibility = View.GONE
        Toast.makeText(context, modelFoodItem.id.toString(), Toast.LENGTH_SHORT).show()
        val dispose = viewModel.getItemOptions(modelFoodItem.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {itemList ->
                            //Toast.makeText( this.context,it.toString(),Toast.LENGTH_LONG).show()
                            //buildOptionView(it,dialog)
                            itemOptionArray = itemList
                            buildOptionSelectorView()
                        },{
                    Toast.makeText(this.context,it.message,Toast.LENGTH_LONG).show()
                })
        return dialog
    }

    override fun onPause() {
        dismiss()
        super.onPause()
    }
    fun addSeparatorToView(context: Context, linearLayout: LinearLayout){
        val line = DrawLine(context)
        line.layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
        linearLayout.addView(line)
    }
    fun resetAllCheckBox()
    {
        val selectedOptionGroup = itemOptionArray.findSelectedOrZero()
        val option = itemOptionArray[selectedOptionGroup].options
        option?.forEach{ option ->
            if( option.type == FoodDialogOptionFragment.ITEM_OPTION_TYPE.TITLED_CHECKBOX.value)
            {
                option.opts?.forEach{
                    it.selected = false
                }
            }
        }
    }
    fun getOptionTotalPrice() : Long {
        var newTotal: Long = 0
        val selectedOptionGroup = itemOptionArray.findSelectedOrZero()
        val option = itemOptionArray[selectedOptionGroup].options
        newTotal = itemOptionArray[selectedOptionGroup].price.toLong()
            option?.forEach { option ->
                if (
                        option.type == FoodDialogOptionFragment.ITEM_OPTION_TYPE.TITLED_CHECKBOX.value ||
                        option.type == FoodDialogOptionFragment.ITEM_OPTION_TYPE.TITLED_GROUP.value
                ) {
                    option.opts?.forEach {
                        if (it.selected) {
                            newTotal += it.price.toLong()
                            //Toast.makeText(this, "index:$index[$it]",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
        return newTotal * modelFoodItem.basket_quantity
    }
    fun buildOptionSelectorView(){
        if(itemOptionArray.isEmpty()) return
        modelFoodItem.basket_quantity = 1
        var selectedOptionGroup : Int = 0
        this.dialog?.let {dlg ->
            selectedOptionGroup = itemOptionArray.findSelectedOrZero()
            resetAllCheckBox()
            val header_inflater = LayoutInflater.from(context)
            val header_view = header_inflater.inflate(R.layout.dialog_elements_head,itemOptionContainerLayout,false)
            Glide.with(this).load(itemOptionArray[selectedOptionGroup].image).into(header_view.food_image)
            dlg.topLayout?.addView(header_view)
            //dlg.topLayout.findViewWithTag<TextView>("option_total_price")?.text = getOptionTotalPrice().toString()
            totalOptionPriceText = dlg.topLayout.findViewWithTag<TextView>("option_total_price")
            dlg.topLayout.findViewWithTag<TextView>("tag_food_name")?.text = modelFoodItem.name

            totalOptionPriceText?.text = getOptionTotalPrice().toString()

            val inflater = LayoutInflater.from(dlg.context)
            val view = inflater.inflate(R.layout.dialog_elements_button,itemOptionContainerLayout,false)
            view.id_button.tag = "option_group_btn"
            view.id_button.text =  itemOptionArray[selectedOptionGroup].title

            selectedOptionGroup.equals(0).let { itemOptionArray[selectedOptionGroup].selected = true }
            val menu = PopupMenu(dlg.context, view.id_button)
            itemOptionArray.forEachIndexed { index, item ->
                menu.menu.add(Menu.NONE, index, index, item.title)
            }
            view.id_button.setOnClickListener {
                menu.show()
            }

            menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                selectedOptionGroup = it.itemId
                itemOptionArray.resetSelected()
                itemOptionArray[selectedOptionGroup].selected = true
                dlg.topLayout.findViewWithTag<Button>("option_group_btn")?.text = itemOptionArray[selectedOptionGroup].title
                buildOptionView(dlg.context)
                totalOptionPriceText?.text = getOptionTotalPrice().toString()
                true
            })

            dlg.topLayout?.addView(view)

            buildOptionView(dlg.context)

            addSeparatorToView(dlg.context,itemOptionContainerLayout)
        }

    }
    @SuppressLint("SetTextI18n")
    fun buildOptionView(context: Context){
        var selectedOptionGroup : Int = 0
        selectedOptionGroup = itemOptionArray.findSelectedOrZero()
        if(selectedOptionGroup == 0 && !itemOptionArray.isEmpty())
            itemOptionArray[0].selected = true

        itemOptionContainerLayout.removeAllViews()
        itemOptionContainerLayout.orientation = LinearLayout.VERTICAL
        itemOptionContainerLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)




        itemOptionArray[selectedOptionGroup].options?.forEachIndexed {optionsIndex, option ->
            when (option.type) {
                //title
                ITEM_OPTION_TYPE.OPTION_TITLE.value -> {

                    val inflater = LayoutInflater.from(context)
                    val view = inflater.inflate(R.layout.dialog_elements_text,itemOptionContainerLayout,false)
                    view.id_text.text = option.title
                    itemOptionContainerLayout.addView(view)
//                    val textView = TextView(context)
//                    textView.text = "option.title"
//                    itemOptionContainerLayout.addView(textView)
                    addSeparatorToView(context,itemOptionContainerLayout)
                }
                //checkbox
                ITEM_OPTION_TYPE.TITLED_CHECKBOX.value -> {

                    option.opts?.forEachIndexed {optIndex,  opt ->
                        val inflater = LayoutInflater.from(context)
                        val view = inflater.inflate(R.layout.dialog_elements_checkbox,itemOptionContainerLayout,false)
                        view.id_text.text = opt.title
                        view.id_price.text = opt.price.toString()
                        itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[optIndex].selected = false
                        //view.id_checkbox.isChecked = itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[optIndex].selected
                        view.setOnClickListener{
                            it.id_checkbox.isChecked = !it.id_checkbox.isChecked
                            itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[optIndex].selected = it.id_checkbox.isChecked
                            opt.selected = it.id_checkbox.isChecked
                            totalOptionPriceText?.text = getOptionTotalPrice().toString()
                        }
                        view.id_checkbox.setOnClickListener {
                            itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[optIndex].selected = view.id_checkbox.isChecked
                            opt.selected = view.id_checkbox.isChecked
                            totalOptionPriceText?.text = getOptionTotalPrice().toString()
                        }


                        itemOptionContainerLayout.addView(view)
                        addSeparatorToView(context,itemOptionContainerLayout)
                        /*
//                        val linearLayout = LinearLayout(context)
//                        linearLayout.isFocusable = true
//                        linearLayout.isClickable = true
//
//                        val attrs = intArrayOf(R.attr.selectableItemBackground)
//                        val typedArray: TypedArray = context.obtainStyledAttributes(attrs)
//                        val backgroundResource = typedArray.getResourceId(0, 0)
//                        linearLayout.setBackgroundResource(backgroundResource)
//                        typedArray.recycle()
//
//                        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//
//                        val relativeLayoutContainer = RelativeLayout(context)
//                        val relativeLayoutContainerParam = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//
//                        val _10sp = (10 / context.resources.displayMetrics.scaledDensity).toInt()
//                        relativeLayoutContainerParam.setMargins(50, 50, 50, 50)
//
//                        relativeLayoutContainer.layoutParams = relativeLayoutContainerParam
//                        relativeLayoutContainer.setPadding(_10sp, _10sp, _10sp, _10sp)
//
//                        linearLayout.addView(relativeLayoutContainer)
//
//                        val text_view = TextView(context)
//                        val textparam = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                        textparam.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
//                        text_view.layoutParams = textparam
//                        text_view.text = opt.title
//                        text_view.gravity = Gravity.CENTER_VERTICAL
//                        text_view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
//                        //text_view.setTextColor(Color.RED)
//                        //text_view.setTypeface(text_view.typeface,Typeface.BOLD_ITALIC)
//                        //text_view.setTypeface(Typeface.MONOSPACE)
//                        //text_view.setBackgroundColor(Color.YELLOW)
//
//
//                        val checkBox = CheckBox(context)
//                        val checkboxparam = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                        checkboxparam.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
//                        checkBox.layoutParams = checkboxparam
//                        checkBox.gravity = Gravity.CENTER
//                        checkBox.id = ViewCompat.generateViewId()
//                        //itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[optIndex].selected = checkBox.isChecked
//                        checkBox.setOnCheckedChangeListener{ view, isChecked ->
//                            itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[optIndex].selected = isChecked
//                        }
//
//
//
//
//                        //LayoutInflater.from(context).inflate()
//                        val text_view_price = TextView(context)
//                        val textparam_price = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
//                        textparam_price.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
//                        textparam_price.addRule(RelativeLayout.LEFT_OF, checkBox.id)
//                        text_view_price.layoutParams = textparam_price
//                        text_view_price.text = opt.price.toString()
//                        text_view_price.gravity = Gravity.CENTER_VERTICAL
//                        text_view_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
//
//                        opt.selected = false
//                        checkBox.isChecked = opt.selected
//
//                        relativeLayoutContainer.addView(text_view, 0)
//                        relativeLayoutContainer.addView(text_view_price, 1)
//
//                        relativeLayoutContainer.addView(checkBox, 2)
//
//                        itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[optIndex].selected = false
//                        linearLayout.setOnClickListener{
//                            checkBox.isChecked = !checkBox.isChecked
//                            //itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[optIndex].selected = checkBox.isChecked
//                            //opt.selected = checkBox.isSelected
//                        }
//                        itemOptionContainerLayout.addView(linearLayout)
//                        addSeparatorToView(context,itemOptionContainerLayout)
*/
                    }
                }
                //radio group
                ITEM_OPTION_TYPE.TITLED_GROUP.value -> {
                    val inflater = LayoutInflater.from(context)
                    val view = inflater.inflate(R.layout.dialog_elements_button,itemOptionContainerLayout,false)
                    view.id_button.tag = "radio_group_$optionsIndex"
                    option.opts?.let {
                        val selectedGroupId = option.opts?.findSelectedOrZero()
                        (selectedGroupId == 0).let { itemOptionArray[selectedOptionGroup].options[optionsIndex]?.opts[0].selected = true }

                        view.id_button.text = itemOptionArray[selectedOptionGroup].options[optionsIndex]?.title + ": "+
                            itemOptionArray[selectedOptionGroup].options[optionsIndex]?.opts[option.opts?.findSelectedOrZero()].title

                        val menu = PopupMenu(context, view.id_button)
                        option?.opts?.forEachIndexed { index, item ->
                            menu.menu.add(Menu.NONE, index, index, item.title)

                        }
                        view.id_button.setOnClickListener {
                            menu.show()
                        }
                        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { menuItem ->
                            option.opts.resetSelected()
                            itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[menuItem.itemId].selected = true
                            itemOptionContainerLayout.findViewWithTag<Button>("radio_group_$optionsIndex")
                                ?.let { btn ->
                                    btn.text =
                                        itemOptionArray[selectedOptionGroup].options[optionsIndex]?.title + ": "+ itemOptionArray[selectedOptionGroup].options[optionsIndex].opts[menuItem.itemId].title
                                }
                            totalOptionPriceText?.text = getOptionTotalPrice().toString()
                            true
                        })
                    }
                    itemOptionContainerLayout.addView(view)
                    addSeparatorToView(context,itemOptionContainerLayout)

                }
                else -> {}
            }
        }

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_elements_counter,itemOptionContainerLayout,false)
        view.id_quantity.text = modelFoodItem.basket_quantity.toString()
        itemOptionContainerLayout.addView(view)

        view.id_inc_button.setOnClickListener {
            modelFoodItem.basket_quantity++
            view.id_quantity.text = modelFoodItem.basket_quantity.toString()
            totalOptionPriceText?.text = getOptionTotalPrice().toString()
        }
        view.id_dec_button.setOnClickListener {
            modelFoodItem.basket_quantity--
            if(modelFoodItem.basket_quantity < 1) modelFoodItem.basket_quantity = 1
            view.id_quantity.text = modelFoodItem.basket_quantity.toString()
            totalOptionPriceText?.text = getOptionTotalPrice().toString()
        }

        this.dialog?.let {
            it.linearLayoutContainer?.removeAllViews()
            it.linearLayoutContainer?.addView(itemOptionContainerLayout)

            it.add_to_basket_button?.setOnClickListener{
                val itemOptionArrayCopy = itemOptionArray.map{ it.deepCopy() }
                val modelFoodItemCopy = modelFoodItem.deepCopy()
                //deepCopy=deepCopy.filter{ it.selected}
                Basket.instance.items.add(FoodItemInBasket(modelFoodItemCopy,itemOptionArrayCopy,selectedOptionGroup))
                //Toast.makeText(dialog.context,itemOptions[selectedOptionGroup].options.toString(),Toast.LENGTH_LONG).show()
                (activity as MainActivity).updateTotalPrice()
            }
            it.dismiss_button?.visibility = View.VISIBLE

            //!!
//            Basket.instance.items.add(FoodItemInBasket(modelFoodItem,itemOptionArray.map{it.deepCopy()},selectedOptionGroup))
//            Basket.instance.items.add(FoodItemInBasket(modelFoodItem,itemOptionArray.map{it.deepCopy()},selectedOptionGroup))
//            Basket.instance.items.add(FoodItemInBasket(modelFoodItem,itemOptionArray.map{it.deepCopy()},selectedOptionGroup))
//            Basket.instance.items.add(FoodItemInBasket(modelFoodItem,itemOptionArray.map{it.deepCopy()},selectedOptionGroup))
            ///!!
        }


    }
}