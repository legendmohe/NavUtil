package com.legendmohe.navutil.model;

import com.legendmohe.navutil.LifecycleEvent;

import java.lang.ref.WeakReference;

import rx.subjects.PublishSubject;

/**
 * Created by legendmohe on 2017/1/23.
 */

public class StateItem<T> {
    public WeakReference<T> item;
    public int state;
    public PublishSubject<LifecycleEvent> mSubject;

    public StateItem(T item, int state) {
        this.item = new WeakReference<>(item);
        this.state = state;
    }

    public StateItem(T item, int state, PublishSubject<LifecycleEvent> subject) {
        this.item = new WeakReference<>(item);
        this.state = state;
        mSubject = subject;
    }
}
