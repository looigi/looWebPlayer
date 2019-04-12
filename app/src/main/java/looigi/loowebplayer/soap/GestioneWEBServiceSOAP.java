/* package looigi.loowebplayer.soap;

import android.content.Context;
import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.SocketTimeoutException;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.ritorno_ws.wsRitorno;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.Traffico;
import looigi.loowebplayer.utilities.Utility;

public class GestioneWEBServiceSOAP {
	// private ProgressDialog progressDialog;
	private String tOperazione;
	private Boolean Errore;
  	private String Urletto;
  	private String messErrore="";
	private Integer Timeout;

    private String NAMESPACE; //  = "http://cvcalcio.org/";
    private String METHOD_NAME = "";
    private String SOAP_ACTION; //  = "http://cvcalcio.org/";
    private String sURL = "";
    private String Parametri[];
    private String result="";
    private int NumeroBrano;
    private int NumeroOperazione;

	private BackgroundAsyncTask bckAsyncTask;
	private EsecuzioneChiamateWEB ecw;

	public GestioneWEBServiceSOAP(int NumeroBrano, String urletto, String TipoOperazione,
                                  String NS, String SA, int Timeout, EsecuzioneChiamateWEB e,
								  int NumeroOperazione) {
		tOperazione=TipoOperazione;
		this.NumeroBrano=NumeroBrano;
		this.NAMESPACE=NS;
		this.SOAP_ACTION=SA;
		this.Timeout=Timeout;
		this.ecw = e;
		this.NumeroOperazione = NumeroOperazione;

		ApriDialog();

		Urletto=urletto;
		
		SplittaCampiUrletto(Urletto);
		
		Errore=false;
	}

	private void SplittaCampiUrletto(String Cosa) {
		String Perc=Cosa;
		int pos;
		String Indirizzo="";
		String Variabili[];
		String Funzione="";
		
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
			sURL=Indirizzo;
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
			sURL=Indirizzo;
			METHOD_NAME = Funzione;
			SOAP_ACTION = NAMESPACE + Funzione;
		}
	}
	
	public void Esegue(final Context context) {
    	bckAsyncTask = new BackgroundAsyncTask(context);
    	bckAsyncTask.execute(Urletto);
	}
	
	private void ChiudeDialog() {
        VariabiliStaticheGlobali.getInstance().setOperazioneInCorso("");
	}
	
	private void ApriDialog() {
	}

	public void StoppaEsecuzione() {
		bckAsyncTask.cancel(true);

		ChiudeDialog();

		messErrore="ERROR: Auto Timeout";

		// bckAsyncTask.ControllaFineCiclo(true);
	}

	private class BackgroundAsyncTask extends AsyncTask<String, Integer, String> {
		private Context context;
		
	    private BackgroundAsyncTask(Context cxt) {
	        context = cxt;
	    }
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected void onPostExecute(String p) {
			super.onPostExecute(p);
			
			ControllaFineCiclo(false);
		}

	    @Override
	    protected String doInBackground(String... sUrl) {
            SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);

            String Parametro="";
            String Valore="";

			VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP: "+sUrl[0]);
			
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
            HttpTransportSE aht = null;
            messErrore="";
            try {
                soapEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    			soapEnvelope.dotNet = true;
                soapEnvelope.setOutputSoapObject(Request);
                aht = new HttpTransportSE(sURL, Timeout);
                aht.call(SOAP_ACTION, soapEnvelope);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP:  OK");
            } catch (SocketTimeoutException e) {
            	Errore=true;
				messErrore = Utility.getInstance().PrendeErroreDaException(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP:  SocketTimeOutException: "+messErrore);
            	if (messErrore!=null) {
            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
            	} else {
            		messErrore="Unknown";
            	}
            	result="ERROR: "+messErrore;
				//Utility.getInstance().VisualizzaPOPUP(context, "Errore di socket sul DB:\n" + messErrore, false, 0, false);
			} catch (IOException e) {
            	Errore=true;
				messErrore = Utility.getInstance().PrendeErroreDaException(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP:  IOException: "+messErrore);
            	if (messErrore!=null)
            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
            	result="ERROR: "+messErrore;
				//Utility.getInstance().VisualizzaPOPUP(context, "Errore di I/O dal DB:\n" + messErrore, false, 0, false);
            } catch (XmlPullParserException e) {
            	Errore=true;
				messErrore = Utility.getInstance().PrendeErroreDaException(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP:  XmlPullParserException: "+messErrore);
            	if (messErrore!=null) {
            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
            	} else {
            		messErrore="Unknown";
            	}
            	result="ERRORE: "+messErrore;
				//Utility.getInstance().VisualizzaPOPUP(context, "Errore di parsing XML:\n" + messErrore, false, 0, false);
            } catch (Exception e) {
            	Errore=true;
				messErrore = Utility.getInstance().PrendeErroreDaException(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP:  Exception: "+messErrore);
            	if (messErrore!=null)
            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
            	result="ERROR: "+messErrore;
				//Utility.getInstance().VisualizzaPOPUP(context, "Errore generico di lettura sul DB:\n" + messErrore, false, 0, false);
            }
            if (!Errore) {
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

					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP: OK anche il result");
	            } catch (SoapFault e) {
	            	Errore=true;
					messErrore = Utility.getInstance().PrendeErroreDaException(e);
					VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP: SoapFault: "+messErrore);
	            	if (messErrore!=null) {
	            		messErrore=messErrore.toUpperCase().replace("LOOIGI.NO-IP.BIZ","Web Service");
	            	} else {
	            		messErrore="Unknown";
	            	}
	            	result="ERRORE: "+messErrore;
	            }
            }
            if (aht!=null) {
            	aht=null;
            }
            if (soapEnvelope!=null) {
            	soapEnvelope=null;
            }

			return null;
	    }
	 	
	    public void ControllaFineCiclo(boolean DaFuori) {
			String Ritorno=result;

			if (!VariabiliStaticheGlobali.getInstance().getMessErrorePerDebug().isEmpty()) {
				messErrore = VariabiliStaticheGlobali.getInstance().getMessErrorePerDebug();
				Ritorno = messErrore;
			}

			if (Ritorno.contains("ERROR:")) {
				messErrore = Ritorno;
				Errore = true;
			}

			ecw.setMessErrore(messErrore);

			ChiudeDialog();

			wsRitorno rRit = new wsRitorno();
			Boolean Ancora = true;

			if (!Errore) {
				if (!ecw.getFerma() || NumeroBrano!=VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando()) {
				// if (!ecw.getFerma()) {
					ecw.setEsecuzioneRoutineWeb(0);
					// if (!DaFuori) {
					ecw.StoppaElaborazioneWEB();
					// }

					while (Ancora) {
						switch (tOperazione) {
							case "RitornaListaBrani":
								rRit.RitornaListaBrani(Ritorno, NumeroOperazione);
								Ancora = false;
								break;
							case "RitornaDatiUtente":
								rRit.RitornaDatiUtente(Ritorno, NumeroOperazione);
								Ancora = false;
								break;
							case "RitornaMultimediaArtista":
								rRit.RitornaMultimediaArtista(NumeroBrano, Ritorno, NumeroOperazione);
								Ancora = false;
								break;
							case "RitornaDettaglioBrano":
								rRit.RitornaDettaglioBrano(NumeroBrano, Ritorno, NumeroOperazione);
								Ancora = false;
								break;
							case "RitornaBrano":
								rRit.RitornaBrano(Ritorno, NumeroOperazione, NumeroBrano);
								Ancora = false;
								break;
							case "RitornaBranoBackground":
								GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.ricerca);

								rRit.RitornaBranoBackground(Ritorno, NumeroOperazione, NumeroBrano);
								Ancora = false;
								break;
							default:
								VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP: Blocco tutto in quanto operazione non gestita: "+tOperazione);
								Ancora=false;
								break;
						}
					}
				} else {
					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP: Stoppata esecuzione da remoto");

                    // ecw.StoppaElaborazioneWEB();
				}
			} else {
				// if (VariabiliStaticheHome.getInstance().getContext()!=null) {
				// 	GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.error);
				// }

				if (Ritorno.contains("ERROR: brano") && Ritorno.contains("in conversione")) {
					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "SOAP: Stoppata esecuzione da web service");

                    ecw.StoppaElaborazioneWEB();
				} else {
					int RitornoErrore = 0;
					VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Esecuzione routine di errore da GestioneWEBServiceSOAP.");
					RitornoErrore = ecw.EsegueErrore(DaFuori);

					if (RitornoErrore<0) {
						// Terminati i tentativi o errore bloccante... Proseguo il ciclo di caricamento
						switch (tOperazione) {
							case "RitornaListaBrani":
								rRit.RitornaListaBrani("PROSEGUI", NumeroOperazione);
								break;
							case "RitornaDatiUtente":
								rRit.RitornaDatiUtente("PROSEGUI", NumeroOperazione);
								break;
							case "RitornaMultimediaArtista":
								rRit.RitornaMultimediaArtista(NumeroBrano, "PROSEGUI", NumeroOperazione);
								break;
							case "RitornaDettaglioBrano":
								rRit.RitornaDettaglioBrano(NumeroBrano, "PROSEGUI", NumeroOperazione);
								break;
							case "RitornaBrano":
								rRit.RitornaBrano("PROSEGUI", NumeroOperazione, NumeroBrano);
								break;
							case "RitornaBranoBackground":
								GestioneOggettiVideo.getInstance().ImpostaIconaBackground(R.drawable.error);

								rRit.RitornaBranoBackground("PROSEGUI", NumeroOperazione, NumeroBrano);
								break;
						}
					}
				}
			}
	    }
	    
	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        
	    }

		@Override
		protected void onCancelled(){
		}
   
	}

	public void cancel(boolean b) {
	}
}
*/