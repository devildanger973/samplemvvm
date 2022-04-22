package implement.swipe.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 *
 */
open class CollectionFragment : Fragment() {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private lateinit var demoCollectionAdapter: DemoCollectionAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var myContext: FragmentActivity? = null


    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.collection_demo, container, false)
        myContext = activity
        viewPager = root.findViewById(R.id.pager)
        viewPager.isSaveEnabled = false
        tabLayout = root.findViewById(R.id.tab_layout)
        demoCollectionAdapter = DemoCollectionAdapter(requireActivity())
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 1")
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 2")
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 3")
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 4")
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 5")
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 6")
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 7")
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 8")
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 9")
        demoCollectionAdapter.addFragment(DemoObjectFragment(), "title 10")

        demoCollectionAdapter.notifyDataSetChanged()
        viewPager.adapter = demoCollectionAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = demoCollectionAdapter.getPageTitle(position)
            //viewPager.setCurrentItem(tab.position, true)
        }.attach()
        return root
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabLayout = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            demoCollectionAdapter = DemoCollectionAdapter(this)
            viewPager = view.findViewById(R.id.pager)
            viewPager.adapter = demoCollectionAdapter
            tab.text = "OBJECT ${(position + 1)}"
            viewPager.setCurrentItem(tab.position, true)
        }.attach()


    }*/
}

/**
 *
 */
class DemoCollectionAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
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
        // Return a NEW fragment instance in createFragment(int)
        val fragment = DemoObjectFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
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

private const val ARG_OBJECT = "object"

/**
 *
 */// Instances of this class are fragments representing a single
// object in our collection.
class DemoObjectFragment : Fragment() {

    /**
     *
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_collection_object, container, false)
    }

    /**
     *
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            val textView: TextView = view.findViewById(android.R.id.text1)
            textView.text = getInt(ARG_OBJECT).toString()
        }
    }
}
