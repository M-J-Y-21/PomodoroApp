package com.example.pomodoroapp

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.example.pomodoroapp.databinding.ActivityPopupBinding

class PopupActivity : Activity() {

    private lateinit var binding: ActivityPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopupBinding.inflate(layoutInflater)
        val view = binding.root
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager: KeyguardManager =
                getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (keyguardManager != null)
                keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        setContentView(view)

        binding.btnBreakY.setOnClickListener {
            Toast.makeText(this, "You can start your break", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
           // intent.putExtra("Start Break", true)
            startActivity(intent)
        }

        binding.btnBreakN.setOnClickListener {
            Toast.makeText(
                this, "I see you don't want to start your break now",
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


}