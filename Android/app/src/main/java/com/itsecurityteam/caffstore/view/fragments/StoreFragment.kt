package com.itsecurityteam.caffstore.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.view.adapters.CaffsAdapter
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
            viewModel.uploadCaff()
        }

        ibSearch.setOnClickListener {

        }

        viewModel.Result.observe(viewLifecycleOwner) {

        }
    }

    private fun addRecyclerView() {
        rvCaffs.layoutManager = GridLayoutManager(context, 1)
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
}