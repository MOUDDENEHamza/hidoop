package ordo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class MergeFiles {
    protected File[] files;

    public MergeFiles(String[] filesName) {
        this.files = new File[filesName.length];
        for (int i = 0; i < filesName.length; i++) {
            files[i] = new File(filesName[i]);
            try {
                files[i].createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void mergeFiles(String nameFMerged) {
        File mergedFile = new File(nameFMerged);
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(mergedFile, true);
            out = new BufferedWriter(fstream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        for (File f : this.files) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));

                String aLine;
                while ((aLine = in.readLine()) != null) {
                    out.write(aLine);
                    out.newLine();
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFiles() {
        for (File fileName : this.files) {
            fileName.delete();
        }
    }
}