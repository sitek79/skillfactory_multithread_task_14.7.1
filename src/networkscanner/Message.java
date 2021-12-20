package networkscanner;

public class Message extends Thread {
    static Object lock = new Object();
    String message;
    Message(String message){
        this.message = message;
    }

    public Message() {
    }

    public void run(){
        System.out.println("Начал выполняться поток: " + Message.currentThread().getName());
        try {
            currentThread().join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized(lock) {
            for(int i = 0; i < message.length(); i++){
                System.out.print(message.charAt(i));
                try{
                    Thread.sleep(50);
                } catch (InterruptedException e){
                    break;
                }
            }
        }
    }
}
