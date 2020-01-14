/* package looigi.loowebplayer.maschere;

import android.content.Context;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheEqualizzatore;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;
import looigi.loowebplayer.utilities.GestioneFiles;

public class Equalizer extends android.support.v4.app.Fragment implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {
    private static Equalizer instance = null;

    public Equalizer() {
    }

    public static Equalizer getInstance() {
        if (instance == null) {
            instance = new Equalizer();
        }

        return instance;
    }

    private Context context;
    private TextView bass_boost_label = null;
    private SeekBar bass_boost = null;
    private CheckBox enabled = null;
    private Button flat = null;

    private int min_level = 0;
    private int max_level = 100;

    private static final int MAX_SLIDERS = 8; // Must match the XML layout
    private SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    private TextView slider_labels[] = new TextView[MAX_SLIDERS];
    private int num_sliders = 0;

    private String pathConfig = VariabiliStaticheGlobali.getInstance().PercorsoDIR+"/";
    private String NomeFileConfig = "config_eq.dat";
    private VariabiliStaticheEqualizzatore ve=VariabiliStaticheEqualizzatore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = this.getActivity();

        View view=null;

        try {
            view=(inflater.inflate(R.layout.equalizer, container, false));
        } catch (Exception ignored) {

        }

        if (view!=null) {
            VariabiliStaticheGlobali.getInstance().setViewActivity(view);

            initializeGraphic();
        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        //isVisible=isVisibleToUser;

        //if (isVisible) {
        //    initializeGraphic();
        //}
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //if (isVisible) {
        //    initializeGraphic();
        //}
    }

    private void initializeGraphic() {
        Context context = VariabiliStaticheGlobali.getInstance().getContext();
        View view = VariabiliStaticheGlobali.getInstance().getViewActivity();

        if (view != null) {
            enabled = (CheckBox) view.findViewById(R.id.enabled);
            enabled.setOnCheckedChangeListener (this);

            flat = (Button) view.findViewById(R.id.flat);
            flat.setOnClickListener(this);

            bass_boost = (SeekBar) view.findViewById(R.id.bass_boost);
            bass_boost.setOnSeekBarChangeListener(this);
            bass_boost_label = (TextView) view.findViewById (R.id.bass_boost_label);

            sliders[0] = (SeekBar) view.findViewById(R.id.slider_1);
            slider_labels[0] = (TextView) view.findViewById(R.id.slider_label_1);
            sliders[1] = (SeekBar) view.findViewById(R.id.slider_2);
            slider_labels[1] = (TextView) view.findViewById(R.id.slider_label_2);
            sliders[2] = (SeekBar) view.findViewById(R.id.slider_3);
            slider_labels[2] = (TextView) view.findViewById(R.id.slider_label_3);
            sliders[3] = (SeekBar) view.findViewById(R.id.slider_4);
            slider_labels[3] = (TextView) view.findViewById(R.id.slider_label_4);
            sliders[4] = (SeekBar) view.findViewById(R.id.slider_5);
            slider_labels[4] = (TextView) view.findViewById(R.id.slider_label_5);
            sliders[5] = (SeekBar) view.findViewById(R.id.slider_6);
            slider_labels[5] = (TextView) view.findViewById(R.id.slider_label_6);
            sliders[6] = (SeekBar) view.findViewById(R.id.slider_7);
            slider_labels[6] = (TextView) view.findViewById(R.id.slider_label_7);
            sliders[7] = (SeekBar) view.findViewById(R.id.slider_8);
            slider_labels[7] = (TextView) view.findViewById(R.id.slider_label_8);

            // VariabiliStaticheGlobali.getInstance().setEq(new android.media.audiofx.Equalizer(0, 0));
            if (ve.getEq() == null) {
                ve.setEq(new android.media.audiofx.Equalizer(0, 0));
            }

            if (ve.getBb() == null) {
                ve.setBb(new BassBoost (0, 0));
            }

            ImpostaValoriMaschera();

            int num_bands = ve.getEq().getNumberOfBands();
            num_sliders = num_bands;
            short r[] = ve.getEq().getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            int[] Valori=ve.getValori();
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++) {
                int[] freq_range = ve.getEq().getBandFreqRange((short)i);
                sliders[i].setOnSeekBarChangeListener(this);
                // slider_labels[i].setText(formatBandLabel(freq_range));
                slider_labels[i].setText(formatBandLabel(freq_range)+": "+Integer.toString(Valori[i]));
            }

            for (int i = num_sliders ; i < MAX_SLIDERS; i++) {
                sliders[i].setVisibility(View.GONE);
                slider_labels[i].setVisibility(View.GONE);
            }

            // bb = new BassBoost (0, 0);
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);

            updateUI();
        }
    }

    public void ImpostaValoriMaschera() {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Imposta valori Maschera Equalizer");
        ve.getEq().setEnabled(ve.getAbilitato());
        if (enabled!=null) {
            enabled.setChecked(ve.getAbilitato());
        }
        ve.getBb().setStrength((short) ve.getBassBoost());
        int a=0;
        int[] Valori=ve.getValori();
        for (int i : Valori) {
            try {
                int new_level = min_level + (max_level - min_level) * i / 100;
                ve.getEq().setBandLevel((short) a, (short) new_level);
                if (sliders[a] != null) {
                    int pos = 100 * i / (max_level - min_level) + 50;
                    sliders[a].setProgress(pos);
                }
            } catch (Exception e) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            }
            a++;
        }
    }

    private String formatBandLabel (int[] band) {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }

    @Override
    public void onProgressChanged (SeekBar seekBar, int level,
                                   boolean fromTouch) {
        if (seekBar == bass_boost) {
            ve.getBb().setEnabled (level > 0 ? true : false);
            ve.getBb().setStrength ((short) level); // Already in the right range 0-1000
        } else if (ve.getEq() != null) {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++) {
                if (sliders[i] == seekBar) {
                    ve.getEq().setBandLevel ((short)i, (short) new_level);
                    ve.AggiornaValore(i, new_level);
                    break;
                }
            }
            SalvaValori();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private String milliHzToString (int milliHz) {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    private void updateSliders () {
        for (int i = 0; i < num_sliders; i++) {
            int level;

            if (ve.getEq() != null) {
                level = ve.getEq().getBandLevel((short) i);
            } else {
                level = 0;
            }
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress (pos);
        }
    }

    private void updateBassBoost () {
        if (ve.getBb() != null) {
            bass_boost.setProgress(ve.getBb().getRoundedStrength());
            ve.setBassBoost(ve.getBb().getRoundedStrength());
        } else {
            bass_boost.setProgress(0);
            ve.setBassBoost(0);
        }
        SalvaValori();
    }

    @Override
    public void onCheckedChanged (CompoundButton view, boolean isChecked) {
        if (view == (View) enabled) {
            ve.getEq().setEnabled (isChecked);
            ve.setAbilitato(isChecked);
            SalvaValori();
        }
    }

    @Override
    public void onClick (View view) {
        if (view == (View) flat) {
            setFlat();
        }
    }

    private void updateUI () {
        updateSliders();
        updateBassBoost();
        enabled.setChecked(ve.getEq().getEnabled());
    }

    private void setFlat () {
        if (ve.getEq() != null) {
            for (int i = 0; i < num_sliders; i++) {
                ve.getEq().setBandLevel ((short)i, (short)0);
            }
        }

        if (ve.getBb() != null) {
            ve.getBb().setEnabled (false);
            ve.getBb().setStrength ((short)0);
        }

        updateUI();

        SalvaValori();
    }

    private void SalvaValori() {
        String sAbilitato = "S"; if (!ve.getAbilitato()) sAbilitato="N";

        String Stringona= sAbilitato+";";
        Stringona+=Integer.toString(ve.getBb().getRoundedStrength())+";";
        int[] Valori=ve.getValori();
        for (int i : Valori) {
            Stringona+=Integer.toString(i)+";";
        }

        GestioneFiles.getInstance().CreaFileDiTesto(pathConfig, NomeFileConfig, Stringona);
    }

    public void CaricaValori() {
        File f = new File(pathConfig+NomeFileConfig);
        if (f.exists()) {
            String Stringona = GestioneFiles.getInstance().LeggeFileDiTesto(pathConfig + NomeFileConfig);
            if (!Stringona.trim().isEmpty()) {
                String Campi[] = Stringona.split(";", -1);

                if (ve.getEq() == null) {
                    ve.setEq(new android.media.audiofx.Equalizer(0, 0));
                }

                if (ve.getBb() == null) {
                    ve.setBb(new BassBoost (0, 0));
                }

                VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Carica valori equalizer");
                try {
                    String sAbilitato = Campi[0];
                    ve.setAbilitato(sAbilitato.equals("S"));
                    ve.getBb().setStrength((short) Integer.parseInt(Campi[1]));
                    int n = 2;
                    while (n<Campi.length-1) {
                        ve.AggiornaValore(n-2, Integer.parseInt(Campi[n]));
                        n++;
                    }
                } catch (Exception e) {
                    VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
                    SalvaValori();
                }
            }
        } else {
            SalvaValori();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch (item.getItemId())
        // {
        //     case R.id.about:
        //         showAbout();
        //         return true;
        // }
        return super.onOptionsItemSelected(item);
    }
}
*/