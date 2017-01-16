package com.legendmohe.navutil;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List<ActivityItem> mActivityItems;
    private Map<Integer, ActivityItem> mActivityMap;

    //////////////////////////////////////////////////////////////////////

    private static class LazyHolder {
        private static final NavUtil INSTANCE = new NavUtil();
    }

    public static NavUtil getInstance() {
        return LazyHolder.INSTANCE;
    }

    private NavUtil() {
        mActivityItems = new ArrayList<>();
        mActivityMap = new HashMap<>();
    }

    //////////////////////////////////////////////////////////////////////

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
            mActivityItems.add(new ActivityItem(activity, ACTIVITY_STATE_CREATED));
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (DEBUG) {
                Log.d(TAG, "activity started: " + activity);
            }
            for (ActivityItem item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.activity.get().equals(activity)) {
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
            for (ActivityItem item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.activity.get().equals(activity)) {
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
            for (ActivityItem item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.activity.get().equals(activity)) {
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
            for (ActivityItem item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.activity.get().equals(activity)) {
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
            for (ActivityItem item :
                    mActivityItems) {
                if (checkActivityReferenceExisted(item) && item.activity.get().equals(activity)) {
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
//            for (ActivityItem item :
//                    mActivityItems) {
//                if (item.activity.get().equals(activity)) {
//                    item.state = ACTIVITY_STATE_DESTORY;
//                    break;
//                }
//            }
            int mark = -1;
            for (int i = mActivityItems.size() - 1; i >= 0; i--) {
                if (checkActivityReferenceExisted(i)
                        && mActivityItems.get(i).activity.get().equals(activity)) {
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
        List<ActivityItem> temp = new ArrayList<>(getInstance().mActivityItems);
        return temp.get(temp.size() - 1).activity.get();
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

    public static void startActivity(Class<? extends Activity> target) {
        startActivity(target, null);
    }

    public static void startActivity(Class<? extends Activity> target, Bundle extras) {
        startActivity(getInstance().mApplication.get(), target, extras);
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
                if (mActivityItems.get(i).activity.get().getClass().equals(targetActivity)) {
                    existed = true;
                    break;
                }
            }
        }
        if (existed) {
            // 2. pop to target
            for (int i = mActivityItems.size() - 1; i >= 0; i--) {
                if (mActivityItems.get(i).activity.get().getClass().equals(targetActivity)) {
                    break;
                } else {
                    mActivityItems.get(i).activity.get().finish();
                }
            }
        }
    }

    private boolean checkActivityReferenceExisted(int index) {
        return mActivityItems.get(index).activity.get() != null;
    }

    private boolean checkActivityReferenceExisted(ActivityItem item) {
        return item.activity.get() != null;
    }

    public String dump() {
        StringBuffer sb = new StringBuffer();
        for (ActivityItem item :
                mActivityItems) {
            if (checkActivityReferenceExisted(item)) {
                sb.append("| ").append(item.activity.get()).append(" -> ").append(item.state).append("\n");
            }
        }
        return sb.toString();
    }

    public void printDump() {
        Log.d(TAG, dump());
    }

    //////////////////////////////////////////////////////////////////////

    private static class ActivityItem {
        WeakReference<Activity> activity;
        int state;

        public ActivityItem(Activity activity, int state) {
            this.activity = new WeakReference<>(activity);
            this.state = state;
        }
    }
}
