/* package looigi.loowebplayer.soap;

import android.content.Context;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.utilities.Utility;

public class CheckURLFile {
    private String Path;
    private static String messErrore = "";
    private CheckFile downloadFile;
    private String Url;
    private int NumeroBrano;
    private String urlUtente;
    private long lastTimePressed = 0;

    public void setContext(Context context) {
        VariabiliStaticheGlobali.getInstance().setCtxPassaggio(context);
    }

    public void setNumeroBrano(int numeroBrano) {
        NumeroBrano = numeroBrano;
    }

    public void startControl(String sUrl) {
        // boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
//
        // if ((System.currentTimeMillis() - lastTimePressed < 1000 && lastTimePressed >0) || !ceRete) {
        //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object() {
        //     }.getClass().getEnclosingMethod().getName(), "CheckUrl File troppo veloce o mancanza di rete");
        //     VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("ESECUZIONE TERMINATA CON ESITO NEGATIVO");
        //     return;
        // }
        // lastTimePressed = System.currentTimeMillis();

        // String Chiave = this.Url;
        // if (!VariabiliStaticheGlobali.getInstance().getChiaveCheckURL().isEmpty() ||
        //         !VariabiliStaticheGlobali.getInstance().getChiaveCheckURL().equals(Chiave)) {
        // if (VariabiliStaticheGlobali.getInstance().getChiaveCheckURL() == null) {
        //     VariabiliStaticheGlobali.getInstance().setChiaveCheckURL("");
        // }
        // if (VariabiliStaticheGlobali.getInstance().getChiaveCheckURL().isEmpty() ||
        //         (!VariabiliStaticheGlobali.getInstance().getChiaveCheckURL().isEmpty() &&
        //         !VariabiliStaticheGlobali.getInstance().getChiaveCheckURL().equals(Chiave))) {
        //     VariabiliStaticheGlobali.getInstance().setChiaveCheckURL(Chiave);

            Url = sUrl;
            messErrore = "";

            VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("");

            downloadFile = new CheckFile();
            downloadFile.execute(Url);
        // }
    }

    public void StoppaEsecuzione(boolean Errore) {
        if (downloadFile!=null) {
            downloadFile.cancel(true);
        }

        if (Errore) {
            VariabiliStaticheGlobali.getInstance().setEsciDaCheckFile(true);
            messErrore = "ESCI";
        } else {
            VariabiliStaticheGlobali.getInstance().setEsciDaCheckFile(false);
            messErrore ="OK";
        }

        if (downloadFile!=null) {
            downloadFile.ControllaFineCiclo();
        }
    }

    private static class CheckFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... sUrl) {
            // boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
//
            // if (!ceRete) {
            //     messErrore="ERROR: Assenza di rete";
            //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
            //             "Check File error; Assenza di rete");
            //     return null;
            // }

            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                    "Check file: "+sUrl[0]);
            try {
                HttpURLConnection.setFollowRedirects(false);
                String sUrlConverted = sUrl[0].replace(" ", "%20").replace("&", "%26");
                HttpURLConnection con = (HttpURLConnection) new URL(sUrlConverted)
                        .openConnection();
                con.setRequestMethod("HEAD");

                int c = con.getResponseCode();
                if (c == HttpURLConnection.HTTP_OK) {
                    messErrore="OK";
                } else {
                    messErrore="ERROR: " + Integer.toString(c);
                }
            } catch (Exception e) {
                messErrore="ERROR: "+Utility.getInstance().PrendeErroreDaException(e);
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Check File error");
                VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ControllaFineCiclo();
        }

        public void ControllaFineCiclo() {
            // VariabiliStaticheGlobali.getInstance().setChiaveCheckURL("");

            // if (VariabiliStaticheNuove.getInstance().getCuf() != null) {
            //     VariabiliStaticheNuove.getInstance().setCuf(null);
            // }

            if (messErrore.equals("ESCI")) {
                VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("ESECUZIONE TERMINATA CON ESITO NEGATIVO");
            } else {
                if (messErrore.equals("OK")) {
                    VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL(messErrore);
                } else {
                    if (VariabiliStaticheGlobali.getInstance().isEsciDaCheckFile()) {
                        VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("ESECUZIONE TERMINATA CON ESITO NEGATIVO");
                    } else {
                        VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL(messErrore);
                    }
                }
            }
            // }
        }
    }
}*/