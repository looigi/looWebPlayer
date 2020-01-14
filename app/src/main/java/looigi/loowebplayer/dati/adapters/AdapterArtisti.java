package looigi.loowebplayer.dati.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheLibreria;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaArtisti;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.db_locale.DBLocaleEsclusi;
import looigi.loowebplayer.dialog.DialogFiltro;
import looigi.loowebplayer.utilities.GestioneFiles;

public class AdapterArtisti extends RecyclerView.Adapter<AdapterArtisti.MyViewHolder> {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("AdapterArtisti");;
    private List<StrutturaArtisti> horizontalList = Collections.emptyList();
    private Context context;
    private boolean Escluso;
    private String PathDirectory;
    private List<String> immagini;

    public AdapterArtisti(List<StrutturaArtisti> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layer;
        ImageView imageView;
        TextView txtview;
        CheckBox chkView;

        public MyViewHolder(View view) {
            super(view);

            layer=(LinearLayout) view.findViewById(R.id.layArtista);
            imageView=(ImageView) view.findViewById(R.id.imgArtista);
            txtview=(TextView) view.findViewById(R.id.txtNomeArtista);
            chkView=(CheckBox) view.findViewById(R.id.chkEsclusa);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_artista, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final String Artista = horizontalList.get(position).getArtista();
        // final int idArtista = horizontalList.get(position).getIdArtista();
        Escluso = horizontalList.get(position).isEscluso();
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String p = "";
        if (!pathBase.equals(Artista)) {
            p = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/";
        } else {
            p = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/";
        }
        PathDirectory = p;
        immagini = new ArrayList<>();

        try {
            List<String> lista = GestioneFiles.getInstance().RitornaListaDirectory(PathDirectory);
            for (String d : lista) {
                List<String> files = GestioneFiles.getInstance().RitornaListaFilesInDirectory(PathDirectory+d);
                for (String ff : files) {
                    if (ff.toUpperCase().contains(".JPG")) {
                        immagini.add(d+"/"+ff);
                    }
                }
            }
            List<String> files = GestioneFiles.getInstance().RitornaListaFilesInDirectory(PathDirectory);
            for (String ff : files) {
                if (ff.toUpperCase().contains(".JPG")) {
                    immagini.add(ff);
                }
            }

            holder.imageView.setVisibility(LinearLayout.INVISIBLE);
            if (immagini.size() > 0) {
                Random r = new Random();
                int immNumber = r.nextInt(((immagini.size() - 1) - 0) + 1) + 0;

                Bitmap b = BitmapFactory.decodeFile(PathDirectory+immagini.get(immNumber));
                Drawable drawable = new BitmapDrawable(VariabiliStaticheGlobali.getInstance().getContext().getResources(), b);
                holder.layer.setBackground(drawable);
                if (Escluso) {
                    holder.imageView.setBackgroundResource(R.drawable.escluso);
                    holder.imageView.setVisibility(LinearLayout.VISIBLE);
                }
            } else {
                holder.layer.setBackgroundResource(R.drawable.ic_launcher);
                if (Escluso) {
                    holder.imageView.setImageResource(R.drawable.escluso);;
                    holder.imageView.setVisibility(LinearLayout.VISIBLE);
                }
            }
        } catch (Exception e) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "AdapterArtisti. Riempimento immagini");
            VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            holder.layer.setBackgroundResource(R.drawable.ic_launcher);
        }

        holder.txtview.setText(Artista);

        holder.chkView.setChecked(Escluso);
        holder.chkView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                DBLocaleEsclusi db = new DBLocaleEsclusi();
                Escluso = horizontalList.get(position).isEscluso();
                int idArtista = horizontalList.get(position).getIdArtista();

                List<StrutturaAlbum> lstAlbum = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaListaAlbum(idArtista);
                if (Escluso) {
                    horizontalList.get(position).setEscluso(false);
                    holder.chkView.setChecked(false);
                    db.cancellaEclusione(Artista, "", "");
                    int idArt = horizontalList.get(position).getIdArtista();
                    StrutturaArtisti sar = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArt);
                    sar.setEscluso(false);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaArtista(idArtista, sar);
                    for (StrutturaAlbum sa : lstAlbum) {
                        String Album = sa.getNomeAlbum();
                        int idAlbum = sa.getIdAlbum();
                        db.cancellaEclusione(Artista, Album, "");
                        sa.setEscluso(false);
                        VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaAlbum(idAlbum, sa);

                        List<StrutturaBrani> lstBrani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaListaBrani(idArtista, idAlbum);
                        for (StrutturaBrani s : lstBrani) {
                            int idBra = s.getIdBrano();
                            db.cancellaEclusione(Artista, Album, s.getNomeBrano());
                            s.setEscluso(false);
                            VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaBrano(idBra, s);
                        }
                    }
                    holder.imageView.setVisibility(LinearLayout.INVISIBLE);
                    Escluso = false;
                } else {
                    horizontalList.get(position).setEscluso(true);
                    holder.chkView.setChecked(true);
                    db.inserisciEsclusione(Artista, "", "");
                    int idArt = horizontalList.get(position).getIdArtista();
                    StrutturaArtisti sar = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArt);
                    sar.setEscluso(true);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaArtista(idArtista, sar);
                    for (StrutturaAlbum sa : lstAlbum) {
                        String Album = sa.getNomeAlbum();
                        int idAlbum = sa.getIdAlbum();
                        db.inserisciEsclusione(Artista, Album, "");
                        sa.setEscluso(true);
                        VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaAlbum(idAlbum, sa);

                        List<StrutturaBrani> lstBrani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaListaBrani(idArtista, idAlbum);
                        for (StrutturaBrani s : lstBrani) {
                            int idBra = s.getIdBrano();
                            db.inserisciEsclusione(Artista, Album, s.getNomeBrano());
                            s.setEscluso(true);
                            VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaBrano(idBra, s);
                        }
                    }
                    holder.imageView.setImageResource(R.drawable.escluso);
                    holder.imageView.setVisibility(LinearLayout.VISIBLE);
                    Escluso = true;
                }
                RicaricaAlbumEBrani(idArtista);
            }
        });

        holder.layer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String Artista=horizontalList.get(position).getArtista();
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Lungo click su artista "+Artista);

                DialogFiltro.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                        "Si vuole impostare il filtro sull'artista\n\n"+Artista, "ARTISTA",
                        Artista, VariabiliStaticheGlobali.NomeApplicazione);

                return true;
            }
        });

        holder.layer.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                int idArtista = horizontalList.get(position).getIdArtista();
                RicaricaAlbumEBrani(idArtista);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }

    private void RicaricaAlbumEBrani(int idArtista) {
        List<StrutturaAlbum> album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaListaAlbum(idArtista);

        // Riempio lista album
        LinearLayoutManager mLayoutManagerAlbum = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        VariabiliStaticheLibreria.getInstance().getRicAlbum().setLayoutManager(mLayoutManagerAlbum);
        VariabiliStaticheLibreria.getInstance().getRicAlbum().setHasFixedSize(true);
        List<StrutturaAlbum> datasetAlbum=album;
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
