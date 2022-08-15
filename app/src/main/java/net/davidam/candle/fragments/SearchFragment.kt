package net.davidam.candle.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import net.davidam.candle.R
import net.davidam.candle.adapter.CustomAdapterWord
import net.davidam.candle.model.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val WORDS = "words"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@Suppress("UNCHECKED_CAST")
class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    // TODO: Rename and change types of parameters
    private var wordsTxt: String? = null

    private var words = mutableListOf<WordDocument>()
    private lateinit var searchBar: SearchView
    private lateinit var rvWords: RecyclerView
    private lateinit var adapter: CustomAdapterWord


    //  **************** FRAGMENT ****************
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param words Parameter 1.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(words: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(WORDS, words)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordsTxt = it.getString(WORDS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //  Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  Configuring search bar
        initSearchBar()
        //  Initiate Recycler View
        initRV()
    }
    //  **************** FRAGMENT ****************



    //  *************** SEARCH BAR ***************
    private fun initSearchBar() {
        searchBar = requireView().findViewById(R.id.search_bar)
        searchBar.setOnQueryTextListener(this)
    }

    override fun onQueryTextChange(query: String): Boolean {
        return false
    }

    override fun onQueryTextSubmit(text: String): Boolean {
        //  This line prevents the code inside onQueryTextSubmit to execute twice
        //  inside the emulator when you press enter. Note that if you press the
        //  blue submit key of the Gboard keyboard, or you execute the app in any
        //  mobile device this problem won't appear.
        searchBar.clearFocus()
        //  Activate Firestore machinery here to create a new document if the queried
        //  word doesn't exist, or return a document with the word info if it does.
        errorHandler(text)
        //  This line clears the text inside the searchView
        //  searchBar.setQuery("", false)
        return false
    }

    private fun errorHandler(text: String) {
        //  Handling errors
        //  (limit the user input to max 10 words and 100 chars to
        //  prevent fraudulent use of this function by the client).
        if (text.length > 130) {
            errorSnack("El limite maximo son 130 caracteres")
        }
        else if (text.split("\\s".toRegex()).size >= 13) {
            errorSnack("El limite maximo son 13 palabras")
        }
        else {
            //  Let's make a toast with the response given by searchDictionary()
            CloudFunctions.searchDictionary(text)
                .addOnSuccessListener { wordResponse ->
                    if (wordResponse.errorCode != -1) {
                        errorSnack(wordResponse.error)
                    }
                    else {
                        wordResponse.docs!!.forEach { doc ->
                            words.add(doc)
                        }
                        drawRV()
                    }
            }
        }
    }
    //  *************** SEARCH BAR ***************



    //  ************** RECYCLER VIEW *************
    private fun initRV() {
        val contextSF = requireActivity().applicationContext
        rvWords = requireView().findViewById(R.id.rvWords)
        adapter = CustomAdapterWord(contextSF, R.layout.row_word)
        rvWords.adapter = adapter
        rvWords.layoutManager = LinearLayoutManager(contextSF)
    }

    private fun drawRV() {
        words
        adapter.setWords(words)
    }
    //  ************** RECYCLER VIEW *************



    //  ************ OTHER STUFF *************
    @SuppressLint("UseCompatLoadingForDrawables", "NewApi")
    //We make a personalised snackbar with the background color as red
    private fun errorSnack(text: String) {
        val styledText = Html.fromHtml("<b>ERROR:</b> $text", Build.VERSION.SDK_INT)
        val snackBar = Snackbar.make(requireView(), styledText, Snackbar.LENGTH_LONG)
        snackBar.view.background = resources.getDrawable(R.drawable.snackbar_error, null)
        snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 10
        snackBar.setTextColor(Color.WHITE)
        snackBar.show()
    }

    private fun toast(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }
    //  ************ OTHER STUFF *************
}