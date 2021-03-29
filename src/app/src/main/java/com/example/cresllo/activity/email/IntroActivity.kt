package com.example.cresllo.activity.email

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.cresllo.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
intro_signup.setOnClickListener {
    startActivity(Intent(this@IntroActivity, SignUp::class.java))
}

        intro_signin.setOnClickListener {
            startActivity(Intent(this@IntroActivity, SignIn::class.java))
        }



    }
}