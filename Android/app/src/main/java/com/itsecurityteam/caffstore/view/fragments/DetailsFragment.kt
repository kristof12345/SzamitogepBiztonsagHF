package com.itsecurityteam.caffstore.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.exceptions.AndroidException
import com.itsecurityteam.caffstore.model.Caff
import com.itsecurityteam.caffstore.model.responses.UserType
import com.itsecurityteam.caffstore.view.adapters.CommentAdapter
import com.itsecurityteam.caffstore.viewmodel.StoreViewModel
import kotlinx.android.synthetic.main.fragment_details.*
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class DetailsFragment : Fragment() {
    private lateinit var viewModel: StoreViewModel
    private var dialog: Dialog? = null

    private var bitmapAvailable: Boolean = false

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

        ivDetailsImage.setOnClickListener {
            if (showDialog()) return@setOnClickListener
        }

        viewModel.selectedCaffProp.observe(viewLifecycleOwner) {
            setView(it)
        }

        btDownload.setOnClickListener {
            try {
                var selected = (viewModel.selectedCaffProp.value?.name ?: "untitled")
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
                    enable(true)
                    dialog.cancel()
                }

            enable(false)
            builder.show()
        }

        btDelete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.confirm).setMessage(R.string.delete_confirm)
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.removeCurrentCaff()
                }.setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.cancel()
                    enable(true)
                }

            builder.show()
            enable(false)
        }

        btDelete.visibility = if (viewModel.user == UserType.Admin) View.VISIBLE else View.GONE
    }

    private fun showDialog(): Boolean {
        if (!bitmapAvailable) return true
        context?.let { it1 ->
            val dialogTmp = Dialog(it1, R.style.AppTheme)
            dialogTmp.window?.setBackgroundDrawable(ColorDrawable(Color.argb(0.8f, 0f, 0f, 0f)))
            dialogTmp.setContentView(R.layout.dialog_image)

            val image = dialogTmp.findViewById<ImageView>(R.id.ivDialogImage)
            val close = dialogTmp.findViewById<ImageButton>(R.id.ibDialogClose)

            image.setImageBitmap(viewModel.selectedCaffProp.value?.image)
            close.setOnClickListener {
                dialogTmp.cancel()
            }

            dialogTmp.setOnCancelListener {
                viewModel.modalPressed = false
            }

            dialog = dialogTmp
            viewModel.modalPressed = true
            dialogTmp.show()
        }
        return false
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }

    private fun addCommentsView() {
        rvComments.layoutManager = GridLayoutManager(context, 1)
        val adapter = CommentAdapter(viewModel.user == UserType.Admin)
        adapter.setOnDeleteListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.confirm).setMessage(R.string.delete_confirm)
                .setPositiveButton(android.R.string.yes) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.removeComment(it)
                }.setNegativeButton(android.R.string.no) { dialog, _ ->
                    dialog.cancel()
                }

            builder.show()
        }

        rvComments.adapter = adapter

        viewModel.commentsProp.observe(viewLifecycleOwner) {
            adapter.commentsProp = it
            tvNoComment.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.resultProp.observe(viewLifecycleOwner) { res ->
            res?.let { result ->
                viewModel.resultProcessed()

                val text = when (result.resultCode) {
                    StoreViewModel.DOWNLOAD_REQUEST -> R.string.download_successfull
                    StoreViewModel.ADD_COMMENT_REQUEST -> R.string.comment_added
                    StoreViewModel.BUY_REQUEST -> R.string.bought_success
                    StoreViewModel.REMOVE_COMMENT_REQUEST -> R.string.comment_delete_conf
                    StoreViewModel.REMOVE_CAFF_REQUEST -> R.string.caff_delete_conf
                    else -> throw Exception("Option not available")
                }

                when (result.success) {
                    true -> view?.let {
                        Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show()
                        if (result.resultCode == StoreViewModel.REMOVE_CAFF_REQUEST) {
                            NavHostFragment.findNavController(this).navigate(R.id.action_details_to_store)
                        }
                        enable(true)
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
            if (it.image == null) {
                ivDetailsImage.visibility = View.GONE
                ivDetailsImage.maxHeight = 0
            } else {
                ivDetailsImage.setImageBitmap(it.image)
                ivDetailsImage.maxHeight = (resources.displayMetrics.heightPixels * 0.75).toInt()
                ivDetailsImage.visibility = View.VISIBLE
                bitmapAvailable = true
            }

            tvDetailsCreator.text = it.creator

            val formatter =
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
                    .withZone(ZoneId.systemDefault())
            tvDetailsDate.text = it.creationDate.format(formatter)


            val mins = it.duration / 1000 / 60
            val secs = it.duration / 1000 - mins * 60
            val ms = it.duration - (mins * 60 + secs) * 1000

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

            if (viewModel.modalPressed) ivDetailsImage.performClick()
        }
    }

    private fun inputComment() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.add_comment)
        builder.setView(R.layout.dialog_comment)

        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val editText = (dialog as AlertDialog).findViewById<TextInputEditText>(R.id.tietComment)
            viewModel.addComment(editText.text.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun enable(value: Boolean) {
        btBuy.isEnabled = value
        btDownload.isEnabled = value
        btDelete.isEnabled = value
    }
}