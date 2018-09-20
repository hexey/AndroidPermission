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

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.support.annotation.MainThread
import android.view.View

/**
 * AndroidPermission
 * Created by Hexey on 2018/9/20.
 */
fun View.onClick(
    permissionRequest: APermission,
    lifecycle: LifecycleOwner? = null,
    onClick: () -> Unit
) {
    setOnClickListener {
        if (this.isRequesting) return@setOnClickListener
        if (lifecycle != null && !lifecycle.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            return@setOnClickListener
        }

        this.isRequesting = true

        permissionRequest.onGranted(context) {
            if (lifecycle != null) {
                lifecycle.lifecycle.doWhenResumed {
                    onClick()
                    this.isRequesting = false
                }
            } else {
                onClick()
                this.isRequesting = false
            }
        }
    }
}

private inline var View.isRequesting: Boolean
    get() = this.getTag(R.id.isRequestingPermission) as? Boolean ?: false
    set(value) = this.setTag(R.id.isRequestingPermission, value)

@MainThread
private inline fun Lifecycle.doWhenResumed(crossinline action: () -> Unit) {
    if (currentState == Lifecycle.State.DESTROYED) return

    if (currentState.isAtLeast(Lifecycle.State.RESUMED)) {
        action()
        return
    }

    addObserver(object : GenericLifecycleObserver {
        override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_RESUME) {
                action()
                removeObserver(this)
            }
        }
    })
}