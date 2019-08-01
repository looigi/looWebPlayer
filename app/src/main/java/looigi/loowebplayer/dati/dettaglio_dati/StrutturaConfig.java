package looigi.loowebplayer.dati.dettaglio_dati;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.ScaricoTesto;

import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.RANDOM;
import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.SEQUENZIALE;

public class StrutturaConfig {
    private boolean Random=true;
    private boolean CompressioneMP3=true;
    private String RapportoCompressione="96";
    private boolean SalvataggioOggetti=true;
    private String Filtro=""; // ARTISTA_Pippo_§BRANO_W la pippa_§
    private int QualeCanzoneStaSuonando;
    private int Ordinamento=0;
    private Boolean Crescente = true;
    private int NumeroBranoInAscolto;
    private Boolean MascheraTestoAperta=false;
    private Boolean SchermoSempreAcceso=true;
    private Boolean RicordaUltimoBrano=true;
    private Boolean TestoInInglese=true;
    private Boolean ScriveLog=true;
    private Boolean Membri=false;
    private Boolean DownloadImmagini=true;
    private Boolean ReloadAutomatico=true;
    private Boolean CaricamentoAnticipato=true;
    private Boolean AnnuncioBrano=true;
    private Boolean VisualizzaTraffico=true;
    private Boolean SfumaBrano=true;
    private Boolean ControlloRete=true;
    private Boolean VisualizzaBellezza=true;
    private Boolean UsaScaricati=false;
    private boolean ScaricoDettagli=true;
    private boolean Stelle=false;
    private boolean Superiore=true;
    private String ValoreBellezza="0";
    private Boolean PronunciaOperazioni=true;
    private int QuantiTentativi=3;
    private boolean PuliziaPerFiles = false;
    private boolean MostraOperazioni = false;
    private boolean PuliziaPerMega = false;
    private int QuantiFilesMemorizzati = 150;
    private int QuantiMBAlMassimo = 500;
    private boolean ScaricaTestoBrano = true;

