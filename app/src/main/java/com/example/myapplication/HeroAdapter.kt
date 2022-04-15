package com.example.myapplication

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


/**
 *
 */
class HeroAdapter(private val mContext: Context, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    /**
     *
     */
    var isShowEdit: Boolean = false

    /**
     *
     */
    var isShowCheck: Boolean = false


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

    private var mHero = mutableListOf<Hero>()
    private var mHeroSelected = mutableListOf<String>()


    /**
     *
     */
    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        /**
         *
         */
        val mImageHero: ImageView = itemView.findViewById(R.id.image_hero)

        /**
         *
         */
        val mImageHeroEdit: ImageView = itemView.findViewById(R.id.isEdited)

        /**
         *
         */
        val mTextName: TextView = itemView.findViewById(R.id.text_name)

        /**
         *
         */
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)

        /**
         *
         */
        val parent1: ViewGroup = itemView.findViewById(R.id.parent1)

        /**
         *
         */
        fun bind(item: Hero, position: Int) {
            mImageHeroEdit.visibility = View.GONE
            if (isShowEdit) {
                if (item.isEdited) {

                    mImageHeroEdit.visibility = View.VISIBLE
                }
            }
            if (isShowCheck) {
                checkBox.visibility = View.VISIBLE
                mImageHero.isClickable = false
                mImageHero.isFocusable = false
                checkBox.isChecked = item.isSelected
                if (checkBox.isChecked) {
                    count++
                    listener.onCheck(count)
                }
                parent1.setOnClickListener {

                    checkBox.isChecked = !checkBox.isChecked
                    if (checkBox.isChecked) {
                        count++
                        mHeroSelected.add(mHero[position].imagePath.orEmpty())
                        listener.onCheck(count)
                        listener.onSelected(mHeroSelected)

                    } else {
                        count--
                        listener.onCheck(count)
                        mHeroSelected.remove(mHero[position].imagePath.orEmpty())
                        listener.onSelected(mHeroSelected)

                    }


                    Log.d("AAAAA", "$count $mHeroSelected")
                }
            } else {
                count = 0
                checkBox.visibility = View.GONE
                parent1.setOnClickListener(null)
                mImageHero.setOnClickListener {
                    listener.onItemClick(item)
                }
            }
            Log.d("SSSSS", "$position = ${mHero[position]}  va isCheck " + isShowCheck)
            Log.d("SSSSS", "===============================================")
            mHeroSelected.clear()
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
    fun setList(list: MutableList<Hero>) {
        mHero.clear()
        mHero.addAll(list)
        notifyDataSetChanged()
    }

    /**
     *
     */
    fun setShowCheckBox(isCheck: Boolean) {
        isShowCheck = isCheck
        notifyDataSetChanged()
    }

    fun setShowEdited(isEdited: Boolean) {
        isShowEdit = isEdited
        notifyDataSetChanged()
    }

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            val inflater = LayoutInflater.from(mContext)
            val heroView = inflater.inflate(R.layout.hero_item, parent, false)
            return ViewHolder(heroView)
        }
        val inflater = LayoutInflater.from(mContext)
        val heroView = inflater.inflate(R.layout.hero_item_open_photo, parent, false)
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
                (holder as ViewHolder2).mTextName.text = hero.name
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

        /**
         *
         */
        fun onCheck(count: Int)

        /**
         *
         */
        fun onSelected(array: List<String>)
    }

}

