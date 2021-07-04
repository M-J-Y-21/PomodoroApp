package com.example.pomodoroapp.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.pomodoroapp.R
import java.lang.IllegalStateException

class WhiteNoiseDialogFragment(val waves: MediaPlayer, val forrest: MediaPlayer, val rain: MediaPlayer) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            var checkedSound = -1
            // use builder to construct dialog
            val builder = AlertDialog.Builder(it)
            builder.setTitle(R.string.noise_dialog)
                .setSingleChoiceItems(
                    R.array.noise_array, checkedSound,
                    DialogInterface.OnClickListener { dialog, which ->
                        checkedSound = which
                    })
                .setPositiveButton(
                    R.string.set_sound,
                    DialogInterface.OnClickListener { dialog, id ->
                        when (checkedSound) {
                            0 -> waves.start()
                            1 -> forrest.start()
                            2 -> rain.start()
                        }
                    })
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        when (checkedSound) {
                            0 -> waves.pause()
                            1 -> forrest.pause()
                            2 -> rain.pause()
                        }
//                        waves.pause()
//                        waves.stop()
//                        waves?.release()
                        // waves = null
                    })

            // create the dialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }


}