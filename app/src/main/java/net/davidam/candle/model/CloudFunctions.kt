package net.davidam.candle.model

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.FirebaseApp
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson


@Suppress("UNCHECKED_CAST")
abstract class CloudFunctions {
    companion object {
        //docs: (https://firebase.google.com/docs/functions/callable)
        private var functions = Firebase.functions("europe-west1")

        fun dictionaryGenerator(text: String): Task<Response> {
            // Create the arguments to the callable function.
            val data = Gson().toJson(Request(text))

            return functions
                .getHttpsCallable("dictionaryGenerator")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then result will throw an Exception which will be
                    // propagated down.
                    val response: Response
                    if (task.exception !== null) {
                        //Log.e(TAG, task.exception.toString())
                        response = Response(WordDocument(), task.exception.toString(), 7)
                        response
                    }
                    else {
                        val result = task.result?.data as HashMap<*, *>
                        val contents = result["contents"] as HashMap<*, *>
                        val word = WordDocument("",
                            contents["words"] as String,
                            contents["wordCount"] as Int,
                            contents["types"] as MutableList<String>?,
                            contents["meanings"] as MutableList<String>?,
                            contents["translations"] as MutableList<String>?,
                            contents["synonyms"] as MutableList<String>?,
                            contents["examples"] as MutableList<String>?,
                            contents["combinations"] as MutableList<String>,)
                        response = Response(word,
                            result["error"] as String,
                            result["errorCode"] as Int,
                            result["exactMatch"] as Boolean)
                        response
                    }
                }
        }

/*        fun searchDictionary(text: String): Task<Response>? {
            return null
        }*/
    }
}
