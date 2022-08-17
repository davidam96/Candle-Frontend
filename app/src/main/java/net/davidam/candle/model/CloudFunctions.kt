package net.davidam.candle.model

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson


@Suppress("UNCHECKED_CAST")
abstract class CloudFunctions {
    companion object {
        //Callable functions documentation: (https://firebase.google.com/docs/functions/callable)
        private var functions = Firebase.functions("europe-west1")

        fun searchDictionary(text: String): Task<WordResponse> {
            // Create the arguments to the callable function.
            val data = Gson().toJson(WordRequest(text))

            return functions
                .getHttpsCallable("searchDictionary")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure.
                    if (task.exception !== null) {
                        WordResponse(mutableListOf(WordDocument(text)),
                            task.exception.toString(), 9)
                    }
                    else {
                        val result = task.result?.data as HashMap<String, *>
                        val docs = mutableListOf<WordDocument>()
                        val docsHM = result["docs"] as MutableList<HashMap<String, *>>

                        docsHM.forEach { docHM ->
                            val doc = WordDocument(docHM["words"] as String,
                                docHM["wordCount"] as Int,
                                docHM["plural"] as String,
                                docHM["types"] as MutableList<String>,
                                docHM["meanings"] as MutableList<String>,
                                docHM["translations"] as MutableList<String>,
                                docHM["synonyms"] as MutableList<String>,
                                docHM["antonyms"] as MutableList<String>,
                                docHM["examples"] as MutableList<String>,
                                docHM["combinations"] as MutableList<String>,
                                docHM["imageUrl"] as String
                            )
                            docs.add(doc)
                        }

                        WordResponse(docs,
                            result["error"] as String,
                            result["errorCode"] as Int,
                            result["exactMatch"] as Boolean)
                    }
                }
        }
    }
}
