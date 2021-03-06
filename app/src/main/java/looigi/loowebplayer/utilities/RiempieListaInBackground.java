package looigi.loowebplayer.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.io.File;
import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaArtisti;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAscoltate;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBellezza;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaConfig;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaImmagini;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaMembri;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaVideo;
import looigi.loowebplayer.db_locale.DBLocaleEsclusi;
import looigi.loowebplayer.db_locale.db_dati;

public class RiempieListaInBackground {
    private static boolean effettuaLogQui = false;
    private Context context;
    private static ProgressDialog progressDialog;
    private String Operazione;
    private static int NumeroOperazione;
    private static boolean SoloCaricamento;

    public void setContext(Context context) {
        this.context = context;
    }

    public void start(String Operazione, boolean SoloCaricamento) {
        this.Operazione=Operazione;
        this.SoloCaricamento = SoloCaricamento;

        ApriDialog();

        FillList fill = new FillList();
        fill.execute();
    }

    private static void ChiudeDialog() {
        try {
            progressDialog.dismiss();
        } catch (Exception ignored) {
        }
        // VariabiliStaticheHome.getInstance().getLayOperazionWEB().setVisibility(LinearLayout.GONE);
        VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
    }

    private void ApriDialog() {
        try {
            progressDialog = new ProgressDialog(VariabiliStaticheGlobali.getInstance().getContext());
            progressDialog.setMessage("Attendere Prego\n"+"Riempimento lista brani");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        } catch (Exception ignored) {

        }

        NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Riempimento lista brani");
    }

