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
import com.google.android.material.textfield.TextInputLayout
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.exceptions.AndroidException
import com.itsecurityteam.caffstore.model.ViewResult
import com.itsecurityteam.caffstore.viewmodel.LoginViewModel
import com.itsecurityteam.caffstore.viewmodel.StoreViewModel
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel

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
        val vm = ViewModelProvider(requireActivity())[StoreViewModel::class.java]
        vm.sigOut()


        btToRegister.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_login_to_register)
        }

        btLogin.setOnClickListener {
            login()
        }

        viewModel.networkResultProp.observe(viewLifecycleOwner) { result -> handleLoginRequest(result) }
    }

    private fun login() {
        var ok = validateField(tilLoginName) { viewModel.validateName(it) }
        ok = ok && validateField(tilLoginPassword) { viewModel.validatePassword(it) }
        if (!ok) return

        tvLoginError?.let {
            it.text = null
            it.visibility = View.GONE
        }

        setAvailability(false)
        viewModel.login(tietLoginName.text.toString(), tietLoginPassword.text.toString())
    }

    private fun handleLoginRequest(result: ViewResult?) {
        Log.i("LoginFragment", "Network result observed $result")
        if (result?.resultCode == LoginViewModel.LOGIN_REQUEST) {
            viewModel.resultProcessed()

            when (result.success) {
                true -> {
                    val vm = ViewModelProvider(requireActivity())[StoreViewModel::class.java]
                    vm.signIn(viewModel.userId)
                    viewModel.userId = -1

                    NavHostFragment.findNavController(this).navigate(R.id.action_login_to_store)
                }
                false -> {
                    tvLoginError?.let {
                        it.text = getString(result.errorStringCode)
                        it.visibility = View.VISIBLE
                    }

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
        btLogin.isEnabled = enabled
        btToRegister.isEnabled = enabled
        tietLoginName.isEnabled = enabled
        tietLoginPassword.isEnabled = enabled
    }
}