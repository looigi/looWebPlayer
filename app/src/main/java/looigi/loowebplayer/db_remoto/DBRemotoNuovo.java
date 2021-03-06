package looigi.loowebplayer.db_remoto;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.soap.AttesaScaricamentoBrano;
// import looigi.loowebplayer.soap.CheckURLFile;
import looigi.loowebplayer.soap.GestioneWEBServiceSOAPNuovo;
import looigi.loowebplayer.thread.ScaricoBranoEAttesa;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.PronunciaFrasi;
import looigi.loowebplayer.utilities.Utility;

public class DBRemotoNuovo {
	private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("DBRemotoNuovo");;
	private String ws = "looWPlayer.asmx/";
	private String NS="http://looWebPlayer.org/";
	private String SA="http://looWebPlayer.org/";
    private Runnable rAttendeRispostaCheckURL;
    private Handler hAttendeRispostaCheckURL;
    private int Secondi;
    private int MaxAttesa = 60;

	private Handler hAttesaProssimo;
	private Runnable rAttesaProssimo;
	private int attesa = 0;

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
		g.Esegue();
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
		g.Esegue();

		return g;
	}

	public void RitornaBrano(final Context context, final String Dire, final String Artista,
													final String Album, final String Brano, final String Converte,
													final String Qualita) {
		int NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false,
				"Controllo esecuzione remota");

		/*String url = VariabiliStaticheGlobali.getInstance().PercorsoURL + "/Temp/" + VariabiliStaticheGlobali.getInstance().getUtente().getUtente() + ".txt";
		final CheckURLFile cuf = new CheckURLFile();
		cuf.setNumeroBrano(-1);
		cuf.startControl(url);

		Secondi=0;
		final int NumeroBrano = Utility.getInstance().ControllaNumeroBrano();

		hAttendeRispostaCheckURL = new Handler(Looper.getMainLooper());
		hAttendeRispostaCheckURL.postDelayed(rAttendeRispostaCheckURL = new Runnable() {
			@Override
			public void run() {
				if (NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
					if (cuf !=null) {
						VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
								"Stoppo CUF controllo esec. per numero brano diverso dall'attuale");

						cuf.StoppaEsecuzione(true);
					}
					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
							"CUF Controllo esecuzione. Cambio brano");
					hAttendeRispostaCheckURL.removeCallbacks(rAttendeRispostaCheckURL);
					rAttendeRispostaCheckURL=null;
					hAttendeRispostaCheckURL = null;
				} else {
					hAttendeRispostaCheckURL.removeCallbacks(rAttendeRispostaCheckURL);

					if (VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL().contains("OK")) {
						if (cuf!=null) {
							VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
									"Stoppo CUF normale per OK");

							cuf.StoppaEsecuzione(false);
						}

						Secondi++;
						VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false,
                                "Attesa termine esecuzione remota: " + Integer.toString(Secondi));

						if (Secondi>=MaxAttesa) {
							rAttendeRispostaCheckURL=null;
							hAttendeRispostaCheckURL=null;

							VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("File remoto rilevato ma proseguo");
							VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false,
									"Attesa termine esecuzione remota. Termine superato. Eseguo comunque la funzione");
							EsegueChiamataMP3(context, cuf, Dire, Artista, Album, Brano, Qualita,
									Converte, NumeroOperazione);
						} else {
							hAttendeRispostaCheckURL.postDelayed(rAttendeRispostaCheckURL, 1000);
						}
					} else {
						rAttendeRispostaCheckURL=null;
						hAttendeRispostaCheckURL=null;

						VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("File remoto non rilevato");
						EsegueChiamataMP3(context, cuf, Dire, Artista, Album, Brano, Qualita,
								Converte, NumeroOperazione);
					}
				}
			}
		}, 1000);*/

		EsegueChiamataMP3(Dire, Artista, Album, Brano, Qualita,
				Converte, NumeroOperazione);
	}

	private void EsegueChiamataMP3(String Dire, String Artista, String Album, String Brano, String Qualita,
								   String Converte, final int NumeroOperazione) {
		VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
				"Controllo esec.: " + VariabiliStaticheGlobali.getInstance().getRitornoCheckFileURL());
		VariabiliStaticheGlobali.getInstance().setRitornoCheckFileURL("");

		hAttendeRispostaCheckURL = null;
		// if (cuf!=null) {
		// 	VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(),
		// 			"Stoppo CUF normale per ESECUZIONE TERMINATA CON ESITO NEGATIVO");
