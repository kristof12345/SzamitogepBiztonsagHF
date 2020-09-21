package com.itsecurityteam.caffstore.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.model.Caff
import com.itsecurityteam.caffstore.view.adapters.CommentAdapter
import com.itsecurityteam.caffstore.viewmodel.StoreViewModel
import kotlinx.android.synthetic.main.fragment_details.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class DetailsFragment : Fragment() {
    lateinit var viewModel: StoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]


        addCommentsView()

        viewModel.SelectedCaff.observe(viewLifecycleOwner) {
            setView(it)
        }

        btDownload.setOnClickListener {
            viewModel.downloadCaff()
        }

        btAddComment.setOnClickListener {
            inputComment()
        }
    }

    private fun addCommentsView() {
        rvComments.layoutManager = GridLayoutManager(context, 1)
        val adapter = CommentAdapter()
        rvComments.adapter = adapter

        viewModel.Comments.observe(viewLifecycleOwner) {
            adapter.Comments = it
            tvNoComment.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.Result.observe(viewLifecycleOwner) { result ->
            if (result?.resultCode == StoreViewModel.ADD_COMMENT_REQUEST) {
                viewModel.resultProcessed()
                when (result.success) {
                    true -> view?.let {
                        Snackbar.make(it, R.string.comment_added, Snackbar.LENGTH_SHORT).show()
                    }
                    false -> view?.let {
                        Snackbar.make(it, result.errorCode, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setView(it: Caff?) {
        it?.let {
            Log.d("Image visibility", "$it - ${ivDetailsImage.visibility}")
            if (it.image == null) {
                ivDetailsImage.visibility = View.GONE
                ivDetailsImage.maxHeight = 0
                Log.d("Image visibility", "Invisible")
            } else {
                ivDetailsImage.setImageBitmap(it.image)
                ivDetailsImage.maxHeight = (resources.displayMetrics.heightPixels * 0.75).toInt()
                ivDetailsImage.visibility = View.VISIBLE
                Log.d("Image visibility", "Visible")
            }

            tvDetailsCreator.text = it.creator

            val formatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
                    .withZone(ZoneId.systemDefault())
            tvDetailsDate.text = it.creationDate.format(formatter)


            val mins = it.length / 1000 / 60
            val secs = it.length / 1000 - mins * 60
            val ms = it.length - (mins * 60 + secs) * 1000

            tvLength.text = getString(R.string.length_value, mins, secs, ms)
            tvtDetailsTitle.text = it.name
        }
    }

    private fun inputComment() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.add_comment)
        builder.setView(R.layout.dialog_comment)

        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val editText = (dialog as AlertDialog).findViewById<TextInputEditText>(R.id.tietComment)
            viewModel.addComment(editText.toString())
            dialog.cancel()
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    override fun onStop() {
        super.onStop()
        viewModel.deselect()
    }
}