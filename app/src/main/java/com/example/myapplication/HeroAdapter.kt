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


class HeroAdapter(private val mContext: Context) :
    RecyclerView.Adapter<HeroAdapter.ViewHolder?>() {
     private var mHero = mutableListOf<Hero>()
    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageHero: ImageView
        val mTextName: TextView


        init {
            mImageHero = itemView.findViewById(R.id.image_hero)
            mTextName = itemView.findViewById(R.id.text_name)
        }
    }

     fun setList(list: MutableList<Hero>){
        mHero.clear()
        mHero.addAll(list)
         notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val heroView = inflater.inflate(R.layout.hero_item, parent, false)
        val viewHolder = ViewHolder(heroView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hero = mHero[position]
        if(hero.image!=-1)
        Glide.with(mContext).load(hero.image).into(holder.mImageHero)
        else{
           if (hero.imagePath!=null)
               Glide.with(mContext).load(hero.imagePath).into(holder.mImageHero)

        }
        holder.mTextName.text = hero.name

    }

    override fun getItemCount(): Int {
        return mHero.size
    }
}

