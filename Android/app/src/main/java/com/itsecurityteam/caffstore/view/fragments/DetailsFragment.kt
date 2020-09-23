package com.itsecurityteam.caffstore.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.exceptions.AndroidException
import com.itsecurityteam.caffstore.model.Caff
import com.itsecurityteam.caffstore.view.adapters.CommentAdapter
import com.itsecurityteam.caffstore.viewmodel.StoreViewModel
import kotlinx.android.synthetic.main.fragment_details.*
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class DetailsFragment : Fragment() {
    companion object {
        const val SAVE_FILE_INTENT = 1007
    }

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
            try {
                var selected = (viewModel.SelectedCaff.value?.name ?: "untitled")
                selected = selected.replace(" ", "_").replace("/", "")
                    .replace("\\", "")

                val uri =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath

                var i = 1
                var append = ""
                while (true) {
                    val file = File(uri, "$selected$append.caff")
                    if (file.exists()) {
                        append = "_$i"
                        i++
                        continue
                    }

                    file.createNewFile()
                    viewModel.downloadCaff(file.toUri())
                    break
                }
            } catch (exception: AndroidException) {
                view.let { w ->
                    Snackbar.make(w, exception.stringCode, Snackbar.LENGTH_SHORT).show()
                }
            } catch (exception: Exception) {
                view.let { w ->
                    Snackbar.make(w, exception.toString(), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        btAddComment.setOnClickListener {
            inputComment()
        }

        btBuy.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.confirm).setMessage(R.string.confirm_buy)
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    viewModel.buy()
                    dialog.dismiss()
                }.setNegativeButton(android.R.string.no) { dialog, _ ->
                    btBuy.isEnabled = true
                    dialog.cancel()
                }

            btBuy.isEnabled = false
            builder.show()
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

        viewModel.Result.observe(viewLifecycleOwner) { res ->
            res?.let { result ->
                viewModel.resultProcessed()

                val text = when (result.resultCode) {
                    StoreViewModel.DOWNLOAD_REQUEST -> R.string.download_successfull
                    StoreViewModel.ADD_COMMENT_REQUEST -> R.string.comment_added
                    StoreViewModel.BUY_REQUEST -> R.string.bought_success
                    else -> throw Exception("Option not available")
                }

                when (result.success) {
                    true -> view?.let {
                        Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show()
                    }
                    false -> view?.let {
                        Snackbar.make(it, result.errorStringCode, Snackbar.LENGTH_SHORT).show()
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

            btBuy.text = getString(R.string.buy, it.cost)

            if (it.bought) {
                btBuy.visibility = View.GONE
                btDownload.visibility = View.VISIBLE
            } else {
                btBuy.visibility = View.VISIBLE
                btDownload.visibility = View.GONE
            }
        }
    }

    private fun inputComment() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.add_comment)
        builder.setView(R.layout.dialog_comment)

        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val editText = (dialog as AlertDialog).findViewById<TextInputEditText>(R.id.tietComment)
            viewModel.addComment(editText.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}