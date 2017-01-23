package com.legendmohe.navutil.model;

import java.lang.ref.WeakReference;

/**
 * Created by legendmohe on 2017/1/23.
 */

public class StateItem<T> {
    public WeakReference<T> item;
    public int state;

    public StateItem(T item, int state) {
        this.item = new WeakReference<>(item);
        this.state = state;
    }
}
