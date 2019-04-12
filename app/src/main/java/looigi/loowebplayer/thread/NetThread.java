/* package looigi.loowebplayer.thread;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;

import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_GOOD;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_GREAT;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_MODERATE;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
import static android.telephony.CellSignalStrength.SIGNAL_STRENGTH_POOR;

public class NetThread {
    private Boolean stopNet;
    private Timer tTmrBattery = null;

    private boolean OkNet=true;
    private boolean ScreenOn=true;
    private Boolean CaroselloBloccato=false;
    private static NetThread instance = null;
    private int signalStrengthValue;
    private TelephonyManager manager;
    private PhoneStateListener phoneListener;
    private SignalStrength LivelloSegnale;
    private int QuantiSecondi=-1;
    private int QuantiSecondiTot=-1;
    private int SecondiDiAttesa=5000;
    private boolean StaGirando=false;
    private boolean haveConnectedWifi = false;
    private boolean haveConnectedMobile = false;
    private Activity act;
    private PowerManager pm;
    private ConnectivityManager connectivityManager;

    private NetThread() {
    }

    public static NetThread getInstance() {
        if (instance == null) {
            instance = new NetThread();
        }

        return instance;
    }

    public Boolean getCaroselloBloccato() {
        return CaroselloBloccato;
    }

    public void setCaroselloBloccato(Boolean caroselloBloccato) {
        CaroselloBloccato = caroselloBloccato;
    }

    public void start() {
        if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getControlloRete()) {
            if (tTmrBattery == null) {
                act = VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale();
                connectivityManager = (ConnectivityManager) VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                pm = (PowerManager) VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().
                        getSystemService(Context.POWER_SERVICE);

                this.stopNet = false;
                // psListener = new myPhoneStateListener();
                // telephonyManager = (TelephonyManager) VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale()
                //         .getSystemService(Context.TELEPHONY_SERVICE);
                // telephonyManager.listen(psListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

                setupSignalStrength();

                if (VariabiliStaticheGlobali.getInstance().getTipoSegnale() == 2) {
                    SecondiDiAttesa = 1000;
                } else {
                    SecondiDiAttesa = 5000;
                }
                StaGirando = true;

                InternalThread();
            }
        }
    }

    private void InternalThread() {
        if (tTmrBattery==null) {
            tTmrBattery = new Timer();
            tTmrBattery.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!stopNet) {
                        // try {
                            NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();

                            for (NetworkInfo ni : netInfo) {
                                if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                                    if (ni.isConnected()) {
                                        haveConnectedWifi = true;
                                        break;
                                    }
                                }

                                if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                                    if (ni.isConnected()) {
                                        haveConnectedMobile = true;
                                        break;
                                    }
                                }
                            }

                            ScreenOn = pm.isScreenOn();

                            /* if (ScreenOn) {
                                if (CaroselloBloccato) {
                                    if (VariabiliStaticheGlobali.getInstance().getBloccaCarosello() &&
                                         VariabiliStaticheGlobali.getInstance().getStaSuonando() &&
                                        !VariabiliStaticheGlobali.getInstance().isCaroselloBloccatoDaAutomatico()) {
                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Schermo bloccato con tasto");
                                        SbloccoCarosello();
                                    }
                                }
                            }
                            if (!ScreenOn) {
                                if (!CaroselloBloccato) {
                                    if (!VariabiliStaticheGlobali.getInstance().getBloccaCarosello() &&
                                            VariabiliStaticheGlobali.getInstance().getStaSuonando()) {
                                        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Schermo sbloccato con tasto");
                                        CaroselloBloccato = true;
                                        VariabiliStaticheGlobali.getInstance().setBloccaCarosello(true);
                                        VariabiliStaticheHome.getInstance().ImpostaUltimaImmagine(true);
                                    }
                                }
                            } *//*

                            if (VariabiliStaticheGlobali.getInstance().getTipoSegnale()!=2 && VariabiliStaticheGlobali.getInstance().getTipoSegnale()!=4) {
                                OkNet = haveConnectedWifi || haveConnectedMobile;
                            } else {
                                if (VariabiliStaticheGlobali.getInstance().getTipoSegnale()==4) {
                                    OkNet = true;
                                }
                            }

                            if (VariabiliStaticheGlobali.getInstance().getTipoSegnale()!=4) {
                                if (OkNet || VariabiliStaticheGlobali.getInstance().getTipoSegnale() == 2) {
                                    // if (LivelloSegnale!=null) {
                                    if (!haveConnectedWifi && VariabiliStaticheGlobali.getInstance().getTipoSegnale() != 1) {
                                        getGsmLevel();
                                    }
                                    //}

                                    // int t = manager.getNetworkType();
                                    // String Rete="";
                                    // switch (t) {
                                    //     case TelephonyManager.NETWORK_TYPE_GPRS:
                                    //     case TelephonyManager.NETWORK_TYPE_EDGE:
                                    //     case TelephonyManager.NETWORK_TYPE_CDMA:
                                    //     case TelephonyManager.NETWORK_TYPE_1xRTT:
                                    //     case TelephonyManager.NETWORK_TYPE_IDEN:
                                    //         Rete = "2G";
                                    //     case TelephonyManager.NETWORK_TYPE_UMTS:
                                    //     case TelephonyManager.NETWORK_TYPE_EVDO_0:
                                    //     case TelephonyManager.NETWORK_TYPE_EVDO_A:
                                    //     case TelephonyManager.NETWORK_TYPE_HSDPA:
                                    //     case TelephonyManager.NETWORK_TYPE_HSUPA:
                                    //     case TelephonyManager.NETWORK_TYPE_HSPA:
                                    //     case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                    //     case TelephonyManager.NETWORK_TYPE_EHRPD:
                                    //     case TelephonyManager.NETWORK_TYPE_HSPAP:
                                    //         Rete = "3G";
                                    //     case TelephonyManager.NETWORK_TYPE_LTE:
                                    //         Rete = "4G";
                                    //     default:
                                    //         Rete = "Unknown";
                                    // }
                                }
                            }

                            if (!OkNet) {
                                act.runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        if (VariabiliStaticheHome.getInstance().getImgOffline()!=null) {
                                            VariabiliStaticheHome.getInstance().getImgOffline().setVisibility(LinearLayout.VISIBLE);
                                        }
                                    }
                                });
                            } else {
                                act.runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        if (VariabiliStaticheHome.getInstance().getImgOffline()!=null) {
                                            VariabiliStaticheHome.getInstance().getImgOffline().setVisibility(LinearLayout.GONE);
                                        }
                                    }
                                });
                            }
                        // } catch (Exception ignored) {
                        // }
                    }
                }
            }, 0, SecondiDiAttesa);
        }
    }

    public void SbloccoCarosello() {
        CaroselloBloccato=false;
        VariabiliStaticheGlobali.getInstance().setCaroselloBloccatoDaAutomatico(false);
        VariabiliStaticheGlobali.getInstance().setBloccaCarosello(false);
        VariabiliStaticheHome.getInstance().ImpostaUltimaImmagine(true);
    }

    private void getGsmLevel() {
        switch (VariabiliStaticheGlobali.getInstance().getTipoSegnale()) {
            case 1:
                if (LivelloSegnale!=null) {
                    int level;
                    // ASU ranges from 0 to 31 - TS 27.007 Sec 8.5
                    // asu = 0 (-113dB or less) is very weak
                    // signal, its better to show 0 bars to the user in such cases.
                    // asu = 99 is a special case, where the signal strength is unknown.
                    int asu = LivelloSegnale.getGsmSignalStrength();
                    if (asu <= 1 || asu == 99) {
                        OkNet = false;
                        level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
                    } else if (asu >= 12) {
                        OkNet = true;
                        level = SIGNAL_STRENGTH_GREAT;
                    } else if (asu >= 8) {
                        OkNet = true;
                        level = SIGNAL_STRENGTH_GOOD;
                    } else if (asu >= 5) {
                        OkNet = true;
                        level = SIGNAL_STRENGTH_MODERATE;
                    } else {
                        OkNet = true;
                        level = SIGNAL_STRENGTH_POOR;
                    }
                } else {
                    OkNet = false;
                }
                break;
            case 2:
                if (QuantiSecondiTot==-1) {
                    Random r = new Random();
                    if (!OkNet) {
                        QuantiSecondiTot = r.nextInt(15 - 5) + 5;
                    } else {
                        QuantiSecondiTot = r.nextInt(40 - 10) + 10;
                    }
                    QuantiSecondi=0;
                } else {
                    QuantiSecondi++;
                    if (QuantiSecondi>=QuantiSecondiTot) {
                        QuantiSecondiTot=-1;
                        if (OkNet) {
                            OkNet=false;
                        } else {
                            OkNet=true;
                        }
                    }
                }
                break;
            case 3:
                OkNet=false;
                break;
        }

        // return level;
    }

    public boolean isStaGirando() {
        return StaGirando;
    }

    private void setupSignalStrength() {
        manager = (TelephonyManager) VariabiliStaticheGlobali.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
        phoneListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                if (manager!=null && manager.getNetworkOperator().equals("")) {
                    // signalIcon.setVisibility(View.GONE);
                } else {
                    // signalIcon.setVisibility(View.VISIBLE);
                    if (signalStrength!=null) {
                        LivelloSegnale = signalStrength;
                    }

                    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // See https://github.com/AlstonLin/TheLearningLock/issues/54
                        // Integer imageRes = signalStrengthToIcon.get(signalStrength.getLevel());
                        // if (imageRes != null) signalIcon.setImageResource(imageRes);
                        // else signalIcon.setImageResource(signalStrengthToIcon.get(4));
                    // } else {
                        // Just show the full icon
                        // signalIcon.setImageResource(signalStrengthToIcon.get(4));
                    // }
                }
            }
        };
        manager.listen(phoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    public boolean isOk() {
        return OkNet;
    }

    public boolean isScreenOn() {
        return ScreenOn;
    }

    public void StopNetThread() {
        StaGirando=false;

        if (tTmrBattery!=null) {
            stopNet = true;

            if (tTmrBattery != null) {
                tTmrBattery.cancel();
                tTmrBattery.purge();
                tTmrBattery = null;
            }

            if (manager!=null) {
                phoneListener=null;
                manager=null;
            }
        }
    }
}
*/