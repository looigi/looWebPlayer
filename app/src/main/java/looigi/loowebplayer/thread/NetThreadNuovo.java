package looigi.loowebplayer.thread;

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

public class NetThreadNuovo {
    private Boolean stopNet;
    private Timer tTmrBattery = null;

    private boolean OkNet = true;
    private boolean ScreenOn = true;
    // private static NetThreadNuovo instance = null;
    // private int signalStrengthValue;
    // private TelephonyManager manager;
    // private PhoneStateListener phoneListener;
    private SignalStrength LivelloSegnale;
    // private int QuantiSecondi=-1;
    // private int QuantiSecondiTot=-1;
    private int SecondiDiAttesa = 5000;
    // private boolean StaGirando=false;
    private boolean haveConnectedWifi = false;
    private boolean haveConnectedMobile = false;
    private Activity act;
    private PowerManager pm;
    private Integer conta=0;
    private Integer quanti=-1;
    // private Boolean RetePresente=true;
    private ConnectivityManager connectivityManager;

    // private NetThreadNuovo() {
    // }
//
    // public static NetThreadNuovo getInstance() {
    //     if (instance == null) {
    //         instance = new NetThreadNuovo();
    //     }
//
    //     return instance;
    // }

    public boolean isScreenOn() {
        return ScreenOn;
    }

    public void setScreenOn(boolean screenOn) {
        ScreenOn = screenOn;
    }

    public void start() {
        // if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getControlloRete()) {
            if (tTmrBattery == null) {
                act = VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale();
                connectivityManager = (ConnectivityManager) VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale()
                         .getSystemService(Context.CONNECTIVITY_SERVICE);
                pm = (PowerManager) VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale().
                        getSystemService(Context.POWER_SERVICE);

                this.stopNet = false;

                // phoneListener = new myPhoneStateListener();
                // manager = (TelephonyManager) VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale()
                //         .getSystemService(Context.TELEPHONY_SERVICE);
                // manager.listen(phoneListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

                // setupSignalStrength();

                // if (VariabiliStaticheGlobali.getInstance().getTipoSegnale() == 2) {
                //     SecondiDiAttesa = 1000;
                // } else {
                //     SecondiDiAttesa = 5000;
                // }
                SecondiDiAttesa = 5000;
                // StaGirando = true;

                ControlloRete();

                InternalThread();
            }
        // }
    }

    private void ControlloRete() {
        NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();

        haveConnectedWifi = false;
        haveConnectedMobile = false;

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                    break;
                }
            }
//
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                    break;
                }
            }
        }

        OkNet = haveConnectedWifi || haveConnectedMobile;
        if (OkNet && !haveConnectedWifi) {
            getGsmLevel();
        }

        /* switch(VariabiliStaticheGlobali.getInstance().getTipoSegnale()) {
            case 1:
                OkNet = haveConnectedWifi || haveConnectedMobile;
                if (OkNet && !haveConnectedWifi) {
                    // getGsmLevel();
                }
                break;
            case 2:
                if (quanti==-1) {
                    quanti=new Random().nextInt(50) + 10;
                }
                conta++;
                if (conta>quanti) {
                    conta=0;
                    // if (RetePresente) {
                    RetePresente=!RetePresente;
                    // }
                    OkNet=RetePresente;
                }
                break;
            case 3:
                OkNet = false;
                break;
            case 4:
                OkNet = true;
                break;
        } */

        if (!OkNet) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (VariabiliStaticheHome.getInstance().getImgOffline() != null) {
                        VariabiliStaticheHome.getInstance().getImgOffline().setVisibility(LinearLayout.VISIBLE);
                    }
                }
            });
        } else {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (VariabiliStaticheHome.getInstance().getImgOffline() != null) {
                        VariabiliStaticheHome.getInstance().getImgOffline().setVisibility(LinearLayout.GONE);
                    }
                }
            });
        }
    }

    private void InternalThread() {
        if (tTmrBattery == null) {
            tTmrBattery = new Timer();
            tTmrBattery.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!stopNet) {
                        ControlloRete();
                    }
                }
            }, 0, SecondiDiAttesa);
        }
    }

    private void getGsmLevel() {
        if (LivelloSegnale!=null) {
            // int level;
            // ASU ranges from 0 to 31 - TS 27.007 Sec 8.5
            // asu = 0 (-113dB or less) is very weak
            // signal, its better to show 0 bars to the user in such cases.
            // asu = 99 is a special case, where the signal strength is unknown.
            int asu = LivelloSegnale.getGsmSignalStrength();
            if (asu <= 1 || asu == 99) {
                OkNet = false;
                // level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
            } else if (asu >= 12) {
                OkNet = true;
                // level = SIGNAL_STRENGTH_GREAT;
            } else if (asu >= 8) {
                OkNet = true;
                // level = SIGNAL_STRENGTH_GOOD;
            } else if (asu >= 5) {
                OkNet = true;
                // level = SIGNAL_STRENGTH_MODERATE;
            } else {
                OkNet = false;
                // level = SIGNAL_STRENGTH_POOR;
            }
        } else {
            OkNet = true;
        }
    }

    // private void setupSignalStrength() {
    //     manager = (TelephonyManager) VariabiliStaticheGlobali.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
    //     phoneListener = new PhoneStateListener() {
    //         @Override
    //         public void onSignalStrengthsChanged(SignalStrength signalStrength) {
    //             if (manager!=null && manager.getNetworkOperator().equals("")) {
    //                 // signalIcon.setVisibility(View.GONE);
    //             } else {
    //                 // signalIcon.setVisibility(View.VISIBLE);
    //                 if (signalStrength!=null) {
    //                     LivelloSegnale = signalStrength;
    //                 }
    //             }
    //         }
    //     };
    //     manager.listen(phoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    // }

  public boolean isOk() {
      return OkNet;
  }

    public void StopNetThread() {
        if (tTmrBattery!=null) {
            stopNet = true;

            if (tTmrBattery != null) {
                tTmrBattery.cancel();
                tTmrBattery.purge();
                tTmrBattery = null;
            }
        }
    }

    // private class myPhoneStateListener extends PhoneStateListener {
    //     public void onSignalStrengthsChanged(SignalStrength signalStrength) {
    //         super.onSignalStrengthsChanged(signalStrength);
    //         if (signalStrength.isGsm()) {
    //             if (signalStrength.getGsmSignalStrength() != 99)
    //                 signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
    //             else
    //                 signalStrengthValue = signalStrength.getGsmSignalStrength();
    //         } else {
    //             signalStrengthValue = signalStrength.getCdmaDbm();
    //         }
    //     }
    // }
}
