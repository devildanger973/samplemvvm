package sticker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

/**
 *
 */
class StickerAdapter(private val stickerFragment: StickerFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val imageClick = ImageClick()
    private val pathList: MutableList<String> = ArrayList()

    /**
     *
     */
    override fun getItemCount(): Int {
        return pathList.size
    }

    /**
     *
     */
    override fun getItemViewType(position: Int): Int {
        return 1
    }

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewtype: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(
            R.layout.view_sticker_item, parent, false)
        return StickerViewHolder(view)
    }

    /**
     *
     */
    class StickerViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        @JvmField
        var image: ImageView = itemView.findViewById(R.id.img)
    }

    /**
     *
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val stickerViewHolder: StickerViewHolder = viewHolder as StickerViewHolder
        val path = pathList[position]
        val imageUrl = "drawable/$path"
        val imageKey =
            stickerFragment.resources.getIdentifier(imageUrl, "drawable", (stickerFragment.context
                ?: return)
                .packageName)
        stickerViewHolder.image.setImageDrawable(stickerFragment.resources.getDrawable(imageKey))
        stickerViewHolder.image.tag = imageUrl
        stickerViewHolder.image.setOnClickListener(imageClick)
    }

    /**
     *
     */
    fun addStickerImages(folderPath: String, stickerCount: Int) {
        pathList.clear()
        for (i in 0 until stickerCount) {
            pathList.add(folderPath + "_" + (i + 1).toString())
        }
        notifyDataSetChanged()
    }

    private inner class ImageClick : View.OnClickListener {
        override fun onClick(v: View) {
            val data = v.tag as String
            stickerFragment.selectedStickerItem(data)
        }
    }
}