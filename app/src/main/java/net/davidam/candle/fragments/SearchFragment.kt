package net.davidam.candle.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.gson.Gson
import net.davidam.candle.MainActivity
import net.davidam.candle.R
import net.davidam.candle.model.Word
import net.davidam.candle.model.WordRequest
import net.davidam.candle.model.WordResponse

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var functions: FirebaseFunctions



    // **************** FRAGMENT ****************
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        const val TAG = "dabudin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialising Firestore functions capabilities
        functions = Firebase.functions("europe-west1")

        //Configuring search bar
        initSearchBar()
    }
    // **************** FRAGMENT ****************



    // *************** SEARCH BAR ***************
    private fun initSearchBar() {
        val searchBar = requireView().findViewById<SearchView>(R.id.search_bar)
        searchBar.setOnQueryTextListener(this)
    }

    override fun onQueryTextChange(query: String): Boolean {
        return false
    }

    override fun onQueryTextSubmit(text: String): Boolean {
        //Activate Firestore machinery here to create a new document if the queried
        //word doesn't exist, or return a document with the word info if it does.
        checkWords(text)
        return false
    }
    // *************** SEARCH BAR ***************



    // ********* GOOGLE CLOUD FUNCTIONS *********
    //docs: (https://firebase.google.com/docs/functions/callable)

    private fun checkWords(text: String) {

        // Handling errors
        // (limit the user input to max 10 words and 100 chars to
        // prevent fraudulent use of this function by the client).
        if (text.length > 100) {
            Toast.makeText(activity,
                "ERROR: El limite maximo son 100 caracteres",
                Toast.LENGTH_LONG).show()
        }
        else if (text.split("\\s".toRegex()).size >= 10) {
            Toast.makeText(activity,
                "ERROR: El limite maximo son 10 palabras",
                Toast.LENGTH_LONG).show()
        }
        else {
            //Serializing the input words provided by the SearchBar
             dictionaryGenerator(text)
        }
    }

    private fun dictionaryGenerator(text: String): Task<WordResponse> {
        // Create the arguments to the callable function.
        val data = Gson().toJson(WordRequest(text))

        return functions
            .getHttpsCallable("dictionaryGenerator-2")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as HashMap<*, *>
                val word = Word("",
                    result["words"] as String,
                    result["wordCount"] as Int,
                    result["combinations"] as MutableList<String>?,
                    result["types"] as MutableList<String>?,
                    result["meanings"] as MutableList<String>?,
                    result["translations"] as MutableList<String>?,
                    result["synonyms"] as MutableList<String>?,
                    result["examples"] as MutableList<String>?)
                Log.d(TAG, Gson().toJson(word))
                val response = WordResponse(word, result["error"] as String, result["errorCode"] as Int)
                Log.d(TAG, Gson().toJson(response))
                response
            }
    }
    // ********* GOOGLE CLOUD FUNCTIONS *********
}