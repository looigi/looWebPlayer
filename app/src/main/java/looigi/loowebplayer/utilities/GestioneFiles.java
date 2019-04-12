package looigi.loowebplayer.utilities;

import android.os.Handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;

public class GestioneFiles {
    //-------- Singleton ----------//
    private static GestioneFiles instance = null;
    private Runnable runCreaCartelle;
    private Handler hCreaCartelle;

    private GestioneFiles() {
    }

    public static GestioneFiles getInstance() {
        if (instance == null) {
            instance = new GestioneFiles();
        }

        return instance;
    }

    public void EliminaCartella(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                EliminaCartella(child);

        fileOrDirectory.delete();
    }

    public void CreaCartella(String PercorsoDIR) {
        // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Creazione cartella "+PercorsoDIR);

        File dDirectory = new File(PercorsoDIR);
        try {
            dDirectory.mkdirs();
        } catch (Exception e) {
            // VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            // e.printStackTrace();
            // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Creazione cartella. Errore: "+e.getMessage());
        }
    }

    public void CreaCartelle(final String Percorso) {
        hCreaCartelle = new Handler();
        hCreaCartelle.postDelayed(runCreaCartelle = new Runnable() {
            @Override
            public void run() {
                // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Creazione cartelle "+Percorso);
                String Campi[]=(Percorso+"/").split("/",-1);
                String ss="";

                for (String s : Campi) {
                    if (!s.isEmpty()) {
                        ss += "/" + s;
                        CreaCartella(ss);
                        if (!fileExistsInSD(".noMedia",ss )) {
                            // Crea file per nascondere alla galleria i files immagine della cartella
                            generateNoteOnSD(ss, ".noMedia","");
                        }
                    }
                }
            }
        }, 50);
    }

    private void generateNoteOnSD(String Percorso, String sFileName, String sBody) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Generazione file di testo: "+Percorso+sFileName);
        try {
            File gpxfile = new File(Percorso, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            // e.printStackTrace();
            // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Generazione file di testo. Errore: "+e.getMessage());
        }
    }

    public void EliminaFile(String path) {
        File f = new File(path);
        f.delete();
    }

    public String LeggeFileDiTesto(String path){
        // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Legge file di testo: "+path);
        File file = new File(path);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        }
        catch (IOException e) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Legge file di testo. Errore: "+ignored.getMessage());
        }

        return text.toString();
    }

    public void CreaFileDiTesto(String Percorso, String sFileName, String sBody) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Crea file di testo: "+Percorso+"/"+sFileName);
        try {
            File gpxfile = new File(Percorso, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            //VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Crea file di testo. Errore: "+e.getMessage());
        }
    }

    public boolean fileExistsInSD(String sFileName, String Percorso){
        // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Controllo esistenza file: "+Percorso+"/"+sFileName);
        String sFile=Percorso+"/"+sFileName;
        File file = new File(sFile);

        return file.exists();
    }

    public void copyFile(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }

    public List<String> RitornaListaDirectory(String Path) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorna lista directory: "+Path);
        List<String> Dirs = new ArrayList<>();

        File f = new File(Path);
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                Dirs.add(inFile.getName());
            }
        }

        return Dirs;
    }

    public List<String> RitornaListaFilesInDirectory(String Path) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritorno lista files in directory: "+Path);
        List<String> files = new ArrayList<>();

        File f = new File(Path);
        File[] ff = f.listFiles();
        for (File inFile : ff) {
            files.add(inFile.getName());
        }

        return files;
    }
}
