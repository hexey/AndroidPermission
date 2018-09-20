# Android permission request library

[![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=true)](http://www.apache.org/licenses/LICENSE-2.0)
![minSdkVersion 16](https://img.shields.io/badge/minSdkVersion-16-red.svg?style=true)

:white_check_mark: Support running outside of Activity

:white_check_mark: No fragment state loss problem

:white_check_mark: Simple as it should be

Download
---------------

```groovy
repositories {
    maven { url "https://dl.bintray.com/hexey/maven" }
}

dependencies {
    implementation "com.github.hexey.permission:core:0.1.0"
}
```

Usage
---------------
```kotlin
APermission(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE).request(this) { result ->
    if (result.isAllGranted) {
        // todo
    }
}
```



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
