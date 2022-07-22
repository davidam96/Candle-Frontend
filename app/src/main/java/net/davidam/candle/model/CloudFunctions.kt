package net.davidam.candle.model

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import net.davidam.candle.fragments.SearchFragment


@Suppress("UNCHECKED_CAST")
abstract class CloudFunctions {
    companion object {
        //docs: (https://firebase.google.com/docs/functions/callable)
        private var functions = Firebase.functions("europe-west1")

        fun searchDictionary(text: String): Task<Response> {
            // Create the arguments to the callable function.
            val data = Gson().toJson(Request(text))

            return functions
                .getHttpsCallable("searchDictionary")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then result will throw an Exception which will be
                    // propagated down.
                    if (task.exception !== null) {
                        val docs = mutableListOf<WordDocument>()
                        docs.add(WordDocument(text))
                        Response(Data(docs, task.exception.toString(), 9))
                    }
                    else {
                        val result = task.result?.data as HashMap<String, *>
                        var response: Response? = null
                        Log.d("dabudin", result["data"].toString())
                        result.mapValues {
                            response = Response(
                                it.value as Data
                            )
                        }
                        Log.d("dabudin", response.toString())
                        response
/*                      val docs = result["docs"] as ArrayList<HashMap<*, *>>

                        val word = WordDocument("",
                            doc["words"] as String,
                            docs["wordCount"] as Int,
                            docs["types"] as MutableList<String>?,
                            docs["meanings"] as MutableList<String>?,
                            docs["translations"] as MutableList<String>?,
                            docs["synonyms"] as MutableList<String>?,
                            docs["examples"] as MutableList<String>?,
                            docs["combinations"] as MutableList<String>,)
                        response = Response(word,
                            result["error"] as String,
                            result["errorCode"] as Int,
                            result["exactMatch"] as Boolean)
                        response */
                    }
                }
        }

/*        fun searchDictionary(text: String): Task<Response>? {
            return null
        }*/
    }
}
