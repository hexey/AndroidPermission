package com.github.hexey.permission

import android.app.Activity
import android.os.Bundle

/**
 * AndroidPermission
 * Created by Hexey on 2018/9/19.
 */
abstract class NonConfigurationActivity<T> : Activity() {

    protected var nonConfigurationInstance: T? = null; private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lastNonConfiguration = lastNonConfigurationInstance
        if (lastNonConfiguration != null) {
            @Suppress("UNCHECKED_CAST")
            nonConfigurationInstance = lastNonConfigurationInstance as T
        } else {
            nonConfigurationInstance = onCreateNonConfigurationInstance()
        }
    }

    abstract fun onCreateNonConfigurationInstance(): T?

    final override fun onRetainNonConfigurationInstance(): Any? = nonConfigurationInstance

}