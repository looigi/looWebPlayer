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

import java.io.File;
import java.util.Collections;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheLibreria;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaArtisti;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.db_locale.DBLocaleEsclusi;
import looigi.loowebplayer.dialog.DialogFiltro;

public class AdapterAlbum extends RecyclerView.Adapter<AdapterAlbum.MyViewHolder> {
    private List<StrutturaAlbum> horizontalList = Collections.emptyList();
    private Context context;
    private boolean Escluso;
    private String PathFile;
    private int idAlbum;

    public AdapterAlbum(List<StrutturaAlbum> horizontalList, Context context) {
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

            layer=(LinearLayout) view.findViewById(R.id.layAlbum);
            imageView=(ImageView) view.findViewById(R.id.imgAlbum);
            txtview=(TextView) view.findViewById(R.id.txtNomeAlbum);
            chkView=(CheckBox) view.findViewById(R.id.chkEsclusa);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_album, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        idAlbum = horizontalList.get(position).getIdAlbum();
        final int idArtista = horizontalList.get(position).getIdArtista();

        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        final String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getArtista();
        final String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlbum).getNomeAlbum();
        Escluso = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlbum).isEscluso();
        PathFile = "";
        if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/" + Album + ".jpg";
        } else {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/Album.jpg";
        }
        File f = new File(PathFile);
        holder.imageView.setVisibility(LinearLayout.INVISIBLE);
        if (f.exists()) {
            Bitmap b = BitmapFactory.decodeFile(PathFile);
            Drawable drawable = new BitmapDrawable(VariabiliStaticheGlobali.getInstance().getContext().getResources(), b);
            holder.layer.setBackground(drawable);
            if (Escluso) {
                holder.imageView.setBackgroundResource(R.drawable.escluso);
                holder.imageView.setVisibility(LinearLayout.VISIBLE);
            }
        } else {
            holder.layer.setBackgroundResource(R.drawable.ic_launcher);
            if (Escluso) {
                holder.imageView.setBackgroundResource(R.drawable.escluso);;
                holder.imageView.setVisibility(LinearLayout.VISIBLE);
            }
        }
        holder.txtview.setText(horizontalList.get(position).getNomeAlbum());

        holder.chkView.setChecked(Escluso);
        holder.chkView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                DBLocaleEsclusi db = new DBLocaleEsclusi();
                Escluso = horizontalList.get(position).isEscluso();

                List<StrutturaBrani> lstBrani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaListaBrani(idArtista, idAlbum);
                if (Escluso) {
                    horizontalList.get(position).setEscluso(false);
                    holder.chkView.setChecked(false);
                    db.cancellaEclusione(Artista, horizontalList.get(position).getNomeAlbum(), "");
                    int idAlbum = horizontalList.get(position).getIdAlbum();
                    StrutturaAlbum sa = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlbum);
                    sa.setEscluso(false);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaAlbum(idAlbum, sa);
                    for (StrutturaBrani s : lstBrani) {
                        int idBra = s.getIdBrano();
                        db.cancellaEclusione(Artista, horizontalList.get(position).getNomeAlbum(), s.getNomeBrano());
                        s.setEscluso(false);
                        VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaBrano(idBra, s);
                    }
                    holder.imageView.setVisibility(LinearLayout.INVISIBLE);
                    Escluso = false;
                } else {
                    horizontalList.get(position).setEscluso(true);
                    holder.chkView.setChecked(true);
                    db.inserisciEsclusione(Artista, horizontalList.get(position).getNomeAlbum(), "");
                    int idAlbum = horizontalList.get(position).getIdAlbum();
                    StrutturaAlbum sa = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlbum);
                    sa.setEscluso(true);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaAlbum(idAlbum, sa);
                    for (StrutturaBrani s : lstBrani) {
                        int idBra = s.getIdBrano();
                        db.inserisciEsclusione(Artista, horizontalList.get(position).getNomeAlbum(), s.getNomeBrano());
                        s.setEscluso(true);
                        VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaBrano(idBra, s);
                    }
                    holder.imageView.setImageResource(R.drawable.escluso);
                    holder.imageView.setVisibility(LinearLayout.VISIBLE);
                    Escluso = true;
                }

                int idArtista = horizontalList.get(position).getIdArtista();
                RicaricaBrani(idArtista);
            }
        });

        holder.layer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String Album=horizontalList.get(position).getNomeAlbum();
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Lungo click su album "+Album);

                DialogFiltro.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                        "Si vuole impostare il filtro sull'album\n\n"+Album, "ALBUM",
                        Album, VariabiliStaticheGlobali.NomeApplicazione);

                return true;
            }
        });

        holder.layer.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                int idArtista = horizontalList.get(position).getIdArtista();
                RicaricaBrani(idArtista);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }

    private void RicaricaBrani(int idArtista) {
        List<StrutturaBrani> brani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaListaBrani(idArtista, idAlbum);

        LinearLayoutManager mLayoutManagerBrani = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        VariabiliStaticheLibreria.getInstance().getRicBrani().setLayoutManager(mLayoutManagerBrani);
        VariabiliStaticheLibreria.getInstance().getRicBrani().setHasFixedSize(true);
        List<StrutturaBrani> datasetBrani=brani;
        AdapterBrani mAdapterBrani = new AdapterBrani(datasetBrani, context);
        VariabiliStaticheLibreria.getInstance().getRicBrani().setAdapter(mAdapterBrani);
    }
}
