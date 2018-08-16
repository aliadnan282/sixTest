package com.workout.sixpacksabs.helper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.workout.sixpacksabs.view.activity.DrawerActivity;

/**
 * Created by AdnanAli on 3/21/2018.
 */

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final static String TAG = MyExceptionHandler.class.getSimpleName();

    private final Context context;
    private final Thread.UncaughtExceptionHandler rootHandler;


    public MyExceptionHandler(Context context) {
        this.context = context;
        // we should store the current exception handler -- to invoke it for all not handled exceptions ...
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        // we replace the exception handler now with us -- we will properly dispatch the exceptions ...
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //  if (e instanceof MyAuthException) {
        // note we can't just open in Android an dialog etc. we have to use Intents here
        // http://stackoverflow.com/questions/13416879/show-a-dialog-in-thread-setdefaultuncaughtexceptionhandler


        Log.e(TAG, "uncaughtException: " + e.getMessage()+"\n\n"+e.getStackTrace()[0].toString());
        Crashlytics.logException(e);
        Intent registerActivity = new Intent(context, DrawerActivity.class);
        // registerActivity.putExtra(EXTRA_MY_EXCEPTION_HANDLER, MyExceptionHandler.class.getName());
        registerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        registerActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.startActivity(registerActivity);
        // make sure we die, otherwise the app will hang ...
        android.os.Process.killProcess(android.os.Process.myPid());
        // sometimes on older android version killProcess wasn't enough -- strategy pattern should be considered here
        System.exit(0);
    }
}
