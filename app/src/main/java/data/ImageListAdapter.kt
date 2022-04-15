package data

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

/**
 *
 */
class ImageListAdapter(
    private val mContext: Context,
    private val listener: OnItemClickListener
) : ListAdapter<ImageData, RecyclerView.ViewHolder?>(IMAGE_COMPARATOR) {


    private var mImage = mutableListOf<ImageData>()

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val imageView = inflater.inflate(R.layout.image_data, parent, false)
        return ViewHolder(imageView)
    }

    /**
     *
     */
    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        val wordItemView: TextView = itemView.findViewById(R.id.text_name_data)

        /**
         *
         */
        val imageData: ImageView = itemView.findViewById(R.id.image_data)

        val imageDataEdit: ImageView = itemView.findViewById(R.id.isEdited1)


        /**
         *
         */
        fun bind(item: ImageData, position: Int) {
            imageData.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = mImage[position]
        if (image.image1 != -1)
            Glide.with(mContext).load(image.image1)
                .into((holder as ImageListAdapter.ViewHolder).imageData)
        else {
            if (image.imagePath1 != null)
                Glide.with(mContext).load(image.imagePath1)
                    .into((holder as ImageListAdapter.ViewHolder).imageData)
        }
        (holder as ImageListAdapter.ViewHolder).wordItemView.text = ""
        holder.bind(item = image, position)
    }

    /**
     *
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<ImageData>) {
        mImage.clear()
        mImage.addAll(list)
        notifyDataSetChanged()

    }

    /**
     *
     */
    override fun getItemCount(): Int {
        return mImage.size
    }

    /**
     *
     */
    interface OnItemClickListener {
        /**
         *
         */
        fun onItemClick(item: ImageData)
    }

    companion object {
        private val IMAGE_COMPARATOR = object : DiffUtil.ItemCallback<ImageData>() {
            override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
                return oldItem.imagePath1 == newItem.imagePath1
            }
        }
    }
}
