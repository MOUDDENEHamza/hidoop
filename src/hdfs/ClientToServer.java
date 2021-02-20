package hdfs;

import java.net.Socket;
import java.io.*;

/**
 * A collection of useful functions to communicate a chunk between a client and a HDFS server
 */
public class ClientToServer {

    /**
     * Send a chunk from a client to a HDFS Server
     * @param fname The file containing the chunk to send
     * @param chunk The metadatas of the chunk to write to the server
     * @param bufferSize The size of the buffer used to send the chunk
     * @param server The server where the chunk should be written
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public static void writeChunkToServer(String fname, ChunkMetadata chunk, int bufferSize, Socket server)
            throws IOException, ClassNotFoundException, InterruptedException {
        ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());

        FileInputStream fis = new FileInputStream(fname);
        fis.skip(chunk.getBaseByte());

        oos.writeObject(Command.CMD_WRITE);
        oos.writeObject(chunk);
        oos.writeObject(bufferSize);

        byte buffer[] = new byte[bufferSize];
        long chunkSize = chunk.getSizeBytes();
        long sent = 0;
        int sentloop = 0;
        OutputStream bos = server.getOutputStream();

        while (sent + bufferSize <= chunkSize) {
            sentloop = fis.read(buffer);
            bos.write(buffer, 0, sentloop);
            sent += sentloop;
        }

        int remaining = (int) (chunkSize - sent);

        while (remaining > 0) {
            sentloop = fis.read(buffer, 0, remaining);
            bos.write(buffer, 0, sentloop);
            remaining -= sentloop;
        }

        ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
        Command c = (Command) ois.readObject();

        if (c.equals(Command.END_OF_TRANSMISSION)) {
            server.close();
        } else {
            throw new IOException("Error while communicating with a HDFS server");
        }
    }

    /**
     * Read a chunk from a HDFS server. The chunk will be written to a temporary file (baseDir/hash.tmp)
     * @param hash The hash of the chunk to retrieve
     * @param baseDir The directory where the chunk will be written
     * @param server The server where the chunk is stored
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static String readFromServer(String hash, String baseDir, Socket server) throws IOException, ClassNotFoundException {
        String fileName = baseDir + "." + hash + ".tmp";

        ObjectOutputStream oos = new ObjectOutputStream(server.getOutputStream());
        FileOutputStream fos = new FileOutputStream(fileName);

        oos.writeObject(Command.CMD_READ);
        oos.writeObject(hash);
        ObjectInputStream ois = new ObjectInputStream(server.getInputStream());
        int bufferSize = (int) ois.readObject();
        long chunkSize = (long) ois.readObject();

        byte buffer[] = new byte[bufferSize];
        long received = 0;
        int receivedLoop;
        InputStream bis = server.getInputStream();

        while (received + bufferSize <= chunkSize) {
            receivedLoop = bis.read(buffer);
            fos.write(buffer,0, receivedLoop);
            received += receivedLoop;
        }

        int remainingBytes = (int) (chunkSize - received);

        while (remainingBytes > 0) {
            receivedLoop = bis.read(buffer, 0, remainingBytes);
            fos.write(buffer, 0, receivedLoop);
            remainingBytes -= receivedLoop;
        }

        oos.writeObject(Command.END_OF_TRANSMISSION);
        server.close();

        return fileName;
    }

    /**
     * Ask a server to delete a chunk
     * @param hash The hash of the chunk to delete
     * @param server The server containing the chunk
     */
    public static void deleteToServer(String hash, Socket server) {
        try {
            ObjectOutputStream outStream = new ObjectOutputStream(server.getOutputStream());

            outStream.writeObject(Command.CMD_DELETE);
            outStream.writeObject(hash);
            outStream.close();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
