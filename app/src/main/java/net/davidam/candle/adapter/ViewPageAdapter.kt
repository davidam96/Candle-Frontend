package net.davidam.candle.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPageAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    private val mFragmentList: ArrayList<Fragment> = ArrayList()
    private val mFragmentTitleList: ArrayList<String> = ArrayList()

    //NO USAR ESTE METODO PARA CREAR UN FRAGMENTO NUEVO
    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun getItemId(position: Int): Long {
        return mFragmentList[position].id.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return mFragmentList.find { fragment -> fragment.id.toLong() == itemId } != null
    }

    //USAR ESTE METODO PARA CREAR UN FRAGMENTO NUEVO
    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }
}