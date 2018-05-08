# Overview

Razer ID login for Apps is a fast and convenient way to streamline your Apps to have more signed-in users across various platforms. It is available for the iOS, Android, Web and the Razer Phone.

## Use Cases
Razer ID Login is used in the provision of the following experiences:
## User Sign Up
Razer ID provides a quick and convenient way to allow user to go onboard your Apps without the need to set a password. User can log into your App with a single click on the supported platforms. With this API, you will also be able to reach the user through their verified email or phone.
## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.


### Installing

1. Add gradle dependencies

build.gradle
```
repositories {
    maven {
        url  "https://dl.bintray.com/josephdeguzman/com.razer.android"
    }
}
 
dependencies {
   ...
   implementation 'com.razer.android:auth-sdk:1.0.64-dev'
   ...
}
```



2. Create a Razer app in the Razer Developer Portal

## Running the sample App
1. Clone the sample project.
2. Replace the RAZER_CLIENT_ID & RAZER_Client_SECRET in Constants.java file with the ID provided by Razer Developer Portal.
3. Replace RAZER_CLIENT_REDIRECT_URL with your redirect URL.
Constants.java
```
package com.razer.sample;

public class Constants {
    public static final String RAZER_CLIENT_ID = "openidstaging";//please visit Razer's Developer portal for your id
    public static final String RAZER_CLIENT_SECRET = "openidpasswdstaging";

    public static final String RAZER_CLIENT_REDIRECT_URL = "http://54.205.51.49/callback";//please visit Razer's Developer portal
    public static final String SERIALIZED_TOKEN_FILE_NAME = "razertoken";
}
```
