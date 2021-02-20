package ordo;

import formats.Format;
import map.Mapper;

public class MapProcess implements Runnable {
	Worker w;
	Mapper m;
	Format reader;
	Format writer;
	CallBack cb;

	public MapProcess(Worker w, Mapper m, Format reader, Format writer, CallBack cb) {
		this.w = w;
		this.m = m;
		this.reader = reader;
		this.writer = writer;
		this.cb = cb;
	}

	public void run() {
		try {
			w.runMap(m, reader, writer, cb);
			cb.taskDone();
		} catch (Exception exception) {
			exception.printStackTrace();

		}
	}

}


