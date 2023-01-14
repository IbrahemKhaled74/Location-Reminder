package com.udacity.project4.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    val viewModel: LoginViewModel by viewModels()
    lateinit var signInLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        login_sign.setOnClickListener {

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
        val intent= Intent(this, RemindersActivity::class.java)
        startActivity(intent)

    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "${user?.email}", Toast.LENGTH_LONG).show()
            // ...
        } else {
            Toast.makeText(this, "${response?.error}", Toast.LENGTH_LONG).show()
        }
    }
    private fun startSignIn(){
        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    goToRemindersActivity()
                }
                LoginViewModel.AuthenticationState.UNAUTHENTICATED  -> {
                    login_sign.setOnClickListener {
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
