package io.github.usharerose.flexpal.gamepad.android

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var slidersContainer: LinearLayout
    private val editTextList = mutableListOf<EditText>()
    private lateinit var valuesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slidersContainer = findViewById<LinearLayout>(R.id.slidersContainer)
        val numberOfSliders = 9
        for (i in 1..numberOfSliders) {
            addSliderWithEditText()
        }

        valuesTextView = findViewById<TextView>(R.id.valuesTextView)
        updateValuesTextView()
    }

    private fun addSliderWithEditText() {
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.chamber_input, slidersContainer, false)

        // 获取 SeekBar 和 EditText
        val seekBar: SeekBar = layout.findViewById(R.id.seekBar)
        seekBar.progress = 50
        val editText: EditText = layout.findViewById(R.id.editText)
        val initialValue = seekBar.progress - 50
        editText.setText(initialValue.toString())
        editTextList.add(editText)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = progress - 50
                editText.setText(value.toString())
                updateValuesTextView()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputValue = s.toString().toIntOrNull()
                if (inputValue != null) {
                    if (inputValue in -50..50) {
                        val progress = inputValue + 50
                        seekBar.progress = progress
                    } else {
                        Toast.makeText(this@MainActivity, "The value range is between -50 to 50", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    seekBar.progress = 50
                }
                updateValuesTextView()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        slidersContainer.addView(layout)
    }

    private fun updateValuesTextView() {
        val values = editTextList.map { it.text.toString() }
        valuesTextView.text = "Values: ${values.joinToString(", ")}"
    }
}
