package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private lateinit var mHeros:MutableList<Hero>
    private lateinit var  mRecyclerHero:RecyclerView
    private lateinit var mHeroAdapter: HeroAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRecyclerHero=findViewById(R.id.recyclerHero)
        mHeros= mutableListOf<Hero>()
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)

        creatHeroList()
        mHeroAdapter= HeroAdapter(this,mHeros)
        mRecyclerHero.adapter=mHeroAdapter
        //mRecyclerHero.layoutManager=LinearLayoutManager(this)
        mRecyclerHero.layoutManager=gridLayoutManager
    }

    private fun creatHeroList()
    {
        mHeros.add(Hero("thor", R.drawable.thor))
        mHeros.add(Hero("ironmen", R.drawable.dafodil))
        mHeros.add(Hero("hulk", R.drawable.hulk))
        mHeros.add(Hero("spidermen", R.drawable.spiderman))

        //mHeros.add(Hero("sad", com.google.android.material.R.drawable.notification_template_icon_low_bg))

    }
}