package net.davidam.candle.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import net.davidam.candle.R
import net.davidam.candle.databinding.ActivityWordBinding

class WordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)

        //  Access activity_word.xml IDs via binding
        binding = ActivityWordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}