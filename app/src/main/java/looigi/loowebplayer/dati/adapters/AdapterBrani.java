package looigi.loowebplayer.dati.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.db_locale.DBLocaleEsclusi;
import looigi.loowebplayer.dialog.DialogFiltro;
import looigi.loowebplayer.utilities.GestioneCaricamentoBraniNuovo;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.Utility;

public class AdapterBrani extends RecyclerView.Adapter<AdapterBrani.MyViewHolder> {
    private List<StrutturaBrani> horizontalList = Collections.emptyList();
    private Context context;
    private boolean Escluso;
    private String PathFile;

    public AdapterBrani(List<StrutturaBrani> horizontalList, Context context) {
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

            layer=(LinearLayout) view.findViewById(R.id.layBrani);
            imageView=(ImageView) view.findViewById(R.id.imgBrano);
            txtview=(TextView) view.findViewById(R.id.txtNomeBrano);
            chkView=(CheckBox) view.findViewById(R.id.chkEsclusa);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_brani, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final int idAlbum = horizontalList.get(position).getIdAlbum();
        final int idArtista = horizontalList.get(position).getIdArtista();

        final String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getArtista();
        final String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlbum).getNomeAlbum();
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        PathFile = "";
        if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/" + Album + ".jpg";
        } else {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/Album.jpg";
        }
        File f = new File(PathFile);
        Escluso = horizontalList.get(position).isEscluso();
        holder.imageView.setVisibility(LinearLayout.INVISIBLE);
        if (f.exists()) {
            Bitmap b = BitmapFactory.decodeFile(PathFile);
            Drawable drawable = new BitmapDrawable(VariabiliStaticheGlobali.getInstance().getContext().getResources(), b);
            holder.layer.setBackground(drawable);
            if (Escluso) {
                holder.imageView.setImageResource(R.drawable.escluso);;
                holder.imageView.setVisibility(LinearLayout.VISIBLE);
            }
        } else {
            holder.layer.setBackgroundResource(R.drawable.ic_launcher);
            if (Escluso) {
                holder.imageView.setImageResource(R.drawable.escluso);;
                holder.imageView.setVisibility(LinearLayout.VISIBLE);
            }
        }
        holder.txtview.setText(horizontalList.get(position).getNomeBrano());

        holder.chkView.setChecked(Escluso);
        holder.chkView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                DBLocaleEsclusi db = new DBLocaleEsclusi();
                Escluso = horizontalList.get(position).isEscluso();

                if (Escluso) {
                    horizontalList.get(position).setEscluso(false);
                    holder.chkView.setChecked(false);
                    db.cancellaEclusione(Artista, Album, horizontalList.get(position).getNomeBrano());
                    int idBra = horizontalList.get(position).getIdBrano();
                    StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBra);
                    s.setEscluso(false);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaBrano(idBra, s);

                    holder.imageView.setVisibility(LinearLayout.INVISIBLE);
                    Escluso = false;
                } else {
                    horizontalList.get(position).setEscluso(true);
                    holder.chkView.setChecked(true);
                    db.inserisciEsclusione(Artista, Album, horizontalList.get(position).getNomeBrano());
                    int idBra = horizontalList.get(position).getIdBrano();
                    StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(idBra);
                    s.setEscluso(true);
                    VariabiliStaticheGlobali.getInstance().getDatiGenerali().ImpostaBrano(idBra, s);

                    holder.imageView.setImageResource(R.drawable.escluso);
                    holder.imageView.setVisibility(LinearLayout.VISIBLE);
                    Escluso = true;
                }
            }
        });

        holder.layer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String Brano=horizontalList.get(position).getNomeBrano();
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Lungo click su brano "+Brano);

                DialogFiltro.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                        "Si vuole impostare il filtro sul\ntitolo del brano o parte di esso\n\n"+Brano, "BRANO",
                        Brano, VariabiliStaticheGlobali.NomeApplicazione);

                return true;
            }
        });

        holder.layer.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                int idBrano = horizontalList.get(position).getIdBrano();
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setNumeroBranoInAscolto(idBrano);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setQualeCanzoneStaSuonando(idBrano);
                // GestioneListaBrani.getInstance().AggiungeBrano(idBrano);
                GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano();
                Utility.getInstance().CambiaMaschera(R.id.home);
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }
}
