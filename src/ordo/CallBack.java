package ordo;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

/**
 * Reminder called when the execution of map is done
 *
 * @author Hamza Mouddene
 * @version 1.0
 */
public class CallBack implements Serializable {

    /**
     * Attributes of Callback class
     */
    private int count;
    private final Semaphore semaphore;        // The semaphore we will use
    private int nbChunks;

    /**
     * Constructor of Callback class
     */
    public CallBack(int nbChunks) {
        this.count = 0;
        this.semaphore = new Semaphore(0);
        this.nbChunks = nbChunks;
    }

    public Semaphore getSemaphore() {
        return this.semaphore;
    }

    /**
     * When the task is done, we increment the counter then if the counter is equal to the number of available fragments
     * it prevents that the process has finished
     */
    public void runMapDone() {
        try {
            System.out.println("map done");
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

    public void waitForFinished() {
        try {
            System.out.println("wait map");
            this.getSemaphore().acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
