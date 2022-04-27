package crop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

/**
 *
 */
class CropAdapter(
    private val mContext: Context,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    /**
     *
     */
    private val mItemCrop: MutableList<ItemCrop> = mutableListOf()

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val itemView = inflater.inflate(R.layout.item_crop, parent, false)
        return ViewHolder(itemView)
    }

    /**
     *
     */
    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        val mItemCrop: ImageView = itemView.findViewById(R.id.itemCrop)

        /**
         *
         */
        val mName: TextView = itemView.findViewById(R.id.name_crop)

        /**
         *
         */
        private val mCrop: ViewGroup = itemView.findViewById(R.id.crop)

        /**
         *
         */
        fun bind(item: ItemCrop, position: Int) {
            mCrop.setOnClickListener {
                listener.onItemClick(item)

            }
        }
    }

    /**
     *
     */
    fun setList(list: MutableList<ItemCrop>) {
        mItemCrop.clear()
        mItemCrop.addAll(list)
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemCrop = mItemCrop[position]
        Glide.with(mContext).load(itemCrop.itemCrop)
            .into((holder as CropAdapter.ViewHolder).mItemCrop)
        holder.mName.text = itemCrop.name
        holder.bind(item = itemCrop, position)
    }

    /**
     *
     */
    override fun getItemCount(): Int {
        return mItemCrop.size
    }

    /**
     *
     */
    interface OnItemClickListener {
        /**
         *
         */
        fun onItemClick(item: ItemCrop?)
    }
}