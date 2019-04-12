package looigi.loowebplayer.dati.dettaglio_dati;

import java.util.Date;

public class StrutturaImmagini {
    private int idCartella;
    private String NomeImmagine;
    private long Lunghezza;
    private String dataCreazione;

    public String getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(String dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public int getIdCartella() {
        return idCartella;
    }

    public void setIdCartella(int idCartella) {
        this.idCartella = idCartella;
    }

    public String getNomeImmagine() {
        return NomeImmagine;
    }

    public void setNomeImmagine(String NomeImmagine) {
        this.NomeImmagine = NomeImmagine;
    }

    public long getLunghezza() {
        return Lunghezza;
    }

    public void setLunghezza(long lunghezza) {
        Lunghezza = lunghezza;
    }
}
