package looigi.loowebplayer.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;

public class Log {
	private String NomeFile="log.csv";
	private Thread tScoda=null;
	private final List<String> listaCompleta = Collections.synchronizedList(new ArrayList<String>());

	public void PulisceFileDiLog() {
    	if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getScriveLog()) {
	    	String Datella="";
			Datella=PrendeDataAttuale()+";"+PrendeOraAttuale();
			
            String sBody = "Data;Ora Invio Log;Ora Scrittura;Delay;Routine;Descrizione\n";

	        File gpxfile = new File(VariabiliStaticheGlobali.getInstance().PercorsoDIR, NomeFile);
	        FileWriter writer;
			try {
				writer = new FileWriter(gpxfile);
		        writer.append(sBody);
		        writer.flush();
		        writer.close();
			} catch (IOException ignored) {
			}

            sBody="Inizio log";
			ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), sBody);
    	}
    }
    
	private String PrendeDataAttuale() {
		String Ritorno="";
		
		Calendar Oggi = Calendar.getInstance();
        int Giorno=Oggi.get(Calendar.DAY_OF_MONTH);
        int Mese=Oggi.get(Calendar.MONTH)+1;
        int Anno=Oggi.get(Calendar.YEAR);
        String sGiorno=Integer.toString(Giorno).trim();
        String sMese=Integer.toString(Mese).trim();
        String sAnno=Integer.toString(Anno).trim();
        if (sGiorno.length()==1) {
        	sGiorno="0"+sGiorno;
        }
        if (sMese.length()==1) {
        	sMese="0"+sMese;
        }
        Ritorno=sGiorno+"/"+sMese+"/"+sAnno;
        
        return Ritorno;
	}
	
	private String PrendeOraAttuale() {
		String Ritorno="";
		
		Calendar Oggi = Calendar.getInstance();
        int Ore=Oggi.get(Calendar.HOUR_OF_DAY);
        int Minuti=Oggi.get(Calendar.MINUTE);
        int Secondi=Oggi.get(Calendar.SECOND);
        int MilliSecondi=Oggi.get(Calendar.MILLISECOND);
        String sOre=Integer.toString(Ore).trim();
        String sMinuti=Integer.toString(Minuti).trim();
        String sSecondi=Integer.toString(Secondi).trim();
        String sMilliSecondi=Integer.toString(MilliSecondi).trim();
        if (sOre.length()==1) {
        	sOre="0"+sOre;
        }
        if (sMinuti.length()==1) {
        	sMinuti="0"+sMinuti;
        }
        if (sSecondi.length()==1) {
        	sSecondi="0"+sSecondi;
        }
        if (sMilliSecondi.length()==1) {
            sMilliSecondi="0"+sMilliSecondi;
        }
        if (sMilliSecondi.length()==2) {
            sMilliSecondi="0"+sMilliSecondi;
        }
        Ritorno=sOre+":"+sMinuti+":"+sSecondi+"."+sMilliSecondi;
        
        return Ritorno;
	}

	public void ScriveMessaggioDiErrore(Exception e) {
		String error = Utility.getInstance().PrendeErroreDaException(e);
		ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), error);
	}

	public void ScriveLog(String DaDove, String Messaggio) {
		CreaCartella(VariabiliStaticheGlobali.getInstance().PercorsoDIR);

		String Datella="";
		Datella=PrendeDataAttuale()+";"+PrendeOraAttuale()+";***DataScrittura***;***Differenza***;" + DaDove.replace(";", "_") +";";

		String sBody=Datella+Messaggio.replace(";", "_");

		synchronized (listaCompleta) {
			listaCompleta.add(sBody);

			if (tScoda == null) {
				tScoda = new ScodaMessaggiDebug();
				tScoda.setPriority(Thread.MIN_PRIORITY);
				tScoda.start();
			}
		}
	}


	private class ScodaMessaggiDebug extends Thread {
		@Override
		public void run() {
			Ciclo();
		}

		private void Ciclo() {
			Boolean Cont=true;

			while (Cont) {
				String s;

				while (!listaCompleta.isEmpty()) {
					synchronized (listaCompleta) {
						s = listaCompleta.remove(0);
					}
					if (s != null) {
						scrivedebug(s);
					}
				}

				try {
					Thread.sleep(100);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}

				Cont = false;
				tScoda.interrupt();
				tScoda = null;
			}
		}

		private void scrivedebug(String MessaggioLog) {
			if (VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getScriveLog()) {
				String m = MessaggioLog;
				String o = PrendeOraAttuale();
				String c[] = m.split(";",-1);
				Date date1 = null;
				Date date2 = null;
				try {
					date1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(c[0] + " " + c[1]);
				} catch (ParseException ignored) {

				}
				try {
					date2 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(c[0] + " " + o);
				} catch (ParseException ignored) {

				}
				String d = "";
				if (date1!=null && date2!=null) {
					long millisDiff = date2.getTime() - date1.getTime();
					d=Long.toString(millisDiff);
				}
				m = m.replace("***Differenza***", d);
				m = m.replace("***DataScrittura***", o);

				File gpxfile = new File(VariabiliStaticheGlobali.getInstance().PercorsoDIR, NomeFile);
				FileWriter writer;
				try {
					writer = new FileWriter(gpxfile,true);
					writer.append(m+"\n");
					writer.flush();
					writer.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private void CreaCartelle(String Origine, String Cartella) {
		for (int i=1;i<Cartella.length();i++) {
			if (Cartella.substring(i,i+1).equals("/")) {
				CreaCartella(Origine+Cartella.substring(0,i));
			}
		}
	}
	
    private void CreaCartella(String Percorso) {
		try {
			File dDirectory = new File(Percorso);
			dDirectory.mkdirs();
		} catch (Exception ignored) {
			
		}  
	}
}
