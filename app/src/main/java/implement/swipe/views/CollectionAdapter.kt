package implement.swipe.views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 *
 */
class CollectionAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val titleList: MutableList<String> = ArrayList()

    /**
     *
     */
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    /**
     *
     */
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    /**
     *
     */
    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        titleList.add(title)
    }

    /**
     *
     */
    fun getPageTitle(position: Int): CharSequence {
        return titleList[position]
    }
}