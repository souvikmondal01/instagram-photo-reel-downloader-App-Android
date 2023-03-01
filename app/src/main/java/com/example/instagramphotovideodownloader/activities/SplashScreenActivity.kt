package com.example.instagramphotovideodownloader.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.instagramphotovideodownloader.R

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
            finish()
        }, 800)
    }
}