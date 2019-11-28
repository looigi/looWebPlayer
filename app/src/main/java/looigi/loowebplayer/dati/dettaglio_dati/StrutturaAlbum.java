package looigi.loowebplayer.dati.dettaglio_dati;

public class StrutturaAlbum {
    private int idArtista;
    private int idAlbum;
    private String NomeAlbum;
    private boolean Escluso;

    public boolean isEscluso() {
        return Escluso;
    }

    public void setEscluso(boolean escluso) {
        Escluso = escluso;
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

    public String getNomeAlbum() {
        return NomeAlbum;
    }

    public void setNomeAlbum(String nomeAlbum) {
        NomeAlbum = nomeAlbum;
    }
}
