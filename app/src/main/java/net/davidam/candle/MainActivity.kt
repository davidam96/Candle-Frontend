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
import com.google.firebase.firestore.FirebaseFirestore
import net.davidam.candle.databinding.ActivityMainBinding
import net.davidam.candle.fragments.AccountFragment
import net.davidam.candle.fragments.PracticeFragment
import net.davidam.candle.fragments.SearchFragment
import net.davidam.candle.model.User

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private var auth: FirebaseUser? = null

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
        //Create firebase and firestore instances
        val app = FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance(app!!)
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
            auth = FirebaseAuth.getInstance().currentUser
            val user = User(auth!!.uid, "${auth!!.email}",
                "", "", "${auth!!.photoUrl}")
            Log.d(TAG, "LOGIN CORRECTO: ${user.email}")

            //  We make sure to store the user information in Firestore
            //  (POR HACER):
            //  1) (put these lines as a function inside the 'model' folder and call it from here)
            //  2) (skip this whole signIn part after a first succesful login, using persistence)
            //  3) Retocar la funcion de CloudFunctions.kt > SearchDictionary para que guarde el
            //      hashmap devuelto por la response dentro de un Response DataClass
            db.collection("users").document(user.uid)
                .get().addOnSuccessListener { userDoc ->
                    if (!userDoc.exists()) {
                        db.collection("users").document(user.uid).set(user)
                    }
                }
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