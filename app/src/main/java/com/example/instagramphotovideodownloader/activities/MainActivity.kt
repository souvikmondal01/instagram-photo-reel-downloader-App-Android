package com.example.instagramphotovideodownloader.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.instagramphotovideodownloader.adapter.ViewpagerAdapter
import com.example.instagramphotovideodownloader.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//        }

        binding.viewpager.adapter = ViewpagerAdapter(supportFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewpager)
    }

}