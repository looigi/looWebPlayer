/* package looigi.loowebplayer.VariabiliStatiche;

import android.media.audiofx.Equalizer;

public class VariabiliStaticheEqualizzatore {
    private static VariabiliStaticheEqualizzatore instance = null;

    public VariabiliStaticheEqualizzatore() {
    }

    public static VariabiliStaticheEqualizzatore getInstance() {
        if (instance == null) {
            instance = new VariabiliStaticheEqualizzatore();
        }

        return instance;
    }

    private int[] Valori=new int[8];
    private int BassBoost=0;
    private Boolean Abilitato=true;
    private android.media.audiofx.Equalizer eq;
    private android.media.audiofx.BassBoost bb = null;

    public void AggiornaValore(int Quale, int Valore) {
        this.Valori[Quale]=Valore;
    }

    public android.media.audiofx.BassBoost getBb() {
        return bb;
    }

    public void setBb(android.media.audiofx.BassBoost bb) {
        this.bb = bb;
    }

    public int[] getValori() {
        return Valori;
    }

    public void setValori(int[] valori) {
        Valori = valori;
    }

    public int getBassBoost() {
        return BassBoost;
    }

    public void setBassBoost(int bassBoost) {
        BassBoost = bassBoost;
    }

    public Boolean getAbilitato() {
        return Abilitato;
    }

    public void setAbilitato(Boolean abilitato) {
        Abilitato = abilitato;
    }

    public Equalizer getEq() {
        return eq;
    }

    public void setEq(Equalizer eq) {
        this.eq = eq;
    }
}
*/