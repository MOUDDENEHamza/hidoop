package ordo;

import formats.Format;
import map.Mapper;

import java.rmi.RemoteException;

public class ThreadMap implements Runnable {
    Mapper m;
    Format reader;
    Format writer;
    CallBack cb;
    int flag;

    public ThreadMap(Mapper m, Format reader, Format writer, CallBack cb) {
        this.m = m;
        this.reader = reader;
        this.writer = writer;
        this.cb = cb;
        this.flag = 2;
    }

    public void run() {
        // Open reader and writer
        reader.open(Format.OpenMode.R);
        writer.open(Format.OpenMode.W);

        // Launch map
        m.map(reader, writer);

        // Close reader and writer
        reader.close();
        writer.close();

        // Report that the process has finished runMap task.
        try {
            cb.runMapDone();
            this.flag = 3;
        } catch (RemoteException e) {
            System.out.println("HEY how are you");
            e.printStackTrace();
        }
    }

}


