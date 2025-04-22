package io.github.usharerose.flexpal.gamepad.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private var chamberPressureInputFragment: ChamberPressureInputFragment? = null
    private var udpConfigFragment: UdpConfigFragment? = null
    private var activeFragment: Fragment? = null
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            chamberPressureInputFragment = ChamberPressureInputFragment()
            udpConfigFragment = UdpConfigFragment()

            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragmentContainer, udpConfigFragment!!, "settings").hide(udpConfigFragment!!)
                add(R.id.fragmentContainer, chamberPressureInputFragment!!, "chamber")
                commit()
            }
            activeFragment = chamberPressureInputFragment
        }

        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_chamber -> {
                    fragmentManager.beginTransaction()
                        .hide(activeFragment!!)
                        .show(chamberPressureInputFragment!!)
                        .commit()
                    activeFragment = chamberPressureInputFragment
                    true
                }
                R.id.navigation_settings -> {
                    fragmentManager.beginTransaction()
                        .hide(activeFragment!!)
                        .show(udpConfigFragment!!)
                        .commit()
                    activeFragment = udpConfigFragment
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}