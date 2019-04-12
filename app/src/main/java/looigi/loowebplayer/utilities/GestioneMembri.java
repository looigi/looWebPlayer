package looigi.loowebplayer.utilities;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheHome;
import looigi.loowebplayer.dati.dettaglio_dati.StrutturaMembri;

public class GestioneMembri {
    private TextView txtCasellaTesto;
    private List<StrutturaMembri> Membri;
    private int Quale;
    private int Quanti;
    private Animation fadeIn = new AlphaAnimation(0.1f, 1.0f);
    private Animation fadeOut = new AlphaAnimation(1.0f, 0.1f);

    public void setTxtCasellaTesto(TextView txtCasellaTesto) {
        this.txtCasellaTesto = txtCasellaTesto;
    }

    public void setMembri(List<StrutturaMembri> membri) {
        Membri = membri;
    }

    public void CominciaAGirare() {
        if (Membri!=null && txtCasellaTesto!=null) {
            Quale=0;
            Quanti=Membri.size()-1;

            String s = SistemaScritte();
            if (s.isEmpty()) {
                VariabiliStaticheHome.getInstance().getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
            } else {
                VariabiliStaticheHome.getInstance().getTxtMembriTitolo().setVisibility(LinearLayout.VISIBLE);
                txtCasellaTesto.setText(s);
            }

            if (Quanti>1) {
                setUpFadeAnimation();
            }
        }
    }

    private String SistemaScritte() {
        if (!Membri.get(Quale).getMembro().isEmpty()) {
            String Attuale = "Sì";
            if (Membri.get(Quale).getAttuale().equals("N") || Membri.get(Quale).getMembro().toUpperCase().contains("NESSUNO") ) {
                // Attuale = "No";

                return "";
            } else {
                String Altro = Membri.get(Quale).getDurata().trim();
                if (Altro.length() > 3) {
                    if (Altro.substring(0, 2).equals("- ")) {
                        Altro = Altro.substring(2, Altro.length());
                    } else {
                        char character[] = Altro.substring(0, 1).toCharArray();
                        int ascii = (int) character[0];
                        if (ascii == 8211) {
                            Altro = Altro.substring(2, Altro.length());
                        }
                    }
                }

                if (Altro.trim().isEmpty()) {
                    return Membri.get(Quale).getMembro(); //  + "\nAttuale: " + Attuale;
                } else {
                    if (Altro.trim().toUpperCase().equals(Membri.get(Quale).getMembro().toUpperCase().trim())) {
                        return Membri.get(Quale).getMembro(); // + "\nAttuale: " + Attuale;
                    } else {
                        return Altro + "\n" + Membri.get(Quale).getMembro(); // + "\nAttuale: " + Attuale;
                    }
                }
            }
        } else {
            return "Nessun membro rilevato";
        }
    }

    public void StoppaCiclo() {
        VariabiliStaticheHome.getInstance().getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
        VariabiliStaticheHome.getInstance().getTxtMembri().setVisibility(LinearLayout.GONE);

        fadeIn.cancel();
        fadeOut.cancel();
    }

    public void RiparteCiclo() {
        VariabiliStaticheHome.getInstance().getTxtMembriTitolo().setVisibility(LinearLayout.VISIBLE);
        VariabiliStaticheHome.getInstance().getTxtMembri().setVisibility(LinearLayout.VISIBLE);

        CominciaAGirare();
    }

    private void setUpFadeAnimation() {
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(3000);

        // End to 0.1f if you desire 90% fade animation
        fadeOut.setDuration(1000);
        fadeOut.setStartOffset(3000);

        fadeIn.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeOut when fadeIn ends (continue)
                txtCasellaTesto.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeIn when fadeOut ends (repeat)
                Quale++;
                if(Quale>Quanti) {
                    Quale=0;
                }

                String s = SistemaScritte();
                if (s.isEmpty()) {
                    VariabiliStaticheHome.getInstance().getTxtMembriTitolo().setVisibility(LinearLayout.GONE);
                } else {
                    VariabiliStaticheHome.getInstance().getTxtMembriTitolo().setVisibility(LinearLayout.VISIBLE);
                    txtCasellaTesto.setText(s);
                }

                txtCasellaTesto.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        txtCasellaTesto.startAnimation(fadeOut);
    }
}
