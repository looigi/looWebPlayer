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
    public static int ritardoDownloadAutomatico = 50;
    public static boolean RitardaDownload = false;
    public static boolean RitardaDownloadAutomatico = false;
    public static boolean TimeoutCortissimo = false;
    public static boolean GeneraSempreErroreSOAP = false;
    public static boolean EsceDaAttesaScaricamentoBranoPerErrore = false;
    private String messErrorePerDebug = "";
    private String messErrorePerDebugMP3 = "";

    public String getMessErrorePerDebugMP3() {
        return messErrorePerDebugMP3;
    }

    public String getMessErrorePerDebug() {
        return messErrorePerDebug;
    }

    public boolean DiceSeCreaLog(String nome) {
        boolean ret;

        switch(nome) {
            case "AdapterAscoltati":
                ret = false;
                break;
            case "AdapterAlbum":
                ret = false;
                break;
            case "AdapterArtisti":
                ret = false;
                break;
            case "AdapterBrani":
                ret = false;
                break;
            case "bckService":
                ret = false;
                break;
            case "CustomAcraSender":
                ret = false;
                break;
            case "DBLocaleTraffico":
                ret = false;
                break;
            case "DBLocaleUtenti":
                ret = false;
                break;
            case "DialogMessaggio":
                ret = false;
                break;
            case "DBLocaleEsclusi":
                ret = false;
                break;
            case "db_dati":
                ret = false;
                break;
            case "DBRemotoNuovo":
                ret = false;
                break;
            case "EliminazioneVecchiFiles":
                ret = false;
                break;
            case "GestioneFiles":
                ret = false;
                break;
            case "GestioneListaBrani":
                ret = true;
                break;
            case "GestioneVideo":
                ret = false;
                break;
            case "GestioneSuonaBrano":
                ret = true;
                break;
            case "GestioneCPU":
                ret = false;
                break;
            case "GestioneChiamate":
                ret = false;
                break;
            case "GestioneOggettiVideo":
                ret = true;
                break;
            case "GestioneCaricamentoBraniNuovo":
                ret = true;
                break;
            case "GestioneImmagini":
                ret = false;
                break;
            case "GestioneTesti":
                ret = false;
                break;
            case "Home":
                ret = false;
                break;
            case "MainActivity":
                ret = false;
                break;
            case "MemoryBoss":
                ret = true;
                break;
            case "Notifica":
                ret = false;
                break;
            case "PassaggioNotifica":
                ret = false;
                break;
            case "PronunciaFrasi":
                ret = false;
                break;
            case "ScaricoBranoEAttesa":
                ret = true;
                break;
            case "ScaricoTesto":
                ret = false;
                break;
            case "ScaricoCover":
                ret = false;
                break;
            case "Settings":
                ret = false;
                break;
            case "StrutturaConfig":
                ret = false;
                break;
            case "Splash":
                ret = false;
                break;
            case "Utility":
                ret = false;
                break;
            case "wsRitornoNuovoPerErrore":
                ret = true;
                break;
            case "wsRitornoNuovo":
                ret = true;
                break;
            default:
                ret = false;
        }

        return ret;
    }
}
