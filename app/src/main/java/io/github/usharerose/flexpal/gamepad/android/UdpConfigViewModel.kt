package io.github.usharerose.flexpal.gamepad.android

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UdpConfigViewModel(private val preferences: SharedPreferences) : ViewModel() {

    companion object {
        private const val DEFAULT_IP = "192.168.1.1"
        private const val DEFAULT_PORT = 8080
    }

    private val udpManager = UdpManager.getInstance()
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
            host = preferences.getString("host", DEFAULT_IP) ?: DEFAULT_IP,
            port = preferences.getInt("port", DEFAULT_PORT)
        )
    }
}

data class UdpConfig(
    val host: String,
    val port: Int,
)
