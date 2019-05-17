package looigi.loowebplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.chiamate.PhoneUnlockedReceiver;
import looigi.loowebplayer.cuffie.GestioneTastoCuffie;
import looigi.loowebplayer.cuffie.GestoreCuffie;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.maschere.Splash;
import looigi.loowebplayer.notifiche.Notifica;
import looigi.loowebplayer.thread.NetThreadNuovo;
import looigi.loowebplayer.utilities.GestioneCaricamentoBraniNuovo;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.Permessi;
import looigi.loowebplayer.utilities.Utility;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private VariabiliStaticheGlobali vg = VariabiliStaticheGlobali.getInstance();
    private AudioManager mAudioManager;
    private ComponentName mReceiverComponent;
    private PhoneUnlockedReceiver receiver;
    // private NetThreadNuovo ntn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Permessi p=new Permessi();
        p.ControllaPermessi(this);

        vg.setContext(this);
        vg.setFragmentActivityPrincipale(this);
        vg.setContextPrincipale(this);
        vg.SettaIcone(this);

        mAudioManager =  (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mReceiverComponent = new ComponentName(this, GestioneTastoCuffie.class);
        mAudioManager.registerMediaButtonEventReceiver(mReceiverComponent);

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        receiver = new PhoneUnlockedReceiver();
        IntentFilter fRecv = new IntentFilter();
        fRecv.addAction(Intent.ACTION_USER_PRESENT);
        fRecv.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, fRecv);

        if (VariabiliStaticheGlobali.getInstance().getMyReceiverCuffie() != null) {
            unregisterReceiver(VariabiliStaticheGlobali.getInstance().getMyReceiverCuffie());
            VariabiliStaticheGlobali.getInstance().setMyReceiverCuffie(null);
        }
        VariabiliStaticheGlobali.getInstance().setMyReceiverCuffie (new GestoreCuffie());
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(VariabiliStaticheGlobali.getInstance().getMyReceiverCuffie(), filter);

        vg.setActButtonNew((FloatingActionButton) findViewById(R.id.fab));
        vg.getActButtonNew().hide();
        vg.getActButtonNew().setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EsegueMenuActionButton();
            }
        });

        vg.setTxtBraniInLista((TextView) findViewById(R.id.txtBraniInLista));
        vg.setTxtTraffico((TextView) findViewById(R.id.txtTraffico));

        ImageView imgRefreshCanzone = findViewById(R.id.imgRefreshCanzone);
        imgRefreshCanzone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
                int n = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
                if (NumeroBrano>n) {
                    if (n>0) {
                        NumeroBrano = 0;
                    } else {
                        NumeroBrano = -1;
                    }
                }

                if (NumeroBrano!=-1) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Eliminazione files per refresh");
                    StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);
                    if (s!=null) {
                        String NomeBrano = s.getNomeBrano();
                        String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(s.getIdArtista()).getArtista();
                        String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(s.getIdAlbum()).getNomeAlbum();
                        String CompattazioneMP3 = VariabiliStaticheGlobali.EstensioneCompressione;
                        if (!VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isCompressioneMP3()) {
                            CompattazioneMP3 = "";
                        }
                        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
                        String PathMP3 = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + NomeBrano;
                        String PathMP3_Compresso = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Dati/" + pathBase + "/" + Artista + "/" + Album + "/" + CompattazioneMP3 + NomeBrano;
                        File f = new File(PathMP3);
                        File fc = new File(PathMP3_Compresso);
                        if (f.exists()) {
                            f.delete();
                        }
                        if (fc.exists()) {
                            fc.delete();
                        }
                    } else {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Ritornata struttura brano nulla");
                    }
                }

                GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano();
            }
        });
        VariabiliStaticheHome.getInstance().setImgRefresh(imgRefreshCanzone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vg.setAppBar((AppBarLayout) findViewById(R.id.appBarLayout));
        vg.getAppBar().setVisibility(LinearLayout.GONE);
        vg.setWindowBackground(getWindow());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        GestioneFiles.getInstance().CreaCartella(VariabiliStaticheGlobali.getInstance().PercorsoDIR+"/");

        // VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Lettura configurazione valori");
        VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().LeggeValori();

        VariabiliStaticheGlobali.getInstance().setNtn(new NetThreadNuovo());
        VariabiliStaticheGlobali.getInstance().getNtn().start();

        if (VariabiliStaticheGlobali.getInstance().getGiaEntrato()==null || !VariabiliStaticheGlobali.getInstance().getGiaEntrato()) {
            Fragment fragment = new Splash();
            FragmentTransaction ft = vg.getFragmentActivityPrincipale().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            int commit = ft.commit();
        } else {
            Utility.getInstance().CambiaMaschera(R.id.home);
        }
    }

    public static void ScriveBraniInLista() {
        int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
        int n = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
        if (NumeroBrano>n) {
            if (n>0) {
                NumeroBrano = 0;
            } else {
                NumeroBrano = -1;
            }
        }

        if (NumeroBrano==-1) {
            VariabiliStaticheGlobali.getInstance().getTxtBraniInLista().setText("Numero brani: 0");
        } else {
            DecimalFormat formatter = new DecimalFormat("#,###");
            String nb = formatter.format(NumeroBrano);
            String qb = formatter.format(VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani());

            VariabiliStaticheGlobali.getInstance().getTxtBraniInLista().setText("Brano: " +
                    nb+" / "+
                    qb);
        }
    }

    private void EsegueMenuActionButton() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode== KeyEvent.KEYCODE_BACK) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                moveTaskToBack(true);

                // super.onBackPressed();
            }
        }
        return false;
    }

    /* @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);

            super.onBackPressed();
        }
    } */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Utility.getInstance().CambiaMaschera(id);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.about:
                Utility.getInstance().CambiaMaschera(id);
                break;
            case R.id.home:
                Utility.getInstance().CambiaMaschera(id);
                break;
            case R.id.utenza:
                Utility.getInstance().CambiaMaschera(id);
                break;
            case R.id.libreria:
                Utility.getInstance().CambiaMaschera(id);
                break;
            case R.id.equalizer:
                Utility.getInstance().CambiaMaschera(id);
                break;
            case R.id.uscita:
                VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().stopService(
                        VariabiliStaticheGlobali.getInstance().getiServizio());

                VariabiliStaticheGlobali.getInstance().getNtn().StopNetThread();

                if (receiver != null) {
                    unregisterReceiver(receiver);
                    receiver = null;
                }
                if (VariabiliStaticheGlobali.getInstance().getMyReceiverCuffie() != null) {
                    unregisterReceiver(VariabiliStaticheGlobali.getInstance().getMyReceiverCuffie());
                    VariabiliStaticheGlobali.getInstance().setMyReceiverCuffie(null);
                }

                Notifica.getInstance().RimuoviNotifica();

                if (VariabiliStaticheGlobali.getInstance().getMyReceiverCuffie()!=null) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Tolgo registrazione receiver cuffie");

                    try {
                        unregisterReceiver(VariabiliStaticheGlobali.getInstance().getMyReceiverCuffie());
                    } catch (Exception e) {
                        VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                    }
                }

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Tolgo il receiver delle cuffie dal manager audio");
                try {
                    mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponent);
                } catch (Exception e) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                }

                // if (VariabiliStaticheGlobali.getInstance().getMyReceiverGTC()!=null) {
                //     try {
                //         unregisterReceiver(VariabiliStaticheGlobali.getInstance().getMyReceiverGTC());
                //     } catch (Exception ignored) {
//
                //     }
                // }

                Utility.getInstance().CambiaMaschera(id);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
