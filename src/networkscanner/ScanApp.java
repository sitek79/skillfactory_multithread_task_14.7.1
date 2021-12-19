package networkscanner;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class ScanApp {
    public static final int MIN_PORTS_PER_THREAD = 20;
    public static final int MAX_THREADS = 0xFF;

    static InetAddress inetAddress;
    static List<Integer> allPorts;

    static List<Integer> allOpenPorts = new ArrayList<Integer>();
    static List<PortScanWorker> workers = new ArrayList<PortScanWorker>(MAX_THREADS);

    static Date startTime;
    static Date endTime;

    public void scanUp() {
        startTime = new Date();

        scanData("172.16.11.1", "0-65535");
        //scanData("nettop.hm", "0-65535");

        if (allPorts.size()/MIN_PORTS_PER_THREAD > MAX_THREADS ) {
            final int PORTS_PER_THREAD = allPorts.size()/MAX_THREADS;

            List<Integer> threadPorts = new ArrayList<Integer>();
            for (int i=0,counter=0; i<allPorts.size();i++,counter++) {
                if (counter<PORTS_PER_THREAD) {
                    threadPorts.add(allPorts.get(i));
                } else {
                    PortScanWorker psw = new PortScanWorker();
                    psw.setInetAddress(inetAddress);
                    psw.setPorts(new ArrayList<Integer>(threadPorts));
                    workers.add(psw);
                    threadPorts.clear();
                    counter=0;
                }
            }
            PortScanWorker psw = new PortScanWorker();
            psw.setInetAddress(inetAddress);
            psw.setPorts(new ArrayList<Integer>(threadPorts));
            workers.add(psw);
        } else {
            List<Integer> threadPorts = new ArrayList<Integer>();
            for (int i=0,counter=0; i<allPorts.size();i++,counter++) {
                if (counter<MIN_PORTS_PER_THREAD) {
                    threadPorts.add(allPorts.get(i));
                } else {
                    PortScanWorker psw = new PortScanWorker();
                    psw.setInetAddress(inetAddress);
                    psw.setPorts(new ArrayList<Integer>(threadPorts));
                    workers.add(psw);
                    threadPorts.clear();
                    counter=0;
                }
            }
            PortScanWorker psw = new PortScanWorker();
            psw.setInetAddress(inetAddress);
            psw.setPorts(new ArrayList<Integer>(threadPorts));
            workers.add(psw);
        }

        System.out.println("Порты для сканирования: "+allPorts.size());
        System.out.println("Потоков в работе: "+workers.size());

        Runnable summarizer = new Runnable() {
            public void run() {
                System.out.println("Завершить сканирование...");

                for (PortScanWorker psw : workers) {
                    List<Integer> openPorts = psw.getOpenPorts();
                    allOpenPorts.addAll(openPorts);
                }

                Collections.sort(allOpenPorts);

                System.out.println("Список открытых портов:");
                for (Integer openedPort : allOpenPorts) {
                    System.out.println(openedPort);
                }

                endTime = new Date();

                System.out.println("Время сканирования: " + (endTime.getTime() - startTime.getTime()) + " ms");
            }
        };

        CyclicBarrier barrier = new CyclicBarrier(workers.size(),summarizer);

        for (PortScanWorker psw : workers) {
            psw.setBarrier(barrier);
        }

        System.out.println("Начать сканирование...");

        for (PortScanWorker psw : workers) {
            new Thread(psw).start();
        }
    }

    static void scanData(String address, String ports) {

        String host = address;

        try {
            inetAddress = InetAddress.getByName(host);
        } catch (IOException ioe) {
            System.out.println("Ошибка разрешения имени хоста!");
            System.exit(2);
        }

        System.out.println("Сканируем хост: "+host);

        int minPort = 0;
        int maxPort = 0x10000-1;

        if (ports.contains("-")) {
            // диапазон указанных портов
            String[] portsSelect = ports.split("-");
            try {
                minPort = Integer.parseInt(portsSelect[0]);
                maxPort = Integer.parseInt(portsSelect[1]);
            } catch (NumberFormatException nfe) {
                System.out.println("Неверно указаны порты!");
                System.exit(3);
            }
        } else {
            // указан один порт
            try {
                minPort = Integer.parseInt(ports);
                maxPort = minPort;
            } catch (NumberFormatException nfe) {
                System.out.println("Неверно указаны порты!");
                System.exit(3);
            }
        }
        //
        allPorts = new ArrayList<Integer>(maxPort-minPort+1);

        for (int i=minPort; i<=maxPort; i++) {
            allPorts.add(i);
        }
    }
}
