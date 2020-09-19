package com.itsecurityteam.caffstore.view.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.exceptions.ValidationException
import com.itsecurityteam.caffstore.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        btToLogin.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_register_to_login)
        }

        btRegister.setOnClickListener {
            var ok = validateField(tilRegName) { viewModel.validateName(it) }
            ok = ok && validateField(tilRegPassword) { viewModel.validatePassword(it) }
            ok = ok && validateField(tilRegPasswordConfirm) {
                val pass = tilRegPassword.editText!!.text!!.toString()
                val confirm = it!!.toString()

                if (pass != confirm) throw ValidationException(R.string.app_name)
            }
            ok = ok && validateField(tilRegEmail) { viewModel.validateEmail(it) }

            if (!ok) return@setOnClickListener

            tvRegError?.let {
                it.text = null
                it.visibility = View.GONE
            }

            viewModel.register(
                tietRegName.text.toString(), tietRegEmail.text.toString(),
                tietRegPassword.text.toString()
            )
        }

        viewModel.NetworkResult.observe(viewLifecycleOwner) { result ->
            Log.i("RegisterFragment", "Network result observed")

            when (result.success) {
                true -> getView()?.let {
                    Snackbar
                        .make(it, "Registration successfull", Snackbar.LENGTH_LONG)
                        .setAction("Vissza") {
                            btToLogin.performClick()
                        }.show()
                }
                false -> {
                    tvRegError?.let {
                        it.text = getString(result.errorCode)
                        it.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun validateField(
        layout: TextInputLayout,
        validator: (text: Editable?) -> Unit
    ): Boolean {
        val editText = layout.editText ?: return false
        val text = editText.text ?: return false

        try {
            if (text.isEmpty()) throw ValidationException(R.string.error_empty_input)

            validator(text)
            layout.error = null
        } catch (exception: ValidationException) {
            layout.error = getString(exception.stringCode)
            return false
        } catch (exception: Exception) {
            layout.error = exception.message
            return false
        }

        return true
    }
}