package io.github.usharerose.flexpal.gamepad.android

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView

class ChamberPressureInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    companion object {
        const val MIN_VAL = -50
        const val MAX_VAL = 50
    }

    private val titleView: MaterialTextView
    private val inputSlider: Slider
    private val inputText: TextInputEditText

    interface OnValueChangeListener {
        fun onValueChanged()
    }

    private var valueChangeListener: OnValueChangeListener? = null
    private var isUpdating = false

    fun setOnValueChangeListener(listener: OnValueChangeListener) {
        valueChangeListener = listener
    }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.chamber_pressure_input, this, true)

        titleView = findViewById(R.id.chamberTitle)
        inputSlider = findViewById(R.id.pressureSlider)
        inputText = findViewById(R.id.pressureInput)
        
        val initialValue = 0
        setInputValue(initialValue)

        inputSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val inputValue = value.toInt()
                setInputValue(inputValue)
                valueChangeListener?.onValueChanged()
            }
        }

        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return

                val inputValue = s.toString().toIntOrNull()
                if (inputValue != null) {
                    if (inputValue in MIN_VAL..MAX_VAL) {
                        setInputValue(inputValue)
                        valueChangeListener?.onValueChanged()
                    }
                } else {
                    setInputValue(initialValue)
                    valueChangeListener?.onValueChanged()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    fun getValue(): Int {
        return inputText.text.toString().toIntOrNull() ?: 0
    }

    private fun setInputValue(inputValue: Int) {
        isUpdating = true
        try {
            inputText.setText(inputValue.toString())
            inputSlider.value = inputValue.toFloat()
        } finally {
            isUpdating = false
        }
    }

    fun setChamberIndex(idx: Int): Unit {
        titleView.text = "Chamber No.$idx"
    }
}
