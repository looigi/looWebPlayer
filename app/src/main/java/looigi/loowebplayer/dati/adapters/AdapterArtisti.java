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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheLibreria;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaArtisti;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.dialog.DialogFiltro;
import looigi.loowebplayer.utilities.GestioneFiles;

public class AdapterArtisti extends RecyclerView.Adapter<AdapterArtisti.MyViewHolder> {
    private List<StrutturaArtisti> horizontalList = Collections.emptyList();
    private Context context;

    public AdapterArtisti(List<StrutturaArtisti> horizontalList, Context context) {
        this.horizontalList = horizontalList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtview;

        public MyViewHolder(View view) {
            super(view);

            imageView=(ImageView) view.findViewById(R.id.imgArtista);
            txtview=(TextView) view.findViewById(R.id.txtNomeArtista);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_artista, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String Artista = horizontalList.get(position).getArtista();
        String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
        String p = "";
        if (!pathBase.equals(Artista)) {
            p = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/";
        } else {
            p = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/";
        }
        final String PathDirectory = p;
        List<String> immagini = new ArrayList<>();

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

            if (immagini.size() > 0) {
                Random r = new Random();
                int immNumber = r.nextInt(((immagini.size() - 1) - 0) + 1) + 0;
                holder.imageView.setImageBitmap(BitmapFactory.decodeFile(PathDirectory+immagini.get(immNumber)));
            } else {
                holder.imageView.setImageResource(R.drawable.ic_launcher);
            }
        } catch (Exception e) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "AdapterArtisti. Riempimento immagini");
            VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            holder.imageView.setImageResource(R.drawable.ic_launcher);
        }

        holder.txtview.setText(Artista);

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String Artista=horizontalList.get(position).getArtista();
                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Lungo click su artista "+Artista);

                DialogFiltro.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                        "Si vuole impostare il filtro sull'artista\n\n"+Artista, "ARTISTA",
                        Artista, VariabiliStaticheGlobali.NomeApplicazione);

                return true;
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                int idArtista = horizontalList.get(position).getIdArtista();
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
        });
    }


    @Override
    public int getItemCount()
    {
        return horizontalList.size();
    }
}
