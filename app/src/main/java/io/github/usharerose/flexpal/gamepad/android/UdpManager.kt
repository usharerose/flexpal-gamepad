package io.github.usharerose.flexpal.gamepad.android

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import android.util.Log

class UdpManager private constructor() {

    private var udpSocket: DatagramSocket? = null
    private var udpHost: String = UdpConstants.DEFAULT_HOST
    private var udpPort: Int = UdpConstants.DEFAULT_PORT

    companion object {
        @Volatile
        private var instance: UdpManager? = null

        fun getInstance(): UdpManager {
            return instance ?: synchronized(this) {
                instance ?: UdpManager().also { instance = it }
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

    fun updateConfig(host: String, port: Int) {
        Log.i("UdpManager", "updateConfig: $host, $port")
        this.udpHost = host
        this.udpPort = port
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