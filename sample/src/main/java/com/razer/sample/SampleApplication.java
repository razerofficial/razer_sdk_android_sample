package com.razer.sample;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;



public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this); //needed by fresco. if you are using your own image loader. you can skip this part.
    }
}

