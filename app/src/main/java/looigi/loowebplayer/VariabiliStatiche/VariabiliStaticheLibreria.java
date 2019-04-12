package looigi.loowebplayer.VariabiliStatiche;

import android.support.v7.widget.RecyclerView;

public class VariabiliStaticheLibreria {
    //-------- Singleton ----------//
    private static VariabiliStaticheLibreria instance = null;

    private VariabiliStaticheLibreria() {
    }

    public static VariabiliStaticheLibreria getInstance() {
        if (instance == null) {
            instance = new VariabiliStaticheLibreria();
        }

        return instance;
    }

    private RecyclerView ricArtisti;
    private RecyclerView ricAlbum;
    private RecyclerView ricBrani;

    public RecyclerView getRicArtisti() {
        return ricArtisti;
    }

    public void setRicArtisti(RecyclerView ricArtisti) {
        this.ricArtisti = ricArtisti;
    }

    public RecyclerView getRicAlbum() {
        return ricAlbum;
    }

    public void setRicAlbum(RecyclerView ricAlbum) {
        this.ricAlbum = ricAlbum;
    }

    public RecyclerView getRicBrani() {
        return ricBrani;
    }

    public void setRicBrani(RecyclerView ricBrani) {
        this.ricBrani = ricBrani;
    }
}
