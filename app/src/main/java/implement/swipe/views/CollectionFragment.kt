package implement.swipe.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import crop.*
import io.reactivex.disposables.CompositeDisposable

/**
 *
 */
open class CollectionFragment : BaseEditFragment(), View.OnClickListener {
    // When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var myContext: FragmentActivity? = null
    override fun onShow() {
    }

    override fun backToMain() {
    }


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
        collectionAdapter = CollectionAdapter(requireActivity())
        collectionAdapter.addFragment(CropFragment(), "Crop")
        collectionAdapter.addFragment(DemoObjectFragment(), "Rotate")
        collectionAdapter.addFragment(DemoObjectFragment(), "Filter")
        collectionAdapter.addFragment(DemoObjectFragment(), "Saturation")
        collectionAdapter.addFragment(DemoObjectFragment(), "Brightness")
        collectionAdapter.addFragment(DemoObjectFragment(), "Portrait")
        collectionAdapter.addFragment(DemoObjectFragment(), "AddText")
        collectionAdapter.addFragment(DemoObjectFragment(), "Sticker")
        collectionAdapter.addFragment(DemoObjectFragment(), "SupportAction")
        collectionAdapter.addFragment(DemoObjectFragment(), "Paint")
        collectionAdapter.notifyDataSetChanged()
        viewPager.adapter = collectionAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = collectionAdapter.getPageTitle(position)
            viewPager.setCurrentItem(tab.position, true)

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
//crop
    private val disposable = CompositeDisposable()

    override fun onDestroyView() {
        disposable.dispose()
        super.onDestroyView()
    }

    override fun onClick(p0: View?) {
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


/**
 *
 */

