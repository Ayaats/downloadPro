package com.udacity.util


import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R


private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0


@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(fileName: String, applicationContext: Context, status: String) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
        .putExtra("fileName",fileName)
        .putExtra("status",status)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    val cloudImage  = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_baseline_cloud_download_24
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(cloudImage)
        .bigLargeIcon(null)



    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_channel_id)
    )




        .setSmallIcon(R.drawable.ic_baseline_arrow_circle_down_24)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(fileName)

        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)



        //  Step 2.3 add action to open the details activity
        .addAction(0,
            applicationContext.getString(R.string.notification_action_details),
            contentPendingIntent)

        //  Step 2.5 set priority
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        // add style to builder
        .setStyle(bigPicStyle)
        .setLargeIcon(cloudImage)


    notify(NOTIFICATION_ID, builder.build())

}


fun NotificationManager.cancelNotifications() {
    cancelAll()
}
