# Register Android SDK

The Register Android SDK lets you quickly build custom Android point-of-sale applications that take swipe, dip, or tap payments through Square hardware.

[Get started](http://docs.connect.squareup.com/articles/register-api-android/)!

## Need help?

Here's [how to get help](https://docs.connect.squareup.com/articles/faq/#implementationquestions).

## Building the Sample App

A sample app is available in the `sample-hellocharge` folder.

Check out this repo:

```
git clone https://github.com/square/register-android-sdk.git
cd register-android-sdk
```

Create a `hellocharge.properties` file in the `sample-hellocharge` folder, with a `clientId` key set to your application's client id which is the same as your application id in the square developer portal (`sq0idp-XXXXXXXXXXXXXXX`)

```
echo clientId=\"YourClientId\" > sample-hellocharge/hellocharge.properties
```

Build the SDK and the sample app:

```
./gradlew build
```

Add the [SHA1 fingerprint](https://docs.connect.squareup.com/articles/android-app-fingerprint/) of the sample app to your application dashboard:

```
keytool -list -printcert -jarfile ./sample-hellocharge/build/outputs/apk/sample-hellocharge-debug.apk
```

Install the sample app:

```
./gradlew :sample-hellocharge:installDebug
```

![hellocharge_demo.gif](sample-hellocharge/assets/hellocharge_demo.gif)

## Contributing

Contributions are welcome! Please read our [contributing guidelines](.github/CONTRIBUTING.md).

## License

    Copyright 2016 Square, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
