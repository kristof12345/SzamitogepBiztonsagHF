package com.itsecurityteam.caffstore.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.view.adapters.CaffsAdapter
import com.itsecurityteam.caffstore.viewmodel.LoginViewModel
import com.itsecurityteam.caffstore.viewmodel.StoreViewModel
import kotlinx.android.synthetic.main.fragment_store.*

class StoreFragment : Fragment() {
    lateinit var viewModel: StoreViewModel

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


        rvCaffs.layoutManager = GridLayoutManager(context, 1)
        val adapter = CaffsAdapter()

        viewModel.Caffs.observe(viewLifecycleOwner) {
            adapter.Caffs = it
        }

        adapter.setOnClickListener {
            viewModel.select(it)
            NavHostFragment.findNavController(this).navigate(R.id.action_store_to_details)
        }

        rvCaffs.adapter = adapter
    }
}