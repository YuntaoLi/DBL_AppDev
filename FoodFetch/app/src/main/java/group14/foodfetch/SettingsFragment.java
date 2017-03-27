package group14.foodfetch;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;;
import android.app.PendingIntent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by s134673 on 16-3-2017.
 */

public class SettingsFragment extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.settings_layout, container, false);
        ButterKnife.inject(this, myView);
        return myView;
    }


    @OnClick(R.id.button)
    public void sendNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getContext());
        builder.setSmallIcon(R.drawable.logo);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.enzolucas.nl/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getContext(), 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
        builder.setContentTitle("New announcement");
        builder.setContentText("The foodbank has created a new announcement");
        builder.setSubText("Tap to view the announcement.");

        NotificationManager notificationManager = (NotificationManager)this.getContext().getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());
    }

    @OnClick(R.id.button2)
    public void cancelNotification() {

        String ns = NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager)this.getContext().getApplicationContext().getSystemService(ns);
        nMgr.cancel(1);
    }
}


