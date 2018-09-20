# Android permission request library

[![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=true)](http://www.apache.org/licenses/LICENSE-2.0)
![minSdkVersion 16](https://img.shields.io/badge/minSdkVersion-16-red.svg?style=true)

:white_check_mark: Support request permission outside of Activity

:white_check_mark: Simple as it should be

Download
---------------

```groovy
repositories {
    maven { url "https://dl.bintray.com/hexey/maven" }
}

dependencies {
    implementation "com.github.hexey.permission:core:0.2.1"
}
```

Usage
---------------
```kotlin
val storagePermission = APermission(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

storagePermission.request(context) { result ->
    if (result.isAllGranted) {
        // todo
    } else {
        result.forEach { permission: Permission ->
            if (!permission.isGranted) {
                Log.w("Permission", "Permission ${permission.name} is denied")
            }
        }
    }
}


storagePermission.onGranted(context) {
    // todo
}.onDenied(context) {
    // todo
}


view.onClick(storagePermission, lifecycle) {
    assert(lifecycle.currentState == Lifecycle.State.RESUMED)
    // todo
}
```
```kotlin
class DemoFragment : Fragment() {

    init {
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.onClick(storagePermission, this) {
            Log.i("Permission", "Granted")
            assert(lifecycle.currentState == Lifecycle.State.RESUMED)
            assert(!isStateSaved)
            fragmentManager.beginTransaction()
                .replace(android.R.id.content, SomeFragment())
                .commit()
        }
    }

}
```

Careful! state may change before calling the callback
---------------
```kotlin
class DoNotDoThisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            storagePermission.onGranted(this) {
                // Don’t do this
                // Activity may already destroyed now
                supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, DemoFragment())
                    .commit()
            }
        }
    }
}
```
This library can't help you with similar case, you may want try other library ( for example: [**PermissionsDispatcher**](https://github.com/permissions-dispatcher/PermissionsDispatcher)) or just do this by original way


License
=======

    Copyright 2018 Hexey

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
