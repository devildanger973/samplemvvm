package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.network.MarsPhoto
import com.example.android.marsphotos.overview.OverviewViewModel
import overview.PhotoGridAdapter


/**
 *
 */
class MarsPhotoActivity : AppCompatActivity() {
    private lateinit var mPhoto: MutableList<MarsPhoto>
    private lateinit var mRecyclerPhoto: RecyclerView
    private lateinit var mPhotoGridAdapter: PhotoGridAdapter

    /**
     *
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_overview)
        listPhoto()
        mRecyclerPhoto = findViewById<RecyclerView?>(R.id.photos_grid)
        mPhotoGridAdapter = PhotoGridAdapter(this)
        mRecyclerPhoto.adapter = mPhotoGridAdapter
    }

    private fun listPhoto() {
        OverviewViewModel().status.observe(this) {

        }
        OverviewViewModel().photos.observe(this) {
            mPhotoGridAdapter.setList(it.toMutableList())

        }
    }

}

