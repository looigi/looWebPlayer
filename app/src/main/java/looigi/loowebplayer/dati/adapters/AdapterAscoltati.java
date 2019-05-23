package looigi.loowebplayer.dati.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaAlbum;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaArtisti;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaBrani;
import looigi.loowebplayer.utilities.GestioneCaricamentoBraniNuovo;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.Utility;

public class AdapterAscoltati extends ArrayAdapter
{
	private Context context;
	private List<Integer> lista;

	public AdapterAscoltati(Context context, int textViewResourceId, List<Integer> objects)
	{	
		super(context, textViewResourceId, objects);
		
		this.context = context;
		this.lista=objects;
	}
	
	@Override
	@Nullable
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.listview_mp3, null);

		int riga = lista.get(position);
		boolean prossimo = false;
		if (riga < 0) {
			riga=-riga;
			prossimo=true;
		}
		StrutturaBrani s = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaBrano(riga);

		int idCanzone = s.getIdBrano();
		String Brano = s.getNomeBrano();
		int idAlbum = s.getIdAlbum();
		StrutturaAlbum sAlbum = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaAlbum(idAlbum);
		String Album = sAlbum.getNomeAlbum();
		int idArtista = s.getIdArtista();
		StrutturaArtisti sArtista = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaArtista(idArtista);
		String Artista = sArtista.getArtista();

		int indice = GestioneListaBrani.getInstance().RitornaIndiceBranoAttuale();
		int id = GestioneListaBrani.getInstance().RitornaIdInBaseAllIndice(indice);
		if (prossimo) {
			convertView.setBackgroundColor(Color.argb(255, 0,220, 0));
		} else {
			if (id == idCanzone) {
				convertView.setBackgroundColor(Color.argb(255, 220, 0, 0));
				VariabiliStaticheHome.getInstance().getLstListaBrani().smoothScrollToPosition(position);
			} else {
				if (Utility.getInstance().ePari(position)) {
					convertView.setBackgroundColor(Color.WHITE);
				} else {
					convertView.setBackgroundColor(Color.argb(255, 230, 230, 230));
				}
			}
		}

		TextView txtId = convertView.findViewById(R.id.idCanzone);
        TextView txtBrano = convertView.findViewById(R.id.txtBrano);
		TextView txtArtista = convertView.findViewById(R.id.txtArtista);
		TextView txtAlbum = convertView.findViewById(R.id.txtAlbum);
		ImageView imgCanzone = convertView.findViewById(R.id.imgCanzone);

		String pathBase = VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
		String PathFile = "";
		if (!pathBase.equals(Artista) && !Artista.equals(Album)) {
			PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/" + Artista + "/" + Album + ".jpg";
		} else {
			PathFile = VariabiliStaticheGlobali.getInstance().PercorsoDIR + "/Immagini/" + pathBase + "/"+Brano+".jpg";
		}
		PathFile = PathFile.replace("#","_");
		File f = new File(PathFile);
		if (f.exists()) {
			imgCanzone.setImageBitmap(BitmapFactory.decodeFile(PathFile));
		} else {
			Drawable icona_nessuna = ContextCompat.getDrawable(VariabiliStaticheGlobali.getInstance().getContext(), R.drawable.nessuna);
			imgCanzone.setImageDrawable(icona_nessuna);
		}

		txtId.setText(Integer.toString(idCanzone));
		txtBrano.setText(Brano);
		txtArtista.setText(Artista);
		txtAlbum.setText(Album);

		convertView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int idCanzone = lista.get(position);

				if (idCanzone>=0) {
					List<Integer> l = new ArrayList<Integer>(GestioneListaBrani.getInstance().RitornaListaBrani());
					int i = 0;
					for (Integer ll : l) {
						if (ll == idCanzone) {
							break;
						}
						i++;
					}
					if (i < l.size() - 1) {
						int NumeroBrano = l.get(i);
						GestioneListaBrani.getInstance().SettaIndice(i);
						if (NumeroBrano > -1) {
							VariabiliStaticheGlobali.getInstance().getDatiGenerali()
									.getConfigurazione().setQualeCanzoneStaSuonando(NumeroBrano);

							VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
									}.getClass().getEnclosingMethod().getName(),
									"Impostazione brano esatto: " + Integer.toString(NumeroBrano));
							GestioneCaricamentoBraniNuovo.getInstance().CaricaBrano();
							VariabiliStaticheHome.getInstance().getRltListaBrani().setVisibility(LinearLayout.GONE);
						}
					}
				}
			}
		});

		return convertView;
	}
}
