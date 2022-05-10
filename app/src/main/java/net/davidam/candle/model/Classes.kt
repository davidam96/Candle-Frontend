package net.davidam.candle.model

import java.io.Serializable

data class WordDocument(var id: String = "",
                var words: String = "",
                var wordCount: Int = 0,
                var types: MutableList<String>? = null,
                var meanings: MutableList<String>? = null,
                var translations: MutableList<String>? = null,
                var synonyms: MutableList<String>? = null,
                var examples: MutableList<String>? = null,
                var combinations: MutableList<String>? = null
): Serializable

data class Request(var words: String=""): Serializable

data class Response(var contents: WordDocument? = null,
                    var error: String = "",
                    var errorCode: Int = -1,
                    var isPerfectMatch: Boolean = false
): Serializable