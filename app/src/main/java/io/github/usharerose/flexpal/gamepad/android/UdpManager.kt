package io.github.usharerose.flexpal.gamepad.android

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpManager private constructor() {

    private var udpSocket: DatagramSocket? = null

    companion object {
        private const val UDP_PORT = 12345
        private const val UDP_HOST = "10.0.2.2"

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
                    InetAddress.getByName(UDP_HOST),
                    UDP_PORT
                )
                udpSocket?.send(packet)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun release() {
        udpSocket?.close()
        udpSocket = null
    }
}