package looigi.loowebplayer.dati;

public class NomiMaschere {
    //-------- Singleton ----------//
    private static NomiMaschere instance = null;

    private NomiMaschere() {
    }

    public static NomiMaschere getInstance() {
        if (instance == null) {
            instance = new NomiMaschere();
        }

        return instance;
    }

    private String HomePerTitolo="Home";
    private String Splash="Splash";
    private String UtenzaPerTitolo="Utenza";
    private String LibreriaPerTitolo="Libreria";
    private String SettingsPerTitolo="Impostazioni";
    private String AboutPerTitolo="About";
    private String EqualizerPerTitolo="Equalizer";

    public String getEqualizerPerTitolo() {
        return EqualizerPerTitolo;
    }

    public String getAboutPerTitolo() {
        return AboutPerTitolo;
    }

    public String getSettingsPerTitolo() {
        return SettingsPerTitolo;
    }

    public String getSettings() {
        return SettingsPerTitolo.toUpperCase().replace(" ","");
    }

    public String getLibreriaPerTitolo() {
        return LibreriaPerTitolo;
    }

    public String getLibreria() {
        return LibreriaPerTitolo.toUpperCase().replace(" ","");
    }

    public String getUtenzaPerTitolo() {
        return UtenzaPerTitolo;
    }

    public String getUtenza() {
        return UtenzaPerTitolo.toUpperCase().replace(" ","");
    }

    public String getHomePerTitolo() {
        return HomePerTitolo;
    }

    public String getHome() {
        return HomePerTitolo.toUpperCase().replace(" ","");
    }

    public String getSplash() {
        return Splash.toUpperCase().replace(" ","");
    }

    public String getSplashPerTitolo() {
        return Splash;
    }
}
