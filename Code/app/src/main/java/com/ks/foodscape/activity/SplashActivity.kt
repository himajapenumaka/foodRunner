package com.ks.foodscape.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.ks.foodscape.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        title=" "

        Handler().postDelayed({
            val startAct= Intent(this@SplashActivity,LoginActivity::class.java)
            startActivity(startAct)
        },
        2000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
