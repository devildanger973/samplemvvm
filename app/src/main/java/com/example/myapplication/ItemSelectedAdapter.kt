package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


/**
 *
 */
class ItemSelectedAdapter(
    private val mContext: Context,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    companion object {
        /**
         *
         */
        const val VIEW_TYPE_ONE1: Int = 1

        /**
         *
         */
        const val VIEW_TYPE_TWO2: Int = 2
    }

    private var mHeroSelected = mutableListOf<HeroSelected>()
    private var mHero = mutableListOf<Hero>()


    /**
     *
     */
    var currentPos: Int = -1

    /**
     *
     */
    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        val mImageSelected: ImageView = itemView.findViewById(R.id.itemSelected)

        /**
         *
         */
        private val mImageBorder: ImageView = itemView.findViewById(R.id.image_hero_border1)

        /**
         *
         */
        val mTextName: TextView = itemView.findViewById(R.id.text_name1)

        /**
         *
         */
        fun bind(item: HeroSelected, position: Int) {
            mTextName.visibility = View.GONE
            mImageSelected.setOnClickListener {
                listener.onItemClick(item)
                if (currentPos != -1) {
                    if (currentPos != position) {
                        mHeroSelected[position].isSelected = true
                        notifyItemChanged(position)
                        mHeroSelected[currentPos].isSelected = false
                        notifyItemChanged(currentPos)
                        Log.d("DDDDD", "1 position = $position currentPos = $currentPos")
                        Log.d(
                            "DDDDD",
                            " mHero[$position].isSelected =${mHeroSelected[position].isSelected}"
                        )
                        Log.d(
                            "DDDDD",
                            " mHero[$currentPos].isSelected =${mHeroSelected[currentPos].isSelected}"
                        )

                    } else {
                        Log.d("DDDDD", "2  position = $position currentPos = $currentPos")
                    }
                } else {
                    item.isSelected = true
                    notifyItemChanged(position)
                    Log.d("DDDDD", "3 position = $position currentPos = $currentPos")
                }
                Log.d("DDDDD", "================================")
                currentPos = position

            }
            if (item.isSelected) {
                Log.d("DDDDD", "binding item.isSelected " + item.isSelected)
                mImageBorder.visibility = View.VISIBLE
                mImageBorder.setBackgroundResource(R.drawable.on_item_select)

            } else {
                mImageBorder.visibility = View.GONE

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
        val mImageHero1: ImageView = itemView.findViewById(R.id.image_editor_hero1)

        /**
         *
         */
        val mTextName1: TextView = itemView.findViewById(R.id.text_name_editor1)

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
    fun setList(list: MutableList<HeroSelected>) {
        mHeroSelected.clear()
        mHeroSelected.addAll(list)
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ONE1) {
            val inflater = LayoutInflater.from(mContext)
            val heroView = inflater.inflate(R.layout.item_selected, parent, false)
            return ViewHolder(heroView)
        }
        val inflater = LayoutInflater.from(mContext)
        val heroView = inflater.inflate(R.layout.hero_editor_item_open_photo, parent, false)
        return ViewHolder2(heroView)
    }

    /**
     *
     */
    override fun getItemViewType(position: Int): Int {
        return mHeroSelected[position].viewType
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hero = mHeroSelected[position]
        when (hero.viewType) {
            VIEW_TYPE_ONE1 -> {
                if (hero.image != -1)
                    Glide.with(mContext).load(hero.image)
                        .into((holder as ViewHolder).mImageSelected)
                else {
                    if (hero.imagePath != null)
                        Glide.with(mContext).load(hero.imagePath)
                            .into((holder as ViewHolder).mImageSelected)
                }
                (holder as ViewHolder).mTextName.text
                holder.bind(item = hero, position)
            }
            VIEW_TYPE_TWO2 -> {
                if (hero.image != -1)
                    Glide.with(mContext).load(hero.image).into((holder as ViewHolder2).mImageHero1)
                else {
                    if (hero.imagePath != null)
                        Glide.with(mContext).load(hero.imagePath)
                            .into((holder as ViewHolder2).mImageHero1)
                }
                (holder as ViewHolder2).mTextName1.text
                holder.bind()
            }
        }
    }

    /**
     *
     */
    override fun getItemCount(): Int {
        return mHeroSelected.size
    }

    /**
     *
     */
    interface OnItemClickListener {
        /**
         *
         */
        fun onItemClick(item: HeroSelected)

        /**
         *
         */
        fun onOpenFolderClick()
    }

}

