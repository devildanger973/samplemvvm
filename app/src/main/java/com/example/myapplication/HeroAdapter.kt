package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class HeroAdapter(private val mContext: Context, private val listener:OnItemClickListener) :
    RecyclerView.Adapter< RecyclerView.ViewHolder?>() {
    companion object {
        const val VIEW_TYPE_ONE = 1
        const val VIEW_TYPE_TWO = 2
    }
     private var mHero = mutableListOf<Hero>()
    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageHero: ImageView
        val mTextName: TextView
        init {
            mImageHero = itemView.findViewById(R.id.image_hero)
            mTextName = itemView.findViewById(R.id.text_name)
        }
       fun bind(item:Hero){
           mImageHero.setOnClickListener{
               listener.onItemClick(item)
           }
       }
    }
    inner class ViewHolder2(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageHero: ImageView
        val mTextName: TextView
        init {
            mImageHero = itemView.findViewById(R.id.image_hero)
            mTextName = itemView.findViewById(R.id.text_name)
        }
        fun bind(item:Hero){
            mImageHero.setOnClickListener{
                listener.onOpenFolderClick()
            }
        }
    }
     fun setList(list: MutableList<Hero>){
        mHero.clear()
        mHero.addAll(list)
         notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ONE) {
            val inflater = LayoutInflater.from(mContext)
            val heroView = inflater.inflate(R.layout.hero_item, parent, false)
            val viewHolder = ViewHolder(heroView)
            return viewHolder
        }
        val inflater = LayoutInflater.from(mContext)
        val heroView = inflater.inflate(R.layout.hero_item_open_photo, parent, false)
        val viewHolder2 = ViewHolder2(heroView)
        return viewHolder2
    }
    override fun getItemViewType(position: Int): Int {
        return mHero[position].viewType
    }
    override fun onBindViewHolder(holder:  RecyclerView.ViewHolder, position: Int) {
        val hero = mHero[position]
        when(hero.viewType){
            VIEW_TYPE_ONE ->{
                if(hero.image !=-1)
                    Glide.with(mContext).load(hero.image).into((holder as ViewHolder).mImageHero)
                else{
                    if (hero.imagePath !=null)
                        Glide.with(mContext).load(hero.imagePath).into((holder as ViewHolder).mImageHero)
                }
                (holder as ViewHolder).mTextName.text = hero.name
                holder.bind(item = hero)
            }
            VIEW_TYPE_TWO -> {
                if(hero.image !=-1)
                    Glide.with(mContext).load(hero.image).into((holder as ViewHolder2).mImageHero)
                else{
                    if (hero.imagePath !=null)
                        Glide.with(mContext).load(hero.imagePath).into((holder as ViewHolder2).mImageHero)
                }
                (holder as ViewHolder2).mTextName.text = hero.name
                holder.bind(item = hero)
            }
        }
    }
    override fun getItemCount(): Int {
        return mHero.size
    }
    interface OnItemClickListener {
        fun onItemClick(item: Hero?)
        fun onOpenFolderClick()
    }

}

