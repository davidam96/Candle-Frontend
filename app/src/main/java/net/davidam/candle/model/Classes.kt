package net.davidam.candle.model

import java.io.Serializable

data class WordRequest(var words: String=""): Serializable

data class Word(var id: String = "",
                var words: String = "",
                var wordCount: Int = 0,
                var combinations: MutableList<String>? = null,
                var types: MutableList<String>? = null,
                var meanings: MutableList<String>? = null,
                var translations: MutableList<String>? = null,
                var synonyms: MutableList<String>? = null,
                var examples: MutableList<String>? = null
)

data class WordResponse(var data: Word? = null,
                        var error: String = "",
                        var errorCode: Int = -1
)