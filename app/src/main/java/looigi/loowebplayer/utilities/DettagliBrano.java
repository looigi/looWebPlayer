package looigi.loowebplayer.utilities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaMembri;
import looigi.loowebplayer.dialog.DialogMessaggio;
import looigi.loowebplayer.soap.DownloadTextFileNuovo;

public class DettagliBrano {
    private StrutturaBrani sb;

    public void RitornaDettagliBrano(View view) {
        int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();
        if (NumeroBrano==-1) {
            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                    "Nessun brano impostato", true, VariabiliStaticheGlobali.NomeApplicazione);
        } else {
            sb = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(NumeroBrano);

            String sNomeBrano = sb.getNomeBrano();
            String Traccia = "";
            if (sNomeBrano.contains("-")) {
                String A[] = sNomeBrano.split("-");
                if (!A[0].isEmpty() && !A[0].equals("00")) {
                    Traccia = "\nTraccia " + A[0].trim();
                }
                sNomeBrano = A[1].trim();
            }
            if (sNomeBrano.contains(".")) {
                sNomeBrano = sNomeBrano.substring(0, sNomeBrano.indexOf("."));
            }
            int idArtista = sb.getIdArtista();
            String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getArtista();
            String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(sb.getIdAlbum()).getNomeAlbum();
            String sAlbum = Album;
            String Anno = "";
            if (sAlbum.contains("-")) {
                String A[] = sAlbum.split("-");
                if (A.length > 1) {
                    if (!A[0].isEmpty() && !A[0].equals("0000")) {
                        Anno = A[0];
                        sAlbum = A[1];
                    } else {
                        Anno = "";
                        sAlbum = A[1];
                    }
                } else {
                    Anno = "";
                    sAlbum = "";
                }
            }

            TextView txtBrano = (TextView) view.findViewById(R.id.txDettagliNome);
            TextView txtTraccia = (TextView) view.findViewById(R.id.txDettagliTraccia);
            TextView txtAlbum = (TextView) view.findViewById(R.id.txDettagliAlbum);
            TextView txtAnno = (TextView) view.findViewById(R.id.txDettagliAnno);
            TextView txtArtista = (TextView) view.findViewById(R.id.txDettagliArtista);
            TextView txtDime = (TextView) view.findViewById(R.id.txDettagliDimensioni);
            TextView txtStelle = (TextView) view.findViewById(R.id.txDettagliStelle);
            TextView txtDurata = (TextView) view.findViewById(R.id.txDettagliDurata);
            TextView txtAscoltato = (TextView) view.findViewById(R.id.txDettagliAscoltato);
            TextView txtMembri = (TextView) view.findViewById(R.id.txDettagliMembri);
            TextView txtImms = (TextView) view.findViewById(R.id.txDettagliNumImmagini);
            ImageView img = (ImageView) view.findViewById(R.id.imgDettagliMP3);
            ImageView imgCondividi = view.findViewById(R.id.imgCondividi);
            ImageView imgScaricoTesto = view.findViewById(R.id.imgScaricoTesto);
            if (!VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().isScaricaTestoBrano()) {
                imgScaricoTesto.setVisibility(LinearLayout.GONE);
            } else {
                imgScaricoTesto.setVisibility(LinearLayout.VISIBLE);
            }

            txtBrano.setText(sNomeBrano);
            txtTraccia.setText(Traccia);
            txtAlbum.setText("Album: " + sAlbum);
            txtAnno.setText("Anno: " + Anno);
            txtArtista.setText("Artista: " + Artista);

            long dime = sb.getDimensioni();
            String tipo[] = {"b.", "kb.", "mb."};
            int qualeTipo = 0;
            while (dime > 1024) {
                dime /= 1024;
                qualeTipo++;
            }

            txtDime.setText("Dim.: " + Long.toString(dime) + " " + tipo[qualeTipo]);
            txtStelle.setText("Stelle: " + Integer.toString(sb.getStelle()));

            String Mp3 = GestioneCaricamentoBraniNuovo.getInstance().RitornaNomeBrano();
            String Durata = GestioneImpostazioneBrani.getInstance().setDurata(Mp3);
            if (!Durata.isEmpty()) {
                String d[] = Durata.split(";", -1);
                String minutes = "";
                String seconds = "";

                try {
                    minutes = d[0];
                    seconds = d[1];
                } catch (Exception ignored) {
                    minutes = "";
                    seconds = "";
                }
                txtDurata.setText("Durata: " + minutes + ":" + seconds);
            } else {
                txtDurata.setText("");
            }

            if (sb.getQuanteVolteAscoltato() == 1) {
                txtAscoltato.setText("Ascoltato " + Integer.toString(sb.getQuanteVolteAscoltato()) + " volta");
            } else {
                txtAscoltato.setText("Ascoltato " + Integer.toString(sb.getQuanteVolteAscoltato()) + " volte");
            }

            String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
            String PathFile = "";
            if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
                PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/" + Album + ".jpg";
            } else {
                PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/"+sNomeBrano+".jpg";
            }

            PathFile = PathFile.replace("#","_");
            File f = new File(PathFile);
            if (f.exists()) {
                img.setImageBitmap(BitmapFactory.decodeFile(PathFile));
            } else {
                Drawable icona_nessuna = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.nessuna);
                img.setImageDrawable(icona_nessuna);
            }

            List<StrutturaMembri> s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getMembri();
            String ss = "Membri:\n";
            for (StrutturaMembri sss : s) {
                if (!sss.getMembro().isEmpty()) {
                    ss += sss.getMembro() + " " + sss.getDurata() + "\n";
                }
            }
            txtMembri.setText(ss);

            imgCondividi.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String fileUri = GestioneCaricamentoBraniNuovo.getInstance().RitornaNomeBrano();

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, fileUri);

                    intent.setType("audio/mpeg");
                    VariabiliStaticheGlobali.getInstance().getContext().startActivity(Intent.createChooser(intent, "Share MP3:"));
                }
            });

            imgScaricoTesto.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    GestioneTesti g = new GestioneTesti();
                    g.ScaricaTestoDaWeb(sb);
                }
            });

            txtImms.setText("Immagini: " + Integer.toString(VariabiliStaticheHome.getInstance().getImms().size()-1));
        }
    }
}
