package io.github.usharerose.flexpal.gamepad.android

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var coordinatesText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        coordinatesText = findViewById(R.id.coordinates_text)
        val joystickView = findViewById<JoystickView>(R.id.joystick_view)

        joystickView.setOnCoordinatesChangedListener { x, y ->
            coordinatesText.text = getString(R.string.coordinates_text, x.toInt(), y.toInt())
        }
    }
}
