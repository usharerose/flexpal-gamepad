package io.github.usharerose.flexpal.gamepad.android

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import android.content.Context
import android.app.Application
import java.lang.ref.WeakReference

class UdpManager private constructor(application: Application) {

    private val applicationContext = application.applicationContext
    private var udpSocket: DatagramSocket? = null
    private var udpHost: String = UdpConstants.DEFAULT_HOST
    private var udpPort: Int = UdpConstants.DEFAULT_PORT

    companion object {
        @Volatile
        private var instance: WeakReference<UdpManager>? = null
        private const val PREFS_NAME = "udp_config"
        private const val KEY_HOST = "host"
        private const val KEY_PORT = "port"

        fun getInstance(context: Context): UdpManager {
            val app = context.applicationContext as Application
            return instance?.get() ?: synchronized(this) {
                instance?.get() ?: UdpManager(app).also {
                    instance = WeakReference(it)
                    it.loadSavedConfig()
                }
            }
        }
    }

    private fun initialize() {
        if (udpSocket == null) {
            udpSocket = DatagramSocket().apply {
                broadcast = true
            }
        }
    }

    fun sendMessage(message: ByteArray) {
        if (udpSocket == null) {
            initialize()
        }

        Thread {
            try {
                val packet = DatagramPacket(
                    message,
                    message.size,
                    InetAddress.getByName(this.udpHost),
                    this.udpPort
                )
                udpSocket?.send(packet)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun loadSavedConfig() {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        this.udpHost = prefs.getString(KEY_HOST, UdpConstants.DEFAULT_HOST) ?: UdpConstants.DEFAULT_HOST
        this.udpPort = prefs.getInt(KEY_PORT, UdpConstants.DEFAULT_PORT)
    }

    fun updateConfig(host: String, port: Int) {
        this.udpHost = host
        this.udpPort = port
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_HOST, host)
            putInt(KEY_PORT, port)
            apply()
        }
        recreateSocket()
    }

    private fun recreateSocket() {
        release()
        initialize()
    }

    fun release() {
        udpSocket?.close()
        udpSocket = null
    }
}