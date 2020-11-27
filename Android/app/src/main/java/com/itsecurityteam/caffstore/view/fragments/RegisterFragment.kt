package com.itsecurityteam.caffstore.view.fragments

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
import com.itsecurityteam.caffstore.exceptions.AndroidException
import com.itsecurityteam.caffstore.model.ViewResult
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
            register()
        }

        viewModel.networkResultProp.observe(viewLifecycleOwner) { result ->
            handleRegistration(result)
        }
    }

    private fun handleRegistration(result: ViewResult?) {
        if (result?.resultCode == LoginViewModel.REGISTER_REQUEST) {
            viewModel.resultProcessed()
            setAvailability(true)

            when (result.success) {
                true -> view?.let {
                    Snackbar
                        .make(it, getString(R.string.reg_success), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.back)) {
                            btToLogin.performClick()
                        }.show()
                }
                false -> {
                    tvRegError?.let {
                        it.text = getString(result.errorStringCode)
                        it.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun register() {
        var ok = validateField(tilRegName) { viewModel.validateName(it) }
        ok = ok && validateField(tilRegPassword) { viewModel.validatePassword(it) }
        ok = ok && validateField(tilRegPasswordConfirm) {
            val pass = tilRegPassword.editText!!.text!!.toString()
            val confirm = it!!.toString()

            if (pass != confirm) throw AndroidException(R.string.confirm_mismatch)
        }
        ok = ok && validateField(tilRegEmail) { viewModel.validateEmail(it) }

        if (!ok) return

        tvRegError?.let {
            it.text = null
            it.visibility = View.GONE
        }

        setAvailability(false)
        viewModel.register(
            tietRegName.text.toString(), tietRegEmail.text.toString(),
            tietRegPassword.text.toString()
        )
    }

    private fun validateField(
        layout: TextInputLayout,
        validator: (text: Editable?) -> Unit
    ): Boolean {
        val editText = layout.editText ?: return false
        val text = editText.text ?: return false

        try {
            if (text.isEmpty()) throw AndroidException(R.string.error_empty_input)

            validator(text)
            layout.error = null
        } catch (exception: AndroidException) {
            layout.error = getString(exception.stringCode)
            return false
        } catch (exception: Exception) {
            layout.error = exception.message
            return false
        }

        return true
    }

    private fun setAvailability(enabled: Boolean) {
        btRegister.isEnabled = enabled
        btToLogin.isEnabled = enabled
        tietRegEmail.isEnabled = enabled
        tietRegName.isEnabled = enabled
        tietRegPassword.isEnabled = enabled
        tietRegPasswordConfirm.isEnabled = enabled
    }
}