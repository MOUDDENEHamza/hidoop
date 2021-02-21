package ordo;

import formats.Format;
import map.Mapper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

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
    static int port;            // The port of the worker
    static int id;              // The Id of the worker
    static String url;          // The url of the worker
    Registry registry;
    MapProcess mapProcess = null;
    /**
     * Constructor of WorkerImpl class that creates a worker
     *
     * @throws RemoteException that may occur during the execution of a remote method call
     */
    public WorkerImpl(String url) throws RemoteException {
        try {
            registry = LocateRegistry.getRegistry(WorkerImpl.port);
            registry.rebind(url, this);
            System.out.println("Registry existant");
        } catch (Exception e) {
            try {
                System.out.println("Registry inexistant, create registry");
                registry = LocateRegistry.createRegistry(WorkerImpl.port);
                registry.rebind(url, this);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void runMap(Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException, InterruptedException {
    	if (this.mapProcess == null) {
            this.mapProcess = new MapProcess(m, reader, writer, cb);
        }
        Thread mapProcess = new Thread(this.mapProcess);
        mapProcess.start();
    }

    /**
     * The main method of WorkerImpl class
     *
     * @param args contain the command line
     */
    public static void main(String[] args) throws RemoteException, UnknownHostException {
        System.out.println("******************************Worker******************************\n");
        // Obtain the port of the worker
        if (args.length == 0) {
            Scanner port = new Scanner(System.in);
            System.out.print("Enter the worker port : ");
            WorkerImpl.port = port.nextInt();

            // Obtain the id of the worker
            Scanner id = new Scanner(System.in);
            System.out.print("Enter the worker id : ");
            WorkerImpl.id = port.nextInt();
        } else if (args.length == 2) {
            WorkerImpl.port = Integer.parseInt(args[0]);
            WorkerImpl.id = Integer.parseInt(args[1]);
        } else {
            System.out.println("Usage : java WorkerImpl port id");
        }

        System.out.println("Address : " + InetAddress.getLocalHost() +" | Port : " + WorkerImpl.port);
        WorkerImpl.url = "//localhost:"+ WorkerImpl.port + "/Worker" + WorkerImpl.id;
        System.out.println("url : " + WorkerImpl.url);

        // Create worker
        new WorkerImpl(WorkerImpl.url);
    }

}
