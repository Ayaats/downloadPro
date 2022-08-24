package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        notificationManager =ContextCompat.getSystemService(
            applicationContext,NotificationManager::class.java
        )as NotificationManager

        notificationManager.cancelAll()

        val nameFile=intent.getStringExtra("fileName")
        val detailsStatus =intent.getStringExtra("status")

        file_name.text=nameFile
        status.text =detailsStatus


    }

    fun backButtonClicked(view: View) {
        finish()

    }



}
