package looigi.loowebplayer.dati.dettaglio_dati;

import java.util.Date;

public class StrutturaFile {
    private String NomeFile;
    private Date DataFile;
    private Long Dimensione;

    public String getNomeFile() {
        return NomeFile;
    }

    public void setNomeFile(String nomeFile) {
        NomeFile = nomeFile;
    }

    public Date getDataFile() {
        return DataFile;
    }

    public void setDataFile(Date dataFile) {
        DataFile = dataFile;
    }

    public Long getDimensione() {
        return Dimensione;
    }

    public void setDimensione(Long dimensione) {
        Dimensione = dimensione;
    }
}
