package com.github.hexey.permission.rx

import android.Manifest
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import com.github.hexey.permission.APermission
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CountDownLatch

/**
 * AndroidPermission
 * Created by Hexey on 2018/9/19.
 */
class APermissionExtensionsKtTest {

    private val context = InstrumentationRegistry.getTargetContext()

    private fun revokePermission(permission: String) {
        val context = InstrumentationRegistry.getTargetContext()
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("pm revoke ${context.packageName} $permission")
    }

    private fun <R> onMainThread(callable: () -> R): R = Espresso.onIdle(callable)


    @Test
    fun test() {
        val latch = CountDownLatch(1)
        onMainThread {
            APermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CALENDAR
            )
                .request(context)
                .filter { it.isAllGranted }
                .observeOn(Schedulers.io())
                .subscribe { result ->
                    assertTrue(result.isAllGranted)
                    latch.countDown()
                }
        }

        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        PermissionGranter.allowPermissionsIfNeeded(Manifest.permission.READ_CALENDAR)

        latch.await()

    }
}
