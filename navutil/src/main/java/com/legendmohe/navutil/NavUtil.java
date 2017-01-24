package com.legendmohe.navutil;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.legendmohe.navutil.model.LifecycleEvent;
import com.legendmohe.navutil.model.StateItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by legendmohe on 2016/12/26.
 */

public class NavUtil {

    public static boolean DEBUG = false;

    private static final String TAG = "NavUtil";

    public static final int ACTIVITY_STATE_CREATED = 0;
    public static final int ACTIVITY_STATE_STARTED = 1;
    public static final int ACTIVITY_STATE_RESUMED = 2;
    public static final int ACTIVITY_STATE_PAUSED = 3;
    public static final int ACTIVITY_STATE_STOPPED = 4;
    public static final int ACTIVITY_STATE_SAVE_INSTANCE_STATE = 5;
    public static final int ACTIVITY_STATE_DESTORY = 6;

    private WeakReference<Application> mApplication;

    private List<StateItem<Activity>> mActivityItems;

    //////////////////////////////////////////////////////////////////////

    private static class LazyHolder {
        private static final NavUtil INSTANCE = new NavUtil();
    }

    public static NavUtil getInstance() {
        return LazyHolder.INSTANCE;
    }

    private NavUtil() {
        mActivityItems = new ArrayList<>();
    }

    /////////////////////////////////////Activity/////////////////////////////////

    public static void init(Application application) {
        LazyHolder.INSTANCE.mApplication = new WeakReference<>(application);
        LazyHolder.INSTANCE.mApplication.get().registerActivityLifecycleCallbacks(LazyHolder.INSTANCE.mLifecycleCallbacks);
    }

    private Application.ActivityLifecycleCallbacks mLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (DEBUG) {
                Log.d(TAG, "activity created: " + activity);
            }
            mActivityItems.add(new StateItem<>(activity, ACTIVITY_STATE_CREATED));
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (DEBUG) {
                Log.d(TAG, "activity started: " + activity);
            }
            for (StateItem<Activity> item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.item.get().equals(activity)) {
                    item.state = ACTIVITY_STATE_STARTED;
                    break;
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (DEBUG) {
                Log.d(TAG, "activity resumed: " + activity);
            }
            for (StateItem<Activity> item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.item.get().equals(activity)) {
                    item.state = ACTIVITY_STATE_RESUMED;
                    break;
                }
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (DEBUG) {
                Log.d(TAG, "activity paused: " + activity);
            }
            for (StateItem<Activity> item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.item.get().equals(activity)) {
                    item.state = ACTIVITY_STATE_PAUSED;
                    break;
                }
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (DEBUG) {
                Log.d(TAG, "activity stopped: " + activity);
            }
            for (StateItem<Activity> item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.item.get().equals(activity)) {
                    item.state = ACTIVITY_STATE_STOPPED;
                    break;
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            if (DEBUG) {
                Log.d(TAG, "activity saveInstanceState: " + activity);
            }
            for (StateItem<Activity> item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.item.get().equals(activity)) {
                    item.state = ACTIVITY_STATE_SAVE_INSTANCE_STATE;
                    break;
                }
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (DEBUG) {
                Log.d(TAG, "activity destroyed: " + activity);
            }
            // activity 会被删掉，还需要这段代码吗？
            for (StateItem<Activity> item :
                    mActivityItems) {
                if (item.item.get().equals(activity)) {
                    item.state = ACTIVITY_STATE_DESTORY;
                    break;
                }
            }
            //
            int mark = -1;
            for (int i = mActivityItems.size() - 1; i >= 0; i--) {
                if (checkActivityReferenceExisted(i)
                        && mActivityItems.get(i).item.get().equals(activity)) {
                    mark = i;
                    break;
                }
            }
            if (mark != -1) {
                mActivityItems.remove(mark);
            }
        }
    };

    //////////////////////////////////////////////////////////////////////

    public static Application getApplication() {
        return getInstance().mApplication.get();
    }

    public static Activity getCurrentActivity() {
        List<StateItem<Activity>> temp = new ArrayList<>(getInstance().mActivityItems);
        return temp.get(temp.size() - 1).item.get();
    }

    public StateItem<Activity> getActivityItem(Activity activity) {
        for (StateItem<Activity> item :
                getInstance().mActivityItems) {
            if (checkActivityReferenceExisted(item) && item.item.get().equals(activity)) {
                return item;
            }
        }
        return null;
    }

    //////////////////////////////////////////////////////////////////////

    public static void startActivityClearTask(Class<? extends Activity> target, Bundle extras) {
        Context parent = getInstance().mApplication.get();
        if (parent != null) {
            Intent intent = new Intent(parent, target);
            if (extras != null && extras.size() != 0) {
                intent.putExtras(extras);
            }
            // set params
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            //
            parent.startActivity(intent);
        }
    }

    public static void startActivity(Context parent, Class<? extends Activity> target) {
        startActivity(parent, target, null);
    }

    public static Bundle createBundle() {
        return new Bundle();
    }

    public static void startActivity(Context parent, Class<? extends Activity> target, Bundle extras) {
        if (parent == null || target == null) {
            return;
        }

        Intent intent = new Intent(parent, target);
        if (extras != null && extras.size() != 0) {
            intent.putExtras(extras);
        }
        parent.startActivity(intent);
    }

    public static void popTo(Class<? extends Activity> targetActivity) {
        getInstance().popToActivity(targetActivity);
    }

    public void popToActivity(Class<? extends Activity> targetActivity) {
        if (mActivityItems.size() == 0) {
            return;
        }
        // 1. check existed
        boolean existed = false;
        //
        for (int i = mActivityItems.size() - 2; i >= 0; i--) {
            if (checkActivityReferenceExisted(i)) {
                if (mActivityItems.get(i).item.get().getClass().equals(targetActivity)) {
                    existed = true;
                    break;
                }
            }
        }
        if (existed) {
            // 2. pop to target
            for (int i = mActivityItems.size() - 1; i >= 0; i--) {
                if (mActivityItems.get(i).item.get().getClass().equals(targetActivity)) {
                    break;
                } else {
                    mActivityItems.get(i).item.get().finish();
                }
            }
        }
    }

    ///////////////////////////////////RxLifeCycle///////////////////////////////////

    public static <R> Observable.Transformer<R, R> subscribeUtilEvent(final Activity target, LifecycleEvent event) {
        return RxLifecycle.subscribeUtilEvent(target, event);
    }

    public static <R> Observable.Transformer<R, R> subscribeUtilEvent(final Fragment target, LifecycleEvent event) {
        return RxLifecycle.subscribeUtilEvent(target, event);
    }

    //////////////////////////////////////////////////////////////////////

    private boolean checkActivityReferenceExisted(int index) {
        return mActivityItems.get(index).item.get() != null;
    }

    private boolean checkActivityReferenceExisted(StateItem<Activity> item) {
        return item.item.get() != null;
    }

    public String dump() {
        StringBuffer sb = new StringBuffer();
        for (StateItem<Activity> item :
                mActivityItems) {
            if (checkActivityReferenceExisted(item)) {
                sb.append("| ").append(item.item.get()).append(" -> ").append(item.state).append("\n");
            }
        }
        return sb.toString();
    }

    public void printDump() {
        Log.d(TAG, dump());
    }

    //////////////////////////////////////////////////////////////////////
}
