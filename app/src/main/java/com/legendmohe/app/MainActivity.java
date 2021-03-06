package com.legendmohe.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.legendmohe.navutil.NavUtil;
import com.legendmohe.navutil.model.LifecycleEvent;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.fragment_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtil.startActivity(MainActivity.this, NavFragmentActivity.class);
            }
        });

//        mSubscription = TransformerFactory.createLifecycle(this)
//                .take(1)
//                .subscribe(new Subscriber<LifecycleEvent>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d(TAG, "createLifecycle onCompleted() called");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d(TAG, "createLifecycle onError() called with: e = [" + e + "]");
//                    }
//
//                    @Override
//                    public void onNext(LifecycleEvent lifecycleEvent) {
//                        Log.d(TAG, "createLifecycle onNext() called with: lifecycleEvent = [" + lifecycleEvent + "]");
//                    }
//                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Observable.interval(1, TimeUnit.SECONDS)
                .compose(NavUtil.<Long>subscribeUtilEvent(this, LifecycleEvent.ON_STOPPED))
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "1onStart onCompleted() called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "1onStart onError() called with: e = [" + e + "]");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "1onStart onNext() called with: aLong = [" + aLong + "]");
                    }
                });

        Observable.interval(3, TimeUnit.SECONDS)
                .compose(NavUtil.<Long>subscribeUtilEvent(this, LifecycleEvent.ON_STOPPED))
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "2onStart onCompleted() called");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "2onStart onError() called with: e = [" + e + "]");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.d(TAG, "2onStart onNext() called with: aLong = [" + aLong + "]");
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
