package looigi.loowebplayer.maschere;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheUtenza;
import looigi.loowebplayer.dati.NomiMaschere;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaUtenti;
import looigi.loowebplayer.db_locale.DBLocaleUtenti;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.utilities.Utility;

public class Utenza extends android.support.v4.app.Fragment {
    private Context context;
    private static String TAG = NomiMaschere.getInstance().getUtenza();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = this.getActivity();

        View view=null;

        try {
            view=(inflater.inflate(R.layout.utenza, container, false));
        } catch (Exception ignored) {
            int e=0;
        }

        if (view!=null) {
            VariabiliStaticheGlobali.getInstance().setViewActivity(view);

            initializeGraphic();
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void initializeGraphic() {
        final Context context = VariabiliStaticheGlobali.getInstance().getContext();
        View view = VariabiliStaticheGlobali.getInstance().getViewActivity();

        if (view != null) {
            VariabiliStaticheUtenza.getInstance().setEdtUtente((EditText) view.findViewById(R.id.edtUtente));
            VariabiliStaticheUtenza.getInstance().setEdtPassword((EditText) view.findViewById(R.id.edtPassword));
            VariabiliStaticheUtenza.getInstance().setTxtErrore((TextView) view.findViewById(R.id.txtErrore));

            VariabiliStaticheUtenza.getInstance().getTxtErrore().setVisibility(LinearLayout.INVISIBLE);

            Button btnAnnulla = view.findViewById(R.id.btnAnnulla);
            btnAnnulla.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    System.exit(0);
                }
            });

            ImageView imgUtenza = view.findViewById(R.id.imgUtente);
            String PathImmagine = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Utente";
            StrutturaUtenti s = VariabiliStaticheGlobali.getInstance().getUtente();
            String Utente = s.getUtente();
            imgUtenza.setImageBitmap(BitmapFactory.decodeFile(PathImmagine + "/" + Utente + ".jpg"));

            TextView txtUtente = view.findViewById(R.id.txtUtente);
            txtUtente.setText(Utente);

            TextView txtCartella = view.findViewById(R.id.txtCartella);
            txtCartella.setText(s.getCartellaBase());

            TextView txtTipoUtente = view.findViewById(R.id.txtTipoUtente);
            if (s.isAmministratore()) {
                txtTipoUtente.setText("Amministratore");
            } else {
                txtTipoUtente.setText("Utente");
            }

            Button btnOk = view.findViewById(R.id.btnOk);
            btnOk.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String Utente = VariabiliStaticheUtenza.getInstance().getEdtUtente().getText().toString();
                    String Password = VariabiliStaticheUtenza.getInstance().getEdtPassword().getText().toString();

                    if (Utente.trim().isEmpty()) {
                        VariabiliStaticheUtenza.getInstance().getTxtErrore().setText(R.string.utente_non_valido);
                        VariabiliStaticheUtenza.getInstance().getTxtErrore().setVisibility(LinearLayout.VISIBLE);
                    } else {
                        if (Password.trim().isEmpty()) {
                            VariabiliStaticheUtenza.getInstance().getTxtErrore().setText(R.string.password_non_valida);
                            VariabiliStaticheUtenza.getInstance().getTxtErrore().setVisibility(LinearLayout.VISIBLE);
                        } else {
                            int NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download dati Utente");
                            DBRemotoNuovo dbr = new DBRemotoNuovo();
                            dbr.RitornaDatiUtente(context, Utente, NumeroOperazione);
                        }
                    }
                }
            });

            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public static void RitornaUtente(String Ritorno) {
        if (Ritorno.toUpperCase().contains("ERROR:")) {
            VariabiliStaticheUtenza.getInstance().getTxtErrore().setText(Ritorno);
            VariabiliStaticheUtenza.getInstance().getTxtErrore().setVisibility(LinearLayout.VISIBLE);
        } else {
            String PasswordImmessa = VariabiliStaticheUtenza.getInstance().getEdtPassword().getText().toString();
            String c[] = Ritorno.split(";",-1);

            String Password = c[2];
            if (Password.equals(PasswordImmessa)) {
                StrutturaUtenti s = new StrutturaUtenti();
                s.setIdUtente(Integer.parseInt(c[0]));
                s.setUtente(c[1]);
                s.setPassword(c[2]);
                if (c[3].toUpperCase().trim().equals("S")) {
                    s.setAmministratore(true);
                } else {
                    s.setAmministratore(false);
                }
                s.setCartellaBase(c[4]);
                VariabiliStaticheGlobali.getInstance().setUtente(s);

                DBLocaleUtenti db = new DBLocaleUtenti();
                // db.open();
                String amm = "N";
                if (s.isAmministratore()) {
                    amm = "S";
                }
                long id = db.inserisciUtente(s.getUtente(), s.getPassword(), amm, s.getCartellaBase(), "-1", "0");
                // db.close();

                String path=VariabiliStaticheGlobali.getInstance().PercorsoDIR+"/Lista.dat";
                File f = new File(path);
                if (f.exists()) {
                    f.delete();
                }
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                        new Object(){}.getClass().getEnclosingMethod().getName(),
                        "Interpello il ws per la lista brani");

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
                }.getClass().getEnclosingMethod().getName(), "Interpello il ws per la lista brani");
                int NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download Lista Brani");
                DBRemotoNuovo dbr = new DBRemotoNuovo();
                dbr.RitornaListaBrani(VariabiliStaticheGlobali.getInstance().getContext(),
                        "", "", "", "", "S", "N",
                        NumeroOperazione);

                // GestioneListaBrani.getInstance().setModalitaAvanzamento(RANDOM);
                // VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setQualeCanzoneStaSuonando(-1);
                // db.close();

                Utility.getInstance().CambiaMaschera(R.id.home);
            } else {
                VariabiliStaticheUtenza.getInstance().getTxtErrore().setText(R.string.password_non_valida);
                VariabiliStaticheUtenza.getInstance().getTxtErrore().setVisibility(LinearLayout.VISIBLE);
            }
        }
    }
}
