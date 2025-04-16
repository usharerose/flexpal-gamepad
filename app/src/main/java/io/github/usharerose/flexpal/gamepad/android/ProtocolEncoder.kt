package io.github.usharerose.flexpal.gamepad.android

class ProtocolEncoder {

    private val encoders = mapOf<Class<out ProtocolMessage>, MessageEncoder<*>>(
        ProtocolMessage.PressureMessage::class.java to PressureMessageEncoder(),
    )
    
    @Suppress("UNCHECKED_CAST")
    fun encode(message: ProtocolMessage): ByteArray {
        val encoder = encoders[message::class.java] 
            ?: throw IllegalArgumentException("No encoder found for ${message::class.java}")
        return (encoder as MessageEncoder<ProtocolMessage>).encode(message)
    }
}

interface MessageEncoder<T : ProtocolMessage> {
    fun encode(message: T): ByteArray
}

class PressureMessageEncoder : MessageEncoder<ProtocolMessage.PressureMessage> {
    /*
    the protocol of byte message is as follows:
        field | size (bytes) |                                              description
    ----------+--------------+----------------------------------------------------------
     CMD_TYPE |            1 |                                0x01: pressure; 0x02: PWM
     RESERVED |            1 |                     placeholder for future flags or sync
      VALUE_0 |            2 | input value for chamber 0, converted to unsigned integer
      VALUE_1 |            2 | input value for chamber 1, converted to unsigned integer
          ... |          ... |                                                      ...
      VALUE_8 |            2 | input value for chamber 8, converted to unsigned integer
    */
    companion object {
        private const val CMD_TYPE_SIZE = 1
        private const val PLACEHOLDER_SIZE = 1
        private const val VALUE_SIZE = 2
        private const val VALUE_OFFSET = 50
    }

    override fun encode(message: ProtocolMessage.PressureMessage): ByteArray {
        val packetSize = CMD_TYPE_SIZE + PLACEHOLDER_SIZE + (message.values.size * VALUE_SIZE)
        return ByteArray(packetSize).apply {
            this[0] = ProtocolMessage.PressureMessage.CMD_TYPE
            this[1] = 0.toByte()
            
            message.values.forEachIndexed { index, inputValue ->
                val offset = CMD_TYPE_SIZE + PLACEHOLDER_SIZE + (index * VALUE_SIZE)
                val mappedValue = inputValue + VALUE_OFFSET
                require(mappedValue in 0..0xFFFF) { "input chamber $index value is out of range" }
                this[offset] = (mappedValue shr 8).toByte()
                this[offset + 1] = (mappedValue and 0xFF).toByte()
            }
        }
    }
}
