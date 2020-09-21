package com.itsecurityteam.caffstore.view.fragments

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.view.adapters.CaffsAdapter
import com.itsecurityteam.caffstore.view.dialog.SearchDialog
import com.itsecurityteam.caffstore.viewmodel.StoreViewModel
import kotlinx.android.synthetic.main.fragment_store.*

class StoreFragment : Fragment() {
    private lateinit var viewModel: StoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]

        addRecyclerView()

        ibSignOut.setOnClickListener {
            viewModel.sigOut()
            NavHostFragment.findNavController(this).navigate(R.id.action_store_to_login)
        }

        ibUpload.setOnClickListener {
            inputName()
        }

        ibSearch.setOnClickListener {
            val dialog = SearchDialog()
            dialog.show(activity?.supportFragmentManager!!, "")
        }

        viewModel.Result.observe(viewLifecycleOwner) { result ->
            if (result?.resultCode == StoreViewModel.UPLOAD_REQUEST) {
                viewModel.resultProcessed()

                when (result.success) {
                    true -> view.let {
                        Snackbar.make(it, R.string.upload_success, Snackbar.LENGTH_SHORT).show()
                    }
                    false -> view.let {
                        Snackbar.make(it, result.errorCode, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun addRecyclerView() {
        rvCaffs.layoutManager =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            } else GridLayoutManager(context, 1)


        val adapter = CaffsAdapter()

        viewModel.Caffs.observe(viewLifecycleOwner) {
            adapter.Caffs = it
            tvResults.text = if (it.isNotEmpty()) getString(
                R.string.results,
                it.size
            ) else getString(R.string.not_found)
        }

        adapter.setOnClickListener {
            viewModel.select(it)
            NavHostFragment.findNavController(this).navigate(R.id.action_store_to_details)
        }

        rvCaffs.adapter = adapter
    }

    private fun inputName() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.add_name)
        builder.setView(R.layout.dialog_upload)

        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val editText = (dialog as AlertDialog).findViewById<TextInputEditText>(R.id.tietName)
            viewModel.uploadCaff(editText.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}