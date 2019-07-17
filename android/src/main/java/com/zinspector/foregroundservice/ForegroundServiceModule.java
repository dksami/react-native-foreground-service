package com.zinspector.foregroundservice;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import static com.zinspector.foregroundservice.Constants.ERROR_INVALID_CONFIG;
import static com.zinspector.foregroundservice.Constants.ERROR_SERVICE_ERROR;
import static com.zinspector.foregroundservice.Constants.NOTIFICATION_CONFIG;
import static com.zinspector.foregroundservice.Constants.TASK_CONFIG;


public class ForegroundServiceModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public ForegroundServiceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "ForegroundService";
    }


    @ReactMethod
    public void startService(ReadableMap notificationConfig, Promise promise) {
        if (notificationConfig == null) {
            promise.reject(ERROR_INVALID_CONFIG, "ForegroundService: Notification config is invalid");
            return;
        }

        if (!notificationConfig.hasKey("id")) {
            promise.reject(ERROR_INVALID_CONFIG , "ForegroundService: id is required");
            return;
        }

        if (!notificationConfig.hasKey("title")) {
            promise.reject(ERROR_INVALID_CONFIG, "ForegroundService: title is reqired");
            return;
        }

        if (!notificationConfig.hasKey("message")) {
            promise.reject(ERROR_INVALID_CONFIG, "ForegroundService: message is required");
            return;
        }

        Intent intent = new Intent(getReactApplicationContext(), ForegroundService.class);
        intent.setAction(Constants.ACTION_FOREGROUND_SERVICE_START);
        intent.putExtra(NOTIFICATION_CONFIG, Arguments.toBundle(notificationConfig));
        ComponentName componentName = getReactApplicationContext().startService(intent);

        if (componentName != null) {
            promise.resolve(null);
        } else {
            promise.reject(ERROR_SERVICE_ERROR, "ForegroundService: Foreground service is not started");
        }
    }

    @ReactMethod
    public void updateNotification(ReadableMap notificationConfig, Promise promise) {
        if (notificationConfig == null) {
            promise.reject(ERROR_INVALID_CONFIG, "ForegroundService: Notification config is invalid");
            return;
        }

        if (!notificationConfig.hasKey("id")) {
            promise.reject(ERROR_INVALID_CONFIG , "ForegroundService: id is required");
            return;
        }

        if (!notificationConfig.hasKey("title")) {
            promise.reject(ERROR_INVALID_CONFIG, "ForegroundService: title is reqired");
            return;
        }

        if (!notificationConfig.hasKey("message")) {
            promise.reject(ERROR_INVALID_CONFIG, "ForegroundService: message is required");
            return;
        }

        Intent intent = new Intent(getReactApplicationContext(), ForegroundService.class);
        intent.setAction(Constants.ACTION_UPDATE_NOTIFICATION);
        intent.putExtra(NOTIFICATION_CONFIG, Arguments.toBundle(notificationConfig));
        ComponentName componentName = getReactApplicationContext().startService(intent);

        if (componentName != null) {
            promise.resolve(null);
        } else {
            promise.reject(ERROR_SERVICE_ERROR, "ForegroundService: Update notification failed");
        }
    }

    @ReactMethod
    public void stopService(Promise promise) {

        // stop main service
        Intent intent = new Intent(getReactApplicationContext(), ForegroundService.class);
        intent.setAction(Constants.ACTION_FOREGROUND_SERVICE_STOP);

        //getReactApplicationContext().stopService(intent);

        // Looks odd, but we do indeed send the stop flag with a start command
        // if it fails, use the violent stop service instead
        try{
            getReactApplicationContext().startService(intent);
        }
        catch(IllegalStateException e){
            getReactApplicationContext().stopService(intent);
        }

        // Also stop headless tasks, should be noop if it's not running.
        // TODO: Not working, headless task must finish regardless. We have to rely on JS code being well done.
        // intent = new Intent(getReactApplicationContext(), ForegroundServiceTask.class);
        // getReactApplicationContext().stopService(intent);

        promise.resolve(null);
    }

    @ReactMethod
    public void stopServiceAll(Promise promise) {

        // stop main service with all action
        Intent intent = new Intent(getReactApplicationContext(), ForegroundService.class);
        intent.setAction(Constants.ACTION_FOREGROUND_SERVICE_STOP_ALL);

        try{
            getReactApplicationContext().startService(intent);
        }
        catch(IllegalStateException e){
            getReactApplicationContext().stopService(intent);
        }

        promise.resolve(null);
    }

    @ReactMethod
    public void runTask(ReadableMap taskConfig, Promise promise) {

        if (!taskConfig.hasKey("taskName")) {
            promise.reject(ERROR_INVALID_CONFIG, "ForegroundService: taskName is required");
            return;
        }

        if (!taskConfig.hasKey("delay")) {
            promise.reject(ERROR_INVALID_CONFIG, "ForegroundService: delay is required");
            return;
        }

        // TODO: Check if service is running and return error if not.

        Intent intent = new Intent(getReactApplicationContext(), ForegroundService.class);
        intent.setAction(Constants.ACTION_FOREGROUND_RUN_TASK);
        intent.putExtra(TASK_CONFIG, Arguments.toBundle(taskConfig));

        ComponentName componentName = getReactApplicationContext().startService(intent);

        if (componentName != null) {
            promise.resolve(null);
        } else {
            promise.reject(ERROR_SERVICE_ERROR, "ForegroundService: Failed to run task.");
        }
    }

}