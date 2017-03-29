package imagecup.nju.intellichens.automemo.recorder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Hanifor on 3/23/2017.
 */

public class TimeListener {
    private Timer timer;
    private boolean timeUp;

    public TimeListener(int seconds) {
        timeUp = false;
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds * 1000);
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    class RemindTask extends TimerTask {
        public void run() {
            timeUp = true;
            timer.cancel();
        }
    }
}
