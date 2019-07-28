package looigi.loowebplayer.maschere;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.NomiMaschere;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaConfig;
// import looigi.loowebplayer.db_remoto.DBRemoto;
import looigi.loowebplayer.db_remoto.DBRemotoNuovo;
import looigi.loowebplayer.dialog.DialogMessaggio;
import looigi.loowebplayer.utilities.GestioneListaBrani;
import looigi.loowebplayer.utilities.GestioneOggettiVideo;
import looigi.loowebplayer.utilities.RiempieListaInBackground;
import looigi.loowebplayer.utilities.Utility;

import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.RANDOM;
import static looigi.loowebplayer.utilities.GestioneListaBrani.ModiAvanzamento.SEQUENZIALE;

public class Settings extends Fragment {
    private Context context;
    private static String TAG = NomiMaschere.getInstance().getSettings();

    private RadioButton optNessuno = null;
    private RadioButton optData = null;
    private RadioButton optAlfabetico = null;
    private Switch chkAscendente = null;
    private Switch chkRandom = null;
    private StrutturaConfig vg = null;
    private boolean nonAggiornare1=true;
    private boolean nonAggiornare2=true;
    private Runnable runRiga1;
    private Handler hSelezionaRiga1;
    private Runnable runRiga2;
    private Handler hSelezionaRiga2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=null;

        try {
            view=(inflater.inflate(R.layout.settings, container, false));
        } catch (Exception ignored) {
            int e=0;
        }

