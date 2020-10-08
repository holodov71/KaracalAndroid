package app.karacal.helpers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.Calendar;

import app.karacal.service.NotificationJobService;

public class NotificationHelper {

    public static final String JOB_UNIQUE_TAG = "karacal_notification_job_tag";

    public static void scheduleNotification(Context context, long time){

        if(! PreferenceHelper.isNotificationsAllowed(context)) return;

        Log.v("NotificationHelper", "scheduleNotification");

        Calendar calendar = Calendar.getInstance();
        int nowHour = calendar.get(Calendar.HOUR_OF_DAY);

        calendar.setTimeInMillis(time);

        int nextHour = calendar.get(Calendar.HOUR_OF_DAY);

        int hour = nextHour - nowHour;

        int startWindow = (hour - 1) * 60 * 60;
        int endWindow = (hour + 1) * 60 * 60;

        if (startWindow < 0){
            startWindow = 0;
        }

        if(endWindow < startWindow){
            endWindow = startWindow + 60;
        }


        // Create a new dispatcher using the Google Play driver.
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString("some_key", "some_value");

        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(NotificationJobService.class)
                // uniquely identifies the job
                .setTag(JOB_UNIQUE_TAG)
                // one-off job
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 60 seconds from now
                .setTrigger(Trigger.executionWindow(startWindow, endWindow))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                // constraints that need to be satisfied for the job to run
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setExtras(myExtrasBundle)
                .build();

        dispatcher.mustSchedule(myJob);
    }
}
