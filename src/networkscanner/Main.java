package networkscanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Сейчас выполняется процесс: " + Message.currentThread().getName());
        new Message("Вас приветствует программа сканирования. ").start();
        new Message("Через несколько секунд мы начнем.").start();

        try {
            Message.currentThread().join(5000);
            System.out.println("\nСейчас выполняется поток: " + Message.currentThread().getName());
        } catch (NullPointerException ne) {
            System.out.println("Получили NullPointerException");
        }
        catch (Exception e) {
            System.out.println("Не дождались");
        }

        System.out.println("Процесс Main запущен...");
        Thread countDown = new Thread(new CountDown(20),"CountDown");
        countDown.start();

        try {
            System.out.println("Состояние потока перед interrupt: " + countDown.getState());
            Thread.currentThread().interrupt();

            countDown.join();
            System.out.println("Ждем..");
            System.out.println("isInterrupted?" + Thread.currentThread().isInterrupted());
            System.out.println("Состояние потока: " + countDown.getState());
            countDown.interrupt();
            System.out.println("isInterrupted?" + Thread.currentThread().isInterrupted());
        }
        catch(InterruptedException e) {
            System.out.printf("%s был прерван\n", countDown.getName());
            System.out.println("Состояние потока после interrupt: " + countDown.getState());
        }

        ScanApp scanApp = new ScanApp();
        scanApp.scanUp();

        System.out.println("Процесс Main завершился...");
    }
}
