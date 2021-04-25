package application;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import map.MapReduce;
import ordo.Job;
import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import formats.KV;
import ordo.JobInterface;

public class MyMapReduce implements MapReduce {
    private static final long serialVersionUID = 1L;

    // MapReduce program that computes word counts
    public void map(FormatReader reader, FormatWriter writer) {

        HashMap<String, Integer> hm = new HashMap<>();
        KV kv;
        while ((kv = reader.read()) != null) {
            String tokens[] = kv.v.split(" ");
            for (String tok : tokens) {
                if (hm.containsKey(tok)) hm.put(tok, hm.get(tok).intValue() + 1);
                else hm.put(tok, 1);
            }
        }
        for (String k : hm.keySet()) writer.write(new KV(k, hm.get(k).toString()));
    }

    public void reduce(FormatReader reader, FormatWriter writer) {
        HashMap<String, Integer> hm = new HashMap<>();
        KV kv;
        while ((kv = reader.read()) != null) {
            if (hm.containsKey(kv.k)) hm.put(kv.k, hm.get(kv.k) + Integer.parseInt(kv.v));
            else hm.put(kv.k, Integer.parseInt(kv.v));
        }
        for (String k : hm.keySet()) writer.write(new KV(k, hm.get(k).toString()));
    }

    public static void relaunch(String fileName) {
        try {
            Registry registry = LocateRegistry.getRegistry("behemot.enseeiht.fr", 9999);
            JobInterface j = (JobInterface) registry.lookup("//localhost:9999/Job");
            j.setInputFormat(Format.Type.LINE);
            j.setInputFileName(fileName);
            long t1 = System.currentTimeMillis();
            j.startJob(new MyMapReduce());
            long t2 = System.currentTimeMillis();
            System.out.println("time in ms =" + (t2 - t1));
            System.exit(0);
        } catch (Exception e) {
            relaunch(fileName);
        }
    }

    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.getRegistry("behemot.enseeiht.fr", 9999);
            JobInterface j = (JobInterface) registry.lookup("//localhost:9999/Job");
            j.setInputFormat(Format.Type.LINE);
            j.setInputFileName(args[0]);
            long t1 = System.currentTimeMillis();
            j.startJob(new MyMapReduce());
            long t2 = System.currentTimeMillis();
            System.out.println("time in ms =" + (t2 - t1));
            System.exit(0);
        } catch (Exception e) {
            relaunch(args[0]);
        }
    }
}
