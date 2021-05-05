package hdfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains informations about the servers of the HDFS filesystem
 */
public class FileRecord {
    private String fileName;

    /**
     * The chunks hash of a file. Each entry contains the hash of a chunk, the servers where it is stored, the part
     * number of a file and the total number of chunks
     */
    private HashMap<String, Pair<ArrayList<ServerRecord>, Pair<Integer, Integer>>> chunksHash;

    /**
     * Constructor for FileRecord
     * @param fileName the name of the file associated to the FileRecord
     */
    public FileRecord(String fileName) {
        this.fileName = fileName;
        this.chunksHash = new HashMap<>();
    }

    @Override
    public String toString() {
        String hashs = "";
        for (Map.Entry<String, Pair<ArrayList<ServerRecord>, Pair<Integer, Integer>>> p : chunksHash.entrySet()) {
            hashs += "\t- " + p.getKey() + " (" + (p.getValue().getRight().getLeft() + 1) + "/" + p.getValue().getRight().getRight() +") -> " + p.getValue().getLeft() + "\n";
        }

        return "file name : " + fileName + " | State : "
                + (this.allChunksAreRegistered() ? "ALL CHUNKS ARE REGISTERED" : "SOME CHUNKS ARE MISSING") + "\n" +
                hashs;
    }

    public HashMap<String, Pair<ArrayList<ServerRecord>, Pair<Integer, Integer>>> getChunksHash() {
        return chunksHash;
    }

    /**
     * Add a chunk contains in a HDFS server to a {@link FileRecord}
     * @param hash The hash of the chunk to add
     * @param server The server where the new chunk is stored
     * @param chunkNumber The chunk number in the file
     * @param totalNumberChunk The total number of chunks of a file
     */
    public void addChunkStoredInServer(String hash, ServerRecord server, int chunkNumber, int totalNumberChunk) {
        for (Map.Entry<String, Pair<ArrayList<ServerRecord>, Pair<Integer, Integer>>> p : chunksHash.entrySet()) {
            if (p.getKey().equals(hash)) {
                for (ServerRecord sr : p.getValue().getLeft()) {
                    if (sr.equals(server)) {
                        return;
                    }
                }
                p.getValue().getLeft().add(new ServerRecord(server));
                return;
            }
        }
        ArrayList<ServerRecord> srToStore = new ArrayList<>();
        srToStore.add(new ServerRecord(server));
        chunksHash.put(hash, new Pair<>(srToStore, new Pair(chunkNumber, totalNumberChunk)));
    }

    /**
     * Handmade checker to check if all chunks of a file are registered to the Name Provider
     * @return Yes if every chunk of the file is registered and false otherwise
     */
    public boolean allChunksAreRegistered() {
        Pair<ArrayList<ServerRecord>, Pair<Integer, Integer>> somePair = null;
        int nb_chunks = this.chunksHash.values().iterator().next().getRight().getRight(); //Nombre total de chunks du fichier, il ne faut vérifier si que si au moins chaque chunk est dispo une fois
        boolean[] presence = new boolean[nb_chunks];
        Arrays.fill(presence, false);
        for (Map.Entry<String, Pair<ArrayList<ServerRecord>, Pair<Integer, Integer>>> chunk : this.chunksHash.entrySet()) {
            int nb_frag = chunk.getValue().getRight().getLeft();
            if(!presence[nb_frag-1]) {
            	presence[nb_frag-1] = true;
            }
        }
        
        return !this.chunksHash.isEmpty() && (!Arrays.asList(presence).contains(false));
    }
}
