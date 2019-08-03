package looigi.loowebplayer.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.widget.Spinner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.NomiMaschere;
import looigi.loowebplayer.maschere.About;
// import looigi.loowebplayer.maschere.Equalizer;
import looigi.loowebplayer.maschere.Home;
import looigi.loowebplayer.maschere.Libreria;
import looigi.loowebplayer.maschere.Settings;
import looigi.loowebplayer.maschere.Utenza;
import looigi.loowebplayer.notifiche.Notifica;

@SuppressLint("SimpleDateFormat")
public class Utility {
	//-------- Singleton ----------//
	private static Utility instance = null;

	private Utility() {
	}

	public static Utility getInstance() {
		if (instance == null) {
			instance = new Utility();
		}

		return instance;
	}

	private int UltimaMaschera=-1;

	private VariabiliStaticheGlobali vg = VariabiliStaticheGlobali.getInstance();

	public String SistemaTesto(String Testo) {
		String t = Testo;

		t = t.replace("\\","_");
		t = t.replace("/","_");
		t = t.replace(">","_");
		t = t.replace("<","_");

		return t;
	}

	public void CambiaMaschera(int viewId) {
		VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Cambia maschera: "+Integer.toString(viewId));

		if (viewId!=UltimaMaschera) {
			UltimaMaschera=viewId;

			Fragment fragment = null;
			String title = ""; // vg.getFragmentActivityPrincipale().getString(R.string.app_name);

			switch (viewId) {
				case R.id.action_settings:
					fragment = new Settings();
					title = NomiMaschere.getInstance().getHomePerTitolo();
					VariabiliStaticheGlobali.MascheraAttuale = NomiMaschere.getInstance().getHome();

					break;
				case R.id.home:
					fragment = new Home();
					title = NomiMaschere.getInstance().getHomePerTitolo();
					VariabiliStaticheGlobali.MascheraAttuale = NomiMaschere.getInstance().getHome();

					break;
				case R.id.about:
					fragment = new About();
					title = NomiMaschere.getInstance().getAboutPerTitolo();
					VariabiliStaticheGlobali.MascheraAttuale = NomiMaschere.getInstance().getHome();

					break;
				// case R.id.equalizer:
				// 	fragment = new Equalizer();
				// 	title = NomiMaschere.getInstance().getEqualizerPerTitolo();
				// 	VariabiliStaticheGlobali.MascheraAttuale = NomiMaschere.getInstance().getHome();

				// 	break;
				case R.id.utenza:
					fragment = new Utenza();
					title = NomiMaschere.getInstance().getHomePerTitolo();
					VariabiliStaticheGlobali.MascheraAttuale = NomiMaschere.getInstance().getHome();

					break;
				case R.id.libreria:
					fragment = new Libreria();
					title = NomiMaschere.getInstance().getLibreriaPerTitolo();
					VariabiliStaticheGlobali.MascheraAttuale = NomiMaschere.getInstance().getHome();

					break;
				case R.id.uscita:
					Notifica.getInstance().RimuoviNotifica();
					System.exit(0);
					break;
			}

			if (fragment != null) {
				try {
					FragmentTransaction ft = vg.getFragmentActivityPrincipale().getSupportFragmentManager().beginTransaction();
					ft.replace(R.id.content_frame, fragment);
					ft.commit();
				} catch (Exception e) {
					VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
				}
			}

			if (vg.getContextPrincipale()!=null && vg.getContextPrincipale().getSupportActionBar() != null) {
				vg.getContextPrincipale().getSupportActionBar().setTitle(title);
			}

			if (vg.getFragmentActivityPrincipale()!=null) {
				DrawerLayout drawer = vg.getFragmentActivityPrincipale().findViewById(R.id.drawer_layout);
				drawer.closeDrawer(GravityCompat.START);
			} else {
				VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
						"Perso il fragment principale");
			}

			ImpostaMenu();
		}
	}

	public void ImpostaMenu() {
		if (vg!=null && vg.getItemNuovo()!=null) {
			try {
				vg.getItemNuovo().setVisible(false);
				vg.getItemMultimedia().setVisible(false);
				vg.getActButtonNew().hide();

				if (VariabiliStaticheGlobali.MascheraAttuale.equals(NomiMaschere.getInstance().getHome())) {
					vg.getItemNuovo().setVisible(true);
					vg.getActButtonNew().show();
				}
			} catch (Exception e) {
				VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
			}
		}
	}

	public boolean ePari(int numero) {
		if ((numero % 2) == 0) {
			return true;
		} else {
			return false;
		}
	}

	public Integer CercaESettaStringaInSpinner(Spinner spn, String Ricerca) {
		int ritorno=-1;

		for (int i=0; i<spn.getCount();i++) {
			if (spn.getItemAtPosition(i)!=null && Ricerca!=null) {
				if (spn.getItemAtPosition(i).toString().trim().toUpperCase().equals(Ricerca.trim().toUpperCase())) {
					ritorno = i;
					break;
				}
			}
		}

		return ritorno;
	}

	public void saveImageFile(Bitmap bitmap, String filename) {
		VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "saveImageFile: "+filename);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (FileNotFoundException e) {
			VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
		}
	}

	public void saveVideoFile(String filename) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
		}
	}

	private String TransformError(String error) {
		String Return=error;

		if (Return.length()>250) {
			Return=Return.substring(0,247)+"...";
		}
		Return=Return.replace("\n"," ");

		return Return;
	}

	public String PrendeErroreDaException(Exception e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));

		return TransformError(errors.toString());
	}

	public String getVersion(Context context) {
		String version = "";
		VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "get Version");

		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			version = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
		}

		return version;
	}

	public int ControllaNumeroBrano() {
		int NumeroBrano = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione().getQualeCanzoneStaSuonando();
		int n = VariabiliStaticheGlobali.getInstance().getDatiGenerali().RitornaQuantiBrani();
		if (NumeroBrano>n) {
			if (n>0) {
				NumeroBrano = 0;
			} else {
				NumeroBrano = -1;
			}
		}

		return NumeroBrano;
	}

	public void ScriveScaricateAscoltate() {
		if (VariabiliStaticheHome.getInstance().getTxtQuanteAscoltate() != null) {
			VariabiliStaticheHome.getInstance().getTxtQuanteAscoltate().setText("Ascoltate: " + Integer.toString(VariabiliStaticheHome.getInstance().getQuanteAscoltate()));
		}
		if (VariabiliStaticheHome.getInstance().getTxtQuanteScaricate() != null) {
			VariabiliStaticheHome.getInstance().getTxtQuanteScaricate().setText("Scaricate: " + Integer.toString(VariabiliStaticheHome.getInstance().getQuanteScaricate()));
		}
	}

	public String ConverteStringaInUrl(String stringa) {
		byte[] a = stringa.getBytes();
		String sRitorno = "";

		for (byte b : a) {
			if (b>=65 && b<=90) {
				sRitorno += (char) b;
			} else {
				if (b>=97 && b<=122) {
					sRitorno += (char) b;
				} else {
					String h = Integer.toHexString(b);
					if (h.length() == 1) {
						h = "0" + h;
					}
					sRitorno += "%" + h;
				}
			}
		}

		return sRitorno;
	}
}
