package com.itsecurityteam.caffstore.view.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.model.filter.OrderBy
import com.itsecurityteam.caffstore.model.filter.OrderDirection
import com.itsecurityteam.caffstore.viewmodel.LoginViewModel
import com.itsecurityteam.caffstore.viewmodel.StoreViewModel
import kotlinx.android.synthetic.main.dialog_search.*


class SearchDialog : DialogFragment() {
    lateinit var viewModel: StoreViewModel

    lateinit var btMore: Button
    lateinit var btLess: Button

    lateinit var rgType: RadioGroup
    lateinit var rgOrder: RadioGroup

    lateinit var tilTitle: TextInputLayout
    lateinit var tilCreator: TextInputLayout

    lateinit var clMore: ConstraintLayout

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
}