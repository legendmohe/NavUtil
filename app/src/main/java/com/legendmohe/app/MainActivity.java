package com.legendmohe.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.legendmohe.navutil.LifecycleEvent;
import com.legendmohe.navutil.NavUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable.interval(1, TimeUnit.SECONDS)
                .compose(NavUtil.<Long>emitUtilEvent(this, LifecycleEvent.ON_DESTROY))
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted() called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: e = [" + e + "]");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "onNext() called with: aLong = [" + aLong + "]");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Observable.interval(1, TimeUnit.SECONDS)
                .compose(NavUtil.<Long>emitUtilEvent(this, LifecycleEvent.ON_DESTROY))
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onStart onCompleted() called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onStart onError() called with: e = [" + e + "]");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "onStart onNext() called with: aLong = [" + aLong + "]");
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
