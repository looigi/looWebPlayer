package looigi.loowebplayer.VariabiliStatiche;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.Date;

import looigi.loowebplayer.R;
import looigi.loowebplayer.cuffie.GestoreCuffie;
import looigi.loowebplayer.dati.DatiGenerali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaUtenti;
import looigi.loowebplayer.soap.AttesaScaricamentoBrano;
import looigi.loowebplayer.soap.DownloadMP3Nuovo;
import looigi.loowebplayer.soap.GestioneWEBServiceSOAPNuovo;
import looigi.loowebplayer.thread.NetThreadNuovo;
import looigi.loowebplayer.thread.ScaricoBranoEAttesa;
import looigi.loowebplayer.utilities.Log;

public class VariabiliStaticheDebug {
    //-------- Singleton ----------//
    private static VariabiliStaticheDebug instance = null;

    private VariabiliStaticheDebug() {
    }

    public static VariabiliStaticheDebug getInstance() {
        if (instance == null) {
            instance = new VariabiliStaticheDebug();
        }

        return instance;
    }

    public static boolean ScaricaSempreIBrani = false;
    public static int ritardoDownload = 10;
    public static int ritardoDownloadAutomatico = 30;
    public static boolean RitardaDownload = false;
    public static boolean RitardaDownloadAutomatico = false;
    public static boolean TimeoutCortissimo = false;
    public static boolean GeneraSempreErroreSOAP = false;
    public static boolean EsceDaAttesaScaricamentoBranoPerErrore = false;
}
