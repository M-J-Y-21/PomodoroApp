package com.example.pomodoroapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import com.example.pomodoroapp.util.NotificationUtil
import com.example.pomodoroapp.util.PrefUtil
import android.os.Vibrator
import android.view.WindowManager

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)

        MainActivity.vibratePhone(context)

        //MainActivity.showDefaultDialog(context)

        PrefUtil.setTimerState(MainActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)


        try {
            val bundle = intent.extras
            val message = bundle!!.getString("alarm_message")
            val newIntent = Intent(context, PopupActivity::class.java)
            newIntent.putExtra("alarm_message", message)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(newIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

}