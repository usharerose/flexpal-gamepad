package io.github.usharerose.flexpal.gamepad.android

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class UdpConfigViewModel(
    application: Application,
    private val preferences: SharedPreferences
) : AndroidViewModel(application) {

    private val udpManager = UdpManager.getInstance(application)
    private val _udpConfig = MutableLiveData<UdpConfig>()
    val udpConfig: LiveData<UdpConfig> = _udpConfig

    init {
        val savedConfig = loadSavedConfig()
        _udpConfig.value = savedConfig
        udpManager.updateConfig(savedConfig.host, savedConfig.port)
    }

    fun updateUdpConfig(config: UdpConfig) {
        _udpConfig.value = config
        udpManager.updateConfig(config.host, config.port)
        saveConfig(config)
    }

    private fun saveConfig(config: UdpConfig) {
        preferences.edit().apply {
            putString("host", config.host)
            putInt("port", config.port)
            apply()
        }
    }

    private fun loadSavedConfig(): UdpConfig {
        return UdpConfig(
            host = preferences.getString("host", UdpConstants.DEFAULT_HOST) ?: UdpConstants.DEFAULT_HOST,
            port = preferences.getInt("port", UdpConstants.DEFAULT_PORT)
        )
    }
}

data class UdpConfig(
    val host: String,
    val port: Int,
)
