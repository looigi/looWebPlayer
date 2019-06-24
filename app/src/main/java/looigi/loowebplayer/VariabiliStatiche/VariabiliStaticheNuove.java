package looigi.loowebplayer.VariabiliStatiche;

import looigi.loowebplayer.soap.AttesaScaricamentoBrano;
import looigi.loowebplayer.soap.CheckURLFile;
import looigi.loowebplayer.soap.DownloadMP3Nuovo;
import looigi.loowebplayer.soap.GestioneWEBServiceSOAPNuovo;
import looigi.loowebplayer.utilities.GestioneTesti;
import looigi.loowebplayer.utilities.ScaricoCover;

public class VariabiliStaticheNuove {
    //-------- Singleton ----------//
    private static VariabiliStaticheNuove instance = null;

    private VariabiliStaticheNuove() {
    }

    public static VariabiliStaticheNuove getInstance() {
        if (instance == null) {
            instance = new VariabiliStaticheNuove();
        }

        return instance;
    }

    private ScaricoCover sc;
    private GestioneTesti gt;
    private GestioneWEBServiceSOAPNuovo gm;
    // private AttesaScaricamentoBrano gb;
    // private DownloadMP3Nuovo d;
    // private GestioneWEBServiceSOAPNuovo db;
    // private DownloadMP3Nuovo d2;
    // private CheckURLFile cuf;

    // public CheckURLFile getCuf() {
    //     return cuf;
    // }

    // public void setCuf(CheckURLFile cuf) {
    //     this.cuf = cuf;
    // }

    public ScaricoCover getSc() {
        return sc;
    }

    public void setSc(ScaricoCover sc) {
        this.sc = sc;
    }

    public GestioneTesti getGt() {
        return gt;
    }

    public void setGt(GestioneTesti gt) {
        this.gt = gt;
    }

    public GestioneWEBServiceSOAPNuovo getGm() {
        return gm;
    }

    public void setGm(GestioneWEBServiceSOAPNuovo gm) {
        this.gm = gm;
    }

    /* public AttesaScaricamentoBrano getGb() {
        return gb;
    }

    public void setGb(AttesaScaricamentoBrano gb) {
        this.gb = gb;
    } */

    // public DownloadMP3Nuovo getD() {
    //     return d;
    // }

    // public void setD(DownloadMP3Nuovo d) {
    //     this.d = d;
    // }

    /* public GestioneWEBServiceSOAPNuovo getDb() {
        return db;
    }

    public void setDb(GestioneWEBServiceSOAPNuovo db) {
        this.db = db;
    } */

    // public DownloadMP3Nuovo getD2() {
    //     return d2;
    // }

    // public void setD2(DownloadMP3Nuovo d2) {
    //     this.d2 = d2;
    // }
}
