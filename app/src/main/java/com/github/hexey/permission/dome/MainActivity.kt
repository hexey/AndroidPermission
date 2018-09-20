package com.github.hexey.permission.dome

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.github.hexey.permission.APermission
import android.Manifest.permission as PERMISSION

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener {
            APermission(
                PERMISSION.READ_EXTERNAL_STORAGE,
                PERMISSION.WRITE_EXTERNAL_STORAGE,
                PERMISSION.READ_CALENDAR
            ).request(this) { result ->
                if (result.isAllGranted) {

                }

                val str = StringBuilder()
                str.append("allGranted: ${result.isAllGranted}")
                result.forEach { permission ->
                    str.append("\nname: ${permission.name} ;isGranted: ${permission.isGranted}")
                }
                findViewById<TextView>(R.id.text).text = str
            }
        }
    }
}
