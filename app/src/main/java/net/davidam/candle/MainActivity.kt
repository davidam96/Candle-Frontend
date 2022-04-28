package net.davidam.candle

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import net.davidam.candle.databinding.ActivityMainBinding
import net.davidam.candle.fragments.AccountFragment
import net.davidam.candle.fragments.PracticeFragment
import net.davidam.candle.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var user: FirebaseUser? = null

    companion object {
        private const val TAG = "dabudin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Access the layout IDs via binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Start Firestore and its services
        bootFirebase()

        //Bottom navigation view binding
        val navView: BottomNavigationView = binding.navView

        //Setting bottom navigation view listener
        navViewListener(navView)
        //Setting initial fragment (search bar)
        setInitialFragment()
    }



    // **************** FIREBASE ****************
    private fun bootFirebase() {
        //Create firebase instance
        FirebaseApp.initializeApp(this)
        //Start Sign-In flow
        bootSignIn()
    }

    private fun bootSignIn() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    //Sign-In Result Launcher
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "LOGIN CORRECTO: '${user!!.email}'", Toast.LENGTH_LONG).show()
            //...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Log.d(TAG, "FirebaseUI error code: '${response!!.error!!.errorCode}' \n" +
                    "To get more information go to: https://github.com/firebase/FirebaseUI-Android" +
                    "/blob/master/auth/src/main/java/com/firebase/ui/auth/ErrorCodes.java")
        }
    }
    // **************** FIREBASE ****************



    // ****************** VIEW ******************
    private fun navViewListener(navView: BottomNavigationView) {
        navView.setOnItemSelectedListener{
            var fragment: Fragment? = null
            when (it.itemId) {
                R.id.fragment_search -> {
                    fragment = SearchFragment()
                }
                R.id.fragment_practice -> {
                    fragment = PracticeFragment()
                }
                R.id.fragment_account -> {
                    fragment = AccountFragment()
                }
            }
            replaceFragment(fragment!!)
            return@setOnItemSelectedListener true
        }
    }

    private fun setInitialFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame, SearchFragment())
        fragmentTransaction.commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }
    // ****************** VIEW ******************
}