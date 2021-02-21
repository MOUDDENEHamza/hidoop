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
public class CallBackImpl extends UnicastRemoteObject implements CallBack {

    /**
     * Attributes of Callback class
     */
    private final Semaphore semaphore;        // The semaphore we will use
    private int count;
    private final int nbChunks;

    /**
     * Constructor of Callback class
     */
    public CallBackImpl(int nbChunks) throws RemoteException {
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
            this.count++;
            if (this.count == this.nbChunks) {
                this.getSemaphore().release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void waitForFinished() {
        try {
            this.getSemaphore().acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
