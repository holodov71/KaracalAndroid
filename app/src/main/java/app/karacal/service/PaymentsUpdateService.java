package app.karacal.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

public class PaymentsUpdateService extends Service {

    private static final int INTERVAL_DURATION = 10 * 1000; // 10 second
    private static final int TICK_INTERVAL = 60 * 1000; // 1 minute

    private static CountDownTimer timer = null;

    public PaymentsUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new CountDownTimer(INTERVAL_DURATION, TICK_INTERVAL) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

//                TODO make api call
                startTimer();
            }
        };
        startTimer();
    }

    public static void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public static void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer.start();
        }
    }
}
