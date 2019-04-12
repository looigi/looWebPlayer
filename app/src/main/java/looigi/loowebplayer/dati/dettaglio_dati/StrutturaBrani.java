package looigi.loowebplayer.dati.dettaglio_dati;

public class StrutturaBrani {
    private int idBrano;
    private int idAlbum;
    private int idArtista;
    private String NomeBrano;
    private long Dimensioni;
    private String Testo;
    private String TestoTradotto;
    private int Stelle;
    private int QuanteVolteAscoltato;
    private String DataCreazione;

    public int getIdBrano() {
        return idBrano;
    }

    public void setIdBrano(int idBrano) {
        this.idBrano = idBrano;
    }

    public String getDataCreazione() {
        return DataCreazione;
    }

    public void setDataCreazione(String dataCreazione) {
        DataCreazione = dataCreazione;
    }

    public int getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(int idAlbum) {
        this.idAlbum = idAlbum;
    }

    public int getIdArtista() {
        return idArtista;
    }

    public void setIdArtista(int idArtista) {
        this.idArtista = idArtista;
    }

    public String getNomeBrano() {
        return NomeBrano;
    }

    public void setNomeBrano(String nomeBrano) {
        NomeBrano = nomeBrano;
    }

    public long getDimensioni() {
        return Dimensioni;
    }

    public void setDimensioni(long dimensioni) {
        Dimensioni = dimensioni;
    }

    public String getTesto() {
        return Testo;
    }

    public void setTesto(String testo) {
        Testo = testo;
    }

    public String getTestoTradotto() {
        return TestoTradotto;
    }

    public void setTestoTradotto(String testoTradotto) {
        TestoTradotto = testoTradotto;
    }

    public int getStelle() {
        return Stelle;
    }

    public void setStelle(int stelle) {
        Stelle = stelle;
    }

    public int getQuanteVolteAscoltato() {
        return QuanteVolteAscoltato;
    }

    public void setQuanteVolteAscoltato(int quanteVolteAscoltato) {
        QuanteVolteAscoltato = quanteVolteAscoltato;
    }
}
