package com.example.pomodoroapp

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsActivityFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }
}