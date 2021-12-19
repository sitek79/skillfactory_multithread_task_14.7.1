package networkscanner;

public class CountDown implements Runnable {
    protected int countDown = 10;
    private static int taskCount = 0;
    private final int id = taskCount++;
    public CountDown() {}
    public CountDown(int countDown) {
        this.countDown = countDown;
    }
    public String status() {
        return "#" + id + "(" + (countDown > 0 ? countDown : "Запуск сканирования!") + ") ";
    }
    public void start() {
        run();
    }

    public void run() {
        while (countDown-- > 0) {
            System.out.println(status());
            Thread.yield();
        }
    }
}
