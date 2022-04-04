package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
class HeroEditorAdapter(private val mContext: Context, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    companion object {
        /**
         *
         */
        const val VIEW_TYPE_ONE = 1

        /**
         *
         */
        const val VIEW_TYPE_TWO = 2
    }

    private var mHero = mutableListOf<Hero>()

    /**
     *
     */
    var currentPos = -1

    /**
     *
     */
    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        val mImageHero: ImageView = itemView.findViewById(R.id.image_hero_editor)

        /**
         *
         */
        private val mImageBorder: ImageView = itemView.findViewById(R.id.image_hero_border)

        /**
         *
         */
        private val mImageHeart: ImageView = itemView.findViewById(R.id.image_hero_heart)
        private val mBorderHeart: ImageView = itemView.findViewById(R.id.image_border_heart)

        /**
         *
         */
        val mTextName: TextView = itemView.findViewById(R.id.text_name_editor)

        /**
         *
         */
        fun bind(item: Hero, position: Int) {

            mImageHero.setOnClickListener {
                listener.onItemClick(item)
                if (currentPos != -1) {
                    if (currentPos != position) {
                        mHero[position].isSelected = true
                        notifyItemChanged(position)
                        mHero[currentPos].isSelected = false
                        notifyItemChanged(currentPos)
                        Log.d("DDDDD", "1 position = $position currentPos = $currentPos")
                        Log.d(
                            "DDDDD",
                            " mHero[$position].isSelected =${mHero[position].isSelected}"
                        )
                        Log.d(
                            "DDDDD",
                            " mHero[$currentPos].isSelected =${mHero[currentPos].isSelected}"
                        )

                    } else {
                        mImageHeart.setBackgroundResource(R.drawable.vector_heart_icon)
                        mBorderHeart.visibility = View.GONE
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
                Log.d("DDDDD", "binding item.isSelected "+item.isSelected)
                mImageBorder.visibility = View.VISIBLE
                mImageHeart.visibility = View.VISIBLE
                mBorderHeart.visibility = View.VISIBLE
                mImageBorder.setBackgroundResource(R.drawable.on_item_select)
                mBorderHeart.setBackgroundResource(R.drawable.border_heart_icon)

            } else {
                mImageBorder.visibility = View.GONE
                mImageHeart.visibility = View.GONE
                mBorderHeart.visibility = View.GONE

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
    fun setList(list: MutableList<Hero>) {
        mHero.clear()
        mHero.addAll(list)
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            val inflater = LayoutInflater.from(mContext)
            val heroView = inflater.inflate(R.layout.hero_editor_item, parent, false)
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
        return mHero[position].viewType
    }

    /**
     *
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hero = mHero[position]
        when (hero.viewType) {
            VIEW_TYPE_ONE -> {
                if (hero.image != -1)
                    Glide.with(mContext).load(hero.image).into((holder as ViewHolder).mImageHero)
                else {
                    if (hero.imagePath != null)
                        Glide.with(mContext).load(hero.imagePath)
                            .into((holder as ViewHolder).mImageHero)
                }
                (holder as ViewHolder).mTextName.text = hero.name
                holder.bind(item = hero, position)
            }
            VIEW_TYPE_TWO -> {
                if (hero.image != -1)
                    Glide.with(mContext).load(hero.image).into((holder as ViewHolder2).mImageHero1)
                else {
                    if (hero.imagePath != null)
                        Glide.with(mContext).load(hero.imagePath)
                            .into((holder as ViewHolder2).mImageHero1)
                }
                (holder as ViewHolder2).mTextName1.text = hero.name
                holder.bind()
            }
        }
    }

    /**
     *
     */
    override fun getItemCount(): Int {
        return mHero.size
    }

    /**
     *
     */
    interface OnItemClickListener {
        /**
         *
         */
        fun onItemClick(item: Hero?)

        /**
         *
         */
        fun onOpenFolderClick()
    }

}

