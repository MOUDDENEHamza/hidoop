package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallBack extends Remote {

    /**
     * When the task is done, we increment the counter then if the counter is equal to the number of available fragments
     * it prevents that the process has finished.
     * @throws RemoteException
     */
    public void runMapDone() throws RemoteException;

    /**
     *
     * @throws RemoteException
     */
    public void waitForFinished() throws RemoteException;
}
