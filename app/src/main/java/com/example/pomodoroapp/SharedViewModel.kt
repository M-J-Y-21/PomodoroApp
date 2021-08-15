package com.example.pomodoroapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private var _tag = MutableLiveData("")
    val tag: LiveData<String> = _tag

    fun saveTag(tag: String) {
        _tag.value = tag
    }




}