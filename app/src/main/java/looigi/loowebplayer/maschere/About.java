package looigi.loowebplayer.maschere;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.utilities.GestioneFiles;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class About extends android.support.v4.app.Fragment {
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = this.getActivity();

        View view=null;

        try {
            view=(inflater.inflate(R.layout.about, container, false));
        } catch (Exception ignored) {

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

        //isVisible=isVisibleToUser;

        //if (isVisible) {
        //    initializeGraphic();
        //}
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //if (isVisible) {
        //    initializeGraphic();
        //}
    }

    private void initializeGraphic() {
        Context context = VariabiliStaticheGlobali.getInstance().getContext();
        View view = VariabiliStaticheGlobali.getInstance().getViewActivity();

        if (view != null) {
            TextView t = view.findViewById(R.id.txtVersione);
            t.setText("Versione "+ Utility.getInstance().getVersion(context));

            String tr = Traffico.getInstance().LeggeTrafficoTotale();
            String[] tt = tr.split(";",-1);

            TextView trr = view.findViewById(R.id.txtTrafficoTotale);
            TextView trm = view.findViewById(R.id.txtTrafficoMax);
            LinearLayout l = view.findViewById(R.id.layTraffico);

            if (tt.length>0) {
                trr.setText("Traffico totale:" + Traffico.getInstance().ModificaTraffico(Long.parseLong(tt[0])));
                trr.setVisibility(LinearLayout.VISIBLE);
                l.setVisibility(LinearLayout.VISIBLE);
            } else {
                trr.setVisibility(LinearLayout.INVISIBLE);
                trm.setVisibility(LinearLayout.INVISIBLE);
                l.setVisibility(LinearLayout.INVISIBLE);
            }

            if (tt.length>2) {
                trm.setText("Max traffico: " + tt[1]+" "+Traffico.getInstance().ModificaTraffico(Long.parseLong(tt[2])));
                trm.setVisibility(LinearLayout.VISIBLE);
            } else {
                trm.setVisibility(LinearLayout.INVISIBLE);
            }

            if (tt.length>4) {
                trm.setText(trm.getText()+"\nMin traffico: " + tt[3]+" "+Traffico.getInstance().ModificaTraffico(Long.parseLong(tt[4])));
                trm.setVisibility(LinearLayout.VISIBLE);
            } else {
                trm.setVisibility(LinearLayout.INVISIBLE);
            }

            if (tt.length>5) {
                trm.setText(trm.getText()+"\nMedia: " + Traffico.getInstance().ModificaTraffico(Long.parseLong(tt[5])));
                trm.setVisibility(LinearLayout.VISIBLE);
            } else {
                trm.setVisibility(LinearLayout.INVISIBLE);
            }

            if (tt.length>6) {
                trm.setText(trm.getText()+"\nMese attuale: " + Traffico.getInstance().ModificaTraffico(Long.parseLong(tt[6])));
                trm.setVisibility(LinearLayout.VISIBLE);
            } else {
                trm.setVisibility(LinearLayout.INVISIBLE);
            }

            String pathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/";
            String nomeFile = "VersioneLibreria.txt";
            String ver = "";
            if (GestioneFiles.getInstance().fileExistsInSD(nomeFile, pathFile)) {
                ver = GestioneFiles.getInstance().LeggeFileDiTesto(pathFile + nomeFile);
            } else {
                ver = "Versione non disponibile";
            }

            TextView t2 = view.findViewById(R.id.txtVersioneLibreria);
            t2.setText("Versione libreria " + ver);

        }
    }
}
