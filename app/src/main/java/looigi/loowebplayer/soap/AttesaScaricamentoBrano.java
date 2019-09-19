package looigi.loowebplayer.soap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheNuove;
import looigi.loowebplayer.ritorno_ws.RitornoDaWSIntermedioAttesa;
import looigi.loowebplayer.ritorno_ws.wsRitornoNuovo;
import looigi.loowebplayer.utilities.GestioneCPU;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class AttesaScaricamentoBrano {
	private static HttpTransportSE aht = null;
	private static BackgroundAsyncTask bckAsyncTask;
	private static String messErrore="";
	private long lastTimePressed = 0;

	private String NAMESPACE;
	private int Timeout;
	private String SOAP_ACTION;
	private int NumeroOperazione;
	private String tOperazione;
	private boolean ApriDialog;
	private String Urletto;
	private static int Tentativo;
	private boolean inBackGround;

	public AttesaScaricamentoBrano(String urletto, String TipoOperazione,
                                   String NS, String SA, int Timeout, int NumeroOperazione,
                                   boolean inBackGround, boolean ApriDialog) {
		this.NAMESPACE = NS;
		this.Timeout = Timeout;
		this.SOAP_ACTION = SA;
		this.NumeroOperazione = NumeroOperazione;
		this.tOperazione = TipoOperazione;
		this.ApriDialog = ApriDialog;
		this.Urletto = urletto;
		this.inBackGround = inBackGround;
		this.Tentativo = 0;

		// boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
//
		// if ((System.currentTimeMillis() - lastTimePressed < 1000 && lastTimePressed >0) || !ceRete) {
		// 	try {
		// 		VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
		// 		}.getClass().getEnclosingMethod().getName(), "SOAP troppo veloce");
		// 	} catch (Exception ignored) {
//
		// 	}
		// 	VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
		// 	return;
		// }
		// lastTimePressed = System.currentTimeMillis();

		// this.QuantiTentativi = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQuantiTentativi();
		// this.Tentativo = 0;

		// this.NumeroBrano = Utility.getInstance().ControllaNumeroBrano();

		if (!urletto.isEmpty()) {
			// String Chiave = this.Urletto + ";" + this.tOperazione;
			// if (VariabiliStaticheGlobali.getInstance().getChiaveDLSoap().isEmpty() ||
			// 		(!VariabiliStaticheGlobali.getInstance().getChiaveDLSoap().isEmpty() &&
			// 		!VariabiliStaticheGlobali.getInstance().getChiaveDLSoap().equals(Chiave))) {
			// 	VariabiliStaticheGlobali.getInstance().setChiaveDLSoap(Chiave);

			// 	ApriDialog();

			// 	SplittaCampiUrletto(Urletto);

			//	Errore = false;
			// } else {
			// 	VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
			// 	String funzione = "";
			// 	if (new Object() {
			// 	}.getClass().getEnclosingMethod() != null) {
			// 		funzione = new Object() {
			// 		}.getClass().getEnclosingMethod().getName();
			// 	}
			// 	VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
			// 			funzione,
			// 			"Skippata operazione SOAP uguale: " + Chiave);
			// }
			// Esegue();
		} else {
			VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, false);
			String funzione = "";
			if (new Object() {
			}.getClass().getEnclosingMethod() != null) {
				funzione = new Object() {
				}.getClass().getEnclosingMethod().getName();
			}
			VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
					funzione,
					"Skippata operazione. URL Vuoto.");
		}
	}

	public void Esegue() {
		// boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();
//
		// if (ceRete) {
			// if (bckAsyncTask==null) {
		boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

		GestioneCPU.getInstance().AttivaCPU();
		VariabiliStaticheGlobali.getInstance().setgAttesa(this);

		bckAsyncTask = new BackgroundAsyncTask(NAMESPACE, Timeout, SOAP_ACTION, NumeroOperazione, tOperazione,
				inBackGround, ApriDialog, Urletto);
		if (ceRete) {
			VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false,
					"Chiamata compressione e download");

			bckAsyncTask.execute(Urletto);
		} else {
			VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, "Attesa scaricamento mancanza di rete");
			VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

			VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
					"Attesa scaricamento mancanza di rete");

			StoppaEsecuzione();
		}
			// }
		// }
	}

	public void StoppaEsecuzione() {
		VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false,
				"Blocco compressione e download per cambio brano");
		VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

		if (aht!=null) {
			aht.reset();
		}

		messErrore="ESCI";

		bckAsyncTask.cancel(true);

		bckAsyncTask.ChiudeDialog();

		messErrore ="ESCI";

		bckAsyncTask.ControllaFineCiclo();
	}

	private static class BackgroundAsyncTask extends AsyncTask<String, Integer, String> {
		private String NAMESPACE;
		private String METHOD_NAME = "";
		private String[] Parametri;
		private Integer Timeout;
		private String SOAP_ACTION;
		private boolean Errore;
		private String result="";
		private int NumeroBrano;
		private int NumeroOperazione;
		private String tOperazione;
		private int QuantiTentativi;
		private Handler hAttesaNuovoTentativo;
		private Runnable rAttesaNuovoTentativo;
		private int SecondiAttesa;
		private boolean ApriDialog;
		private ProgressDialog progressDialog;
		private String Urletto;
		private String UrlConvertito;
		private boolean inBackground;

		private Handler hAttesaTermine;
		private Runnable rAttesaTermine;
		private int secondi;

		private BackgroundAsyncTask(String NAMESPACE, int TimeOut,
									String SOAP_ACTION, int NumeroOperazione, String tOperazione,
									boolean inBackground, boolean ApriDialog, String Urletto) {
			this.NAMESPACE = NAMESPACE;
			// this.METHOD_NAME = METHOD_NAME;
			// this.Parametri = Parametri;
			this.Timeout = TimeOut;
			this.SOAP_ACTION = SOAP_ACTION;
			this.NumeroOperazione = NumeroOperazione;
			this.tOperazione = tOperazione;
			this.ApriDialog = ApriDialog;
			this.Urletto = Urletto;
			this.inBackground = inBackground;

			this.NumeroBrano = Utility.getInstance().ControllaNumeroBrano();

			this.QuantiTentativi = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQuantiTentativi();

			FaiPartireTimerDiProsecuzione();
		}

		private void FaiPartireTimerDiProsecuzione() {
			secondi = 0;

			hAttesaTermine = new Handler(Looper.getMainLooper());
			hAttesaTermine.postDelayed(rAttesaTermine = new Runnable() {
				@Override
				public void run() {
					secondi++;
					VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false,
							"Chiamata compressione e download: secondi " + secondi);
                    hAttesaTermine.postDelayed(rAttesaTermine, 1000);
				}
			}, 1000);
		}

		private void TerminaTimerDiProsecuzione() {
			if (hAttesaTermine!=null && rAttesaTermine!=null) {
				hAttesaTermine.removeCallbacks(rAttesaTermine);
				hAttesaTermine = null;
			}
		}

		private String SplittaCampiUrletto(String Cosa) {
			String Perc=Cosa;
			int pos;
			String Indirizzo="";
			String[] Variabili;
			String Funzione="";
			String Urletto = Cosa;

			pos=Perc.indexOf("?");
			if (pos>-1) {
				Indirizzo=Perc.substring(0, pos);
				for (int i=Indirizzo.length()-1;i>0;i--) {
					if (Indirizzo.substring(i, i+1).equals("/")) {
						Funzione=Indirizzo.substring(i+1, Indirizzo.length());
						Indirizzo=Indirizzo.substring(0, i);
						break;
					}
				}
				Urletto=Indirizzo;
				METHOD_NAME = Funzione;
				SOAP_ACTION = NAMESPACE + Funzione;
				Perc=Perc.substring(pos+1, Perc.length());
				pos=Perc.indexOf("&");
				if (pos>-1) {
					Variabili=Perc.split("&",-1);
				} else {
					Variabili=new String[1];
					Variabili[0]=Perc;
				}
				Parametri=Variabili;
			} else {
				Indirizzo=Perc;
				for (int i=Indirizzo.length()-1;i>0;i--) {
					if (Indirizzo.substring(i, i+1).equals("/")) {
						Funzione=Indirizzo.substring(i+1, Indirizzo.length());
						Indirizzo=Indirizzo.substring(0, i);
						break;
					}
				}
				Urletto=Indirizzo;
				METHOD_NAME = Funzione;
				SOAP_ACTION = NAMESPACE + Funzione;
			}

			return Urletto;
		}

		private void ChiudeDialog() {
			if (ApriDialog) {
				try {
					progressDialog.dismiss();
				} catch (Exception ignored) {
				}
			}
			VariabiliStaticheGlobali.getInstance().setOperazioneInCorso("");
			VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(this.NumeroOperazione, false);
		}

		private void ApriDialog() {
			if (ApriDialog) {
				try {
					progressDialog = new ProgressDialog(VariabiliStaticheGlobali.getInstance().getContext());
					progressDialog.setMessage("Attendere Prego\n"+tOperazione);
					progressDialog.setCancelable(false);
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();
				} catch (Exception ignored) {

				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			ApriDialog();
			this.UrlConvertito = SplittaCampiUrletto(this.Urletto);
		}
		
		@Override
		protected void onPostExecute(String p) {
			super.onPostExecute(p);
			
			ControllaFineCiclo();
		}

	    @Override
	    protected String doInBackground(String... sUrl) {
			// boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

			Errore = false;
			result = "";

			// if (!ceRete) {
			// 	messErrore="ERROR: Assenza di rete";
			// 	VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP: Assenza di rete");
			// 	return null;
			// }

			SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            String Parametro="";
            String Valore="";

			VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
					"SOAP: "+sUrl[0]);
			
            if (Parametri!=null) {
	            for (int i=0;i<Parametri.length;i++) {
					if (Parametri[i] != null) {
						int pos = Parametri[i].indexOf("=");
						if (pos > -1) {
							Parametro = Parametri[i].substring(0, pos);
							Valore = Parametri[i].substring(pos + 1, Parametri[i].length());
						}
						Request.addProperty(Parametro, Valore);
					}
				}
            }

            SoapSerializationEnvelope soapEnvelope = null;
            messErrore="";
            try {
				// VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false,
				// 		"Inizio chiamata compressione e download");
				String uu = UrlConvertito.replace(" ", "%20");
				uu = uu.replace("#", "%23");
				uu = uu.replace("&", "%26");

				soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    			soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                aht = new HttpTransportSE(uu, 75000);
                aht.call(SOAP_ACTION, soapEnvelope);

				// VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

				if(isCancelled() || messErrore.equals("ESCI")) {
					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass()
									.getEnclosingMethod().getName(),
							"SOAP:  Stoppato da remoto");
				} else {
					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
							}.getClass().getEnclosingMethod().getName(),
							"SOAP:  OK");
				}
            } catch (SocketTimeoutException e) {
            	Errore=true;
				messErrore = Utility.getInstance().PrendeErroreDaException(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
						"SOAP:  SocketTimeOutException: "+messErrore);
            	if (messErrore!=null) {
            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
            	} else {
            		messErrore="Unknown";
            	}
            	result="ERROR: "+messErrore;
            	messErrore = result;
				//Utility.getInstance().VisualizzaPOPUP(context, "Errore di socket sul DB:\n" + messErrore, false, 0, false);
			} catch (IOException e) {
            	Errore=true;
				messErrore = Utility.getInstance().PrendeErroreDaException(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
						"SOAP:  IOException: "+messErrore);
            	if (messErrore!=null)
            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
            	result="ERROR: "+messErrore;
				messErrore = result;
				//Utility.getInstance().VisualizzaPOPUP(context, "Errore di I/O dal DB:\n" + messErrore, false, 0, false);
            } catch (XmlPullParserException e) {
            	Errore=true;
				messErrore = Utility.getInstance().PrendeErroreDaException(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
						"SOAP:  XmlPullParserException: "+messErrore);
            	if (messErrore!=null) {
            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
            	} else {
            		messErrore="Unknown";
            	}
            	result="ERRORE: "+messErrore;
				messErrore = result;
				//Utility.getInstance().VisualizzaPOPUP(context, "Errore di parsing XML:\n" + messErrore, false, 0, false);
            } catch (Exception e) {
            	Errore=true;
				messErrore = Utility.getInstance().PrendeErroreDaException(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
						"SOAP:  Exception: "+messErrore);
            	if (messErrore!=null)
            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
            	result="ERROR: "+messErrore;
				messErrore = result;
				//Utility.getInstance().VisualizzaPOPUP(context, "Errore generico di lettura sul DB:\n" + messErrore, false, 0, false);
            }
            if (!Errore && !isCancelled()) {
	            try {
	                result = ""+soapEnvelope.getResponse();
					// VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP result: "+result);

	                // if (!isCancelled()) {
						// Traffico
						long bs = VariabiliStaticheGlobali.getInstance().getBytesScaricati();
						bs += result.length();
						VariabiliStaticheGlobali.getInstance().setBytesScaricati(bs);
						if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getVisualizzaTraffico()) {
							Traffico.getInstance().ScriveTrafficoAVideo(VariabiliStaticheGlobali.getInstance().getBytesScaricati());
						}
						// Traffico
					// } else {
					// 	VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP: isCancelled");
					// }

					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
							"SOAP: OK anche il result");
	            } catch (SoapFault e) {
	            	Errore=true;
					messErrore = Utility.getInstance().PrendeErroreDaException(e);
					VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
							new Object(){}.getClass().getEnclosingMethod().getName(),
							"SOAP: SoapFault: "+messErrore);
	            	if (messErrore!=null) {
	            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
	            	} else {
	            		messErrore="Unknown";
	            	}
	            	result="ERROR: "+messErrore;
					messErrore = result;
	            }
            } else {
            	int a = 0;
			}
            if (aht!=null) {
            	aht=null;
            }
            if (soapEnvelope!=null) {
            	soapEnvelope=null;
            }
            if (isCancelled()) {
            	messErrore="ESCI";
			}

			return null;
	    }
	 	
	    private void ControllaFineCiclo() {
			// VariabiliStaticheGlobali.getInstance().setChiaveDLSoap("***");

 			// if (VariabiliStaticheNuove.getInstance().getDb()!=null) {
			// 	VariabiliStaticheNuove.getInstance().setDb(null);
			//}
			// if (VariabiliStaticheNuove.getInstance().getGb()!=null) {
			// 	VariabiliStaticheNuove.getInstance().setGb(null);
			// }
			// if (VariabiliStaticheNuove.getInstance().getGm()!=null) {
				VariabiliStaticheNuove.getInstance().setGm(null);
			// }
			// if (VariabiliStaticheNuove.getInstance().getGt()!=null) {
				VariabiliStaticheNuove.getInstance().setGt(null);
			// }
			VariabiliStaticheGlobali.getInstance().setgAttesa(null);

			TerminaTimerDiProsecuzione();

			VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, false, "Termine download / compressione");
			VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

			VariabiliStaticheGlobali.getInstance().setAsb(null);

			ChiudeDialog();

			final RitornoDaWSIntermedioAttesa rr = new RitornoDaWSIntermedioAttesa();

			if (NumeroBrano>-1 &&
					(NumeroBrano != VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) &&
					(VariabiliStaticheGlobali.getInstance().getNumeroProssimoBrano() == -1)) {
				NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
						"SOAP: Cambio brano");
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
				}.getClass().getEnclosingMethod().getName(), "SOAP: Cambio brano");

				rr.ChiamaRoutinesInCasoDiErrore(result, NumeroOperazione, NumeroBrano, tOperazione, inBackground);
			} else {
				if (!messErrore.equals("ESCI") && !messErrore.contains("ERROR")) {
					rr.ChiamaRoutinesInCasoDiOK(result, messErrore, NumeroOperazione, NumeroBrano, Errore, tOperazione, inBackground);
				} else {
					if (!messErrore.equals("ESCI")) {
						// Errore... Riprovo ad eseguire la funzione
						boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

						if (!ceRete) {
							VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
							}.getClass().getEnclosingMethod().getName(), "SOAP Attesa: Mancanza di rete");

							rr.ChiamaRoutinesInCasoDiErrore(result, NumeroOperazione, NumeroBrano, tOperazione, inBackground);
						} else {
							if (Tentativo < QuantiTentativi &&
								VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getReloadAutomatico() &&
								!tOperazione.equals("RitornaDatiUtente")) {
								Tentativo++;

								final int TempoAttesa = (VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3() * (Tentativo)) / 1000;

								NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
										"Errore SOAP. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));
								VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
								}.getClass().getEnclosingMethod().getName(),
										"SOAP: Errore. Attendo " + Integer.toString(TempoAttesa) + " secondi e riprovo: " +
										Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi));

								SecondiAttesa = 0;
								hAttesaNuovoTentativo = new Handler(Looper.getMainLooper());
								rAttesaNuovoTentativo = (new Runnable() {
									@Override
									public void run() {
										SecondiAttesa++;
										NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true,
												"Errore SOAP. Riprovo. Tentativo :" + Integer.toString(Tentativo) + "/" + Integer.toString(QuantiTentativi) +
														" Secondi " + Integer.toString(SecondiAttesa) + "/" + Integer.toString(TempoAttesa));
										if (SecondiAttesa >= TempoAttesa) {
											// VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);

											boolean ceRete = VariabiliStaticheGlobali.getInstance().getNtn().isOk();

											if (ceRete) {
												VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
												}.getClass().getEnclosingMethod().getName(), "SOAP Attesa: Richiamata");

												TerminaTimerDiProsecuzione();

												// FaiPartireTimerDiProsecuzione();

												bckAsyncTask = new AttesaScaricamentoBrano.BackgroundAsyncTask(NAMESPACE, Timeout, SOAP_ACTION, NumeroOperazione, tOperazione,
														inBackground, ApriDialog, Urletto);
												bckAsyncTask.execute(Urletto);
											} else {
												VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
												}.getClass().getEnclosingMethod().getName(), "SOAP Attesa: Mancanza di rete 2");

												rr.ChiamaRoutinesInCasoDiErrore(result, NumeroOperazione, NumeroBrano, tOperazione, inBackground);
											}
											// }

											hAttesaNuovoTentativo.removeCallbacks(rAttesaNuovoTentativo);
											hAttesaNuovoTentativo = null;
										} else {
											hAttesaNuovoTentativo.postDelayed(rAttesaNuovoTentativo, 1000);
										}
									}
								});
								hAttesaNuovoTentativo.postDelayed(rAttesaNuovoTentativo, 1000);
								// Errore... Riprovo ad eseguire la funzione
							} else {
								NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, messErrore);
								VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
								}.getClass().getEnclosingMethod().getName(), "SOAP: Stoppata esecuzione causa errore");

								rr.ChiamaRoutinesInCasoDiErrore(result, NumeroOperazione, NumeroBrano, tOperazione, inBackground);
							}
						}
					} else {
						NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(NumeroOperazione, true, messErrore);
						VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object() {
						}.getClass().getEnclosingMethod().getName(), "SOAP: Stoppata esecuzione causa timeout");

						rr.ChiamaRoutinesInCasoDiErrore(result, NumeroOperazione, NumeroBrano, tOperazione, inBackground);
					}
				}
			}
			bckAsyncTask = null;
			VariabiliStaticheHome.getInstance().EliminaOperazioneWEB(NumeroOperazione, true);
			GestioneCPU.getInstance().DisattivaCPU();
		}

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        
	    }

		@Override
		protected void onCancelled(){
	    	messErrore="ESCI";
		}
	}
}
