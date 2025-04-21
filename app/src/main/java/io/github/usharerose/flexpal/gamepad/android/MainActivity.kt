package io.github.usharerose.flexpal.gamepad.android

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        const val CHAMBER_COUNT = 9
    }

    private lateinit var slidersContainer: LinearLayout
    private lateinit var valuesTextView: TextView
    private val udpManager = UdpManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slidersContainer = findViewById<LinearLayout>(R.id.slidersContainer)
        for (i in 1..CHAMBER_COUNT) {
            appendChamberPressureInputView(i)
        }

        valuesTextView = findViewById<TextView>(R.id.valuesTextView)
        updateValuesDisplay(sendMessage = false)
    }

    override fun onDestroy() {
        super.onDestroy()
        udpManager.release()
    }

    private fun appendChamberPressureInputView(idx: Int) {
        val chamberPressureInputView = ChamberPressureInputView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }

            setOnValueChangeListener(object : ChamberPressureInputView.OnValueChangeListener {
                override fun onValueChanged() {
                    updateValuesDisplay()
                }
            })
        }
        chamberPressureInputView.setChamberIndex(idx)

        slidersContainer.addView(chamberPressureInputView)
    }

    private fun updateValuesDisplay(sendMessage: Boolean = true) {
        val values = getAllValues()
        valuesTextView.text = getString(
            R.string.chamber_inputs,
            values.joinToString(", ")
        )
        if (sendMessage) {
            val message = ProtocolMessage.PressureMessage(values)
            val encodedMessage = ProtocolEncoder().encode(message)
            udpManager.sendMessage(encodedMessage)
        }
    }

    private fun getAllValues(): List<Int> {
        return (0 until slidersContainer.childCount).map { index ->
            val chamberInputView = slidersContainer.getChildAt(index) as ChamberPressureInputView
            chamberInputView.getValue()
        }
    }
}
