package com.vrihas.assignment.pratilipi.pratilipiapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}