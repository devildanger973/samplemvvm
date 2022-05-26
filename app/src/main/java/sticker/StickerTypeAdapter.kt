package sticker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

/**
 *
 */
class StickerTypeAdapter(private val stickerFragment: StickerFragment) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val stickerPath: Array<String> =
        stickerFragment.resources.getStringArray(R.array.types)
    private val stickerPathName: Array<String> =
        stickerFragment.resources.getStringArray(R.array.names)
    private val stickerCount: IntArray =
        stickerFragment.resources.getIntArray(R.array.count)
    private val imageClick: ImageClick = ImageClick()

    /**
     *
     */
    class ImageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        var icon: ImageView = itemView.findViewById(R.id.icon)

        /**
         *
         */
        var text: TextView = itemView.findViewById(R.id.text)

    }

    /**
     *
     */
    override fun getItemCount(): Int {
        return stickerPathName.size
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
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.view_sticker_type_item, parent, false)
        return ImageHolder(view)
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val imageHolder = holder as ImageHolder
        val name = stickerPathName[position]
        imageHolder.text.text = name
        imageHolder.text.setTag(R.id.TAG_STICKERS_PATH,
            stickerPath[position])
        imageHolder.text.setTag(R.id.TAG_STICKERS_COUNT,
            stickerCount[position])
        imageHolder.text.setOnClickListener(imageClick)
    }

    private inner class ImageClick : View.OnClickListener {
        override fun onClick(v: View) {
            val data = v.getTag(R.id.TAG_STICKERS_PATH) as String
            val count = v.getTag(R.id.TAG_STICKERS_COUNT) as Int
            stickerFragment.swipToStickerDetails(data, count)
            stickerFragment.stickerView?.visibility = View.VISIBLE
        }
    }

}
