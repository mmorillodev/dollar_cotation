import java.io.IOException;
import java.util.Timer;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Timer timer = new Timer();
        timer.schedule(new Scheduler(), 0, getMilis(30));

//        new Scheduler().run();
    }

    private static int getMilis(int minutes) {
        return minutes*60*1000;
    }
}