    public void SalvaDati() {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Salva dati config");

        String pathConfig = VariabiliStaticheGlobali.getInstance().PercorsoDIR;
        String NomeFileConfig = "config.dat";

        String sRandom = "S"; if (!Random) sRandom="N";
        String sCompressione = "S"; if (!CompressioneMP3) sCompressione="N";
        String sSalva = "S"; if (!SalvataggioOggetti) sSalva="N";
        String sCrescente = "S"; if (!Crescente) sCrescente="N";
        String sMascheraTesto = "S"; if (!MascheraTestoAperta) sMascheraTesto="N";
        String sSchermoAcceso = "S"; if (!SchermoSempreAcceso) sSchermoAcceso="N";
        String sRicorda = "S"; if (!RicordaUltimoBrano) sRicorda="N";
        String sTestoInInglese = "S"; if (!TestoInInglese) sTestoInInglese="N";
        String sScriveLog = "S"; if (!ScriveLog) sScriveLog="N";
        String sDownloadImmagini = "S"; if (!DownloadImmagini) sDownloadImmagini="N";
        String sReloadAutomatico = "S"; if (!ReloadAutomatico) sReloadAutomatico="N";
        String sCaricamentoAnticipato = "S"; if (!CaricamentoAnticipato) sCaricamentoAnticipato="N";
        String sAnnuncioBrano = "S"; if (!AnnuncioBrano) sAnnuncioBrano="N";
        String sVisualizzaTraffico = "S"; if (!VisualizzaTraffico) sVisualizzaTraffico="N";
        String sVisualizzaBellezza = "S"; if (!VisualizzaBellezza) sVisualizzaBellezza="N";
        String sUsaScaricati = "S"; if (!UsaScaricati) sUsaScaricati="N";
        String sScaricoDettagli = "S"; if (!ScaricoDettagli) sScaricoDettagli="N";
        String sStelle = "S"; if (!Stelle) sStelle="N";
        String sSuperiore = "S"; if (!Superiore) sSuperiore="N";
        String sPronuncia = "S"; if (!PronunciaOperazioni) sPronuncia="N";
        String sMembri = "S"; if (!Membri) sMembri="N";
        String sSfumaBrano = "S"; if (!SfumaBrano) sSfumaBrano="S";
        String sControlloRete = "S"; if (!ControlloRete) sControlloRete="S";
        String toMP3 = Integer.toString(VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3());
        String toLB = Integer.toString(VariabiliStaticheGlobali.getInstance().getTimeOutListaBrani());
        String toAMP3 = Integer.toString(VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3());
        String toIMM = Integer.toString(VariabiliStaticheGlobali.getInstance().getTimeOutImmagini());
        String tipoSegnale = Integer.toString(VariabiliStaticheGlobali.getInstance().getTipoSegnale());
        String sPuliziaPerFiles = "S"; if (!PuliziaPerFiles) sPuliziaPerFiles="N";
        String sPuliziaPerMega = "S"; if (!PuliziaPerMega) sPuliziaPerMega="N";
        String QuantiFilesPulizia = Integer.toString(QuantiFilesMemorizzati);
        String QuantiMegaPulizia = Integer.toString(QuantiMBAlMassimo);
        String sMostraOperazioni = "S"; if (!MostraOperazioni) sMostraOperazioni="N";
        String sScaricaTesto = "S"; if (!ScaricaTestoBrano) sScaricaTesto="N";

        String Stringona = 
                sRandom + ";" +
                sCompressione + ";" +
                RapportoCompressione + ";" +
                sSalva + ";" +
                Filtro + ";" +
                QualeCanzoneStaSuonando + ";" +
                Ordinamento + ";" +
                sCrescente + ";" +
                NumeroBranoInAscolto + ";" +
                sMascheraTesto + ";" +
                sSchermoAcceso + ";" +
                sRicorda + ";" +
                sTestoInInglese + ";" +
                sScriveLog + ";" +
                sDownloadImmagini + ";" +
                sReloadAutomatico + ";" +
                sCaricamentoAnticipato + ";" +
                sAnnuncioBrano + ";" +
                sVisualizzaTraffico + ";" +
                sUsaScaricati + ";" +
                sScaricoDettagli + ";" +
                sStelle + ";" +
                sSuperiore + ";" +
                ValoreBellezza + ";" +
                QuantiTentativi + ";" +
                sPronuncia + ";" +
                sMembri + ";" +
                toMP3 +";"+
                toLB +";"+
                toAMP3 +";"+
                toIMM +";"+
                tipoSegnale+";"+
                sVisualizzaBellezza+";"+
                sSfumaBrano+";"+
                sControlloRete+";"+
                sPuliziaPerFiles+';'+
                QuantiFilesPulizia+';'+
                sPuliziaPerMega+';'+
                QuantiMegaPulizia+';'+
                sMostraOperazioni+';'+
                sScaricaTesto+';'
        ;
        GestioneFiles.getInstance().CreaFileDiTesto(pathConfig, NomeFileConfig, Stringona);
    }

