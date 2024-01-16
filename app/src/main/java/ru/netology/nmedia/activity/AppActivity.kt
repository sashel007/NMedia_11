package ru.netology.nmedia.activity

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ru.netology.nmedia.databinding.ActivityAppLayoutBinding

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationsPermission()

    }

    private fun requestNotificationsPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, permission
                ) == PackageManager.PERMISSION_GRANTED
            ) return
        }
        requestPermissions(arrayOf(permission), 1)

    }
}