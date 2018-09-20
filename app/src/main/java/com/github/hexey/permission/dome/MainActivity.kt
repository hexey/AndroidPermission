package com.github.hexey.permission.dome

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, DemoFragment())
                .commit()
        }
    }
}

class DemoFragment : Fragment() {

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.onClick(apr, this) {
            assert(lifecycle.currentState == Lifecycle.State.RESUMED)
            Log.i("Permission", "Granted")
        }
    }

}