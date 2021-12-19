package networkscanner;

public class Main {
    public static void main(String[] args) {
        //new Message("Message first. ").start();
        //new Message("Message second.").start();
        //
        System.out.println("Поток Main запущен...");
        Runnable r = ()->{
            System.out.printf("%s started... \n", Thread.currentThread().getName());
            try{
                //Thread.sleep(500);
                CountDown launch = new CountDown(10);
                launch.run();
            }
            catch(Exception e){
                System.out.println("Thread has been interrupted");
            }
            System.out.printf("%s finished... \n", Thread.currentThread().getName());
        };
        Thread myThread = new Thread(r,"Программа сканирования");
        myThread.start();
        System.out.println("Поток Main завершился...");
        //
        //CountDown launch = new CountDown(10);
        //launch.run();
        //
        ScanApp scanApp = new ScanApp();
        scanApp.scanUp();
    }
}
