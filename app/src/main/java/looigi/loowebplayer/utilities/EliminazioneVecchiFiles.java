package looigi.loowebplayer.utilities;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaConfig;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaFile;

public class EliminazioneVecchiFiles {
    private static int NumeroOperazione;

    private static class EliminaFilesAsync extends AsyncTask<String, Integer, String> {
        private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("EliminazioneVecchiFiles");
        private long MbSuDisco;
        private boolean CancellaVeramente = true;

        @Override
        protected String doInBackground(String... sUrl) {
            StrutturaConfig vg = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione();
            boolean perFiles = vg.isPuliziaPerFiles();
            boolean perMega = vg.isPuliziaPerMega();

            if (perFiles || perMega) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                }.getClass().getEnclosingMethod().getName(), "Eliminazione vecchi files: " + perFiles + "-" + perMega);

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
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(), "Controllo per files:" + (files.size()-1) + "/" + Quanti);

                        int DaEliminare = (files.size() - 1) - Quanti;
                        int Eliminati = 0;
                        for (StrutturaFile sf : filesDettagli) {
                            File f = new File(sf.getNomeFile());
                            if (f.exists()) {
                                if (CancellaVeramente) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                            }.getClass().getEnclosingMethod().getName(),
                                            "Eliminazione:" + f);

                                    f.delete();
                                }
                            }
                            f = new File(sf.getNomeFile());
                            if (f.exists()) {
                                if (CancellaVeramente) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                            }.getClass().getEnclosingMethod().getName(),
                                            "Eliminazione:" + f);

                                    f.delete();
                                }
                            }
                            Eliminati++;
                            if (Eliminati > DaEliminare) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                        }.getClass().getEnclosingMethod().getName(),
                                        "Blocco eliminazione:" + Eliminati + "/" + DaEliminare);

                                break;
                            }

                            Thread.yield();
                        }
                    }

                    // Poi elimina per dimensioni
                    if (perMega && MbSuDisco > QuantiMB) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                        }.getClass().getEnclosingMethod().getName(),
                                "Controllo per mega:" + MbSuDisco + "/" + QuantiMB);

                        for (StrutturaFile sf : filesDettagli) {
                            File f = new File(sf.getNomeFile());
                            if (f.exists()) {
                                if (CancellaVeramente) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                            }.getClass().getEnclosingMethod().getName(),
                                            "Eliminazione:" + f);

                                    f.delete();
                                }
                            }
                            f = new File(sf.getNomeFile() + ".dat");
                            if (f.exists()) {
                                if (CancellaVeramente) {
                                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                            }.getClass().getEnclosingMethod().getName(),
                                            "Eliminazione:" + f);

                                    f.delete();
                                }
                            }
                            long perc = (QuantiMB  * 15) / 100;
                            MbSuDisco -= (sf.getDimensione() / 1024 / 1024);
                            if (MbSuDisco < QuantiMB - perc) {
                                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                        }.getClass().getEnclosingMethod().getName(),
                                        "Blocco eliminazione:" + MbSuDisco + "/" + QuantiMB);

                                break;
                            }

                            Thread.yield();
                        }
                    }
                // }
            }

            return null;
        }

        private List<StrutturaFile> PrendeDettaglioBrani(List<File> files) {
            List<StrutturaFile> Ritorno = new ArrayList<>();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Prende dettaglio brani");

            for (File file : files) {
                StrutturaFile sf = new StrutturaFile();
                sf.setNomeFile(file.getAbsolutePath());
                sf.setDataFile(new Date(file.lastModified()));
                sf.setDimensione(file.length());
                MbSuDisco += file.length() / 1024 / 1024;

                Ritorno.add(sf);

                Thread.yield();
            }

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Prende dettaglio brani: " + Ritorno.size());

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

                    Thread.yield();
                }
            }

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Prende dettaglio brani: Fine lettura informazioni");

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
