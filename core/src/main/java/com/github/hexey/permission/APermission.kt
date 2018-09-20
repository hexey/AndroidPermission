/*
 *  Copyright 2018 Hexey.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hexey.permission

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Looper
import android.support.annotation.MainThread
import java.util.NoSuchElementException
import kotlin.collections.ArrayList
import kotlin.collections.Iterable
import kotlin.collections.Iterator
import kotlin.collections.all
import kotlin.collections.forEach
import kotlin.collections.isEmpty

typealias Callback = (result: APermission.Result) -> Unit

/**
 * AndroidPermission
 * Created by Hexey on 2018/9/18.
 */
private val LOOPER = Looper.getMainLooper()

class APermission(private vararg val permissions: String) {

    init {
        if (permissions.isEmpty()) {
            throw IllegalArgumentException("requires least one permission")
        }
    }

    // track is all permission granted.
    // user revoke permission in settings will cause the process to terminate, so this will not be a problem
    private var allGranted = false
    private var callbacks = ArrayList<Callback>()

    @SuppressLint("NewApi") // lint bug
    @MainThread
    fun request(context: Context, @MainThread callback: Callback) {
        if (Looper.myLooper() != LOOPER) {
            throw IllegalThreadStateException()
        }

        if (allGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            callback(Result(permissions, null))
            return
        }
        allGranted = permissions.all {
            context.checkSelfPermission(it) == PERMISSION_GRANTED
        }
        if (allGranted) {
            callback(Result(permissions, null))
            return
        }

        callbacks.add(callback)
        if (callbacks.size == 1) {
            PermissionRequestActivity.startRequest(context, permissions) { result ->
                allGranted = result.isAllGranted
                callbacks.forEach { it(result) }
                callbacks.clear()
            }
        }
    }

    class Result(
        private val permissions: Array<out String>,
        private val grantResults: IntArray? // null mean all granted
    ) : Iterable<Permission> {

        val isAllGranted
            get() = grantResults?.all { it == PERMISSION_GRANTED } ?: true

        override fun iterator(): Iterator<Permission> {
            return object : Iterator<Permission> {
                private var cursor = 0

                override fun hasNext() = cursor < permissions.size

                override fun next(): Permission {
                    if (cursor >= permissions.size) throw NoSuchElementException()
                    val i = cursor++
                    return Permission(
                        permissions[i],
                        if (grantResults == null) true else grantResults[i] == PERMISSION_GRANTED
                    )
                }
            }
        }

    }

}