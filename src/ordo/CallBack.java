package ordo;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Semaphore;

/**
 * Reminder called when the execution of map is done
 *
 * @author Hamza Mouddene
 * @version 1.0
 */
public class CallBack extends UnicastRemoteObject implements CallBackInterface {

    /**
     * Attributes of Callback class
     */
    private final Semaphore semaphore;        // The semaphore we will use
    private int count;
    private final int nbChunks;

    /**
     * Constructor of Callback class
     */
    public CallBack(int nbChunks) throws RemoteException {
        this.semaphore = new Semaphore(0);
        this.count = 0;
        this.nbChunks = nbChunks;
    }

    public Semaphore getSemaphore() {
        return this.semaphore;
    }

    @Override
    public void runMapDone() {
        try {
            System.out.println("map done");
            System.out.println(this.count + " " + this.nbChunks);
            if (this.count == this.nbChunks) {
                System.out.println("map done : release");
                this.getSemaphore().release();
            } else {
                System.out.println("map done : count++");
                this.count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void waitForFinished() {
        try {
            System.out.println("wait map");
            System.out.println(this.count + " " + this.nbChunks);
            this.getSemaphore().acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