//
		// 	cuf.StoppaEsecuzione(false);
		// }

		String Urletto="RitornaBrano?";
		Urletto+="NomeUtente=" + VariabiliStaticheGlobali.getInstance().getUtente().getUtente();
		Urletto+="&DirectBase=" + Dire;
		Urletto+="&Artista=" + ToglieCaratteriStrani(Artista);
		Urletto+="&Album=" + ToglieCaratteriStrani(Album);
		Urletto+="&Brano=" + ToglieCaratteriStrani(Brano);
		Urletto+="&Converte=" + Converte;
		Urletto+="&Qualita=" + Qualita;
		Urletto+="&Attendi=true";

		final String Urletto2 = Urletto;
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

		VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, messaggio);

		/* GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"RitornaBrano",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
				NumeroOperazione,
				false);
		g.Esegue(); */

		if (VariabiliStaticheGlobali.getInstance().getAsb() != null) {
			attesa = 0;
			VariabiliStaticheGlobali.getInstance().getAsb().StoppaEsecuzione();
			VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Attesa blocco download per canzone precedente");
			hAttesaProssimo = new Handler(Looper.getMainLooper());
			rAttesaProssimo = new Runnable() {
				@Override
				public void run() {
					if (VariabiliStaticheGlobali.getInstance().getAsb() != null) {
						attesa++;
						if (attesa<10) {
							hAttesaProssimo.postDelayed(rAttesaProssimo, 1000);
						} else {
							VariabiliStaticheGlobali.getInstance().setAsb(null);
							hAttesaProssimo.removeCallbacks(rAttesaProssimo);
							hAttesaProssimo = null;

							VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Blocco download automatico per canzone precedente");
							AttesaScaricamentoBrano g = new AttesaScaricamentoBrano(
									VariabiliStaticheGlobali.RadiceWS + ws + Urletto2,
									"RitornaBrano",
									NS,
									SA,
									VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
									NumeroOperazione,
									false,
									false);
							VariabiliStaticheGlobali.getInstance().setAsb(g);
							g.Esegue();
						}
					} else {
						hAttesaProssimo.removeCallbacks(rAttesaProssimo);
						hAttesaProssimo = null;

						VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Compressione e downloadn brano");
						AttesaScaricamentoBrano g = new AttesaScaricamentoBrano(
								VariabiliStaticheGlobali.RadiceWS + ws + Urletto2,
								"RitornaBrano",
								NS,
								SA,
								VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
								NumeroOperazione,
								false,
								false);
						VariabiliStaticheGlobali.getInstance().setAsb(g);
						g.Esegue();
					}
				}
			};
			hAttesaProssimo.postDelayed(rAttesaProssimo, 1000);
		} else {
			VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Compressione e downloadn brano");
			AttesaScaricamentoBrano g = new AttesaScaricamentoBrano(
					VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
					"RitornaBrano",
					NS,
					SA,
					VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
					NumeroOperazione,
					false,
					false);
			VariabiliStaticheGlobali.getInstance().setAsb(g);
			g.Esegue();
		}

		// VariabiliStaticheNuove.getInstance().setGb(g);
	}

	public void RitornaBranoBackground(Context context, String Dire, String Artista, String Album,
							String Brano, String Converte, String Qualita, int NumeroBrano, boolean NonFernmareDownload,
									   final int NumeroOperazione) {
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
			Urletto +="&Attendi=true";

			final String Urletto2 = Urletto;
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
        //     VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Download Brano in background '"+Artista+ " " +Album+ " " + Brano + "'. "+
        //             "Skippato... Già ce n'è un altro in coda");
        // }

		/* GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"RitornaBranoBackground",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
				NumeroOperazione,
				false);
		g.Esegue(); */

		if (VariabiliStaticheGlobali.getInstance().getAsb() != null) {
			attesa = 0;
			VariabiliStaticheGlobali.getInstance().getAsb().StoppaEsecuzione();
			VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Attesa blocco download per canzone precedente");
			hAttesaProssimo = new Handler(Looper.getMainLooper());
			rAttesaProssimo = new Runnable() {
				@Override
				public void run() {
					if (VariabiliStaticheGlobali.getInstance().getAsb() != null) {
						attesa++;
						if (attesa<10) {
							hAttesaProssimo.postDelayed(rAttesaProssimo, 1000);
						} else {
							VariabiliStaticheGlobali.getInstance().setAsb(null);
							hAttesaProssimo.removeCallbacks(rAttesaProssimo);
							hAttesaProssimo = null;

							VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Blocco download automatico per canzone precedente");
							AttesaScaricamentoBrano g = new AttesaScaricamentoBrano(
									VariabiliStaticheGlobali.RadiceWS + ws + Urletto2,
									"RitornaBranoBackground",
									NS,
									SA,
									VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
									NumeroOperazione,
									true,
									false);
							g.Esegue();
						}
					} else {
						hAttesaProssimo.removeCallbacks(rAttesaProssimo);
						hAttesaProssimo = null;

						VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Compressione e download brano in background");
						AttesaScaricamentoBrano g = new AttesaScaricamentoBrano(
								VariabiliStaticheGlobali.RadiceWS + ws + Urletto2,
								"RitornaBranoBackground",
								NS,
								SA,
								VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
								NumeroOperazione,
								true,
								false);
						g.Esegue();
					}
				}
			};
			hAttesaProssimo.postDelayed(rAttesaProssimo, 1000);
		} else {
			VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Compressione e download brano in background");
			AttesaScaricamentoBrano g = new AttesaScaricamentoBrano(
					VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
					"RitornaBranoBackground",
					NS,
					SA,
					VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3(),
					NumeroOperazione,
					true,
					false);
			g.Esegue();
		}
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
		g.Esegue();

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
				true);
		g.Esegue();
	}

	public void AggiornaBellezza(String idCanzone, String Bellezza, int NumeroOperazione) {
		String Urletto="ModificaBellezza?";
		Urletto+="idCanzone=" +idCanzone;
		Urletto+="&Bellezza=" +Bellezza;

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"ModificaBellezza",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutListaBrani(),
				NumeroOperazione,
				false);
		g.Esegue();
	}

	public void VolteAscoltata(String idCanzone, int NumeroOperazione) {
		String Urletto="VolteAscoltata?";
		Urletto+="idCanzone=" +idCanzone;

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"VolteAscoltata",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutListaBrani(),
				NumeroOperazione,
				false);
		g.Esegue();
	}

    public void RitornaVersioneApplicazione(Context context, int NumeroOperazione) {
        String Urletto="RitornaVersioneApplicazione";

        GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
                VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
                "RitornaVersioneApplicazione",
                NS,
                SA,
                VariabiliStaticheGlobali.getInstance().getTimeOutImmagini(),
                NumeroOperazione,
                false);
        g.Esegue();
    }

	public void RitornaSeDeveAggiornare(Context context, int NumeroOperazione) {
		String Urletto="RitornaSeDeveAggiornare";

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"RitornaSeDeveAggiornare",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutImmagini(),
				NumeroOperazione,
				false);
		g.Esegue();
	}

	public void EliminaCanzone(Context context, String pathBase, String Artista, String Album,
							   String Canzone, int NumeroOperazione) {
		String Urletto="EliminaCanzone?";
		Urletto+="PathBase=" + pathBase;
		Urletto+="&Artista=" + ToglieCaratteriStrani(Artista);
		Urletto+="&Album=" + ToglieCaratteriStrani(Album);
		Urletto+="&Canzone=" + ToglieCaratteriStrani(Canzone);

		GestioneWEBServiceSOAPNuovo g = new GestioneWEBServiceSOAPNuovo(
				VariabiliStaticheGlobali.RadiceWS + ws + Urletto,
				"EliminaCanzone",
				NS,
				SA,
				VariabiliStaticheGlobali.getInstance().getTimeOutImmagini(),
				NumeroOperazione,
				false);
		g.Esegue();
	}
}
