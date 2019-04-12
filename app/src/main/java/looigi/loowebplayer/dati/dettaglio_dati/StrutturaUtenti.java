package looigi.loowebplayer.dati.dettaglio_dati;

public class StrutturaUtenti {
    private int idUtente;
    private String Utente;
    private String Password;
    private Boolean Amministratore;
    private String CartellaBase;
    // private Boolean CompressioneMP3=true;
    // private String Qualita="96";

    // public String getQualita() {
    //     return Qualita;
    // }
//
    // public void setQualita(String qualita) {
    //     Qualita = qualita;
    // }
//
    // public Boolean getCompressioneMP3() {
    //     return CompressioneMP3;
    // }
//
    // public void setCompressioneMP3(Boolean compressioneMP3) {
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

    public Boolean isAmministratore() {
        return Amministratore;
    }

    public void setAmministratore(Boolean amministratore) {
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
