package com.example.torre.yora.infrastructure;


import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionScheduler
{
    private final YoraApplication application;
    private final Handler handler;
    private final ArrayList<TimedCallback> timedCallbacks;
    private final HashMap<Class, Runnable> onResumeActions; //we queue the actions in this map
    private boolean isPaused;

    public ActionScheduler(YoraApplication application)
    {
        this.application = application;
        handler = new Handler();
        timedCallbacks = new ArrayList<>();
        onResumeActions = new HashMap<>();
    }

    public void onPause()
    {
        isPaused = true;
    }

    public void onResume()
    {
        isPaused = false;

        //Loop all our timed callbacks and tell them to reschedule themselves to be invoked in "x" milliseconds
        for (TimedCallback callback : timedCallbacks)
        {
            callback.schedule();
        }

        //Iterate all our queued actions and execute them.
        for (Runnable runnable : onResumeActions.values())
        {
            runnable.run();
        }

        //clear the actions
        onResumeActions.clear();
    }

    //Responsible for queueing up a runnable to be invoked when an Activity/Fragment has been resumed
    public void invokeOnResume(Class cls, Runnable runnable)
    {
        if (!isPaused)
        {
            runnable.run();
            return;
        }

        onResumeActions.put(cls, runnable);
    }

    public void postDelayed(Runnable runnable, long milliseconds)
    {
        handler.postDelayed(runnable, milliseconds);
    }

    public void invokeEveryMilliseconds(Runnable runnable, long milliseconds)
    {
        invokeEveryMilliseconds(runnable, milliseconds, true);
    }

    //Should we run the "Runnable" passed in right now in addition to scheduling it to run every "x" milliseconds
    public void invokeEveryMilliseconds(Runnable runnable, long milliseconds, boolean runImmediately)
    {
        TimedCallback callback = new TimedCallback(runnable, milliseconds);
        timedCallbacks.add(callback);

        if (runImmediately)
        {
            callback.run();
        }
        else
        {
            postDelayed(callback, milliseconds);
        }
    }

    public void postEveryMilliseconds(Object request, long milliseconds)
    {
        postEveryMilliseconds(request, milliseconds, true);
    }

    public void postEveryMilliseconds(final Object request, long milliseconds, boolean postImmediately)
    {
        invokeEveryMilliseconds(new Runnable()
        {
            @Override
            public void run()
            {
                application.getBus().post(request);
            }
        }, milliseconds, postImmediately);
    }

    private class TimedCallback implements Runnable
    {
        private final Runnable runnable;
        private final long delay;

        public TimedCallback(Runnable runnable, long delay)
        {
            this.runnable = runnable;
            this.delay = delay;
        }

        @Override
        public void run()
        {
            if (isPaused)
            {
                return;
            }

            runnable.run();
            schedule();

        }

        public void schedule()
        {
            //Reschedule the runnable we just executed
            handler.postDelayed(this, delay);
        }
    }
}
