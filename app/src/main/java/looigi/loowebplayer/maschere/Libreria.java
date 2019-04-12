package looigi.loowebplayer.maschere;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheLibreria;
import looigi.loowebplayer.dati.NomiMaschere;
import looigi.loowebplayer.dati.adapters.AdapterAlbum;
import looigi.loowebplayer.dati.adapters.AdapterArtisti;
import looigi.loowebplayer.dati.adapters.AdapterBrani;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaArtisti;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dialog.DialogMessaggio;

public class Libreria extends android.support.v4.app.Fragment {
    private Context context;
    private static String TAG = NomiMaschere.getInstance().getLibreria();
    private int pos=0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = this.getActivity();

        View view=null;

        try {
            view=(inflater.inflate(R.layout.libreria, container, false));
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
        final View view = VariabiliStaticheGlobali.getInstance().getViewActivity();

        if (view != null) {
            VariabiliStaticheLibreria.getInstance().setRicArtisti((RecyclerView) view.findViewById(R.id.recArtisti));
            VariabiliStaticheLibreria.getInstance().setRicAlbum((RecyclerView) view.findViewById(R.id.recAlbum));
            VariabiliStaticheLibreria.getInstance().setRicBrani((RecyclerView) view.findViewById(R.id.recBrani));

            // VariabiliStaticheGlobali.getInstance().setGiaEntrato(false);

            ReimpostaListe();

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

            Button cmdRicerca = view.findViewById(R.id.cmdRicerca);
            cmdRicerca.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    EditText edtRicerca = view.findViewById(R.id.edtRicerca);
                    String Ricerca = edtRicerca.getText().toString().trim().toUpperCase();
                    if (Ricerca.isEmpty()) {
                        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getContext(),
                                "Selezionare un filtro",
                                false,
                                VariabiliStaticheGlobali.NomeApplicazione);
                    } else {
                        List<StrutturaArtisti> datasetArtisti=VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtisti();
                        int pos2=0;
                        for (StrutturaArtisti sa : datasetArtisti) {
                            if (pos2>pos) {
                                String sar = sa.getArtista().toUpperCase().trim();
                                if (sar.contains(Ricerca)) {
                                    pos=pos2;
                                    break;
                                }
                                pos2++;
                                if (pos2> datasetArtisti.size()-1) {
                                    pos2=-1;
                                    pos=0;
                                    break;
                                }
                            } else {
                                pos2++;
                            }
                        }

                        if (pos2>-1) {
                            VariabiliStaticheLibreria.getInstance().getRicArtisti().scrollToPosition(pos2);
                        } else {
                            pos2=0;
                        }
                    }
                }
            });

            Button cmdAnnulla = view.findViewById(R.id.cmdAnnulla);
            cmdAnnulla.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    EditText edtRicerca = view.findViewById(R.id.edtRicerca);
                    edtRicerca.setText("");
                    pos=0;
                    VariabiliStaticheLibreria.getInstance().getRicArtisti().scrollToPosition(pos);
                }
            });
        }
    }

    public static void ReimpostaListe() {
        Context context = VariabiliStaticheGlobali.getInstance().getContext();

        // Lista orizzontale album
        LinearLayoutManager mLayoutManagerArtisti = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        VariabiliStaticheLibreria.getInstance().getRicArtisti().setLayoutManager(mLayoutManagerArtisti);
        VariabiliStaticheLibreria.getInstance().getRicArtisti().setHasFixedSize(true);
        List<StrutturaArtisti> datasetArtisti=VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtisti();
        AdapterArtisti mAdapterArtisti = new AdapterArtisti(datasetArtisti, context);
        VariabiliStaticheLibreria.getInstance().getRicArtisti().setAdapter(mAdapterArtisti);

        // Pulisco lista album
        LinearLayoutManager mLayoutManagerAlbum = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        VariabiliStaticheLibreria.getInstance().getRicAlbum().setLayoutManager(mLayoutManagerAlbum);
        VariabiliStaticheLibreria.getInstance().getRicAlbum().setHasFixedSize(true);
        List<StrutturaAlbum> datasetAlbum=new ArrayList<>();
        AdapterAlbum mAdapterAlbum = new AdapterAlbum(datasetAlbum, context);
        VariabiliStaticheLibreria.getInstance().getRicAlbum().setAdapter(mAdapterAlbum);

        // Pulisco lista brani
        LinearLayoutManager mLayoutManagerBrani = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        VariabiliStaticheLibreria.getInstance().getRicBrani().setLayoutManager(mLayoutManagerBrani);
        VariabiliStaticheLibreria.getInstance().getRicBrani().setHasFixedSize(true);
        List<StrutturaBrani> datasetBrani=new ArrayList<>();
        AdapterBrani mAdapterBrani = new AdapterBrani(datasetBrani, context);
        VariabiliStaticheLibreria.getInstance().getRicBrani().setAdapter(mAdapterBrani);
    }
}