        if (view!=null) {
            VariabiliStaticheGlobali.getInstance().setViewActivity(view);

            initializeGraphic();

            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        return true;
                    }
                    return false;
                }
            });
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void initializeGraphic() {
        final Context context = VariabiliStaticheGlobali.getInstance().getContext();
        final View view = VariabiliStaticheGlobali.getInstance().getViewActivity();
        vg = VariabiliStaticheGlobali.getInstance().getDatiGenerali().getConfigurazione();

        if (view != null) {
            // VariabiliStaticheGlobali.getInstance().setGiaEntrato(false);

            final Spinner spnrate = view.findViewById(R.id.spnRapporto);
            final Spinner spnbellezza = view.findViewById(R.id.spnBellezza);
            chkRandom = view.findViewById(R.id.chkRandom);
            Switch chkCompressione = view.findViewById(R.id.chkCompressione);
            Switch chkUsaScaricati = view.findViewById(R.id.chkUsaScaricati);
            Switch chkDownloadImmagini = view.findViewById(R.id.chkDownloadImmagini);
            Switch chkMostraTraffico = view.findViewById(R.id.chkMostraTraffico);
            Switch chkMostraBellezza = view.findViewById(R.id.chkMostraBellezza);
            Switch chkReload = view.findViewById(R.id.chkReload);
            Switch chkCaricamentoAnticipato = view.findViewById(R.id.chkCaricamentoAnticipato);
            Switch chkAnnuncio = view.findViewById(R.id.chkAnnuncio);
            Switch chkSalvataggio = view.findViewById(R.id.chkSalvataggio);
            Switch chkScaricoDettagli = view.findViewById(R.id.chkDettagli);
            Switch chkSchermoAcceso = view.findViewById(R.id.chkSchermoAcceso);
            Switch chkRiprendiUltimo = view.findViewById(R.id.chkRiprendiUltimo);
            Switch chkTestoInglese = view.findViewById(R.id.chkTestoInInglese);
            Switch chkLog = view.findViewById(R.id.chkLog);
            Switch chkMembri = view.findViewById(R.id.chkMembri);
            Switch chkBellezza = view.findViewById(R.id.chkStelle);
            Switch chkPronuncia = view.findViewById(R.id.chkPronuncia);
            Switch chkSfumaBrano = view.findViewById(R.id.chkSfumaBrano);
            final Switch chkSuperiore = view.findViewById(R.id.chkSuperiore);
            Switch chkControlloRete = view.findViewById(R.id.chkControlloRete);

            Switch chkPuliziaPerNumeroFiles = view.findViewById(R.id.chkPerNumeroFiles);
            Switch chkPuliziaPerMega = view.findViewById(R.id.chkPerMega);
            final EditText edtNumeroFiles = view.findViewById(R.id.edtNumeroFiles);
            final EditText edtMega = view.findViewById(R.id.edtMega);
            final Button cmdPuliziaPerNumeroFiles = view.findViewById(R.id.btnSalvaNumeroFiles);
            final Button cmdPuliziaPerMega = view.findViewById(R.id.btnSalvaMega);

            Button cmdPulisceLog = view.findViewById(R.id.btnPulisceLog);
            Button cmdRefreshBrani = view.findViewById(R.id.btnRefreshBrani);
            Button cmdPulisceFiltro = view.findViewById(R.id.btnPulisceFiltro);
            final Button cmdPiu = view.findViewById(R.id.btnPiu);
            final Button cmdMeno = view.findViewById(R.id.btnMeno);

            TextView txtFiltro = view.findViewById(R.id.txtFiltro);
            final TextView txtTentativi = view.findViewById(R.id.txtQuantiTentativi);

            Switch chkMostraOperazioni = view.findViewById(R.id.chkMostraOperazioni);

            optNessuno = view.findViewById(R.id.optNessuno);
            optData = view.findViewById(R.id.optData);
            optAlfabetico= view.findViewById(R.id.optAlfabetico);
            chkAscendente = view.findViewById(R.id.chkAscendente);

            int tipoOrdinamento = vg.getOrdinamento();
            ImpostaMascheraOrdinamento(tipoOrdinamento);

            // Valori framerate
            List<String> Rapp = new ArrayList<>();
            Rapp.add("48");
            Rapp.add("96");
            Rapp.add("128");
            Rapp.add("196");
            final List<String> Rapporti = Rapp;

            ArrayAdapter<String> adapterRate = new ArrayAdapter<String>(
                    VariabiliStaticheGlobali.getInstance().getContext(), R.layout.spinner_item_piccolo, Rapporti);
            adapterRate.setDropDownViewResource(R.layout.spinner_item_piccolo);
            spnrate.setAdapter(adapterRate);

            spnrate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Gestito - Funzionante
                    if (!nonAggiornare1) {
                        vg.setRapportoCompressione(Rapporti.get(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            // if (!VariabiliStaticheGlobali.getInstance().isEseguiHandlerConMainLooper()) {
            //     hSelezionaRiga1 = new Handler();
            // } else {
                hSelezionaRiga1 = new Handler(Looper.getMainLooper());
            // }
            hSelezionaRiga1.postDelayed(runRiga1=new Runnable() {
                @Override
                public void run() {
                    hSelezionaRiga1.removeCallbacks(runRiga1);
                    int pos = Utility.getInstance().CercaESettaStringaInSpinner(spnrate, vg.getRapportoCompressione());
                    if (pos>-1) {
                        spnrate.setSelection(pos);
                    }
                    nonAggiornare1=false;
                }
            }, 500);
            // Valori Framerate

            // Valori stelle

            List<String> Bellezza = new ArrayList<>();
            Bellezza.add("0");
            Bellezza.add("1");
            Bellezza.add("2");
            Bellezza.add("3");
            Bellezza.add("4");
            Bellezza.add("5");
            Bellezza.add("6");
            Bellezza.add("7");
            final List<String> Stelle = Bellezza;

            ArrayAdapter<String> adapterBellezza = new ArrayAdapter<String>(
                    VariabiliStaticheGlobali.getInstance().getContext(), R.layout.spinner_item_piccolo, Bellezza);
            adapterBellezza.setDropDownViewResource(R.layout.spinner_item_piccolo);
            spnbellezza.setAdapter(adapterBellezza);

            spnbellezza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Gestito - Funzionante
                    if (!nonAggiornare2) {
                        vg.setValoreBellezza(Stelle.get(position));

                        RiempieListaInBackground r = new RiempieListaInBackground();
                        r.RiempieStrutture(true);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            // if (!VariabiliStaticheGlobali.getInstance().isEseguiHandlerConMainLooper()) {
            //     hSelezionaRiga2 = new Handler();
            // } else {
                hSelezionaRiga2 = new Handler(Looper.getMainLooper());
            // }
            hSelezionaRiga2.postDelayed(runRiga2=new Runnable() {
                @Override
                public void run() {
                    hSelezionaRiga2.removeCallbacks(runRiga2);
                    int pos = Utility.getInstance().CercaESettaStringaInSpinner(spnbellezza, vg.getValoreBellezza());
                    if (pos>-1) {
                        spnbellezza.setSelection(pos);
                    }
                    nonAggiornare2=false;
                }
            }, 500);
            // Valori stelle

            // Switch mostra operazioni
            boolean bMostraOperazioni = vg.isMostraOperazioni();
            chkMostraOperazioni.setChecked(bMostraOperazioni);
            chkMostraOperazioni.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Selezionato switch mostra operazioni: "+isChecked);

                    vg.setMostraOperazioni(isChecked);

                    vg.SalvaDati();
                }
            });

            // Switch pulizia per numero files
            boolean bNumeroFiles = vg.isPuliziaPerFiles();
            chkPuliziaPerNumeroFiles.setChecked(bNumeroFiles);
            chkPuliziaPerNumeroFiles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Selezionato switch pulizia per numero files: "+isChecked);

                    vg.setPuliziaPerFiles(isChecked);

                    if (isChecked) {
                        edtNumeroFiles.setEnabled(true);
                        cmdPuliziaPerNumeroFiles.setEnabled(true);
                    } else {
                        edtNumeroFiles.setEnabled(false);
                        cmdPuliziaPerNumeroFiles.setEnabled(false);
                    }

                    vg.SalvaDati();
                }
            });
            if (bNumeroFiles) {
                edtNumeroFiles.setEnabled(true);
                cmdPuliziaPerNumeroFiles.setEnabled(true);
            } else {
                edtNumeroFiles.setEnabled(false);
                cmdPuliziaPerNumeroFiles.setEnabled(false);
            }
            edtNumeroFiles.setText(Integer.toString(vg.getQuantiFilesMemorizzati()));

            cmdPuliziaPerNumeroFiles.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Selezionato Salva Numero Files per Pulizia");

                    String ee = edtNumeroFiles.getText().toString();
                    int e = Integer.parseInt(ee);
                    if (e < 10 || e > 1000) {
                        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                                "Valore non valido (10 - 1000)", true, VariabiliStaticheGlobali.NomeApplicazione);
                    } else {
                        vg.setQuantiFilesMemorizzati(e);
                        vg.SalvaDati();

                        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                                "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                    }
                }
            });
            // Switch pulizia per numero files

            // Switch pulizia per mega
            boolean bMega = vg.isPuliziaPerMega();
            chkPuliziaPerMega.setChecked(bMega);
            chkPuliziaPerMega.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Selezionato switch pulizia per mega: "+isChecked);

                    vg.setPuliziaPerMega(isChecked);

                    if (isChecked) {
                        edtMega.setEnabled(true);
                        cmdPuliziaPerMega.setEnabled(true);
                    } else {
                        edtMega.setEnabled(false);
                        cmdPuliziaPerMega.setEnabled(false);
                    }

                    vg.SalvaDati();
                }
            });
            if (bMega) {
                edtMega.setEnabled(true);
                cmdPuliziaPerMega.setEnabled(true);
            } else {
                edtMega.setEnabled(false);
                cmdPuliziaPerMega.setEnabled(false);
            }
            edtMega.setText(Integer.toString(vg.getQuantiMBAlMassimo()));

            cmdPuliziaPerMega.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Selezionato Salva Mega per Pulizia");

                    String ee = edtMega.getText().toString();
                    int e = Integer.parseInt(ee);
                    if (e < 10 || e > 1000) {
                        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                                "Valore non valido (10 - 1000)", true, VariabiliStaticheGlobali.NomeApplicazione);
                    } else {
                        vg.setQuantiMBAlMassimo(e);
                        vg.SalvaDati();

                        DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                                "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                    }
                }
            });
            // Switch pulizia per mega

            // Switch stelle
            Boolean bStelle = vg.isStelle();
            chkBellezza.setChecked(bStelle);
            chkBellezza.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch bellezza: "+isChecked);

                    vg.setStelle(isChecked);

                    if (isChecked) {
                        spnbellezza.setEnabled(true);
                        chkSuperiore.setEnabled(true);
                    } else {
                        spnbellezza.setEnabled(false);
                        chkSuperiore.setEnabled(false);
                    }
                    ImpostaMascheraOrdinamento(vg.getOrdinamento());

                    RiempieListaInBackground r = new RiempieListaInBackground();
                    r.RiempieStrutture(true);
                }
            });
            if (bStelle) {
                spnbellezza.setEnabled(true);
                chkSuperiore.setEnabled(true);
            } else {
                spnbellezza.setEnabled(false);
                chkSuperiore.setEnabled(false);
            }
            // Switch stelle

            // Switch superiore
            Boolean bSuperiore = vg.isSuperiore();
            chkSuperiore.setChecked(bSuperiore);
            chkSuperiore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch superiore: "+isChecked);

                    vg.setSuperiore(isChecked);

                    ImpostaMascheraOrdinamento(vg.getOrdinamento());

                    RiempieListaInBackground r = new RiempieListaInBackground();
                    r.RiempieStrutture(true);
                }
            });
            if (bStelle) {
                spnbellezza.setEnabled(true);
                chkSuperiore.setEnabled(true);
            } else {
                spnbellezza.setEnabled(false);
                chkSuperiore.setEnabled(false);
            }
            // Switch superiore

            // Switch random
            Boolean bRandom = vg.isRandom();
            chkRandom.setChecked(bRandom);
            chkRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch random: "+isChecked);

                    vg.setRandom(isChecked);

                    if (isChecked) {
                        GestioneListaBrani.getInstance().setModalitaAvanzamento(RANDOM);
                    } else {
                        GestioneListaBrani.getInstance().setModalitaAvanzamento(SEQUENZIALE);
                    }
                    ImpostaMascheraOrdinamento(vg.getOrdinamento());

                    RiempieListaInBackground r = new RiempieListaInBackground();
                    r.RiempieStrutture(true);
                }
            });

            // Switch compressione
            Boolean bCompressione = vg.isCompressioneMP3();
            chkCompressione.setChecked(bCompressione);
            chkCompressione.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch compressione: "+isChecked);

                    vg.setCompressioneMP3(isChecked);
                    if (isChecked) {
                        spnrate.setEnabled(true);
                    } else {
                        spnrate.setEnabled(false);
                    }
                }
            });
            if (bCompressione) {
                spnrate.setEnabled(true);
            } else {
                spnrate.setEnabled(false);
            }

            // Switch usa scaricati
            Boolean bUsaScaricati = vg.getUsaScaricati();
            chkUsaScaricati.setChecked(bUsaScaricati);
            chkUsaScaricati.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch usa scaricati: "+isChecked);

                    vg.setUsaScaricati(isChecked);

                    RiempieListaInBackground r = new RiempieListaInBackground();
                    r.RiempieStrutture(true);
                }
            });

            // Switch download immagini
            Boolean bdownloadImmagini = vg.getDownloadImmagini();
            chkDownloadImmagini.setChecked(bdownloadImmagini);
            chkDownloadImmagini.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - DA PROVARE
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch download immagini: "+isChecked);

                    vg.setDownloadImmagini(isChecked);
                }
            });

            // Switch mostra traffico
            Boolean bMostraTraffico = vg.getVisualizzaTraffico();
            chkMostraTraffico.setChecked(bMostraTraffico);
            chkMostraTraffico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch visualizza traffico: "+isChecked);

                    vg.setVisualizzaTraffico(isChecked);
                }
            });

            // Switch sfuma
            Boolean bSfumaBrano = vg.getSfumaBrano();
            chkSfumaBrano.setChecked(bSfumaBrano);
            chkSfumaBrano.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch sfuma brano: "+isChecked);

                    vg.setSfumaBrano(isChecked);
                }
            });

            // Switch mostra bellezza
            Boolean bMostraBellezza = vg.getVisualizzaBellezza();
            chkMostraBellezza.setChecked(bMostraBellezza);
            chkMostraBellezza.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch visualizza Bellezza: "+isChecked);

                    vg.setVisualizzaBellezza(isChecked);
                }
            });

            // Switch reload automatico
            Boolean bReload = vg.getReloadAutomatico();
            if (bReload) {
                cmdMeno.setEnabled(true);
                cmdPiu.setEnabled(true);
                txtTentativi.setEnabled(true);
            } else {
                cmdMeno.setEnabled(false);
                cmdPiu.setEnabled(false);
                txtTentativi.setEnabled(false);
            }
            txtTentativi.setText(Integer.toString(vg.getQuantiTentativi()));
            chkReload.setChecked(bReload);
            chkReload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch reload automatico: "+isChecked);

                    vg.setReloadAutomatico(isChecked);

                    if (isChecked) {
                        cmdMeno.setEnabled(true);
                        cmdPiu.setEnabled(true);
                        txtTentativi.setEnabled(true);
                    } else {
                        cmdMeno.setEnabled(false);
                        cmdPiu.setEnabled(false);
                        txtTentativi.setEnabled(false);
                    }
                }
            });
            cmdPiu.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int t = vg.getQuantiTentativi();
                    t++;
                    vg.setQuantiTentativi(t);
                    txtTentativi.setText(Integer.toString(vg.getQuantiTentativi()));
                }
            });
            cmdMeno.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int t = vg.getQuantiTentativi();
                    if (t>1) {
                        t--;
                        vg.setQuantiTentativi(t);
                        txtTentativi.setText(Integer.toString(vg.getQuantiTentativi()));
                    }
                }
            });

            // Switch caricamento anticipato
            Boolean bCaricamentoAnticipato = vg.getCaricamentoAnticipato();
            chkCaricamentoAnticipato.setChecked(bCaricamentoAnticipato);
            chkCaricamentoAnticipato.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch caricamento anticipato: "+isChecked);

                    vg.setCaricamentoAnticipato(isChecked);
                }
            });

            // Switch annuncio brano
            Boolean bAnnuncio = vg.getAnnuncioBrano();
            chkAnnuncio.setChecked(bAnnuncio);
            chkAnnuncio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch annuncio brano: "+isChecked);

                    vg.setAnnuncioBrano(isChecked);
                }
            });

            // Switch salvataggio oggetti
            Boolean bSalvatggio = vg.isSalvataggioOggetti();
            chkSalvataggio.setChecked(bSalvatggio);
            chkSalvataggio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - DA PROVARE
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch salvataggio oggetti: "+isChecked);

                    vg.setSalvataggioOggetti(isChecked);
                }
            });

            // Switch scarico dettagli
            Boolean bScaricoDettagli = vg.isScaricoDettagli();
            chkScaricoDettagli.setChecked(bScaricoDettagli);
            chkScaricoDettagli.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - DA PROVARE
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch scarico dettagli: "+isChecked);

                    vg.setScaricoDettagli(isChecked);
                }
            });

            // Switch schermo acceso
            Boolean bSchermoAcceso = vg.getSchermoSempreAcceso();
            chkSchermoAcceso.setChecked(bSchermoAcceso);
            chkSchermoAcceso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch schermo sempre acceso: "+isChecked);

                    vg.setSchermoSempreAcceso(isChecked);

                    GestioneOggettiVideo.getInstance().SchermoAccesoSpento();
                }
            });

            // Switch ricorda ultimo brano
            Boolean bRiprendiUltimo = vg.getRicordaUltimoBrano();
            chkRiprendiUltimo.setChecked(bRiprendiUltimo);
            chkRiprendiUltimo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch ricorda ultimo brano: "+isChecked);

                    vg.setRicordaUltimoBrano(isChecked);
                }
            });

            // Switch testo in inglese
            Boolean bTestoInglese = vg.getTestoInInglese();
            chkTestoInglese.setChecked(bTestoInglese);
            chkTestoInglese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch testo in inglese: "+isChecked);

                    vg.setTestoInInglese(isChecked);
                }
            });

            // Switch log
            Boolean bLog = vg.getScriveLog();
            chkLog.setChecked(bLog);
            chkLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch scrive log: "+isChecked);

                    vg.setScriveLog(isChecked);
                }
            });

            // Membri
            Boolean bMembri = vg.getMembri();
            chkMembri.setChecked(bMembri);
            chkMembri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch membri: "+isChecked);

                    vg.setMembri(isChecked);

                    if (VariabiliStaticheHome.getInstance().getGm()!=null) {
                        if (isChecked) {
                            VariabiliStaticheHome.getInstance().getGm().RiparteCiclo();
                        } else {
                            VariabiliStaticheHome.getInstance().getGm().StoppaCiclo();
                        }
                    }
                }
            });

            // Pronuncia
            Boolean bPronuncia = vg.getPronunciaOperazioni();
            chkPronuncia.setChecked(bPronuncia);
            chkPronuncia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch pronuncia operazioni: "+isChecked);

                    vg.setPronunciaOperazioni(isChecked);
                }
            });

            // Button Refresh brani
            cmdRefreshBrani.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                            new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Selezionato refresh brani");

                    String path=VariabiliStaticheGlobali.getInstance().PercorsoDIR+"/Lista.dat";
                    File f = new File(path);
                    if (f.exists()) {
                        f.delete();
                    }
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(
                            new Object(){}.getClass().getEnclosingMethod().getName(),
                            "Interpello il ws per la lista brani");

                    int NumeroOperazione = VariabiliStaticheHome.getInstance().AggiungeOperazioneWEB(-1, false, "Download Lista Brani");
                    DBRemotoNuovo dbr = new DBRemotoNuovo();
                    dbr.RitornaListaBrani(VariabiliStaticheGlobali.getInstance().getContext(),
                            "", "", "", "", "S", "N",
                            NumeroOperazione);

                    // DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                    //         "Brani NON ricaricati.\nSISTEMARE LA ROUTINE in Settings", true, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });

            // Button pulisce filtro
            if (vg.getFiltro().isEmpty()) {
                txtFiltro.setText("");
            } else {
                String f[] = vg.getFiltro().split("_");
                txtFiltro.setText(f[0] + " (" + f[1].replace("***UNDERLINE***", "_") + ")");
            }
            if (vg.getFiltro().isEmpty()) {
                cmdPulisceFiltro.setEnabled(false);
            } else {
                cmdPulisceFiltro.setEnabled(true);
            }
            cmdPulisceFiltro.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato pulisce filtro");

                    vg.setFiltro("");

                    RiempieListaInBackground r = new RiempieListaInBackground();
                    r.RiempieStrutture(true);
                    GestioneOggettiVideo.getInstance().ScriveFiltro();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "Brani ricaricati", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });

            // Button pulisce log
            cmdPulisceLog.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato pulisce log");

                    VariabiliStaticheGlobali.getInstance().getLog().PulisceFileDiLog();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "File di log pulito", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });

            // Switch ascendente
            Boolean bAscendente = vg.getCrescente();
            chkAscendente.setChecked(bAscendente);
            chkAscendente.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - DA PROVARE
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch ordinamento crescente: "+isChecked);

                    vg.setCrescente(isChecked);

                    RiempieListaInBackground r = new RiempieListaInBackground();
                    r.RiempieStrutture(true);
                }
            });

            // opt ordinamento nessuno
            optNessuno.setOnClickListener(clickNessuno);

            // opt ordinamento data
            optData.setOnClickListener(clickData);

            // opt ordinamento alfabetico
            optAlfabetico.setOnClickListener(clickAlfabetico);

            ImpostaMascheraOrdinamento(vg.getOrdinamento());

            // Editing valori
            final EditText edtTimeoutDLMP3 = view.findViewById(R.id.edtTimeoutDLMP3);
            final EditText edtTimeoutDLLB = view.findViewById(R.id.edtTimeoutDLLB);
            final EditText edtAttesaDLMP3 = view.findViewById(R.id.edtAttesaDLMP3);
            final EditText edtTimeoutDLIMM = view.findViewById(R.id.edtTimeoutDLIMM);

            edtTimeoutDLMP3.setText(Integer.toString(VariabiliStaticheGlobali.getInstance().getTimeOutDownloadMP3()));
            edtTimeoutDLLB.setText(Integer.toString(VariabiliStaticheGlobali.getInstance().getTimeOutListaBrani()));
            edtAttesaDLMP3.setText(Integer.toString(VariabiliStaticheGlobali.getInstance().getAttesaControlloEsistenzaMP3()));
            edtTimeoutDLIMM.setText(Integer.toString(VariabiliStaticheGlobali.getInstance().getTimeOutImmagini()));

            Button btnTimeoutDLMP3 = view.findViewById(R.id.btTimeoutDLMP3);
            btnTimeoutDLMP3.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato btnTimeoutDLMP3");

                    String ee = edtTimeoutDLMP3.getText().toString();
                    VariabiliStaticheGlobali.getInstance().setTimeOutDownloadMP3(Integer.parseInt(ee));
                    vg.SalvaDati();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });
            Button btnTimeoutDLLB = view.findViewById(R.id.btTimeoutDLLB);
            btnTimeoutDLLB.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato btnTimeoutDLLB");

                    String ee = edtTimeoutDLLB.getText().toString();
                    VariabiliStaticheGlobali.getInstance().setTimeOutListaBrani(Integer.parseInt(ee));
                    vg.SalvaDati();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });
            Button btnAttesaDLMP3 = view.findViewById(R.id.btAttesaDLMP3);
            btnAttesaDLMP3.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato btnAttesaDLMP3");

                    String ee = edtAttesaDLMP3.getText().toString();
                    VariabiliStaticheGlobali.getInstance().setAttesaControlloEsistenzaMP3(Integer.parseInt(ee));
                    vg.SalvaDati();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });
            Button btnTimeoutDLIMM = view.findViewById(R.id.btTimeoutDLIMM);
            btnTimeoutDLIMM.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato btnTimeoutDLIMM");

                    String ee = edtTimeoutDLIMM.getText().toString();
                    VariabiliStaticheGlobali.getInstance().setTimeOutImmagini(Integer.parseInt(ee));
                    vg.SalvaDati();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });

            // Tipo segnale
            final RadioButton optNormale = view.findViewById(R.id.optNormale);
            final RadioButton optIrregolare = view.findViewById(R.id.optIrregolare);
            final RadioButton optAssente = view.findViewById(R.id.optAssente);
            final RadioButton optPresente = view.findViewById(R.id.optPresente);

            // Controllo rete
            Boolean bControlloRete = vg.getControlloRete();
            chkControlloRete.setChecked(bControlloRete);
            chkControlloRete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Gestito - Funzionante
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato switch controllo rete: "+isChecked);

                    vg.setControlloRete(isChecked);

                    if (isChecked) {
                        optNormale.setEnabled(true);
                        optIrregolare.setEnabled(true);
                        optAssente.setEnabled(true);
                        optPresente.setEnabled(true);
                    } else {
                        optNormale.setEnabled(false);
                        optIrregolare.setEnabled(false);
                        optAssente.setEnabled(false);
                        optPresente.setEnabled(false);
                    }
                }
            });
            if (bControlloRete) {
                optNormale.setEnabled(true);
                optIrregolare.setEnabled(true);
                optAssente.setEnabled(true);
                optPresente.setEnabled(true);
            } else {
                optNormale.setEnabled(false);
                optIrregolare.setEnabled(false);
                optAssente.setEnabled(false);
                optPresente.setEnabled(false);
            }

            optNormale.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato optNormale");

                    optNormale.setChecked(true);
                    optIrregolare.setChecked(false);
                    optAssente.setChecked(false);
                    optPresente.setChecked(false);
                    VariabiliStaticheGlobali.getInstance().setTipoSegnale(1);

                    // NetThread.getInstance().StopNetThread();
                    // NetThread.getInstance().start();

                    vg.SalvaDati();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });
            optIrregolare.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato optIrregolare");

                    optNormale.setChecked(false);
                    optIrregolare.setChecked(true);
                    optAssente.setChecked(false);
                    optPresente.setChecked(false);
                    VariabiliStaticheGlobali.getInstance().setTipoSegnale(2);

                    // NetThread.getInstance().StopNetThread();
                    // NetThread.getInstance().start();

                    vg.SalvaDati();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });
            optAssente.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato optAssente");

                    optNormale.setChecked(false);
                    optIrregolare.setChecked(false);
                    optAssente.setChecked(true);
                    optPresente.setChecked(false);
                    VariabiliStaticheGlobali.getInstance().setTipoSegnale(3);

                    // NetThread.getInstance().StopNetThread();
                    // NetThread.getInstance().start();

                    vg.SalvaDati();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });
            optPresente.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato optPresente");

                    optNormale.setChecked(false);
                    optIrregolare.setChecked(false);
                    optAssente.setChecked(false);
                    optPresente.setChecked(true);
                    VariabiliStaticheGlobali.getInstance().setTipoSegnale(4);

                    // NetThread.getInstance().StopNetThread();
                    // NetThread.getInstance().start();

                    vg.SalvaDati();

                    DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                            "Salvataggio effettuato", false, VariabiliStaticheGlobali.NomeApplicazione);
                }
            });

            switch (VariabiliStaticheGlobali.getInstance().getTipoSegnale()) {
                case 1:
                    optNormale.setChecked(true);
                    optIrregolare.setChecked(false);
                    optAssente.setChecked(false);
                    optPresente.setChecked(false);
                    break;
                case 2:
                    optNormale.setChecked(false);
                    optIrregolare.setChecked(true);
                    optAssente.setChecked(false);
                    optPresente.setChecked(false);
                    break;
                case 3:
                    optNormale.setChecked(false);
                    optIrregolare.setChecked(false);
                    optAssente.setChecked(true);
                    optPresente.setChecked(false);
                    break;
                case 4:
                    optNormale.setChecked(false);
                    optIrregolare.setChecked(false);
                    optAssente.setChecked(false);
                    optPresente.setChecked(true);
                    break;
            }
        }
    }

    View.OnClickListener clickNessuno = new View.OnClickListener(){
        public void onClick(View v) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato nessun ordinamento");

            vg.setOrdinamento(0);

            ImpostaMascheraOrdinamento(0);

            RiempieListaInBackground r = new RiempieListaInBackground();
            r.RiempieStrutture(true);

            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                    "Impostato ordinamento e sistemata lista brani", false, VariabiliStaticheGlobali.NomeApplicazione);
        }
    };

    View.OnClickListener clickData = new View.OnClickListener(){
        public void onClick(View v) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato ordinamento per data");

            vg.setOrdinamento(1);

            ImpostaMascheraOrdinamento(1);

            RiempieListaInBackground r = new RiempieListaInBackground();
            r.RiempieStrutture(true);

            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                    "Impostato ordinamento e sistemata lista brani", false, VariabiliStaticheGlobali.NomeApplicazione);
        }
    };

    View.OnClickListener clickAlfabetico = new View.OnClickListener(){
        public void onClick(View v) {
            VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(new Object(){}.getClass().getEnclosingMethod().getName(), "Selezionato ordinamento alfabetico");

            vg.setOrdinamento(2);

            ImpostaMascheraOrdinamento(2);

            RiempieListaInBackground r = new RiempieListaInBackground();
            r.RiempieStrutture(true);

            DialogMessaggio.getInstance().show(VariabiliStaticheGlobali.getInstance().getFragmentActivityPrincipale(),
                    "Impostato ordinamento e sistemata lista brani", false, VariabiliStaticheGlobali.NomeApplicazione);
        }
    };

    private void ImpostaMascheraOrdinamento(int tipo) {
        if (chkRandom.isChecked()) {
            optNessuno.setEnabled(false);
            optData.setEnabled(false);
            optAlfabetico.setEnabled(false);
            chkAscendente.setEnabled(false);
        } else {
            optNessuno.setEnabled(true);
            optData.setEnabled(true);
            optAlfabetico.setEnabled(true);
            chkAscendente.setEnabled(true);
        }

        switch (tipo) {
            case 0:
                // Nessun ordinamento
                optNessuno.setChecked(true);
                optData.setChecked(false);
                optAlfabetico.setChecked(false);
                chkAscendente.setEnabled(false);
                break;
            case 1:
                // Data
                optNessuno.setChecked(false);
                optData.setChecked(true);
                optAlfabetico.setChecked(false);
                if (chkRandom.isChecked()) {
                    chkAscendente.setEnabled(false);
                } else {
                    chkAscendente.setEnabled(true);
                }
                break;
            case 2:
                // Alfabetico
                optNessuno.setChecked(false);
                optData.setChecked(false);
                optAlfabetico.setChecked(true);
                if (chkRandom.isChecked()) {
                    chkAscendente.setEnabled(false);
                } else {
                    chkAscendente.setEnabled(true);
                }
                break;
        }
    }
}
