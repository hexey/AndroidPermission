package com.github.hexey.permission.dome

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.github.hexey.permission.APermission
import com.github.hexey.permission.onClick
import android.Manifest.permission as PERMISSION

val apr = APermission(
    PERMISSION.READ_EXTERNAL_STORAGE,
    PERMISSION.WRITE_EXTERNAL_STORAGE,
    PERMISSION.READ_CALENDAR
)

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).onClick(apr, this) {
            assert(lifecycle.currentState == Lifecycle.State.RESUMED)
            findViewById<TextView>(R.id.text).text = "Granted"
        }
    }
}
