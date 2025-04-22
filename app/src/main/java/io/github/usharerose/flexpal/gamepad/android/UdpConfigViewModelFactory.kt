package io.github.usharerose.flexpal.gamepad.android

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UdpConfigViewModelFactory(
    private val application: Application,
    private val preferences: SharedPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UdpConfigViewModel::class.java)) {
            return UdpConfigViewModel(application, preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
