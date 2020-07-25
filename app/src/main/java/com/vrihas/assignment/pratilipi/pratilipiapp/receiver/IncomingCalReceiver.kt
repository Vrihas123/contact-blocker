package com.vrihas.assignment.pratilipi.pratilipiapp.receiver


import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import com.android.internal.telephony.ITelephony
import com.vrihas.assignment.pratilipi.pratilipiapp.data.repository.ContactDataRepository
import com.vrihas.assignment.pratilipi.pratilipiapp.ui.activity.HomeActivity
import java.lang.reflect.Method


class IncomingCalReceiver : BroadcastReceiver() {

    lateinit var contactDataRepository: ContactDataRepository

    var telephonyService: ITelephony? = null
    var telecomManager: TelecomManager? = null

    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onReceive(context: Context, intent: Intent) {

            contactDataRepository = ContactDataRepository()
        try {
            val state: String = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val number: String? = intent.getExtras()?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING, ignoreCase = true)) {
                val tm = context.getSystemService(Context.TELEPHONY_SERVICE)
                try {
                    val m: Method = tm.javaClass.getDeclaredMethod("getITelephony")
                    m.setAccessible(true)
                    telephonyService = m.invoke(tm) as ITelephony?
                    if (number != null) {
                        Log.e("incoming", "number===> " + number)
                        val numberList = contactDataRepository.getBlockedContacts(true)
                        for (contact in numberList) {
                            if (contact.number?.contains(number)!! || number.contains(contact.number)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager?
                                    if (context.getSystemService(Context.TELECOM_SERVICE) != null) {
                                        val success = telecomManager?.endCall()
                                    }
                                } else{
                                    telephonyService!!.endCall()
                                }

                                showNotifications(context, contact.name.toString())
                                break
                            }
                        }
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                                    telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager?
//                                    if (context.getSystemService(Context.TELECOM_SERVICE) != null) {
//                                        val success = telecomManager?.endCall()
//                                        showNotifications(context, number)
//                                    }
//                                }
//                        Toast.makeText(
//                            context,
//                            "Ending the call from: $number",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
//                Toast.makeText(context, "Ring $number", Toast.LENGTH_SHORT).show()
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK, ignoreCase = true)) {
//                Toast.makeText(context, "Answered $number", Toast.LENGTH_SHORT).show()
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE, ignoreCase = true)) {
//                Toast.makeText(context, "Idle $number", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        try {
//            val c = Class.forName(tm.javaClass.name)
//            val m: Method = c.getDeclaredMethod("getITelephony")
//            m.setAccessible(true)
//            telephonyService = m.invoke(tm) as ITelephony
//            val bundle = intent.extras
//            val phoneNumber = bundle!!.getString("incoming_number")
//            Log.d("INCOMING", phoneNumber)
//            if (phoneNumber != null) {
//                telephonyService!!.endCall()
//                Log.d("HANG UP", phoneNumber)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }

    fun showNotifications(context: Context, name: String) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context,
            HomeActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(context,
            0,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        //checking if android version is greater than oreo(API 26) or not
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                channelId,name,NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context,channelId)
                .setSmallIcon(R.drawable.ic_dialog_alert)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                    R.drawable.ic_dialog_alert))
                .setContentTitle("Blocked!")
                .setContentText("The incoming call from " + name + " is blocked")
                .setContentIntent(pendingIntent)
        }else{
            builder = Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_dialog_alert)
                .setLargeIcon(
                    BitmapFactory.decodeResource(context.resources,
                    R.drawable.ic_dialog_alert))
                .setContentTitle("Blocked")
                .setContentText("The incoming call from " + name + " is blocked")
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234,builder.build())
    }



}

