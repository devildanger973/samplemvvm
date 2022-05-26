package implement.swipe.views

import add.text.AddTextFragment
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
import filter.FilterListFragment
import io.reactivex.disposables.CompositeDisposable
import paint.PaintFragment
import sticker.StickerFragment

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
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.collection_demo, container, false)
        myContext = activity
        viewPager = root.findViewById(R.id.pager)
        viewPager.isSaveEnabled = false
        viewPager.isUserInputEnabled = false
        tabLayout = root.findViewById(R.id.tab_layout)
        collectionAdapter = CollectionAdapter(requireActivity())
        collectionAdapter.addFragment(CropFragment(), "Crop")
        collectionAdapter.addFragment(PaintFragment(), "Paint")
        collectionAdapter.addFragment(FilterListFragment(), "Filter")
        collectionAdapter.addFragment(AddTextFragment(), "AddText")
        collectionAdapter.addFragment(StickerFragment(), "Sticker")
        collectionAdapter.notifyDataSetChanged()
        viewPager.adapter = collectionAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = collectionAdapter.getPageTitle(position)
            viewPager.setCurrentItem(2, false)
            tabLayout.setOnClickListener(this)
        }.attach()
        viewPager.offscreenPageLimit = 1
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0) {
                    ensureEditActivity()?.getCrop()?.visibility = View.VISIBLE
                    ensureEditActivity()?.paintView?.visibility = View.GONE
                    ensureEditActivity()?.stickerView?.visibility = View.GONE
                    ensureEditActivity()?.stickerView?.clear()

                } else if (position == 1) {
                    ensureEditActivity()?.paintView?.visibility = View.VISIBLE
                    ensureEditActivity()?.getCrop()?.visibility = View.GONE
                    ensureEditActivity()?.stickerView?.visibility = View.GONE
                    ensureEditActivity()?.stickerView?.clear()

                } else if (position == 2) {
                    ensureEditActivity()?.paintView?.visibility = View.GONE
                    ensureEditActivity()?.getCrop()?.visibility = View.GONE
                    ensureEditActivity()?.stickerView?.visibility = View.GONE
                    ensureEditActivity()?.stickerView?.clear()

                } else if (position == 3) {
                    ensureEditActivity()?.paintView?.visibility = View.GONE
                    ensureEditActivity()?.getCrop()?.visibility = View.GONE
                    ensureEditActivity()?.stickerView?.visibility = View.GONE
                    ensureEditActivity()?.stickerView?.clear()

                } else if (position == 4) {
                    ensureEditActivity()?.paintView?.visibility = View.GONE
                    ensureEditActivity()?.getCrop()?.visibility = View.GONE
                    ensureEditActivity()?.stickerView?.visibility = View.VISIBLE

                }

            }
        })
        return root
    }

    private val disposable = CompositeDisposable()

    override fun onDestroyView() {
        disposable.dispose()
        super.onDestroyView()
    }


    override fun onClick(v: View?) {

    }

}

private const val ARG_OBJECT = "object"


