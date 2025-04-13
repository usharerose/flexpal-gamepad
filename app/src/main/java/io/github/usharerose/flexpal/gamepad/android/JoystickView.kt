package io.github.usharerose.flexpal.gamepad.android

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class JoystickView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val basePaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.FILL
    }

    private val stickPaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    private var stickX = 0f
    private var stickY = 0f
    private var isDragging = false

    private var initialX = 0f
    private var initialY = 0f

    private var onCoordinatesChanged: ((Float, Float) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(width / 2f, height / 2f, width / 2f, basePaint)
        canvas.drawCircle(width / 2f + stickX, height / 2f + stickY, 50f, stickPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = event.x
                initialY = event.y
                performClick()
            }
            MotionEvent.ACTION_MOVE -> {
                isDragging = true
                val dx = event.x - initialX
                val dy = event.y - initialY
                val distance = sqrt(dx * dx + dy * dy)
    
                if (distance < width / 2) {
                    stickX = dx
                    stickY = dy
                } else {
                    val angle = atan2(dy, dx)
                    stickX = (width / 2 * cos(angle.toDouble())).toFloat()
                    stickY = (height / 2 * sin(angle.toDouble())).toFloat()
                }
                onCoordinatesChanged?.invoke(stickX, stickY)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                isDragging = false
                stickX = 0f
                stickY = 0f
                onCoordinatesChanged?.invoke(stickX, stickY)
                invalidate()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setOnCoordinatesChangedListener(listener: ((Float, Float) -> Unit)?) {
        onCoordinatesChanged = listener
    }
}
