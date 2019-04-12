package looigi.loowebplayer.utilities;

import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.db_locale.DBLocaleTraffico;

public class Traffico {
    private Runnable runRiga;
    private Handler hSelezionaRiga;
    private static Traffico instance = null;

    private Traffico() {
    }

    public static Traffico getInstance() {
        if (instance == null) {
            instance = new Traffico();
        }

        return instance;
    }

    public String ModificaTraffico(Long Traffico) {
        String tipo = "b.";
        float bsf = Traffico;

        if (bsf>1024) {
            bsf /= 1024;
            tipo="Kb.";
        }
        if (bsf>(1024)) {
            bsf /= (1024);
            tipo="Mb.";
        }
        if (bsf>(1024)) {
            bsf /= (1024);
            tipo="Gb.";
        }

        long bsl = Math.round(bsf*100);
        final Float fbsf = bsl/100F;
        final String sTipo = tipo;

        return String.format("%.2f", fbsf) + " " + sTipo;
    }

    public void SalvaDBTraffico() {
        Boolean Ok = true;

        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String oggi = formatter.format(todayDate);

        DBLocaleTraffico db = new DBLocaleTraffico(VariabiliStaticheGlobali.getInstance().getContext());
        db.open();
        Cursor c = db.ottieniValoriPerData(oggi);
        if (c.moveToFirst()) {
            do {
                String data = c.getString(0);

                if (data.equals(oggi)) {
                    db.aggiornaValori(oggi, Long.toString(VariabiliStaticheGlobali.getInstance().getBytesScaricati()));
                    Ok=false;
                    break;
                }
            } while (c.moveToNext());
        }

        if (Ok) {
            long l = db.inserisciDati(oggi, Long.toString(VariabiliStaticheGlobali.getInstance().getBytesScaricati()));
        }

        db.close();
    }

    public String LeggeTrafficoTotale() {
        DBLocaleTraffico db = new DBLocaleTraffico(VariabiliStaticheGlobali.getInstance().getContext());
        db.open();

        Long MaxV=0L;
        String DataMax="";
        Long MinV=99999999L;
        String DataMin="";
        Long Tot=0L;
        int Giorni=0;
        Long Mese=0L;

        Calendar cl = Calendar.getInstance();
        int month = cl.get(Calendar.MONTH)+1;
        String mm=Integer.toString(month);
        if (mm.length()==1) { mm="0"+mm; }

        Cursor c = db.ottieniValori();
        if (c.moveToFirst()) {
            do {
                String data = c.getString(0);
                String v= c.getString(1);
                Long Valore = Long.parseLong(v);

                if (Valore>MaxV) {
                    MaxV=Valore;
                    DataMax=data;
                }

                if (Valore<MinV && Valore>0) {
                    MinV=Valore;
                    DataMin=data;
                }

                if (data.contains("/"+mm+"/")) {
                    Mese+=Valore;
                }

                Giorni++;
                Tot+=Valore;
            } while (c.moveToNext());
        }

        db.close();

        Long Media = Tot/Giorni;

        return Long.toString(Tot)+";"+DataMax+";"+Long.toString(MaxV) + ";"+DataMin+";"+Long.toString(MinV)+";"+Long.toString(Media)+";" +Long.toString(Mese)+";";
    }

    public void LeggeTrafficoDiOggi() {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String oggi = formatter.format(todayDate);

        DBLocaleTraffico db = new DBLocaleTraffico(VariabiliStaticheGlobali.getInstance().getContext());
        db.open();

        // db.cancellaDati();

        Cursor c = db.ottieniValoriPerData(oggi);
        if (c.moveToFirst()) {
            do {
                String data = c.getString(0);
                String valore = c.getString(1);

                if (data.equals(oggi)) {
                    VariabiliStaticheGlobali.getInstance().setBytesScaricati(Long.parseLong(valore));
                    ScriveTrafficoAVideo(VariabiliStaticheGlobali.getInstance().getBytesScaricati());
                    break;
                }
            } while (c.moveToNext());
        } else {
            VariabiliStaticheGlobali.getInstance().setBytesScaricati(0L);
            ScriveTrafficoAVideo(0L);
        }

        db.close();
    }

    public void ScriveTrafficoAVideo(final Long t) {
        if (VariabiliStaticheGlobali.getInstance().getTxtTraffico()!=null) {
            hSelezionaRiga = new Handler(Looper.getMainLooper());
            hSelezionaRiga.postDelayed(runRiga=new Runnable() {
                @Override
                public void run() {
                    hSelezionaRiga.removeCallbacks(runRiga);

                    String tt = ModificaTraffico(t);

                    VariabiliStaticheGlobali.getInstance().getTxtTraffico().setText("Traffico: "+tt);
                    SalvaDBTraffico();
                }
            }, 50);
        }
    }
}
