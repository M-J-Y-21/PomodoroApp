package com.example.pomodoroapp

import android.app.AlarmManager
import android.app.ListActivity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.nfc.Tag
import android.os.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.pomodoroapp.databinding.ActivityMainBinding
import com.example.pomodoroapp.dialog.WhiteNoiseDialogFragment
import com.example.pomodoroapp.dialog.WorkTagDialogFragment
import com.example.pomodoroapp.util.NotificationUtil
import com.example.pomodoroapp.util.PrefUtil
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.util.*
import java.util.EnumSet.of
import java.util.List.of

open class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    lateinit var viewModel: SharedViewModel

    private lateinit var waves: MediaPlayer
    private lateinit var forrest: MediaPlayer
    private lateinit var rain: MediaPlayer


    companion object {


        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
            //showDefaultDialog(context)
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000

        fun vibratePhone(context: Context) {
            val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        200,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(200)
            }
        }

        // to set status bar for settings
        var darkMode: Boolean = false


    }


    enum class TimerState {
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped

    private var firstBackgroundChange = true

    private var secondsRemaining = 0L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        supportActionBar?.setIcon(R.drawable.ic_timer)
        supportActionBar?.title = "         Timer"


        binding.fabStart.setOnClickListener { v ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

        binding.fabPause.setOnClickListener { v ->
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        binding.fabStop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }



        waves = MediaPlayer.create(this, R.raw.thai_wave)
        forrest = MediaPlayer.create(this, R.raw.forest_sound)
        rain = MediaPlayer.create(this, R.raw.rain_sound)
        val noiseDialog = WhiteNoiseDialogFragment(waves, forrest, rain)
        binding.fabNoise.setOnClickListener {
            noiseDialog.show(supportFragmentManager, "white noise dialog")
        }

        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        viewModel.tag.observe(this, Observer { newTag ->
            if (viewModel.tag.value != null) {
                if (viewModel.tag.value != "") {
                    binding.workTagTxt.text = viewModel.tag.value

                    if(firstBackgroundChange)
                        binding.workTagTxt.background = getDrawable(R.drawable.text_bg)
                }

                else {
                    binding.workTagTxt.text = viewModel.tag.value
                    binding.workTagTxt.background = null
                }
            }


        })

        Log.d("onCreate", "onCreate is called")

        val appSettingPrefs: SharedPreferences = getSharedPreferences("AppSettingPrefs", 0)
        val sharedPrefsEdit: SharedPreferences.Editor = appSettingPrefs.edit()
        val isNightModeOn: Boolean = appSettingPrefs.getBoolean("NightMode", false)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.purple_500)
        }

        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            window.statusBarColor = this.resources.getColor(R.color.grey)
            binding.btnChangeBg.text = "Disable Dark Mode"
            darkMode = true
            Log.d("MainActivity", "Tag value is '${viewModel.tag.value}' when night mode is on")
            firstBackgroundChange = false
            if (viewModel.tag.value.toString() != "") {
                binding.workTagTxt.text = viewModel.tag.value
                binding.workTagTxt.background = getDrawable(R.drawable.text_bg_dark)
                Log.d("MainActivity", "tag if conditional goes through for night mode on")
            }
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            window.statusBarColor = this.resources.getColor(R.color.purple_500)
            binding.btnChangeBg.text = "Enable Dark Mode"
            darkMode = false
            Log.d("MainActivity", "Tag value is '${viewModel.tag.value}' when night mode is off")
            if (viewModel.tag.value.toString() != "") {
                firstBackgroundChange = false
                binding.workTagTxt.text = viewModel.tag.value
                binding.workTagTxt.background = getDrawable(R.drawable.text_bg)
                Log.d("MainActivity", "tag if conditional goes through for night mode off")
            }

        }
        binding.btnChangeBg.setOnClickListener {
            firstBackgroundChange = false
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                sharedPrefsEdit.apply()

                binding.btnChangeBg.text = "Enable Dark Mode"
                if (viewModel.tag.value != "") {
                    binding.workTagTxt.text = viewModel.tag.value
                    binding.workTagTxt.background = getDrawable(R.drawable.text_bg)
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                sharedPrefsEdit.apply()

                binding.btnChangeBg.text = "Disable Dark Mode"
                if (viewModel.tag.value != "") {
                    binding.workTagTxt.text = viewModel.tag.value
                    binding.workTagTxt.background = getDrawable(R.drawable.text_bg_dark)
                }
            }
        }



    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "On start is called")
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "On start is called")
    }

    // called every single time activity comes up on the screen
    override fun onResume() {
        Log.d("MainActivity", "onResume Called")
        super.onResume()

        initTimer()

        removeAlarm(this)

        NotificationUtil.hideTimerNotification(this)


    }

    override fun onPause() {
        super.onPause()

        if (timerState == MainActivity.TimerState.Running) {
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this, wakeUpTime)
        } else if (timerState == MainActivity.TimerState.Paused) {
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)

//        if (viewModel.tag.value != null) {
//            binding.workTagTxt.text = viewModel.tag.value
//            binding.workTagTxt.background = getDrawable(R.drawable.text_bg)
//        }

        // Check Lifecycle method or alt that will be able to called everytime dialogfrag is dismissed

        Log.d("MainActivity", "onPause Called")

    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(this)

        if (timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0)
            secondsRemaining -= nowSeconds - alarmSetTime

        if (secondsRemaining <= 0)
            onTimerFinished()
        else if (timerState == TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer() {
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }

        }.start()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        val progressCountdown = findViewById<MaterialProgressBar>(R.id.progress_countdown)
        val textviewCountdown = findViewById<TextView>(R.id.textview_countdown)
        textviewCountdown.text = "$minutesUntilFinished:${
            if (secondsStr.length == 2) secondsStr
            else "0" + secondsStr
        }"
        progressCountdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun onTimerFinished() {
        timerState = TimerState.Stopped

        setNewTimerLength()

        val progressCountdown = findViewById<MaterialProgressBar>(R.id.progress_countdown)
        progressCountdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
        //showDefaultDialog(this)
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes * 60L)
        val progressCountdown = findViewById<MaterialProgressBar>(R.id.progress_countdown)
        progressCountdown.max = timerLengthSeconds.toInt()
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Running -> {
                binding.fabStart.isEnabled = false
                binding.fabPause.isEnabled = true
                binding.fabStop.isEnabled = true
            }
            TimerState.Stopped -> {
                binding.fabStart.isEnabled = true
                binding.fabPause.isEnabled = false
                binding.fabStop.isEnabled = false
            }
            TimerState.Paused -> {
                binding.fabStart.isEnabled = true
                binding.fabPause.isEnabled = false
                binding.fabStop.isEnabled = true
            }
        }
    }


    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        val progressCountdown = findViewById<MaterialProgressBar>(R.id.progress_countdown)
        progressCountdown.max = timerLengthSeconds.toInt()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_tags -> {
                val workTagDialog = WorkTagDialogFragment()
                workTagDialog.show(supportFragmentManager, "workTagDialog")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        Log.d("onDestroy", "This is when activity is destroyed")
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}

