package com.example.cresllo.activity

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.cresllo.Firestore.Firebasestore
import com.example.cresllo.R
import com.example.cresllo.activity.email.IntroActivity
import com.example.cresllo.activity.tasks.MainActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val typeface = Typeface.createFromAsset(assets,"CarbonBlock.ttf")
     tv_app_name.typeface =typeface

        Handler().postDelayed({
            var currentUserId = Firebasestore().getCurrentUserId()
            if (currentUserId.isNotEmpty()){
                startActivity(Intent(this@SplashScreen,
                    MainActivity::class.java))
            }else {
                startActivity(
                    Intent(
                        this@SplashScreen,
                        IntroActivity::class.java
                    )
                )
            }
            finish()
            },2500
        )
    }
}