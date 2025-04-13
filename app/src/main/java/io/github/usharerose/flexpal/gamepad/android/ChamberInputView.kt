package io.github.usharerose.flexpal.gamepad.android

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast

class ChamberInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    companion object {
        const val MIN_VAL = -50
        const val MAX_VAL = 50
    }

    private val seekBar: SeekBar
    private val editText: EditText

    interface OnValueChangeListener {
        fun onValueChanged()
    }

    private var valueChangeListener: OnValueChangeListener? = null

    fun setOnValueChangeListener(listener: OnValueChangeListener) {
        valueChangeListener = listener
    }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.chamber_input, this, true)

        editText = findViewById(R.id.editText)
        val initialValue = (MAX_VAL - MIN_VAL) / 2 + MIN_VAL
        editText.setText(initialValue.toString())

        seekBar = findViewById(R.id.seekBar)
        seekBar.progress = inputValueToProgress(initialValue)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val inputValue = progressToInputValue(progress)
                editText.setText(inputValue.toString())
                valueChangeListener?.onValueChanged()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputValue = s.toString().toIntOrNull()
                if (inputValue != null) {
                    if (inputValue in MIN_VAL..MAX_VAL) {
                        val progress = inputValueToProgress(inputValue)
                        seekBar.progress = progress
                    } else {
                        Toast.makeText(
                            context,
                            "The value range is between $MIN_VAL to $MAX_VAL",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    seekBar.progress = inputValueToProgress(initialValue)
                }
                valueChangeListener?.onValueChanged()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun progressToInputValue(progress: Int): Int {
        return (progress.toFloat() / seekBar.max * (MAX_VAL - MIN_VAL) + MIN_VAL).toInt()
    }

    private fun inputValueToProgress(inputValue: Int): Int {
        return ((inputValue - MIN_VAL).toFloat() / (MAX_VAL - MIN_VAL) * seekBar.max).toInt()
    }

    fun getValue(): Int {
        return editText.text.toString().toInt()
    }
}
