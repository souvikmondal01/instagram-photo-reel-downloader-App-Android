package com.example.instagramphotovideodownloader.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramphotovideodownloader.adapter.ViewpagerAdapter
import com.example.instagramphotovideodownloader.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewpager.adapter = ViewpagerAdapter(supportFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewpager)
    }

}