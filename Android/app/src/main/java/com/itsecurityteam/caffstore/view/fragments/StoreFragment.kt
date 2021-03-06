package com.itsecurityteam.caffstore.view.fragments

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
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
import com.itsecurityteam.caffstore.exceptions.AndroidException
import com.itsecurityteam.caffstore.view.adapters.CaffsAdapter
import com.itsecurityteam.caffstore.view.dialog.SearchDialog
import com.itsecurityteam.caffstore.viewmodel.StoreViewModel
import kotlinx.android.synthetic.main.fragment_store.*
import java.lang.Exception
import java.util.*

class StoreFragment : Fragment() {
    companion object {
        const val FILE_OPEN_INTENT = 1006
    }

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
            val openFileIntent = Intent()
            openFileIntent.type = "*/*"
            openFileIntent.action = Intent.ACTION_OPEN_DOCUMENT

            startActivityForResult(
                Intent.createChooser(
                    openFileIntent,
                    getString(R.string.open_caff)
                ), FILE_OPEN_INTENT
            )
        }

        ibSearch.setOnClickListener {
            val dialog = SearchDialog()
            dialog.show(activity?.supportFragmentManager!!, "")
        }

        viewModel.resultProp.observe(viewLifecycleOwner) { result ->
            if (result?.resultCode == StoreViewModel.UPLOAD_REQUEST) {
                viewModel.resultProcessed()

                when (result.success) {
                    true -> view.let {
                        Snackbar.make(it, R.string.upload_success, Snackbar.LENGTH_SHORT).show()
                    }
                    false -> view.let {
                        Snackbar.make(it, result.errorStringCode, Snackbar.LENGTH_SHORT).show()
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

        viewModel.caffsProp.observe(viewLifecycleOwner) {
            adapter.caffProp = it
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

    private fun inputName(uri: Uri) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.add_name)
        builder.setView(R.layout.dialog_upload)

        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val editText = (dialog as AlertDialog).findViewById<TextInputEditText>(R.id.tietName)
            val price = dialog.findViewById<TextInputEditText>(R.id.tietPrice)

            viewModel.uploadCaff(editText.text.toString(), price.text.toString().toDouble(), uri)
            dialog.dismiss()
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_OPEN_INTENT && resultCode == RESULT_OK) {
            data?.data?.let {
                try {
                    if (it.encodedPath?.split(".")?.last()?.toLowerCase(Locale.ROOT) == "caff") {
                        inputName(it)
                    } else {
                        throw AndroidException(R.string.invalid_file_type)
                    }
                } catch (exception: AndroidException) {
                    view?.let { w ->
                        Snackbar.make(w, exception.stringCode, Snackbar.LENGTH_SHORT).show()
                    }
                } catch (exception: Exception) {
                    view?.let { w ->
                        Snackbar.make(w, exception.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}