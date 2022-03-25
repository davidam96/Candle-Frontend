package net.davidam.candle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import net.davidam.candle.adapter.ViewPageAdapter
import net.davidam.candle.databinding.ActivityViewPageBinding
import net.davidam.candle.fragments.AccountFragment
import net.davidam.candle.fragments.PracticeFragment
import net.davidam.candle.fragments.SearchFragment

class ViewPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewPageBinding

    //Iconos de la ViewPage Bar
    private val icons = arrayOf(android.R.drawable.ic_input_add,android.R.drawable.ic_input_delete)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Access the layout IDs via binding
        binding = ActivityViewPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configuring ViewPager and setting up tabs
        setupViewPager(binding.viewPager2)
        setupTabs(binding.tabs, binding.viewPager2)
    }

    private fun setupViewPager(viewPager2: ViewPager2) {
        val adapter = ViewPageAdapter(this)
        adapter.addFragment(SearchFragment(), "Search")
        adapter.addFragment(PracticeFragment(), "Practice")
        adapter.addFragment(AccountFragment(), "Account")
        viewPager2.adapter = adapter
    }

    private fun setupTabs(tabs: TabLayout, viewPager2: ViewPager2) {
        //This method is the equivalent to setupWithViewPager() for the ViewPager2 class
        TabLayoutMediator(tabs, viewPager2) { tab, i ->
            //Set up an icon, optional
            tab.icon = ContextCompat.getDrawable(this, icons[i])
        }
    }
}