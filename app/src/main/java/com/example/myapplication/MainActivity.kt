package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        mHeroAdapter= HeroAdapter(this,mHeros)
        mRecyclerHero.adapter=mHeroAdapter
        mRecyclerHero.layoutManager=LinearLayoutManager(this)
    }
    private fun creatHeroList()
    {
        mHeros.add(Hero("thor", com.google.android.material.R.drawable.abc_ab_share_pack_mtrl_alpha))
        mHeros.add(Hero("ironmen", com.google.android.material.R.drawable.avd_hide_password))
        mHeros.add(Hero("hulk", com.google.android.material.R.drawable.design_ic_visibility))
        mHeros.add(Hero("spidermen", com.google.android.material.R.drawable.abc_ic_menu_selectall_mtrl_alpha))
        mHeros.add(Hero("sad", com.google.android.material.R.drawable.notification_template_icon_low_bg))

    }
}