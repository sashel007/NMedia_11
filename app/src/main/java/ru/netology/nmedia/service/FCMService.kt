package ru.netology.nmedia.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AppActivity
import kotlin.random.Random

class FCMService : FirebaseMessagingService() {

    private val channelId = "server"

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        message.data["action"]?.let {
            when (Actions.valueOf(it)) {
                Actions.LIKE -> handleLike(
                    Gson().fromJson(
                        message.data["content"],
                        Like::class.java
                    )
                )
            }
        }
        println(Gson().toJson(message))
    }

    private fun handleLike(like: Like) {
        val intent = Intent(this, AppActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setContentText(
                getString(
                    R.string.notification_user_liked,
                    like.userName,
                    like.postAuthor
                )
            )
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(
                Random.nextInt(10_000),
                notification
            )
        }

    }

    override fun onNewToken(token: String) {
        println(token)
    }
}

enum class Actions {
    LIKE
}

data class Like(
    val userId: Int,
    val userName: String,
    val postId: Int,
    val postAuthor: String
)