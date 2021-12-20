package networkscanner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PortScanWorker implements Runnable {
    static int globalId = 1;

    private int id;
    private List<Integer> ports;
    private List<Integer> openPorts;
    private InetAddress inetAddress;
    private int timeout = 200;
    CyclicBarrier barrier;

    public PortScanWorker() {
        id = globalId++;
    }

    public int getId() {
        return id;
    }

    public void setBarrier(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public List<Integer> getOpenPorts() {
        return openPorts;
    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    public void run() {
        System.out.println("Стартовал поток с id = " + id);
        scan(inetAddress);
        try {
            barrier.await();
        } catch (InterruptedException ex) {
            return;
        } catch (BrokenBarrierException ex) {
            return;
        }
    }

    void scan(InetAddress inetAddress) {
        openPorts = new ArrayList<Integer>();
        //System.out.println("scanning ports: ");
        for (Integer port : ports) {
            //System.out.print(port);
            try {
                InetSocketAddress isa = new InetSocketAddress(inetAddress,port);
                Socket socket = new Socket();
                socket.connect(isa,timeout);
                System.out.println("Найден открытый порт: " + port);
                openPorts.add(port);
                socket.close();
            } catch (IOException ioe) {
                // чтобы не получать ошибки по закрытым портам, это лучше закомментировать
                //System.err.println("I/O exception");
            }
        }
        //System.out.println("FINISH, id = " + id);
    }
}
