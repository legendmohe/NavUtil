# NavUtil
利用 NavUtil，你可以在 Activity 或 Fragment 的生命周期发生变化时，停止订阅你的 Observable。

# Usage

在Activity 中，为你的 Observable 应用 compose 操作符，如下所示：

    // 假设这是你的 Observable
    Observable.interval(1, TimeUnit.SECONDS) 
        // 你的 Observable 将在 Activity 的 OnStopped 触发后停止发射
        .compose(NavUtil.<Long>subscribeUtilEvent(this, LifecycleEvent.ON_STOPPED))
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
        
 在 Fragment 中的用法如下所示：
 
    // 假设这是你的 Observable
    Observable.interval(1, TimeUnit.SECONDS)
        // 你的 Observable 将在 Activity 的 onPaused 触发后停止发射
        .compose(NavUtil.<Long>subscribeUtilEvent(this, LifecycleEvent.ON_PAUSED))
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
        
 目前支持的生命周期事件如下所示：
 
     public enum LifecycleEvent {
        ON_PAUSED,
        ON_STOPPED,
        ON_SAVE_INSTANCE_STATE,
        ON_DESTROYED,
        ON_VIEW_DESTORYED,
        ON_DETACHED,
    }

# Gradle

    compile 'com.legendmohe.maven:navutil:x.y'

[ ![Download](https://api.bintray.com/packages/legendmohe/maven/NavUtil/images/download.svg) ](https://bintray.com/legendmohe/maven/NavUtil/_latestVersion)
