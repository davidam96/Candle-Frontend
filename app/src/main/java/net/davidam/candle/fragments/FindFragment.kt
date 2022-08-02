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
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import net.davidam.candle.R
import net.davidam.candle.model.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FindFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

@Suppress("UNCHECKED_CAST")
class FindFragment : Fragment(), SearchView.OnQueryTextListener {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



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
            FindFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
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
        return inflater.inflate(R.layout.fragment_find, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        errorHandler(text)
        return false
    }

    private fun errorHandler(text: String) {
        // Handling errors
        // (limit the user input to max 10 words and 100 chars to
        // prevent fraudulent use of this function by the client).
        if (text.length > 130) {
            errorSnack("El limite maximo son 130 caracteres")
        }
        else if (text.split("\\s".toRegex()).size >= 13) {
            errorSnack("El limite maximo son 13 palabras")
        }
        else {
            //Let's make a toast with the response given by searchDictionary()
            CloudFunctions.searchDictionary(text)
                .continueWith { task ->
                val response = task.result!!
                if (response.errorCode != -1) {
                    errorSnack(response.error)
                }
                else {
                    toast(": ${Gson().toJson(response)}")

                    // (POR HACER) RV code...
/*                    private fun initRV() {
                        adapter = CustomAdapterProducto(this, R.layout.row_producto)
                        binding.rvProductos.adapter = adapter
                        binding.rvProductos.layoutManager = LinearLayoutManager(this)
                    }

                    private fun drawRV() {
                        adapter.setProductos(productosFav)
                        toolbar.title = categoria.nombre
                    }*/
                }
            }
        }
    }
    // *************** SEARCH BAR ***************



    // ************ OTHER STUFF *************
    private fun toast(text: String) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("UseCompatLoadingForDrawables", "NewApi")
    private fun errorSnack(text: String) {
        //We make a personalised snackbar with the background color as red
        val styledText = Html.fromHtml("<b>ERROR:</b> $text", Build.VERSION.SDK_INT)
        val snackBar = Snackbar.make(requireView(), styledText, Snackbar.LENGTH_LONG)
        snackBar.view.background = resources.getDrawable(R.drawable.snackbar_error, null)
        snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 10
        snackBar.setTextColor(Color.WHITE)
        snackBar.show()
    }
    // ************ OTHER STUFF *************
}