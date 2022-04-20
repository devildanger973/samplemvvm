package bottomsheet

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView

import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.HeroAdapter
import com.example.myapplication.R


/**
 *
 */
class FolderDataAdapter(private val mContext: Context, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    companion object {

        /**
         *
         */
        const val VIEW_TYPE_ONE: Int = 1

        /**
         *
         */
        const val VIEW_TYPE_TWO: Int = 2
    }

    /**
     *
     */
    var count: Int = 0

    private var mFolderData = mutableListOf<FolderData>()


    /**
     *
     */
    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        val mImageFolder: ImageView = itemView.findViewById(R.id.btn_sheet)

        /**
         *
         */
        val mTitle: TextView = itemView.findViewById(R.id.tt_sheet)

        /**
         *
         */
        val mSize: TextView = itemView.findViewById(R.id.size_sheet)

        /**
         *
         */
        val parent1: ViewGroup = itemView.findViewById(R.id.bottom_sheet)

        /**
         *
         */
        fun bind(item: FolderData, position: Int) {
            mTitle.text = item.folderPath
            mSize.text = "Size: ${item.numberOfImage}"
            parent1.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }

    /**
     *
     */
    inner class ViewHolder2(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        val mImageHero1: ImageView = itemView.findViewById(R.id.image_hero1)

        /**
         *
         */
        val mTextName: TextView = itemView.findViewById(R.id.text_name1)

        /**
         *
         */
        fun bind() {
            mImageHero1.setOnClickListener {
                listener.onOpenFolderClick()
            }
        }
    }

    /**
     *
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<FolderData>) {
        mFolderData.clear()
        mFolderData.addAll(list)
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val folderView = inflater.inflate(R.layout.bottom_sheet, parent, false)
        return ViewHolder(folderView)
    }


    /**
     *
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val folder = mFolderData[position]
        val url = folder.imageViewFolder
        Glide.with(mContext).load(url)
            .into((holder as FolderDataAdapter.ViewHolder).mImageFolder)
        holder.bind(item = folder, position)
    }

    /**
     *
     */
    interface OnItemClickListener {
        /**
         *
         */
        fun onItemClick(item: FolderData?)

        /**
         *
         */
        fun onOpenFolderClick()
    }

    /**
     *
     */
    override fun getItemCount(): Int {
        return mFolderData.size
    }

}

