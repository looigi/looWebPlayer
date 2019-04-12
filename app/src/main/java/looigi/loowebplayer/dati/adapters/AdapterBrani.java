package looigi.loowebplayer.dati.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Collections;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dialog.DialogFiltro;
import looigi.loowebplayer.utilities.GestioneCaricamentoBraniNuovo;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.Utility;

public class AdapterBrani extends RecyclerView.Adapter<AdapterBrani.MyViewHolder> {
    private List<StrutturaBrani> horizontalList = Collections.emptyList();
    private Context context;

    public AdapterBrani(List<StrutturaBrani> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtview;

        public MyViewHolder(View view) {
            super(view);

            imageView=(ImageView) view.findViewById(R.id.imgBrano);
            txtview=(TextView) view.findViewById(R.id.txtNomeBrano);
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

        String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getArtista();
        String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlbum).getNomeAlbum();
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String PathFile = "";
        if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/" + Album + ".jpg";
        } else {
            PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/Album.jpg";
        }
        File f = new File(PathFile);
        if (f.exists()) {
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(PathFile));
        }
        holder.txtview.setText(horizontalList.get(position).getNomeBrano());

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
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

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                int idBrano = horizontalList.get(position).getIdBrano();
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setNumeroBranoInAscolto(idBrano);
                VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().setQualeCanzoneStaSuonando(idBrano);
                GestioneListaBrani.getInstance().AggiungeBrano(idBrano);
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
