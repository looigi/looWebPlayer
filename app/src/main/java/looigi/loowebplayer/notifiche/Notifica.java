package looigi.loowebplayer.notifiche;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import looigi.loowebplayer.R;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheDebug;
import looigi.loowebplayer.VariabiliStatiche.VariabiliStaticheGlobali;

public class Notifica {
    private boolean effettuaLogQui = VariabiliStaticheDebug.getInstance().DiceSeCreaLog("Notifica");;
    //-------- Singleton ----------//
    private static Notifica instance = null;

    private Notifica() {
    }

    public static Notifica getInstance() {
        if (instance == null) {
            instance = new Notifica();
        }

        return instance;
    }

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private RemoteViews contentView;
    private Context context;
    private int Icona;
    private String Titolo;
    private String Contenuto;
    private String Immagine;
    private String Artista;
    private String Album;
    private boolean inDownload=false;
    private static final int NOTIF_ID = 272727;

    public void setInDownload(boolean inDownload) {
        this.inDownload = inDownload;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setIcona(int icona) {
        Icona = icona;
    }

    public void setTitolo(String titolo) {
        Titolo = titolo;
    }

    public void setContenuto(String contenuto) {
        Contenuto = contenuto;
    }

    public void setImmagine(String immagine) {
        Immagine = immagine;
    }

    public void setArtista(String artista) {
        Artista = artista;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    /* public void CreaNotificaVecchio() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationManager = (NotificationManager) VariabiliStaticheGlobali.getInstance()
                    .getFragmentActivityPrincipale().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder = new NotificationCompat.Builder(context);
            notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
            notificationBuilder.setOngoing(true);

            contentView = new RemoteViews(VariabiliStaticheGlobali.getInstance()
                    .getFragmentActivityPrincipale().getPackageName(), R.layout.barra_notifica);
            setListenersTasti(contentView);

            notificationBuilder.setContent(contentView);
            notificationBuilder.setAutoCancel(false);

            notificationManager.notify(1, notificationBuilder.build());
        } else {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            Notification notification =  new NotificationCompat.Builder(context).setAutoCancel(false)
                    .setContentTitle(Titolo)
                    .setContentText(Contenuto)
                    .setContentIntent(pi)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(Titolo)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) VariabiliStaticheGlobali.getInstance()
                            .getFragmentActivityPrincipale().getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }
    } */

    public void AggiornaNotifica() {
        setListeners(contentView);
        if (notificationManager!=null && notificationBuilder != null) {
            notificationManager.notify(NOTIF_ID, notificationBuilder.build());
        }
    }

    public void CreaNotifica() {
        String id = "id_loowebplayer"; // default_channel_id
        String title = "title_lwp"; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        // NotificationCompat.Builder builder;
        if (notificationManager == null) {
            notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, NotificationManager.IMPORTANCE_LOW);
                mChannel.enableVibration(false);
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationBuilder = new NotificationCompat.Builder(context, id);

            intent = new Intent(context, PassaggioNotifica.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            contentView = new RemoteViews(VariabiliStaticheGlobali.getInstance()
                    .getFragmentActivityPrincipale().getPackageName(),
                    R.layout.barra_notifica);
            setListenersTasti(contentView);

            notificationBuilder
                    .setContentTitle(Titolo)                            // required
                    .setSmallIcon(android.R.drawable.ic_media_play)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    // .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    // .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                    // .setGroup("LOO'S WEB PLAYER")
                    // .setGroupSummary(true)
                    // .setDefaults(NotificationCompat.DEFAULT_ALL)
                    // .setPriority(NotificationManager.IMPORTANCE_LOW)
                    .setContentIntent(pendingIntent)
                    .setTicker("")
                    .setContent(contentView);
        }

        if (notificationBuilder != null) {
            Notification notification = notificationBuilder.build();
            notificationManager.notify(NOTIF_ID, notification);
        }
    }

    private void setListeners(RemoteViews view){
        if (view!=null) {
            if (Artista != null && !Artista.isEmpty()) {
                view.setTextViewText(R.id.txtArtista, Artista);
            } else {
                view.setTextViewText(R.id.txtArtista, "---");
            }

            String Traccia="";

            if (Titolo!=null && !Titolo.isEmpty()) {
                String sTitolo=Titolo;
                if (sTitolo.contains("-")) {
                    String A[] = sTitolo.split("-");
                    if (!A[0].isEmpty() && !A[0].equals("00")) {
                        Traccia = "        Traccia " + A[0];
                    }
                    sTitolo = A[1].trim();
                }
                if (sTitolo.contains(".")) {
                    sTitolo=sTitolo.substring(0,sTitolo.indexOf("."));
                }
                view.setTextViewText(R.id.txtTitoloBrano, sTitolo);
            } else {
                view.setTextViewText(R.id.txtTitoloBrano, "---");
            }

            if (Album!=null && !Album.isEmpty()) {
                String sAlbum = Album;
                if (sAlbum.contains("-")) {
                    String A[] = sAlbum.split("-");
                    if (!A[0].isEmpty() && !A[0].equals("0000")) {
                        sAlbum = A[1] + " (Anno " + A[0] + ")";
                    } else {
                        sAlbum = A[1];
                    }
                }
                sAlbum="Album: " + sAlbum + Traccia;
                view.setTextViewText(R.id.txtAlbum, sAlbum);
            } else {
                view.setTextViewText(R.id.txtAlbum, "---");
            }

            if (Immagine!=null && !Immagine.isEmpty()) {
                 try {
                     view.setImageViewBitmap(R.id.imgCopertina, BitmapFactory.decodeFile(Immagine));
                 } catch (Exception ignored) {
                     view.setImageViewResource(R.id.imgCopertina, R.drawable.ic_launcher);
                 }
             } else {
                 view.setImageViewResource(R.id.imgCopertina, R.drawable.ic_launcher);
             }

             if (inDownload) {
                 view.setViewVisibility(R.id.imgDownload, LinearLayout.VISIBLE);
             } else {
                 view.setViewVisibility(R.id.imgDownload, LinearLayout.GONE);
             }
        }
    }

    private void setListenersTasti(RemoteViews view){
        view.setTextViewText(R.id.txtTitoloBrano, "");

        Intent play=new Intent(context, PassaggioNotifica.class);
        play.putExtra("DO", "play");
        PendingIntent pplay = PendingIntent.getActivity(context, 3, play, 0);
        view.setOnClickPendingIntent(R.id.imgPlay, pplay);
        // view.setImageViewResource(R.id.imgPlay, R.drawable.play);

        Intent indietro=new Intent(context, PassaggioNotifica.class);
        indietro.putExtra("DO", "indietro");
        PendingIntent pCambia = PendingIntent.getActivity(context, 0, indietro, 0);
        view.setOnClickPendingIntent(R.id.imgIndietro, pCambia);
        // view.setImageViewResource(R.id.imgIndietro, R.drawable.indietro);

        Intent avanti=new Intent(context, PassaggioNotifica.class);
        avanti.putExtra("DO", "avanti");
        PendingIntent pAvanti = PendingIntent.getActivity(context, 1, avanti, 0);
        view.setOnClickPendingIntent(R.id.imgAvanti, pAvanti);
        // view.setImageViewResource(R.id.imgAvanti, R.drawable.avanti);

        Intent apre=new Intent(context, PassaggioNotifica.class);
        apre.putExtra("DO", "apre");
        PendingIntent pApre= PendingIntent.getActivity(context, 2, apre, 0);
        view.setOnClickPendingIntent(R.id.imgCasa, pApre);
    }

    public void RimuoviNotifica() {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        VariabiliStaticheGlobali.getInstance().getLog().ScriveLog(effettuaLogQui, new Object(){}.getClass().getEnclosingMethod().getName(), "Rimuovi notifica");
        if (notificationManager!=null) {
            try {
                notificationManager.cancel(NOTIF_ID);
                notificationManager=null;
                notificationBuilder=null;
            } catch (Exception e) {
                VariabiliStaticheGlobali.getInstance().getLog().ScriveMessaggioDiErrore(e);
            }
        }
    }
}
