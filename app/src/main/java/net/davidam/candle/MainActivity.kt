package net.davidam.candle

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.SearchView
import android.widget.TextView

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import net.davidam.candle.databinding.ActivityMainBinding
import net.davidam.candle.fragments.AccountFragment
import net.davidam.candle.fragments.FindFragment
import net.davidam.candle.fragments.PracticeFragment

import net.davidam.candle.model.User
import net.davidam.candle.viewmodel.ViewModel

class MainActivity : AppCompatActivity() {

    //  (POR HACER):
    //  1) FindFragment.kt (line 127) ---> Implementar el codigo del RecyclerView para que vaya
    //  guardando las palabras buscadas por el usuario.
    //  2) AccountFragment.kt ---> When the user decides to logout in the profile section,
    //  the user Shared Preferences must be deleted.
    //  3) ViewModel.kt (line 24) --> Remember to later make Firestore's security rules in order to
    //  restrict the format of all future database requests.
    //  4) Implementar en el backend que solo se almacenen palabras en singular, si la palabra termina
    //  en 's' o en 'es' no se crea una nueva entrada en plural, se devuelve la del singular.

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private lateinit var userSP: SharedPreferences
    private  var user: User? = null

    companion object {
        private const val TAG = "dabudin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Access the layout IDs via binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Load user data if locally stored
        userSP = getSharedPreferences("User", Context.MODE_PRIVATE)

        //  Boot up Firebase services
        bootFirebase()

        //  Bottom navigation view binding
        val bottomNavView: BottomNavigationView = binding.bottomNavView
        //  Setting bottom navigation view listener
        navViewListener(bottomNavView)
        //  Setting initial fragment (search bar)
        setInitialFragment()
    }



    // **************** FIREBASE & LOGIN ****************
    private fun bootFirebase() {
        //  Create firebase instance
        val app = FirebaseApp.initializeApp(this)
        //  Initialize custom ViewModel class
        viewModel = ViewModel(app!!)

        //  Start Sign-In flow, but only if there is no user in local persistence;
        //  if there is already one in shared_prefs, then serialize it into a user object
        if (!userSP.contains("user")) {
            bootSignIn()
        } else {
            user = Gson().fromJson(userSP.getString("user", "{}"), User::class.java)
        }
    }

    private fun bootSignIn() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())
/*            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build())*/

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    //  Sign-In Launcher
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val authUser = FirebaseAuth.getInstance().currentUser!!

            //  We make sure to store the user information in Firestore
            viewModel.checkUser(authUser).continueWith { task ->
                if (task.exception != null) {
                    errorSnack(task.exception.toString())
                } else {
                    //  We store the user as a global variable
                    //  for later use across different activities
                    user = task.result as User
                    Log.d(ViewModel.TAG, "LOGIN CORRECTO: ${authUser.email}")

                    //  We also store the user info in locally using SharedPreferences
                    val editor = userSP.edit()
                    val userJson = Gson().toJson(user)
                    editor.putString("user", userJson)
                    editor.apply()
                }
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.error.errorCode and handle the error.
            Log.d(TAG, "FirebaseUI error code: '${response!!.error!!.errorCode}' \n" +
                    "To get more information go to: https://github.com/firebase/FirebaseUI-Android" +
                    "/blob/master/auth/src/main/java/com/firebase/ui/auth/ErrorCodes.java")
        }
    }
    // **************** FIREBASE & LOGIN ****************



    // ****************** VIEW ******************
    private fun navViewListener(bottomNavView: BottomNavigationView) {
        bottomNavView.setOnItemSelectedListener{
            var fragment: Fragment? = null
            when (it.itemId) {
                R.id.fragment_menu -> {
                    fragment = FindFragment()
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
        fragmentTransaction.add(R.id.frame, FindFragment())
        fragmentTransaction.commit()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        fragmentTransaction.commit()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun errorSnack(text: String) {
        //We make a personalised snackbar with the background color as red
        val mainActivityView = findViewById<SearchView>(R.id.mainActivity)
        val styledText = Html.fromHtml("<b>ERROR:</b> $text", Build.VERSION.SDK_INT)
        val snackBar = Snackbar.make(mainActivityView, styledText, Snackbar.LENGTH_LONG)
        snackBar.view.background = resources.getDrawable(R.drawable.snackbar_error, null)
        snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 10
        snackBar.setTextColor(Color.WHITE)
        snackBar.show()
    }
    // ****************** VIEW ******************
}