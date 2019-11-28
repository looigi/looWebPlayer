package looigi.loowebplayer.dati.dettaglio_dati;

import java.util.ArrayList;
import java.util.List;

public class StrutturaArtisti {
    private int idArtista;
    private String Artista;
    private List<StrutturaMembri> Membri=new ArrayList<>();
    private List<StrutturaVideo> Video=new ArrayList<>();
    private List<StrutturaImmagini> Immagini=new ArrayList<>();
    private boolean Escluso;

    public boolean isEscluso() {
        return Escluso;
    }

    public void setEscluso(boolean escluso) {
        Escluso = escluso;
    }

    public int getIdArtista() {
        return idArtista;
    }

    public void setIdArtista(int idArtista) {
        this.idArtista = idArtista;
    }

    public List<StrutturaMembri> getMembri() {
        return Membri;
    }

    public List<StrutturaVideo> getVideo() {
        return Video;
    }

    public List<StrutturaImmagini> getImmagini() {
        return Immagini;
    }

    public void AggiungeImmagine(StrutturaImmagini Immagine) {
        this.Immagini.add(Immagine);
    }

    public void AggiungeVideo(StrutturaVideo Video) {
        this.Video.add(Video);
    }

    public void AggiungeMembro(StrutturaMembri Membri) {
        this.Membri.add(Membri);
    }

    public String getArtista() {
        return Artista;
    }

    public void setArtista(String artista) {
        Artista = artista;
    }
}
