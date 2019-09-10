package looigi.loowebplayer.dati.dettaglio_dati;

public class StrutturaUtenti {
    private int idUtente;
    private String Utente;
    private String Password;
    private boolean Amministratore;
    private String CartellaBase;
    // private boolean CompressioneMP3=true;
    // private String Qualita="96";

    // public String getQualita() {
    //     return Qualita;
    // }
//
    // public void setQualita(String qualita) {
    //     Qualita = qualita;
    // }
//
    // public boolean getCompressioneMP3() {
    //     return CompressioneMP3;
    // }
//
    // public void setCompressioneMP3(boolean compressioneMP3) {
    //     CompressioneMP3 = compressioneMP3;
    // }

    public String getCartellaBase() {
        return CartellaBase;
    }

    public void setCartellaBase(String cartellaBase) {
        CartellaBase = cartellaBase;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isAmministratore() {
        return Amministratore;
    }

    public void setAmministratore(boolean amministratore) {
        Amministratore = amministratore;
    }

    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public String getUtente() {
        return Utente;
    }

    public void setUtente(String utente) {
        Utente = utente;
    }
}
