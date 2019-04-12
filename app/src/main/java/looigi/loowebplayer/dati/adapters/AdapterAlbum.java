package looigi.loowebplayer.dati.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
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
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheLibreria;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dialog.DialogFiltro;

public class AdapterAlbum extends RecyclerView.Adapter<AdapterAlbum.MyViewHolder> {
    private List<StrutturaAlbum> horizontalList = Collections.emptyList();
    private Context context;

    public AdapterAlbum(List<StrutturaAlbum> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtview;

        public MyViewHolder(View view) {
            super(view);

            imageView=(ImageView) view.findViewById(R.id.imgAlbum);
            txtview=(TextView) view.findViewById(R.id.txtNomeAlbum);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_album, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final int idAlbum = horizontalList.get(position).getIdAlbum();
        final int idArtista = horizontalList.get(position).getIdArtista();

        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String Artista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista).getArtista();
        String Album = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlbum).getNomeAlbum();
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
        holder.txtview.setText(horizontalList.get(position).getNomeAlbum());

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
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

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                List<StrutturaBrani> brani = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaListaBrani(idArtista, idAlbum);

                LinearLayoutManager mLayoutManagerBrani = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

                VariabiliStaticheLibreria.getInstance().getRicBrani().setLayoutManager(mLayoutManagerBrani);
                VariabiliStaticheLibreria.getInstance().getRicBrani().setHasFixedSize(true);
                List<StrutturaBrani> datasetBrani=brani;
                AdapterBrani mAdapterBrani = new AdapterBrani(datasetBrani, context);
                VariabiliStaticheLibreria.getInstance().getRicBrani().setAdapter(mAdapterBrani);
            }
        });
    }


    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }
}
