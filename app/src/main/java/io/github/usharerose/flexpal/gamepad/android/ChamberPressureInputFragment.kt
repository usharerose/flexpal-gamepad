package io.github.usharerose.flexpal.gamepad.android

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.usharerose.flexpal.gamepad.android.databinding.ChamberPressureInputFragmentBinding

class ChamberPressureInputFragment : Fragment() {

    private var _binding: ChamberPressureInputFragmentBinding? = null

    private val binding get() = _binding!!

    companion object {
        const val CHAMBER_COUNT = 9
    }

    private val udpManager = UdpManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ChamberPressureInputFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChamberInputs()
        updateValuesDisplay(sendMessage = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        udpManager.release()
    }

    private fun initChamberInputs() {
        for (i in 1..CHAMBER_COUNT) {
            appendChamberPressureInputView(i)
        }
    }

    private fun appendChamberPressureInputView(idx: Int) {
        val chamberPressureInputView = ChamberPressureInputView(requireContext()).apply {
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
        binding.chamberPressureInputContainer.addView(chamberPressureInputView)
    }

    private fun getAllValues(): List<Int> {
        val container = binding.chamberPressureInputContainer
        return (0 until container.childCount).map { index ->
            val chamberInputView = container.getChildAt(index) as ChamberPressureInputView
            chamberInputView.getValue()
        }
    }

    private fun updateValuesDisplay(sendMessage: Boolean = true) {
        val values = getAllValues()
        binding.valuesTextView.text = getString(
            R.string.chamber_inputs,
            values.joinToString(", ")
        )
        if (sendMessage) {
            val message = ProtocolMessage.PressureMessage(values)
            val encodedMessage = ProtocolEncoder().encode(message)
            udpManager.sendMessage(encodedMessage)
        }
    }
}
