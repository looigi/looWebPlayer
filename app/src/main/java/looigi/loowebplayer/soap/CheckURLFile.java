package looigi.loowebplayer.soap;

import android.content.Context;
import android.os.AsyncTask;

import java.net.HttpURLConnection;
import java.net.URL;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.utilities.Utility;

public class CheckURLFile {
    private String Path;
    private String messErrore = "";
    private CheckFile downloadFile;
    private String Url;
    private int NumeroBrano;
    private long lastTimePressed = 0;

    public void setContext(Context context) {
        VariabiliStaticheGlobali.getInstance().setCtxPassaggio(context);
    }

    public void setNumeroBrano(int numeroBrano) {
        NumeroBrano = numeroBrano;
    }

    public void startControl(String sUrl) {
        if (System.currentTimeMillis() - lastTimePressed < 1000) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
            }.getClass().getEnclosingMethod().getName(), "CheckUrl File troppo veloce");
            VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("BRANO DIVERSO O SKIPPATO");
            return;
        }
        lastTimePressed = System.currentTimeMillis();

        String Chiave = this.Url;
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

    public void StoppaEsecuzione() {
        downloadFile.cancel(true);

        VariabiliStaticheGlobali.getInstance().setEsciDaCheckFile(true);
        messErrore="ESCI";

        downloadFile.ControllaFineCiclo();
    }

    private class CheckFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... sUrl) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
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
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Check File error");
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
                VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("BRANO DIVERSO O SKIPPATO");
            } else {
                if (VariabiliStaticheGlobali.getInstance().isEsciDaCheckFile()) {
                    VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("BRANO DIVERSO O SKIPPATO");
                } else {
                    VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL(messErrore);
                }
            }
            // }
        }
    }
}