package io.github.usharerose.flexpal.gamepad.android

sealed class ProtocolMessage {

    data class PressureMessage(val values: List<Int>) : ProtocolMessage() {
        init {
            require(values.size == CHAMBER_COUNT) { "Must provide exactly $CHAMBER_COUNT values" }
            require(values.all { it in -50..50 }) { "All values must be between -50 and 50" }
        }

        companion object {
            const val CHAMBER_COUNT = 9
            const val CMD_TYPE: Byte = 0x01
        }
    }
}
