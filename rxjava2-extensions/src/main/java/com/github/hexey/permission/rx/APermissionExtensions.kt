package com.github.hexey.permission.rx

import android.content.Context
import com.github.hexey.permission.APermission
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * AndroidPermission
 * Created by Hexey on 2018/9/18.
 */
fun APermission.request(context: Context): Single<APermission.Result> {
    val appContext = context.applicationContext
    return Single.create<APermission.Result> { emitter ->
        request(appContext) {
            emitter.onSuccess(it)
        }
    }.subscribeOn(AndroidSchedulers.mainThread())
}
