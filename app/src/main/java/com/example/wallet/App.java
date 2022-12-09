package com.example.wallet;

import android.app.Application;

import com.yandex.mapkit.MapKitFactory;

public class App extends Application {

    public static UserData userData;

    @Override
    public void onCreate() {
        super.onCreate();

        userData = new UserData();
        MapKitFactory.setApiKey("f3d8a01d-d843-4d79-895a-f165fc3fc10c");
    }
}
