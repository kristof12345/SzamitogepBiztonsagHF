package com.itsecurityteam.caffstore.view.fragments

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.textfield.TextInputEditText
import com.itsecurityteam.caffstore.R
import com.itsecurityteam.caffstore.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.fragment_login.*


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

        addTextValidator(tietLoginName) { viewModel.validateName(it) }
        addTextValidator(tietLoginPassword) { viewModel.validatePassword(it) }

        btToRegister.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_login_to_register)
        }

        btLogin.setOnClickListener {

        }
    }

    private fun addTextValidator(editText: TextInputEditText, validator: (text: Editable?) -> Unit) {
        editText.addTextChangedListener { text: Editable? ->
            try {
                validator(text)
            } catch (exception: RuntimeException) {
                editText.error = exception.message
            }
        }
    }
}