package com.legendmohe.navutil;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.legendmohe.navutil.model.LifecycleEvent;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by legendmohe on 2017/1/23.
 */

public class TransformerFactory {

    public static <R> Observable.Transformer<R, R> subscribeUtilEventTransformer(final Activity target, LifecycleEvent event) {
        final Application app = target.getApplication();
        final PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
        final Application.ActivityLifecycleCallbacks callbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity == target)
                    publishSubject.onNext(LifecycleEvent.ON_PAUSED);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (activity == target)
                    publishSubject.onNext(LifecycleEvent.ON_STOPPED);
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                if (activity == target)
                    publishSubject.onNext(LifecycleEvent.ON_SAVE_INSTANCE_STATE);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity == target)
                    publishSubject.onNext(LifecycleEvent.ON_DESTROYED);
            }
        };

        app.registerActivityLifecycleCallbacks(callbacks);
        return subscribeUtilEvent(publishSubject, event, new Action0() {
            @Override
            public void call() {
                app.unregisterActivityLifecycleCallbacks(callbacks);
            }
        });
    }

    public static <R> Observable.Transformer<R, R> subscribeUtilEventTransformer(final Fragment target, LifecycleEvent event) {
        final FragmentManager manager = target.getFragmentManager();
        if (manager == null) {
            throw new NullPointerException("fragment manager is null!");
        }

        final PublishSubject<LifecycleEvent> publishSubject = PublishSubject.create();
        final FragmentManager.FragmentLifecycleCallbacks callbacks = manager.new FragmentLifecycleCallbacks() {

            @Override
            public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
            }

            @Override
            public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            }

            @Override
            public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            }

            @Override
            public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            }

            @Override
            public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            }

            @Override
            public void onFragmentStarted(FragmentManager fm, Fragment f) {
            }

            @Override
            public void onFragmentResumed(FragmentManager fm, Fragment f) {
            }

            @Override
            public void onFragmentPaused(FragmentManager fm, Fragment f) {
                if (f == target)
                    publishSubject.onNext(LifecycleEvent.ON_PAUSED);
            }

            @Override
            public void onFragmentStopped(FragmentManager fm, Fragment f) {
                if (f == target)
                    publishSubject.onNext(LifecycleEvent.ON_STOPPED);
            }

            @Override
            public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
                if (f == target)
                    publishSubject.onNext(LifecycleEvent.ON_SAVE_INSTANCE_STATE);
            }

            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                if (f == target)
                    publishSubject.onNext(LifecycleEvent.ON_VIEW_DESTORYED);
            }

            @Override
            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                if (f == target)
                    publishSubject.onNext(LifecycleEvent.ON_DESTROYED);
            }

            @Override
            public void onFragmentDetached(FragmentManager fm, Fragment f) {
                if (f == target)
                    publishSubject.onNext(LifecycleEvent.ON_DESTROYED);
            }
        };
        manager.registerFragmentLifecycleCallbacks(callbacks, true);

        return subscribeUtilEvent(publishSubject, event, new Action0() {
            @Override
            public void call() {
                manager.unregisterFragmentLifecycleCallbacks(callbacks);
            }
        });
    }

    private static <R, T> Observable.Transformer<R, R> subscribeUtilEvent(final Observable<T> source, final T event, final Action0 doOnComplete) {
        return new Observable.Transformer<R, R>() {
            @Override
            public Observable<R> call(Observable<R> rObservable) {
                return rObservable.takeUntil(takeUntilEvent(source, event)).doOnCompleted(doOnComplete);
            }
        };
    }

    private static <T> Observable<T> takeUntilEvent(final Observable<T> src, final T event) {
        return src.takeFirst(new Func1<T, Boolean>() {
            @Override
            public Boolean call(T lifecycleEvent) {
                return lifecycleEvent.equals(event);
            }
        });
    }
}
