package looigi.loowebplayer.db_remoto;

import android.content.Context;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.soap.GestioneWEBServiceSOAPNuovo;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.PronunciaFrasi;

public class DBRemotoNuovo {
	private String ws = "looWPlayer.asmx/";
	private String NS="http://looWebPlayer.org/";
	private String SA="http://looWebPlayer.org/";

	private String ToglieCaratteriStrani(String Cosa) {
		if (Cosa!=null) {
			String sCosa = Cosa.replace("?", "***PI***");
			sCosa = sCosa.replace("&", "***AND***");

			return sCosa;
		} else {
			return "";
		}
	}

	public void RitornaListaBrani(Context context, String Artista, String Album, String Brano,
								  String Filtro, String Refresh, String Dettagli, int NumeroOperazione) {
		String Urletto="RitornaListaBrani?";
		Urletto+="NomeUtente=" + VariabiliStaticheGlobali.getInstance().getUtente().getUtente();
		Urletto+="&Artista=" + ToglieCaratteriStrani(Artista);
		Urletto+="&Album=" + ToglieCaratteriStrani(Album);
		Urletto+="&Brano=" + ToglieCaratteriStrani(Brano);
		Urletto+="&Filtro=" + ToglieCaratteriStrani(Filtro);
		Urletto+="&Refresh=" + Refresh;
		Urletto+="&Dettagli=" + Dettagli;

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"RitornaListaBrani",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutListaBrani(),
				NumeroOperazione,
				true);
		g.Esegue(context);
	}

	public GestioneWEBServiceSOAPNuovo RitornaDettaglioBrano(Context context, String Artista, String Album, String Brano, int NumeroOperazione) {
		String Urletto="RitornaDettaglioBrano?";
		Urletto+="Artista=" + ToglieCaratteriStrani(Artista);
		Urletto+="&Album=" + ToglieCaratteriStrani(Album);
		Urletto+="&Brano=" + ToglieCaratteriStrani(Brano);

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"RitornaDettaglioBrano",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutListaBrani(),
				NumeroOperazione,
				false);
		g.Esegue(context);

		return g;
	}

	public GestioneWEBServiceSOAPNuovo RitornaBrano(Context context, String Dire, String Artista, String Album,
							 String Brano, String Converte, String Qualita) {
		String Urletto="RitornaBrano?";
		Urletto+="NomeUtente=" + VariabiliStaticheGlobali.getInstance().getUtente().getUtente();
		Urletto+="&DirectBase=" + Dire;
		Urletto+="&Artista=" + ToglieCaratteriStrani(Artista);
		Urletto+="&Album=" + ToglieCaratteriStrani(Album);
		Urletto+="&Brano=" + ToglieCaratteriStrani(Brano);
		Urletto+="&Converte=" + Converte;
		Urletto+="&Qualita=" + Qualita;

		String messaggio="";

		if (Converte.equals("S")) {
			PronunciaFrasi pf = new PronunciaFrasi();
			pf.PronunciaFrase("Compressione brano","ITALIANO");

			messaggio="Compressione e download brano";
		} else {
			PronunciaFrasi pf = new PronunciaFrasi();
			pf.PronunciaFrase("Download brano", "INGLESE");

			messaggio="Download brano";
		}

		int NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, messaggio);

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"RitornaBrano",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
				NumeroOperazione,
				false);
		g.Esegue(context);

		return g;
	}

	public GestioneWEBServiceSOAPNuovo RitornaBranoBackground(Context context, String Dire, String Artista, String Album,
							String Brano, String Converte, String Qualita, int NumeroBrano, Boolean NonFernmareDownload,
									   int NumeroOperazione) {
		// if (!VariabiliStaticheGlobali.getInstance().isStaScaricandoBrano()) {
			VariabiliStaticheGlobali.getInstance().setStaScaricandoBrano(true);

			String Urletto = "RitornaBrano?";
			Urletto += "NomeUtente=" + VariabiliStaticheGlobali.getInstance().getUtente().getUtente();
			Urletto += "&DirectBase=" + Dire;
			Urletto += "&Artista=" + ToglieCaratteriStrani(Artista);
			Urletto += "&Album=" + ToglieCaratteriStrani(Album);
			Urletto += "&Brano=" + ToglieCaratteriStrani(Brano);
			Urletto += "&Converte=" + Converte;
			Urletto += "&Qualita=" + Qualita;

			String messaggio = "";
			if (Converte.equals("S")) {

				// PronunciaFrasi pf = new PronunciaFrasi();
				// pf.PronunciaFrase("Compressione","ITALIANO");

				messaggio = "Compressione e download brano";
			} else {

				// PronunciaFrasi pf = new PronunciaFrasi();
				// pf.PronunciaFrase("Download", "INGLESE");

				messaggio = "Download brano";
			}

			GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.download);

			// EsecuzioneChiamateWEB e = new EsecuzioneChiamateWEB();
			// int n = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, messaggio);
			// e.EsegueChiamataSOAP(context, ws, Urletto, "RitornaBranoBackground",
			// 		NS, SA, messaggio, VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(), NumeroBrano, NonFernmareDownload,
			// 		n, new Date(System.currentTimeMillis()));
		// } else {
        //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Download Brano in background '"+Artista+ " " +Album+ " " + Brano + "'. "+
        //             "Skippato... Già ce n'è un altro in coda");
        // }

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"RitornaBranoBackground",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
				NumeroOperazione,
				false);
		g.Esegue(context);

		return g;
	}

	public GestioneWEBServiceSOAPNuovo RitornaMultimediaArtista(Context context, String Artista, int NumeroOperazione) {
		String Urletto="RitornaMultimediaArtista?";
		Urletto+="PathBase=" + VariabiliStaticheGlobali.getInstance().getUtente().getCartellaBase();
		Urletto+="&Artista=" + ToglieCaratteriStrani(Artista);

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"RitornaMultimediaArtista",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutListaBrani(),
				NumeroOperazione,
				false);
		g.Esegue(context);

		return g;
	}

	public void RitornaDatiUtente(Context context, String Utente, int NumeroOperazione) {
		String Urletto="RitornaDatiUtente?";
		Urletto+="NomeUtente=" +Utente;

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"RitornaDatiUtente",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutListaBrani(),
				NumeroOperazione,
				false);
		g.Esegue(context);
	}
}
