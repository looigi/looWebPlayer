package looigi.loowebplayer.dati.dettaglio_dati;

public class StrutturaVideo {
    private int idCartella;
    private String NomeVideo;
    private long Lunghezza;
    private String DataCreazione;

    public String getDataCreazione() {
        return DataCreazione;
    }

    public void setDataCreazione(String dataCreazione) {
        DataCreazione = dataCreazione;
    }

    public int getIdCartella() {
        return idCartella;
    }

    public void setIdCartella(int idCartella) {
        this.idCartella = idCartella;
    }

    public String getNomeVideo() {
        return NomeVideo;
    }

    public void setNomeVideo(String nomeVideo) {
        NomeVideo = nomeVideo;
    }

    public long getLunghezza() {
        return Lunghezza;
    }

    public void setLunghezza(long lunghezza) {
        Lunghezza = lunghezza;
    }
}
