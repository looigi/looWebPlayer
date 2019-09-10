package looigi.loowebplayer.dati;

import java.util.ArrayList;
import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaArtisti;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaConfig;

public class DatiGenerali {
    private boolean ValoriCaricati=false;
    private List<StrutturaBrani> Brani=new ArrayList<>();
    private List<StrutturaAlbum> Album =new ArrayList<>();
    private List<StrutturaArtisti> Artisti=new ArrayList<>();
    private List<StrutturaBrani> BraniFiltrati=new ArrayList<>();
    // private List<StrutturaArtisti> ArtistiFiltrati=new ArrayList<>();
    // private List<StrutturaAlbum> AlbumFiltrati =new ArrayList<>();
    private StrutturaConfig Configurazione=new StrutturaConfig();

    public void PulisceTutto() {
        ValoriCaricati=false;

        Brani=new ArrayList<>();
        Album =new ArrayList<>();
        Artisti=new ArrayList<>();
        // ArtistiFiltrati=new ArrayList<>();
        BraniFiltrati=new ArrayList<>();
        // AlbumFiltrati =new ArrayList<>();
    }

    public List<StrutturaBrani> getBrani() {
        return Brani;
    }

    public void setBraniFiltrati(List<StrutturaBrani> braniFiltrati) {
        BraniFiltrati = braniFiltrati;
    }

    public List<StrutturaBrani> getBraniFiltrati() {
        return BraniFiltrati;
    }

    public StrutturaConfig getConfigurazione() {
        return Configurazione;
    }

    public void setConfigurazione(StrutturaConfig configurazione) {
        Configurazione = configurazione;
    }

    public boolean getValoriCaricati() {
        return ValoriCaricati;
    }

    public void setValoriCaricati(boolean valoriCaricati) {
        ValoriCaricati = valoriCaricati;
    }

    public void AggiungeAlbum(StrutturaAlbum Cartella) {
        Album.add(Cartella);
    }

    public void AggiungeBrano(StrutturaBrani Brano) {
        Brani.add(Brano);
    }

    public void AggiungeArtista(StrutturaArtisti Artista) {
        Artisti.add(Artista);
    }

    public StrutturaBrani RitornaBrano(int Indice) {
        if (Indice<BraniFiltrati.size() && Indice>-1) {
            return BraniFiltrati.get(Indice);
        } else {
            return null;
        }
    }

    public StrutturaAlbum RitornaAlbum(int Indice) {
        return Album.get(Indice);
    }

    public int RitornaQuantiAlbum() {
        return Album.size();
    }

    public int RitornaQuantiBrani() {
        return BraniFiltrati.size()-1;
    }

    public int RitornaQuantiArtisti() {
        return Artisti.size()-1;
    }

    // public StrutturaAlbum RitornaAlbumFiltrato(int Indice) {
    //     return AlbumFiltrati.get(Indice);
    // }

    public List<StrutturaAlbum> RitornaListaAlbum(int idArtista) {
        List<StrutturaAlbum> a = new ArrayList<>();

        for (StrutturaBrani s : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati()) {
            if (s.getIdArtista()==idArtista) {
                int idAlbum = s.getIdAlbum();
                StrutturaAlbum aa = Album.get(idAlbum);
                boolean Ok = true;
                for (StrutturaAlbum aaa : a) {
                    if (aaa.getNomeAlbum().equals(aa.getNomeAlbum())) {
                        Ok = false;
                        break;
                    }
                }
                if (Ok) {
                    a.add(aa);
                }
            }
        }

        // AlbumFiltrati = a;

        return a;
    }

    public List<StrutturaBrani> RitornaListaBrani(int idArtista, int idAlbum) {
        List<StrutturaBrani> lista = new ArrayList<>();

        for (StrutturaBrani a : BraniFiltrati) {
            if (a.getIdArtista()==idArtista && a.getIdAlbum()==idAlbum) {
                lista.add(a);
            }
        }

        return lista;
    }

    public StrutturaArtisti RitornaArtista(int Indice) {
        return Artisti.get(Indice);
    }

    // public StrutturaArtisti RitornaArtistaFiltrato(int Indice) {
    //     return ArtistiFiltrati.get(Indice);
    // }

    public List<StrutturaArtisti> RitornaTuttiGliArtisti() {
        return Artisti;
    }

    public List<StrutturaArtisti> RitornaArtisti() {
        List<StrutturaArtisti> a = new ArrayList<>();

        for (StrutturaBrani s : VariabiliStaticheGlobali.getInstance().getDatiGenerali().getBraniFiltrati()) {
            int idArtista = s.getIdArtista();
            StrutturaArtisti aa = Artisti.get(idArtista);
            boolean Ok = true;
            for (StrutturaArtisti aaa : a) {
                if (aaa.getArtista().equals(aa.getArtista())) {
                    Ok=false;
                    break;
                }
            }
            if (Ok) {
                a.add(aa);
            }
        }

        // ArtistiFiltrati = a;

        return a;
    }
}
