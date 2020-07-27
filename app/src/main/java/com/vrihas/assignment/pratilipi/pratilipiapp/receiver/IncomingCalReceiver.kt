package com.vrihas.assignment.pratilipi.pratilipiapp.receiver


import android.annotation.SuppressLint
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
import com.vrihas.assignment.pratilipi.pratilipiapp.R
import com.vrihas.assignment.pratilipi.pratilipiapp.data.repository.ContactDataRepository
import com.vrihas.assignment.pratilipi.pratilipiapp.ui.activity.HomeActivity
import java.lang.reflect.Method

/*
    Broadcast Receiver class containing logic for listening incoming calls and disconnecting them
 */

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class IncomingCalReceiver : BroadcastReceiver() {

    private lateinit var contactDataRepository: ContactDataRepository

    // Objects responsible for ending incoming call
    private lateinit var telephonyService: ITelephony
    private lateinit var telecomManager: TelecomManager

    // Notification related objects
    private lateinit var notificationManager : NotificationManager
    private lateinit var notificationChannel : NotificationChannel
    private lateinit var builder : Notification.Builder
    private val channelId = "i.apps.notifications"

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

            contactDataRepository = ContactDataRepository()
        try {
            val state: String = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val number: String? = intent.extras?.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING, ignoreCase = true)) { // Checking the state of the incoming call
                val tm = context.getSystemService(Context.TELEPHONY_SERVICE)
                try {
                    val m: Method = tm.javaClass.getDeclaredMethod("getITelephony")
                    m.isAccessible = true
                    telephonyService = m.invoke(tm) as ITelephony
                    if (number != null) {
                        Log.e("incoming", "number===> $number")
                        /*
                            Fetching list of blocked contacts and then matching the incoming call to the numbers of blocked contacts
                         */
                        val numberList = contactDataRepository.getBlockedContacts(true)
                        for (contact in numberList) {
                            if (contact.number?.contains(number)!! || number.contains(contact.number)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                                    if (context.getSystemService(Context.TELECOM_SERVICE) != null) {
                                        telecomManager.endCall() // Ending the call for android version greater than 8.0
                                    }
                                } else{
                                    telephonyService.endCall()  // Ending the call for android version less than 8.0
                                }

                                showNotifications(context, contact.name.toString())
                                break
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /*
        Creating notifications according to the android versions
     */

    private fun showNotifications(context: Context, name: String) {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, HomeActivity::class.java)

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
                .setSmallIcon(R.drawable.ic_baseline_error_48)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                    R.drawable.ic_baseline_error_48))
                .setContentTitle("Blocked!")
                .setContentText("The incoming call from $name is blocked")
                .setContentIntent(pendingIntent)
        }else{
            builder = Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_baseline_error_48)
                .setLargeIcon(
                    BitmapFactory.decodeResource(context.resources,
                    R.drawable.ic_baseline_error_48))
                .setContentTitle("Blocked")
                .setContentText("The incoming call from $name is blocked")
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1234,builder.build())
    }



}