    public void RiempieStrutture(boolean ForzaCaricamento, boolean SoloCar) {
        if (!VariabiliStaticheGlobali.getInstance().getGiaEntrato() || ForzaCaricamento) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
            }.getClass().getEnclosingMethod().getName(), "Inizio Riempio strutture");
            RiempieListaInBackground r = new RiempieListaInBackground();
            r.setContext(VariabiliStaticheGlobali.getInstance().getContext());
            r.start("Caricamento lista mp3", SoloCar);
        }
    }

    private static class FillList extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... sUrl) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Riempie lista in background");
            VariabiliStaticheGlobali vg = VariabiliStaticheGlobali.getInstance();
            String path=VariabiliStaticheGlobali.getInstance().PercorsoDIR+"/Lista.dat";
            String filone=GestioneFiles.getInstance().LeggeFileDiTesto(path);
            String tipologia="";
            String[] Oggetti=filone.split("ç",-1);
            int idBrano=0;
            int idArtista=0;

            vg.getDatiGenerali().PulisceTutto();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Riempie lista in background. Oggetti: "+Integer.toString(Oggetti.length));
            for (String oggetto : Oggetti) {
                if (!oggetto.isEmpty()) {
                    String[] righe = oggetto.split("§",-1);

                    tipologia = righe[0].replace("***","");
                    for (String riga : righe) {
                        if (!riga.isEmpty() && !riga.contains("***")) {
                            String[] campi = riga.split(";", -1);
                            switch (tipologia) {
                                case "DIRECTORY PRINCIPALE":
                                    if (vg.getUtente()!=null) {
                                        vg.getUtente().setCartellaBase(campi[0]);
                                    }
                                    break;
                                case "DIRECTORIES":
                                    StrutturaAlbum album = new StrutturaAlbum();
                                    album.setIdArtista(Integer.parseInt(campi[0]));
                                    album.setIdAlbum(vg.getDatiGenerali().RitornaQuantiAlbum());
                                    album.setNomeAlbum(campi[1]);

                                    vg.getDatiGenerali().AggiungeAlbum(album);
                                    break;
                                case "ARTISTI":
                                    StrutturaArtisti Artista=new StrutturaArtisti();
                                    Artista.setIdArtista(idArtista);
                                    Artista.setArtista(campi[0]);

                                    vg.getDatiGenerali().AggiungeArtista(Artista);
                                    idArtista++;
                                    break;
                                case "MP3":
                                    StrutturaBrani Brano=new StrutturaBrani();
                                    Brano.setIdBrano(idBrano);
                                    Brano.setIdAlbum(Integer.parseInt(campi[0]));
                                    Brano.setIdArtista(Integer.parseInt(campi[1]));
                                    Brano.setNomeBrano(campi[2]);
                                    if (Utility.getInstance().isNumeric(campi[3])) {
                                        Brano.setDimensioni(Long.parseLong(campi[3]));
                                    } else {
                                        Brano.setDimensioni(0);
                                    }
                                    Brano.setTesto(campi[4].replace("**PV**",";").replace("**A CAPO**","§"));
                                    Brano.setTestoTradotto(campi[5].replace("**PV**",";").replace("**A CAPO**","§"));
                                    if (!campi[6].isEmpty()) {
                                        Brano.setQuanteVolteAscoltato(Integer.parseInt(campi[6]));
                                    } else {
                                        Brano.setQuanteVolteAscoltato(0);
                                    }
                                    if (!campi[7].isEmpty()) {
                                        Brano.setStelle(Integer.parseInt(campi[7]));
                                    } else {
                                        Brano.setStelle(0);
                                    }
                                    Brano.setDataCreazione(campi[8]);
                                    idBrano++;

                                    vg.getDatiGenerali().AggiungeBrano(Brano);
                                    break;
                                case "VIDEO":
                                    int idArtistaV=Integer.parseInt(campi[1]);

                                    StrutturaVideo Video=new StrutturaVideo();
                                    Video.setIdCartella(Integer.parseInt(campi[0]));
                                    Video.setNomeVideo(campi[2]);
                                    Video.setLunghezza(Long.parseLong(campi[3]));
                                    Video.setDataCreazione(campi[4]);

                                    vg.getDatiGenerali().RitornaArtista(idArtistaV).AggiungeVideo(Video);
                                    break;
                                case "IMMAGINI":
                                    int idArtistaI=Integer.parseInt(campi[1]);

                                    StrutturaImmagini Immagine=new StrutturaImmagini();
                                    Immagine.setIdCartella(Integer.parseInt(campi[0]));
                                    Immagine.setNomeImmagine(campi[2]);
                                    try {
                                        Immagine.setLunghezza(Long.parseLong(campi[3]));
                                    } catch (Exception ignored) {

                                    }
                                    Immagine.setDataCreazione(campi[4]);

                                    vg.getDatiGenerali().RitornaArtista(idArtistaI).AggiungeImmagine(Immagine);
                                    break;
                                case "MEMBRI":
                                    int idArtistaM=Integer.parseInt(campi[1]);

                                    String[] r = riga.split("\\|",-1);

                                    for (String rr : r) {
                                        String[] rrr = rr.split(";",-1);

                                        if (rrr.length>4) {
                                            StrutturaMembri Membro = new StrutturaMembri();
                                            Membro.setMembro(rrr[3]);
                                            Membro.setDurata(rrr[4]);
                                            Membro.setAttuale(rrr[5]);

                                            vg.getDatiGenerali().RitornaArtista(idArtistaM).AggiungeMembro(Membro);
                                        }
                                    }
                                    break;
                            }
                            // }
                            // }
                        }
                    }
                    // }
                }
            }

            // Riempie la struttura con eventuali files dat contenenti testo e stelle
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass()
                    .getEnclosingMethod().getName(),
                    "Riempie lista in background. Riempie la struttura con eventuali files di testo relativi alla bellezza e al testo");
            for (StrutturaBrani s : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBrani()) {
                String NomeBrano = s.getNomeBrano();
                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
                if (VariabiliStaticheGlobali.getInstance().getUtente()!=null) {
                    String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
                    NomeBrano += ".dat";
                    String PathMP3 = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + NomeBrano;
                    File f = new File(PathMP3);
                    if (f.exists()) {
                        String riga = GestioneFiles.getInstance().LeggeFileDiTesto(PathMP3);
                        String[] Campi = riga.split(";");
                        if (Campi.length > 3) {
                            s.setTesto(Campi[0]);
                            s.setTestoTradotto(Campi[1]);
                            s.setQuanteVolteAscoltato(Integer.parseInt(Campi[2]));
                            s.setStelle(Integer.parseInt(Campi[3]));
                        }
                    }
                }
            }
            // Riempie la struttura con eventuali files dat contenenti testo e stelle

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Riempie lista in background. Riempie la struttura con eventuali files di testo relativi al multimedia creati in precedenza");
            // Riempie la struttura con eventuali files di testo relativi al multimedia creati in precedenza
            for (StrutturaArtisti a : vg.getDatiGenerali().RitornaTuttiGliArtisti()) {
                if (VariabiliStaticheGlobali.getInstance().getUtente()!=null) {
                    String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
                    String fileMultimediaArtista = "";
                    if (!pathBase.equals(a.getArtista())) {
                        fileMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + a.getArtista() + "/ListaImmagini.dat";
                    } else {
                        fileMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/ListaImmagini.dat";
                    }
                    File f = new File(fileMultimediaArtista);
                    if (f.exists()) {
                        String[] Immagini = GestioneFiles.getInstance().LeggeFileDiTesto(fileMultimediaArtista).split("§", -1);
                        for (String i : Immagini) {
                            if (!i.isEmpty()) {
                                String[] campi = i.split(";", -1);

                                if (campi.length > 2) {
                                    try {
                                        StrutturaImmagini ii = new StrutturaImmagini();
                                        ii.setIdCartella(-1);
                                        ii.setNomeImmagine(campi[0]);
                                        ii.setLunghezza(Integer.parseInt(campi[1]));

                                        a.getImmagini().add(ii);
                                    } catch(Exception ignored) {

                                    }
                                }
                            }
                        }
                    }

                    fileMultimediaArtista = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/Video/" + pathBase + "/" + a.getArtista() + "/ListaVideo.dat";

                    f = new File(fileMultimediaArtista);
                    if (f.exists()) {
                        String[] Immagini = GestioneFiles.getInstance().LeggeFileDiTesto(fileMultimediaArtista).split("§", -1);
                        for (String i : Immagini) {
                            if (!i.isEmpty()) {
                                String[] campi = i.split(";", -1);

                                StrutturaVideo vv = new StrutturaVideo();
                                vv.setIdCartella(-1);
                                vv.setNomeVideo(campi[1]);
                                vv.setLunghezza(Integer.parseInt(campi[2]));

                                a.getVideo().add(vv);
                            }
                        }
                    }
                }
            }
            // Riempie la struttura con eventuali files di testo relativi al multimedia creati in precedenza

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Riempie lista in background. Copia brani filtrati");
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().CopiaBraniInFiltrati();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Riempie lista in background. Ordina lista brani");
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().OrdinaListaBrani();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Riempie lista in background. Prende solo brani scaricati");
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().PrendeSoloBraniScaricati();

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Riempie lista in background. Prende solo brani filtrati per stelle");
            VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().FiltraInBaseAlleStelle();

            // Aggiorna i dati caricati da remoto con quelli in locale
            db_dati db = new db_dati();
            List<StrutturaBellezza> lb = db.RitornaTuttiDatiBellezza();
            List<StrutturaAscoltate> la = db.RitornaTuttiDatiAscoltate();
            if (lb != null) {
                for (StrutturaBellezza b : lb) {
                    if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(b.getIdCanzone())!=null) {
                        VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(b.getIdCanzone()).setStelle(b.getBellezza());
                    }
                }
            }
            if (la != null) {
                for (StrutturaAscoltate a : la) {
                    if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(a.getIdCanzone())!=null) {
                        VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(a.getIdCanzone()).setQuanteVolteAscoltato(a.getQuante());
                    }
                }
            }
            // Aggiorna i dati caricati da remoto con quelli in locale

            // Aggiorna le esclusioni
            DBLocaleEsclusi dbe = new DBLocaleEsclusi();
            Cursor c = dbe.ottieniTutteEsclusioni();
            if (c.moveToFirst()) {
                do {
                    String Artista = c.getString(1);
                    String Album = c.getString(2);
                    String Brano = c.getString(3);

                    for (StrutturaBrani s : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBrani()) {
                        String sBrano = s.getNomeBrano();
                        String sArtista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                        String sAlbum = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
                        int idArt = s.getIdArtista();
                        int idAlb = s.getIdAlbum();
                        int idBra = s.getIdBrano();

                        if (!Artista.isEmpty() && !Album.isEmpty() && !Brano.isEmpty()) {
                            if (Artista.equals(sArtista) && Album.equals(sAlbum) && Brano.equals(sBrano)) {
                                s.setEscluso(true);
                                VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaBrano(idBra, s);
                            }
                        } else {
                            if (!Artista.isEmpty() && !Album.isEmpty()) {
                                if (Artista.equals(sArtista) && Album.equals(sAlbum)) {
                                    StrutturaAlbum sa = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlb);
                                    sa.setEscluso(true);
                                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaAlbum(idAlb, sa);
                                }
                            } else {
                                if (!Artista.isEmpty()) {
                                    if (Artista.equals(sArtista)) {
                                        StrutturaArtisti sa = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArt);
                                        sa.setEscluso(true);
                                        VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaArtista(idArt, sa);
                                    }
                                }
                            }
                        }
                    }
                } while (c.moveToNext());
            }
            // Aggiorna le esclusioni

            VariabiliStaticheGlobali.getInstance().getDatiGenerali().setValoriCaricati(true);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    "Riempie lista in background. Fatto");
            StrutturaConfig vg = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione();

            ChiudeDialog();

            if (!SoloCaricamento) {
                if (VariabiliStaticheGlobali.getInstance().getDisegnaMascheraHomeCompleta()) {
                    if (vg.getQualeCanzoneStaSuonando() == -1) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                                }.getClass().getEnclosingMethod().getName(),
                                "Riempie lista in background. Imposta canzone visto che è nulla");
                        int NumeroBrano;
                        if (vg.getRicordaUltimoBrano() &&
                                vg.getNumeroBranoInAscolto() > -1) {
                            NumeroBrano = vg.getNumeroBranoInAscolto();
                            // GestioneListaBrani.getInstance().AggiungeBrano(NumeroBrano);
                        } else {
                            NumeroBrano = GestioneListaBrani.getInstance().RitornaNumeroProssimoBranoNuovo(true, true);
                        }
                        vg.setQualeCanzoneStaSuonando(NumeroBrano);
                    }

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
                            }.getClass().getEnclosingMethod().getName(),
                            "Riempie lista in background. CaricaBrano in Home");
                    GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano();
                }
            }
        }
    }
}