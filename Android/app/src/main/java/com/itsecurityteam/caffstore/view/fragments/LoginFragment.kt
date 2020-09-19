package com.itsecurityteam.caffstore.view.fragments

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.exceptions.ValidationException
import com.itsecurityteam.caffstore.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*


class LoginFragment : Fragment() {
    lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        btToRegister.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_login_to_register)
        }

        btLogin.setOnClickListener { btn ->
            var ok = validateField(tilLoginName) { viewModel.validateName(it) }
            ok = ok && validateField(tilLoginPassword) { viewModel.validatePassword(it) }
            if (!ok) return@setOnClickListener

            tvLoginError?.let {
                it.text = null
                it.visibility = View.GONE
            }

            setAvailability(false)

            viewModel.login(tietLoginName.text.toString(), tietLoginPassword.text.toString())
        }

        viewModel.NetworkResult.observe(viewLifecycleOwner) {result ->
            Log.i("LoginFragment", "Network result observed")

            when (result.success) {
                true -> {
                    // Transact to activity
                }
                false -> {
                    tvLoginError?.let {
                        it.text = getString(result.errorCode)
                        it.visibility = View.VISIBLE
                    }

                    // TODO: Check on return what happens (from next activity to this)
                    setAvailability(true)
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

    private fun setAvailability(enabled: Boolean) {
        btLogin.isEnabled = enabled
        btToRegister.isEnabled = enabled
        tietLoginName.isEnabled = enabled
        tietLoginPassword.isEnabled = enabled
    }
}