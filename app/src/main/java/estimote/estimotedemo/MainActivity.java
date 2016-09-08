package estimote.estimotedemo;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private Region region;
    MediaPlayer mySound;
    TextView distance;
    private static final String LIGHTBULB_FILE = "/sys/class/gpio/gpio915/value";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        distance = (TextView)findViewById(R.id.distance);
        mySound = MediaPlayer.create(this, R.raw.imperial_march);
        Log.d("Rohit mesage", "oncreate");
        distance.setText("hello rohit");
        beaconManager = new BeaconManager(this);
        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),64286, 51833);

        distance.setText("Rohit");

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });

        beaconManager.setForegroundScanPeriod(250, 0);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list)
            {
                Log.d("Rohit mesage", "onBeaconsDiscovered");
                if (!list.isEmpty())
                {

                    Beacon nearestBeacon = list.get(0);
                    Log.d("Rohit Message", Integer.toString(Math.abs(nearestBeacon.getRssi())));
                    int dist = Math.abs(nearestBeacon.getRssi());
                    distance.setText("");
                    distance.setText(Integer.toString(dist));
                    if (dist <= 65)
                    {
                        /*
                        //mySound.start();
                        distance.setTextColor(Color.GREEN);
                        if(mySound.isPlaying() != true)
                        {
                            distance.setTextColor(Color.GREEN);
                            mySound.seekTo(0);
                            mySound.start();
                        }
                        Log.d("Rohit Message", "in range");
                        if (dist <= 50)
                        {
                            distance.setTextColor(Color.RED);
                            mySound.pause();
                        }
                        */

                        /*
                        try{
                            //Process p = Runtime.getRuntime().exec("cat sys/class/gpio/gpio915/value\n");
                            Process p = Runtime.getRuntime().exec("echo 1 > /sys/class/gpio/gpio915/value\n");
                            p.waitFor();

                            //outputStream.writeBytes("echo 1 > /sys/class/gpio/gpio915/value\n");


                            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

                            String line = "";
                            while ((line = reader.readLine())!= null) {
                                Log.d("Rohit -------- ", line);
                            }

                        }catch(IOException e){

                        }catch(InterruptedException e){

                        }
                        */
                        try {
                            Log.d("Rohit ...","trying");
                            Process process = Runtime.getRuntime().exec("su");
                            DataOutputStream os = new DataOutputStream(process.getOutputStream());
                            os.writeBytes("echo 915 > /sys/class/gpio/export\n");
                            os.writeBytes("echo 1 > /sys/class/gpio/gpio915/value\n");

                        } catch (IOException e) {

                        }

                    }
                    else
                    {
                        /*
                        distance.setTextColor(Color.RED);
                        if(mySound.isPlaying() == true)
                        {
                            mySound.pause();
                        }
                        */
                        String cmd = "echo 0 > /sys/class/gpio/gpio915/value";
                        Runtime run = Runtime.getRuntime();
                        try {
                            Process pr = run.exec(cmd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }
}
