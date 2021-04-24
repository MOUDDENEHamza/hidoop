package ordo;

import formats.Format;
import map.Mapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/**
 * Realizes Worker interface that launch the demon on each machine using RMI to communicate between the client and the
 * demon
 *
 * @author Hamza Mouddene
 * @version 1.0
 */
public class WorkerImpl extends UnicastRemoteObject implements Worker {

    /**
     * Attributes of WorkerImpl class
     */
    static int port;                // The port of the worker
    static int id;                  // The Id of the worker
    static String url;              // The url of the worker
    Registry registry;              //
    private int flag;
    private Mapper m;
    public Format reader, writer;
    public CallBack cb;
    public ThreadMap threadMap;

    /**
     * Constructor of WorkerImpl class that creates a worker
     *
     * @throws RemoteException that may occur during the execution of a remote method call
     */
    public WorkerImpl(String url) throws RemoteException, UnknownHostException {
        try {
            this.flag = 1;
            registry = LocateRegistry.getRegistry(WorkerImpl.port);
            registry.rebind(url, this);
            System.out.println("Registry existent");
        } catch (Exception e) {
            try {
                System.out.println("Registry nonexistent, create registry");
                registry = LocateRegistry.createRegistry(WorkerImpl.port);
                registry.rebind(url, this);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public Mapper getMapper() {
        return this.m;
    }

    @Override
    public Format getReader() {
        return this.reader;
    }

    @Override
    public Format getWriter() {
        return this.writer;
    }

    @Override
    public CallBack getCallBack() {
        return this.cb;
    }

    @Override
    public void runMap(Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException, InterruptedException {
        this.threadMap = new ThreadMap(m, reader, writer, cb);
        Thread t = new Thread(this.threadMap);
        t.start();
        this.flag = 2;
        this.m = m;
        this.reader = reader;
        this.writer = writer;
        this.cb = cb;
    }

    @Override
    public String beat() throws RemoteException {
        if (this.threadMap.flag == 3) {
            return "done";
        } else {
            if (this.flag == 1) {
                return "up";
            } else {
                return "map";
            }
        }
    }

    /**
     * The main method of WorkerImpl class
     *
     * @param args contain the command line
     */
    public static void main(String[] args) throws IOException {
        System.out.println("******************************Worker******************************\n");
        // Obtain the port of the worker
        if (args.length == 0) {
            Scanner port = new Scanner(System.in);
            System.out.print("Enter the worker port : ");
            WorkerImpl.port = port.nextInt();
            // Obtain the id of the worker
            Scanner id = new Scanner(System.in);
            System.out.print("Enter the worker id : ");
            WorkerImpl.id = id.nextInt();
        } else if (args.length == 2) {
            WorkerImpl.port = Integer.parseInt(args[0]);
            WorkerImpl.id = Integer.parseInt(args[1]);
        } else {
            System.out.println("Usage : java WorkerImpl port id");
        }

        System.out.println("Address : " + InetAddress.getLocalHost() + " | Port : " + WorkerImpl.port);
        WorkerImpl.url = "//localhost:" + WorkerImpl.port + "/Worker" + WorkerImpl.id;
        System.out.println("url : " + WorkerImpl.url);

        // Create worker
        new WorkerImpl(WorkerImpl.url);
    }

}
