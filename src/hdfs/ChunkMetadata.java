package hdfs;

import formats.Format;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * This class stores the metadatas of a specific chunk
 */
public class ChunkMetadata implements Serializable {
    /**
     * A crumb is a part of a chunk. It is useful to send a chunk through network.
     * This value represents the maximum size a crumb can have
     */
    public static final int MAX_CRUMB_SIZE = 500<<20;

    /**
     * This is the minimum size a crumb can have.
     */
    public static final int MIN_CRUMB_SIZE = 32;

    /**
     * This is the fraction of the chunk used to determine the size of a crumb dynamically
     */
    public static final int CHUNK_FACTOR = 10*10*10;

    private String fileName;
    private Format.Type type;
    private long baseByte;
    private long sizeBytes;
    private int chunkNumber;
    private int totalChunkNumber;

    /**
     * This constructor is useful for the YAML parser only.
     */
    public ChunkMetadata() {
    }

    /**
     * Main constructor for a ChunkMetadata
     * @param fileName The name of the file containing the chunk
     * @param baseByte The number of bytes to skip from a file to get the first byte of the chunk
     * @param sizeBytes The size (in bytes) of the chunk
     * @param type The type of the file containing the chunk
     * @param chunkNumber The part number of the chunk
     * @param totalNumberChunk The total number of chunks of a file
     */
    public ChunkMetadata(String fileName, long baseByte, long sizeBytes, Format.Type type, int chunkNumber, int totalNumberChunk) {
        this.fileName = fileName;
        this.baseByte = baseByte;
        this.sizeBytes = sizeBytes;
        this.type = type;
        this.chunkNumber = chunkNumber;
        this.totalChunkNumber = totalNumberChunk;
    }

    /**
     * Copy constructor for ChunkMetadata
     * @param c The chunk to copy
     */
    public ChunkMetadata(ChunkMetadata c) {
        this.fileName = c.getFileName();
        this.type = c.getType();
        this.baseByte = c.getBaseByte();
        this.sizeBytes = c.getSizeBytes();
        this.chunkNumber = c.getChunkNumber();
        this.totalChunkNumber = c.getTotalChunkNumber();
    }

    // Get the hex string corresponding to a byte array

    /**
     * Returns the string containing the SHA256 hash (in hexadecimal format) of a specified byte array
     * @param bytes The byte array used to compute the hash
     * @return The hash of the byte array
     */
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

    public int getTotalChunkNumber() {
        return totalChunkNumber;
    }

    public Format.Type getType() {
        return this.type;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public int getChunkNumber() {
        return chunkNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public long getBaseByte() {
        return baseByte;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setType(Format.Type type) {
        this.type = type;
    }

    public void setBaseByte(long baseByte) {
        this.baseByte = baseByte;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public void setChunkNumber(int chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public void setTotalChunkNumber(int totalChunkNumber) {
        this.totalChunkNumber = totalChunkNumber;
    }

    // chunk a file in chunkNumber parts
    // Return an ArrayList of <Offset in file, ChunkMetadata>

    /**
     * Chunk a specified file in a list of chunkMetadata
     * @param fname The name of the file to chunk
     * @param type The type of the file to chunk
     * @param totalNumberChunk The total number of chunks to use
     * @return A list of totalNumberChunk {@link ChunkMetadata}
     * @throws IOException
     */
    public static ArrayList<ChunkMetadata> chunkFile(String fname, Format.Type type, int totalNumberChunk) throws IOException {
        FileInputStream fis = new FileInputStream(fname);
        long fileSizeBytes = fis.getChannel().size();
        long chunkSizeBytes = fileSizeBytes/totalNumberChunk;
        long totalSkipped = 0;
        int chunkNumber = 0;
        long internalSkipped = 0;

        ArrayList<ChunkMetadata> chunkList = new ArrayList<>();

        while (totalSkipped + chunkSizeBytes < fileSizeBytes) {
            internalSkipped = fis.skip(chunkSizeBytes);
            int read;
            while ((read = fis.read()) != '\n' && read != -1) {
                internalSkipped += 1;
            }
            internalSkipped += 1;
            chunkList.add(new ChunkMetadata(fname, totalSkipped, internalSkipped, type, chunkNumber, totalNumberChunk));
            totalSkipped += internalSkipped;
            chunkNumber++;
        }

        if (totalSkipped + 1 < fileSizeBytes) {
            chunkList.add(new ChunkMetadata(fname, totalSkipped, fileSizeBytes - totalSkipped, type, chunkNumber, totalNumberChunk));
        }

        totalNumberChunk = chunkList.size();
        for (ChunkMetadata cm : chunkList) {
            cm.setTotalChunkNumber(totalNumberChunk);
        }

        return chunkList;
    }

    /**
     * Determine the size of a crumb given the {@link ChunkMetadata} list of a file
     * @param chunksMetadata the chunks metadatas of a file
     * @return the size of a crumb
     */
    public static int crumbSize(ArrayList<ChunkMetadata> chunksMetadata) {
        long somme = 0;
        for (ChunkMetadata cm : chunksMetadata) {
            somme += cm.getSizeBytes();
        }

        return Math.min(Math.max(MIN_CRUMB_SIZE, (int) ((somme/chunksMetadata.size())/CHUNK_FACTOR)), MAX_CRUMB_SIZE);
    }

    @Override
    public String toString() {
        return "ChunkMetadata{" +
                "fileName='" + fileName + '\'' +
                ", type=" + type +
                ", baseByte=" + baseByte +
                ", sizeBytes=" + sizeBytes +
                ", chunkNumber=" + chunkNumber +
                ", totalChunkNumber=" + totalChunkNumber +
                '}';
    }
}
