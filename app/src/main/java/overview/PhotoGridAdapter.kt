/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package overview

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.marsphotos.network.MarsPhoto
import com.example.myapplication.Hero
import com.example.myapplication.R

/**
 *
 *
 */
class PhotoGridAdapter(
    private val mContext: Context,
    private val mPhoto: MutableList<MarsPhoto> = mutableListOf<MarsPhoto>()
) :
    RecyclerView.Adapter<PhotoGridAdapter.ViewHolder?>() {
    inner class ViewHolder(
        @NonNull itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        val mImagePhoto: ImageView

        init {
            mImagePhoto = itemView.findViewById(R.id.mars_image)
        }
    }


    /**
     *
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val photoView = inflater.inflate(R.layout.grid_view_item, parent, false)
        return ViewHolder(photoView)
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val marsPhoto = mPhoto[position]
       /* Glide.with(mContext)
            .load(marsPhoto.imgSrcUrl)
            .into(holder.mImagePhoto)*/
        val url =marsPhoto.imgSrcUrl.replace("http","https")
        //holder.mImagePhoto.setImageURI(Uri.parse(url))
        Glide.with(mContext)
            .load(url)
            .into(holder.mImagePhoto)
        Log.d("clickimage","image ${url}");
    }

    /**
     *
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<MarsPhoto>) {
        mPhoto.clear()
        mPhoto.addAll(list)
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun getItemCount(): Int {
        return mPhoto.size
    }
}
