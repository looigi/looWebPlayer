package looigi.loowebplayer.dati.dettaglio_dati;

public class StrutturaOperazioneWEB {
    private int NumeroOperazione=0;
    private String Operazione="";
    private long OraIniziale;

    public long getOraIniziale() {
        return OraIniziale;
    }

    public void setOraIniziale(long oraIniziale) {
        OraIniziale = oraIniziale;
    }

    public int getNumeroOperazione() {
        return NumeroOperazione;
    }

    public void setNumeroOperazione(int numeroOperazione) {
        NumeroOperazione = numeroOperazione;
    }

    public String getOperazione() {
        return Operazione;
    }

    public void setOperazione(String operazione) {
        Operazione = operazione;
    }
}
