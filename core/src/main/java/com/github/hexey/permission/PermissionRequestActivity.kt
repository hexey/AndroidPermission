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

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.annotation.RequiresApi
import android.util.SparseArray

/**
 * AndroidPermission
 * Created by Hexey on 2018/9/18.
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class PermissionRequestActivity : NonConfigurationActivity<PermissionRequestActivity.Request>() {

    internal class Request(val permissions: Array<out String>, val callback: Callback)

    companion object {
        private const val KEY_ID = "ID"
        private var mID = 0
        private val requests = SparseArray<Request>()

        @RequiresApi(Build.VERSION_CODES.M)
        fun startRequest(
            context: Context,
            permissions: Array<out String>,
            @MainThread callback: Callback
        ) {
            val id = mID++
            requests.put(id, Request(permissions, callback))
            val intent = Intent(context, PermissionRequestActivity::class.java)
            intent.putExtra(KEY_ID, id)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun onCreateNonConfigurationInstance(): Request? {
        val id = intent.getIntExtra(KEY_ID, -1)
        val request = requests[id] ?: return null
        requests.remove(id)
        return request
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val request = nonConfigurationInstance
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || request == null) {
            finish()
            return
        }
        if (savedInstanceState == null) {
            requestPermissions(request.permissions, 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val request = nonConfigurationInstance
        if (request == null) {
            finish()
            return
        }

        request.callback(APermission.Result(permissions, grantResults))

        finish()
    }


    override fun onBackPressed() {}

}
