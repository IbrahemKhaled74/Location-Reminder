package com.udacity.project4.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.FragmentAuthBinding
import com.udacity.project4.locationreminders.RemindersActivity

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding
    val viewModel: LoginViewModel by viewModels()
//    companion object {
//        const val SIGN_IN_RESULT_CODE = 1001
//    }
lateinit var signInLauncher: ActivityResultLauncher<Intent>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_auth, container, false)

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginSign.setOnClickListener {

            launchSignIn()
        }
        signInLauncher = registerForActivityResult(
            FirebaseAuthUIActivityResultContract()
        ) { result ->
            onSignInResult(result)

        }
        startSignIn()


    }

    private fun goToRemindersActivity(){
        val intent= Intent(requireContext(), RemindersActivity::class.java)
        startActivity(intent)

    }




    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(requireContext(), "${user?.email}", Toast.LENGTH_LONG).show()
            // ...
        } else {
            Toast.makeText(requireContext(), "${response?.error}", Toast.LENGTH_LONG).show()
        }
    }
    private fun startSignIn(){
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    goToRemindersActivity()
                }
                LoginViewModel.AuthenticationState.UNAUTHENTICATED  -> {
                    binding.loginSign.setOnClickListener {
                        launchSignIn()
                    }
                }
                else -> {}
            }
        })


//// Create and launch sign-in intent
    }
    private fun launchSignIn(){
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        //custom layout
        val customLayout = AuthMethodPickerLayout
            .Builder(R.layout.custome_sing_in)
            .setGoogleButtonId(R.id.google_sign_in_button)
            .setEmailButtonId(R.id.email_sign_in_button) // ...
            .build()


        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.AppTheme)
            .setAuthMethodPickerLayout(customLayout)
            .build()




        signInLauncher.launch(signInIntent)

    }


}