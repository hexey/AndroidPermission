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

import android.Manifest.permission.*
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.AssertionError
import java.util.concurrent.CountDownLatch


/**
 * AndroidPermission
 * Created by Hexey on 2018/9/18.
 */
// fixme Test running failed: Instrumentation run failed due to 'Process crashed.'
class APermissionTest {

    private val context = InstrumentationRegistry.getTargetContext()

    @Before
    fun setUp() {
        revokePermission(READ_EXTERNAL_STORAGE)
        revokePermission(WRITE_EXTERNAL_STORAGE)
        revokePermission(READ_CALENDAR)
        Thread.sleep(400)
    }

    @Test
    fun allGranted() {
        val latch = CountDownLatch(3)

        val permission = APermission(
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE,
            READ_CALENDAR
        )

        fun checkResult(result: APermission.Result) {
            assertTrue(result.isAllGranted)
            assertEquals(3, result.count())
            assertTrue(result.all { it.isGranted })
        }

        onMainThread {
            permission.request(context) { result ->
                checkResult(result)
                latch.countDown()
            }
            permission.request(context) { result ->
                checkResult(result)
                latch.countDown()
            }
            permission.request(context) { result ->
                checkResult(result)
                latch.countDown()
            }
        }

        PermissionGranter.allowPermissionsIfNeeded(WRITE_EXTERNAL_STORAGE)
        PermissionGranter.allowPermissionsIfNeeded(READ_CALENDAR)

        latch.await()

        onMainThread {
            var r = false
            permission.request(context) { result ->
                checkResult(result)
                r = true
            }
            assertTrue(r)
        }
    }


    @Test
    fun allDenied() {
        val latch = CountDownLatch(1)
        onMainThread {
            APermission(
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE,
                READ_CALENDAR
            ).request(context) { result ->
                assertFalse(result.isAllGranted)
                assertTrue(result.all { !it.isGranted })
                assertEquals(3, result.count())
                latch.countDown()
            }
        }

        PermissionGranter.deniedPermissions(WRITE_EXTERNAL_STORAGE)
        PermissionGranter.deniedPermissions(READ_CALENDAR)

        latch.await()
    }

    @Test
    fun halfGranted() {
        val latch = CountDownLatch(1)
        onMainThread {
            APermission(
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE,
                READ_CALENDAR
            ).request(context) { result ->
                assertFalse(result.isAllGranted)
                assertEquals(3, result.count())

                result.forEachIndexed { index, permission ->
                    when (index) {
                        0    -> assertEquals(READ_EXTERNAL_STORAGE, permission.name)
                        1    -> assertEquals(WRITE_EXTERNAL_STORAGE, permission.name)
                        2    -> assertEquals(READ_CALENDAR, permission.name)
                        else -> throw AssertionError()
                    }
                }
                result.forEachIndexed { index, permission ->
                    when (index) {
                        0    -> assertTrue(permission.isGranted)
                        1    -> assertTrue(permission.isGranted)
                        2    -> assertFalse(permission.isGranted)
                        else -> throw AssertionError()
                    }
                }

                latch.countDown()
            }
        }

        PermissionGranter.allowPermissionsIfNeeded(WRITE_EXTERNAL_STORAGE)
        PermissionGranter.deniedPermissions(READ_CALENDAR)

        latch.await()
    }

    private fun revokePermission(permission: String) {
        val context = InstrumentationRegistry.getTargetContext()
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("pm revoke ${context.packageName} $permission")
    }

    private fun <R> onMainThread(callable: () -> R): R = Espresso.onIdle(callable)

}