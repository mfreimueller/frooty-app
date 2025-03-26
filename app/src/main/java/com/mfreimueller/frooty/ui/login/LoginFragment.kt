package com.mfreimueller.frooty.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.mfreimueller.frooty.MainActivity
import com.mfreimueller.frooty.R
import com.mfreimueller.frooty.dataStore
import com.mfreimueller.frooty.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels { LoginViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val loginButton: Button = binding.loginButton
        val registerButton: Button = binding.registerButton

        val usernameTextView: TextView = binding.usernameText
        val passwordTextView: TextView = binding.passwordText

        loginButton.setOnClickListener {
            val username = usernameTextView.text.toString()
            val password = passwordTextView.text.toString()

            loginViewModel.login(username, password).observe(viewLifecycleOwner, Observer<Result<Boolean>> { result ->
                result.onSuccess {
                    MainActivity.navigateToHome()
                }.onFailure {
                    // TODO: show error message
                }
            })
        }

        registerButton.setOnClickListener {
            // TODO:
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.replace(R.id.login_view, RegisterFragment())
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}