    public void LeggeValori() {
        // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Legge dati config");

        String pathConfig = VariabiliStaticheGlobali.getInstance().PercorsoDIR +"/";
        String NomeFileConfig = "config.dat";
        
        File f = new File(pathConfig+NomeFileConfig);
        if (f.exists()) {
            String Stringona = GestioneFiles.getInstance().LeggeFileDiTesto(pathConfig+NomeFileConfig);
            if (!Stringona.trim().isEmpty()) {
                String Campi[] = Stringona.split(";", -1);

                try {
                    String sRandom = Campi[0];
                    String sCompressione = Campi[1];
                    RapportoCompressione = Campi[2];
                    String sSalva = Campi[3];
                    Filtro = Campi[4];
                    QualeCanzoneStaSuonando = Integer.parseInt(Campi[5]);
                    Ordinamento = Integer.parseInt(Campi[6]);
                    String sCrescente = Campi[7];
                    NumeroBranoInAscolto = Integer.parseInt(Campi[8]);
                    String sMascheraTesto = Campi[9];
                    String sSchermoAcceso = Campi[10];
                    String sRicorda = Campi[11];
                    String sTestoInInglese = Campi[12];
                    String sScriveLog = Campi[13];
                    String sDownloadImmagini = Campi[14];
                    String sReloadAutomatico = Campi[15];
                    String sCaricamentoAnticipato = Campi[16];
                    String sAnnuncioBrano = Campi[17];
                    String sVisualizzaTraffico = Campi[18];
                    String sUsaScaricati = Campi[19];
                    String sScaricoDettagli = Campi[20];
                    String sStelle = Campi[21];
                    String sSuperiore = Campi[22];
                    ValoreBellezza = Campi[23];
                    QuantiTentativi = Integer.parseInt(Campi[24]);
                    String sPronuncia = Campi[25];
                    String sMembri = Campi[26];

                    VariabiliStaticheGlobali.getInstance().setTimeOutDownloadMP3(Integer.parseInt(Campi[27]));
                    VariabiliStaticheGlobali.getInstance().setTimeOutListaBrani(Integer.parseInt(Campi[28]));
                    VariabiliStaticheGlobali.getInstance().setAttesaControlloEsistenzaMP3(Integer.parseInt(Campi[29]));
                    VariabiliStaticheGlobali.getInstance().setTimeOutImmagini(Integer.parseInt(Campi[30]));
                    VariabiliStaticheGlobali.getInstance().setTipoSegnale(Integer.parseInt(Campi[31]));

                    String sVisualizzaBellezza = Campi[32];
                    String sSfumaBrano = Campi[33];
                    String sControlloRete = Campi[34];

                    VisualizzaTraffico = sVisualizzaTraffico.equals("S");
                    DownloadImmagini = sDownloadImmagini.equals("S");
                    ReloadAutomatico = sReloadAutomatico.equals("S");
                    CaricamentoAnticipato = sCaricamentoAnticipato.equals("S");
                    AnnuncioBrano = sAnnuncioBrano.equals("S");
                    ScriveLog = sScriveLog.equals("S");
                    TestoInInglese = sTestoInInglese.equals("S");
                    RicordaUltimoBrano = sRicorda.equals("S");
                    Random = sRandom.equals("S");
                    CompressioneMP3 = sCompressione.equals("S");
                    SalvataggioOggetti = sSalva.equals("S");
                    Crescente = sCrescente.equals("S");
                    MascheraTestoAperta = sMascheraTesto.equals("S");
                    SchermoSempreAcceso = sSchermoAcceso.equals("S");
                    UsaScaricati = sUsaScaricati.equals("S");
                    ScaricoDettagli = sScaricoDettagli.equals("S");
                    Stelle = sStelle.equals("S");
                    Superiore = sSuperiore.equals("S");
                    PronunciaOperazioni = sPronuncia.equals("S");
                    Membri = sMembri.equals("S");
                    VisualizzaBellezza = sVisualizzaBellezza.equals("S");
                    SfumaBrano = sSfumaBrano.equals("S");
                    ControlloRete = sControlloRete.equals("S");

                    String sPuliziaPerFiles = Campi[35];
                    String sQuantiFilesPulizia = Campi[36];
                    String sPuliziaPerMega = Campi[37];
                    String sQuantiMegaPulizia = Campi[38];

                    PuliziaPerFiles = sPuliziaPerFiles.equals("S");
                    QuantiFilesMemorizzati = Integer.parseInt(sQuantiFilesPulizia);
                    PuliziaPerMega = sPuliziaPerMega.equals("S");
                    QuantiMBAlMassimo = Integer.parseInt(sQuantiMegaPulizia);

                    if (Random) {
                        GestioneListaBrani.getInstance().setModalitaAvanzamento(RANDOM);
                    } else {
                        GestioneListaBrani.getInstance().setModalitaAvanzamento(SEQUENZIALE);
                    }

                    String sMostraOperazioni = Campi[39];
                    MostraOperazioni = sMostraOperazioni.equals("S");
                    String sScaricoTesto = Campi[40];
                    ScaricaTestoBrano = sScaricoTesto.equals("S");

                    // NetThread.getInstance().StopNetThread();
                    // NetThread.getInstance().start();
                } catch (Exception e) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);

                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Legge dati config. Errore nella struttura del file, la ricreo");
                    SalvaDati();

                    // NetThread.getInstance().StopNetThread();
                    // NetThread.getInstance().start();
                }
            } else {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Legge dati config. Errore nella stringa vuota del file, lo ricreo");
                SalvaDati();

                // NetThread.getInstance().StopNetThread();
                // NetThread.getInstance().start();
            }
        } else {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Legge dati config. Errore nel file non esistente, lo ricreo");
            SalvaDati();

            // NetThread.getInstance().StopNetThread();
            // NetThread.getInstance().start();
        }

        // Equalizer.getInstance().CaricaValori();
    }

    public void CopiaBraniInFiltrati() {
        List<StrutturaBrani> b = new ArrayList<>();

        if (!Filtro.isEmpty()) {
            String Filtri[] = Filtro.split("§",-1);

            for (StrutturaBrani s : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBrani()) {
                for (String f : Filtri) {
                    String TipoFiltro[] = f.split("_",-1);
                    String Cosa = "";
                    if (TipoFiltro.length>1) {
                        Cosa = TipoFiltro[1].replace("***UNDERLINE***", "_").toUpperCase().trim();
                        Boolean Ok = false;

                        switch (TipoFiltro[0]) {
                            case "ARTISTA":
                                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista().toUpperCase().trim();
                                if (Artista.contains(Cosa)) {
                                    Ok = true;
                                }
                                break;
                            case "ALBUM":
                                String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum().toUpperCase().trim();
                                if (Album.contains(Cosa)) {
                                    Ok = true;
                                }
                                break;
                            case "BRANO":
                                if (s.getNomeBrano().toUpperCase().trim().contains(Cosa)) {
                                    Ok = true;
                                }
                                break;
                            case "TESTO":
                                if (s.getTesto().toUpperCase().contains(Cosa)) {
                                    Ok = true;
                                }
                                break;
                        }

                        if (Ok) {
                            b.add(s);
                            break;
                        }
                    }
                }
            }
        } else {
            b = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBrani();
        }

        VariabiliStaticheGlobali.getInstance().getDatiGenerali().setBraniFiltrati(b);
    }

    public void PrendeSoloBraniScaricati() {
        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getUsaScaricati()) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Prende brani scaricati");

            List<StrutturaBrani> b = new ArrayList<>();

            for (StrutturaBrani s : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati()) {
                String NomeBrano = s.getNomeBrano();
                String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
                String CompattazioneMP3 = VariabiliStaticheGlobali.EstensioneCompressione;
                if (VariabiliStaticheGlobali.getInstance().getUtente()!=null && !isCompressioneMP3()) {
                    CompattazioneMP3 = "";
                }
                String pathBase=VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
                String PathMP3 = VariabiliStaticheGlobali.getInstance().PercorsoDIR +"/Dati/"+ pathBase+"/"+Artista + "/" + Album + "/" + NomeBrano;
                String PathMP3_Compresso = VariabiliStaticheGlobali.getInstance().PercorsoDIR +"/Dati/"+ pathBase+"/"+Artista + "/" + Album + "/" + CompattazioneMP3 + NomeBrano;
                File f = new File(PathMP3);
                File fc = new File(PathMP3_Compresso);
                if (f.exists() || fc.exists()) {
                    b.add(s);
                }
            }

            VariabiliStaticheGlobali.getInstance().getDatiGenerali().setBraniFiltrati(b);
        }
    }

    public void OrdinaListaBrani() {
        if (GestioneListaBrani.getInstance().getModalitaAvanzamento()==GestioneListaBrani.ModiAvanzamento.SEQUENZIALE) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ordina lista brani");

            StrutturaBrani app = new StrutturaBrani();
            SimpleDateFormat dateForm = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

            switch (Ordinamento) {
                case 0:
                    // Nessuno
                    break;
                case 1:
                    // Data
                    for (StrutturaBrani b1 : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati()) {
                        for (StrutturaBrani b2 : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati()) {
                            if (b1.getIdBrano() != (b2.getIdBrano())) {
                                Boolean Ok = true;
                                Date d1 = null;
                                Date d2 = null;

                                try {
                                    d1 = dateForm.parse(b1.getDataCreazione());
                                } catch (ParseException e) {
                                    Ok = false;
                                }
                                if (Ok) {
                                    try {
                                        d2 = dateForm.parse(b2.getDataCreazione());
                                    } catch (ParseException e) {
                                        Ok = false;
                                    }

                                    if (Ok) {
                                        int o = d1.compareTo(d2);
                                        if (Crescente.equals("S")) {
                                            if (o > 0) {
                                                app = b1;
                                                b1 = b2;
                                                b2 = app;
                                            }
                                        } else {
                                            if (o < 0) {
                                                app = b1;
                                                b1 = b2;
                                                b2 = app;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 2:
                    // Alfabetico
                    for (StrutturaBrani b1 : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati()) {
                        for (StrutturaBrani b2 : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati()) {
                            if (b1.getIdBrano() != (b2.getIdBrano())) {
                                int o = b1.getNomeBrano().compareTo(b2.getNomeBrano());
                                if (Crescente.equals("S")) {
                                    if (o > 0) {
                                        app = b1;
                                        b1 = b2;
                                        b2 = app;
                                    }
                                } else {
                                    if (o < 0) {
                                        app = b1;
                                        b1 = b2;
                                        b2 = app;
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ordina lista brani. Fine");
        }
    }

    public void FiltraInBaseAlleStelle(){
        if (Stelle) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Prende brani filtrati per bellezza");

            List<StrutturaBrani> b = new ArrayList<>();

            for (StrutturaBrani s : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati()) {
                int Bellezza = s.getStelle();

                Boolean Ok = false;
                if (Superiore) {
                    if (Bellezza>Integer.parseInt(ValoreBellezza)-1) {
                        Ok=true;
                    }
                } else {
                    if (Bellezza==Integer.parseInt(ValoreBellezza)) {
                        Ok=true;
                    }
                }
                if (Ok) {
                    b.add(s);
                }
            }

            VariabiliStaticheGlobali.getInstance().getDatiGenerali().setBraniFiltrati(b);
        }
    }

    public boolean isScaricaTestoBrano() {
        return ScaricaTestoBrano;
    }

    public void setScaricaTestoBrano(boolean scaricaTestoBrano) {
        ScaricaTestoBrano = scaricaTestoBrano;
    }

    public boolean isMostraOperazioni() {
        return MostraOperazioni;
    }

    public void setMostraOperazioni(boolean mostraOperazioni) {
        MostraOperazioni = mostraOperazioni;
    }

    public boolean isPuliziaPerFiles() {
        return PuliziaPerFiles;
    }

    public void setPuliziaPerFiles(boolean puliziaPerFiles) {
        PuliziaPerFiles = puliziaPerFiles;
    }

    public boolean isPuliziaPerMega() {
        return PuliziaPerMega;
    }

    public void setPuliziaPerMega(boolean puliziaPerMega) {
        PuliziaPerMega = puliziaPerMega;
    }

    public int getQuantiMBAlMassimo() {
        return QuantiMBAlMassimo;
    }

    public void setQuantiMBAlMassimo(int quantiMBAlMassimo) {
        QuantiMBAlMassimo = quantiMBAlMassimo;
    }

    public int getQuantiFilesMemorizzati() {
        return QuantiFilesMemorizzati;
    }

    public void setQuantiFilesMemorizzati(int quantiFilesMemorizzati) {
        QuantiFilesMemorizzati = quantiFilesMemorizzati;
    }

    public Boolean getMembri() {
        return Membri;
    }

    public void setMembri(Boolean membri) {
        Membri = membri;
        SalvaDati();
    }

    public Boolean getPronunciaOperazioni() {
        return PronunciaOperazioni;
    }

    public void setPronunciaOperazioni(Boolean pronunciaOperazioni) {
        PronunciaOperazioni = pronunciaOperazioni;
        SalvaDati();
    }

    public int getQuantiTentativi() {
        return QuantiTentativi;
    }

    public void setQuantiTentativi(int quantiTentativi) {
        QuantiTentativi = quantiTentativi;
        SalvaDati();
    }

    public boolean isSuperiore() {
        return Superiore;
    }

    public void setSuperiore(boolean superiore) {
        Superiore = superiore;
        SalvaDati();
    }

    public boolean isStelle() {
        return Stelle;
    }

    public void setStelle(boolean stelle) {
        Stelle = stelle;
        SalvaDati();
    }

    public String getValoreBellezza() {
        return ValoreBellezza;
    }

    public void setValoreBellezza(String valoreBellezza) {
        ValoreBellezza = valoreBellezza;
        SalvaDati();
    }

    public boolean isScaricoDettagli() {
        return ScaricoDettagli;
    }

    public void setScaricoDettagli(boolean scaricoDettagli) {
        ScaricoDettagli = scaricoDettagli;
        SalvaDati();
    }

    public Boolean getUsaScaricati() {
        return UsaScaricati;
    }

    public void setUsaScaricati(Boolean usaScaricati) {
        UsaScaricati = usaScaricati;
        SalvaDati();
    }

    public Boolean getVisualizzaTraffico() {
        return VisualizzaTraffico;
    }

    public void setVisualizzaTraffico(Boolean visualizzaTraffico) {
        VisualizzaTraffico = visualizzaTraffico;
        SalvaDati();
    }

    public Boolean getSfumaBrano() {
        return SfumaBrano;
    }

    public void setSfumaBrano(Boolean sfumaBrano) {
        SfumaBrano = sfumaBrano;
        SalvaDati();
    }

    public Boolean getControlloRete() {
        return ControlloRete;
    }

    public void setControlloRete(Boolean controlloRete) {
        ControlloRete = controlloRete;
        SalvaDati();
    }

    public Boolean getVisualizzaBellezza() {
        return VisualizzaBellezza;
    }

    public void setVisualizzaBellezza(Boolean visualizzaBellezza) {
        VisualizzaBellezza = visualizzaBellezza;
        SalvaDati();
    }

    public Boolean getDownloadImmagini() {
        return DownloadImmagini;
    }

    public void setDownloadImmagini(Boolean downloadImmagini) {
        DownloadImmagini = downloadImmagini;
        SalvaDati();
    }

    public Boolean getReloadAutomatico() {
        return ReloadAutomatico;
    }

    public void setReloadAutomatico(Boolean reloadAutomatico) {
        ReloadAutomatico = reloadAutomatico;
        SalvaDati();
    }

    public Boolean getCaricamentoAnticipato() {
        return CaricamentoAnticipato;
    }

    public void setCaricamentoAnticipato(Boolean caricamentoAnticipato) {
        CaricamentoAnticipato = caricamentoAnticipato;
        SalvaDati();
    }

    public Boolean getAnnuncioBrano() {
        return AnnuncioBrano;
    }

    public void setAnnuncioBrano(Boolean annuncioBrano) {
        AnnuncioBrano = annuncioBrano;
        SalvaDati();
    }

    public Boolean getScriveLog() {
        return ScriveLog;
    }

    public void setScriveLog(Boolean scriveLog) {
        ScriveLog = scriveLog;
        SalvaDati();
    }

    public Boolean getTestoInInglese() {
        return TestoInInglese;
    }

    public void setTestoInInglese(Boolean testoInInglese) {
        TestoInInglese = testoInInglese;
        SalvaDati();
    }

    public Boolean getRicordaUltimoBrano() {
        return RicordaUltimoBrano;
    }

    public void setRicordaUltimoBrano(Boolean ricordaUltimoBrano) {
        RicordaUltimoBrano = ricordaUltimoBrano;
        SalvaDati();
    }

    public Boolean getSchermoSempreAcceso() {
        return SchermoSempreAcceso;
    }

    public void setSchermoSempreAcceso(Boolean schermoSempreAcceso) {
        SchermoSempreAcceso = schermoSempreAcceso;
        SalvaDati();
    }

    public Boolean getMascheraTestoAperta() {
        return MascheraTestoAperta;
    }

    public void setMascheraTestoAperta(Boolean mascheraTestoAperta) {
        MascheraTestoAperta = mascheraTestoAperta;
        SalvaDati();
    }

    public int getNumeroBranoInAscolto() {
        return NumeroBranoInAscolto;
    }

    public void setNumeroBranoInAscolto(int numeroBranoInAscolto) {
        NumeroBranoInAscolto = numeroBranoInAscolto;
        SalvaDati();
    }

    public int getOrdinamento() {
        return Ordinamento;
    }

    public void setOrdinamento(int ordinamento) {
        Ordinamento = ordinamento;
        SalvaDati();
    }

    public Boolean getCrescente() {
        return Crescente;
    }

    public void setCrescente(Boolean crescente) {
        Crescente = crescente;
        SalvaDati();
    }

    public String getFiltro() {
        return Filtro;
    }

    public void setFiltro(String filtro) {
        Filtro = filtro;
        SalvaDati();
    }

    public int getQualeCanzoneStaSuonando() {
        return QualeCanzoneStaSuonando;
    }

    public void setQualeCanzoneStaSuonando(int qualeCanzoneStaSuonando) {
        QualeCanzoneStaSuonando = qualeCanzoneStaSuonando;
        SalvaDati();
    }

    public boolean isRandom() {
        return Random;
    }

    public void setRandom(boolean random) {
        Random = random;
        SalvaDati();
    }

    public boolean isCompressioneMP3() {
        return CompressioneMP3;
    }

    public void setCompressioneMP3(boolean compressioneMP3) {
        CompressioneMP3 = compressioneMP3;
        SalvaDati();
    }

    public String getRapportoCompressione() {
        return RapportoCompressione;
    }

    public void setRapportoCompressione(String rapportoCompressione) {
        RapportoCompressione = rapportoCompressione;
        SalvaDati();
    }

    public boolean isSalvataggioOggetti() {
        return SalvataggioOggetti;
    }

    public void setSalvataggioOggetti(boolean salvataggioOggetti) {
        SalvataggioOggetti = salvataggioOggetti;
        SalvaDati();
    }
}
