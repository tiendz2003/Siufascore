package com.jerry.ronaldo.siufascore.utils

import android.content.BroadcastReceiver
import android.content.Intent
import timber.log.Timber


class PiPMediaReceiver : BroadcastReceiver() {
    companion object{
         const val ACTION_MEDIA_CONTROL = "ACTION_MEDIA_CONTROL"
         const val ACTION_PIP_MEDIA_CONTROL = "com.jerry.ronaldo.siufascore.ACTION_PIP_MEDIA_CONTROL"
    }
    override fun onReceive(context: android.content.Context?, intent: Intent?) {
        if (intent?.action == ACTION_PIP_MEDIA_CONTROL) {
            val control = intent.getStringExtra("control")
            when (control) {
                "play_pause" -> {
                    val mediaBroadcast =
                        Intent(ACTION_PIP_MEDIA_CONTROL).putExtra("control", "play_pause").setPackage(
                            context?.packageName
                        )
                    context?.sendBroadcast(mediaBroadcast)
                }

                else -> {
                    Timber.w("Unknown action received: ${intent?.action}")
                }
            }
        }
    }
}