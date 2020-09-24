package com.itsecurityteam.caffstore.view.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.model.filter.OrderBy
import com.itsecurityteam.caffstore.model.filter.OrderDirection
import com.itsecurityteam.caffstore.viewmodel.StoreViewModel


class SearchDialog : DialogFragment() {
    private lateinit var viewModel: StoreViewModel

    private lateinit var btMore: Button
    private lateinit var btLess: Button

    private lateinit var rgType: RadioGroup
    private lateinit var rgOrder: RadioGroup

    private lateinit var tilTitle: TextInputLayout
    private lateinit var tilCreator: TextInputLayout

    private lateinit var clMore: ConstraintLayout

    private lateinit var cbBought: CheckBox
    private lateinit var cbFree: CheckBox

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflate = requireActivity().layoutInflater

        val view = inflate.inflate(R.layout.dialog_search, null, false)

        setView(view, builder)
        setDefaults()

        return builder.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    private fun setView(view: View, builder: AlertDialog.Builder) {
        btMore = view.findViewById(R.id.btMore)
        btLess = view.findViewById(R.id.btLess)
        rgType = view.findViewById(R.id.rgType)
        rgOrder = view.findViewById(R.id.rgDir)
        tilTitle = view.findViewById(R.id.tilSearchTitle)
        tilCreator = view.findViewById(R.id.tilSearchCreator)
        clMore = view.findViewById(R.id.clDetails)
        cbBought = view.findViewById(R.id.cbBought)
        cbFree = view.findViewById(R.id.cbFree)

        btMore.setOnClickListener {
            btMore.visibility = View.GONE
            clMore.visibility = View.VISIBLE
            btLess.visibility = View.VISIBLE
        }

        btLess.setOnClickListener {
            btMore.visibility = View.VISIBLE
            btLess.visibility = View.GONE
            clMore.visibility = View.GONE
        }

        builder.setView(view)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                setFilter()
                setOrder()
                setCheckboxes()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
    }

    private fun setDefaults() {
        tilCreator.editText?.setText(viewModel.filter.creator)
        tilTitle.editText?.setText(viewModel.filter.title)

        rgType.check(when (viewModel.orderBy){
            OrderBy.Date -> R.id.rbDate
            OrderBy.Creator -> R.id.rbCreator
            OrderBy.Name -> R.id.rbName
            OrderBy.Length -> R.id.rbLength
        })

        rgOrder.check(when(viewModel.orderDir) {
            OrderDirection.Ascending -> R.id.rbAscend
            OrderDirection.Descending -> R.id.rbDescend
        })

        cbBought.isChecked = viewModel.bought
        cbFree.isChecked = viewModel.free
    }

    private fun setFilter() {
        val title = tilTitle.editText!!.text.toString()
        val creator = tilCreator.editText!!.text.toString()
        viewModel.setFilter(title, creator)
    }

    private fun setOrder() {
        val orderBy = when(rgType.checkedRadioButtonId){
            R.id.rbDate -> OrderBy.Date
            R.id.rbCreator -> OrderBy.Creator
            R.id.rbLength -> OrderBy.Length
            R.id.rbName -> OrderBy.Name
            else -> OrderBy.Date
        }

        val orderDir = when(rgOrder.checkedRadioButtonId){
            R.id.rbAscend -> OrderDirection.Ascending
            R.id.rbDescend -> OrderDirection.Descending
            else -> OrderDirection.Descending
        }

        viewModel.setOrdering(orderBy, orderDir)
    }

    private fun setCheckboxes() {
        viewModel.setCheckbox(cbFree.isChecked, cbBought.isChecked)
    }
}