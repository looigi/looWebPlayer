package looigi.loowebplayer.utilities;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaConfig;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaFile;

public class EliminazioneVecchiFiles {
    private static int NumeroOperazione;

    private static class EliminaFilesAsync extends AsyncTask<String, Integer, String> {
        private long MbSuDisco;
        private boolean CancellaVeramente = false;

        @Override
        protected String doInBackground(String... sUrl) {
            StrutturaConfig vg = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione();
            boolean perFiles = vg.isPuliziaPerFiles();
            boolean perMega = vg.isPuliziaPerMega();

            if (perFiles || perMega) {
                int Quanti = vg.getQuantiFilesMemorizzati();
                if (!perFiles) Quanti = 0;
                int QuantiMB = vg.getQuantiMBAlMassimo();
                if (!perMega) QuantiMB = 0;
                MbSuDisco = 0;
                String Path = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/";
                List<File> files = GestioneFiles.getInstance().ScansionaDirectory(new File(Path));
                List<StrutturaFile> filesDettagli = PrendeDettaglioBrani(files);
                // if (files.size() - 1 > Quanti || MbSuDisco > QuantiMB) {
                    // Prima elimina per numero di files
                    if (perFiles && (files.size() - 1) > Quanti) {
                        int DaEliminare = (files.size() - 1) - Quanti;
                        int Eliminati = 0;
                        for (StrutturaFile sf : filesDettagli) {
                            File f = new File(sf.getNomeFile());
                            if (f.exists()) {
                                if (CancellaVeramente) {
                                    f.delete();
                                }
                            }
                            f = new File(sf.getNomeFile() + ".dat");
                            if (f.exists()) {
                                if (CancellaVeramente) {
                                    f.delete();
                                }
                            }
                            Eliminati++;
                            if (Eliminati > DaEliminare) {
                                break;
                            }
                        }
                    }

                    // Poi elimina per dimensioni
                    if (perMega && MbSuDisco > QuantiMB) {
                        for (StrutturaFile sf : filesDettagli) {
                            File f = new File(sf.getNomeFile());
                            if (f.exists()) {
                                if (CancellaVeramente) {
                                    f.delete();
                                }
                            }
                            f = new File(sf.getNomeFile() + ".dat");
                            if (f.exists()) {
                                if (CancellaVeramente) {
                                    f.delete();
                                }
                            }
                            MbSuDisco -= (sf.getDimensione() / 1024 / 1024);
                            if (MbSuDisco < QuantiMB) {
                                break;
                            }
                        }
                    }
                // }
            }

            return null;
        }

        private List<StrutturaFile> PrendeDettaglioBrani(List<File> files) {
            List<StrutturaFile> Ritorno = new ArrayList<>();

            for (File file : files) {
                StrutturaFile sf = new StrutturaFile();
                sf.setNomeFile(file.getAbsolutePath());
                sf.setDataFile(new Date(file.lastModified()));
                sf.setDimensione(file.length());
                MbSuDisco += file.length() / 1024 / 1024;

                Ritorno.add(sf);
            }

            for (int i=0; i<Ritorno.size(); i++) {
                for (int k=0; k<Ritorno.size(); k++) {
                    if (i!=k) {
                        long dime1 = Ritorno.get(i).getDimensione();
                        long dime2 = Ritorno.get(k).getDimensione();
                        Date data1 = Ritorno.get(i).getDataFile();
                        Date data2 = Ritorno.get(k).getDataFile();
                        if (data1.compareTo(data2) < 0) {
                            StrutturaFile app = Ritorno.get(i);
                            Ritorno.set(i, Ritorno.get(k));
                            Ritorno.set(k, app);
                        }
                    }
                }
            }

            return Ritorno;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
        }

    }

    public void EliminaFilesSeMaggioriAlNumeroImpostato() {
        NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
                "Controllo pulizia disco");

        EliminaFilesAsync fill = new EliminaFilesAsync();
        fill.execute();
    }
}
