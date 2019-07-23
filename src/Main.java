import java.util.Timer;

public class Main {

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new Scheduler(), 0, getMilis(10));

//        new Scheduler().run();
    }

    private static int getMilis(int minutes) {
        return minutes*60*1000;
    }
}
