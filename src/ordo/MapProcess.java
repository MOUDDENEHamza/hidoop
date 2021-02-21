package ordo;

import formats.Format;
import map.Mapper;

import java.rmi.RemoteException;

public class MapProcess implements Runnable {
	Mapper m;
	Format reader;
	Format writer;
	CallBackInterface cb;

	public MapProcess(Mapper m, Format reader, Format writer, CallBack cb) {
		this.m = m;
		this.reader = reader;
		this.writer = writer;
		this.cb = cb;
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
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}


