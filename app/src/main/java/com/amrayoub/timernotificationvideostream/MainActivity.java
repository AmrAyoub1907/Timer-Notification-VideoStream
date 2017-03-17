package com.amrayoub.timernotificationvideostream;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class MainActivity extends AppCompatActivity {

    private TextView tvDay, tvHour, tvMinute, tvSecond;
    private TextView Day, Hour, Minute, Second,tv;
    public EditText Da, Ho, Min, Sec;
    private LinearLayout linearLayout1, linearLayout2,linearLayout3;
    private Handler handler;
    private Runnable runnable;
    public AlarmManager alarms;
    public Button startbut,openbut;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Day = (TextView) findViewById(R.id.txt_TimerDay);
        Hour = (TextView) findViewById(R.id.txt_TimerHour);
        Minute = (TextView) findViewById(R.id.txt_TimerMinute);
        Second = (TextView) findViewById(R.id.txt_TimerSecond);
        alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

    }
    @SuppressLint("SimpleDateFormat")
    private void initUI() {
        startbut= (Button) findViewById(R.id.starttime_button);
        linearLayout1 = (LinearLayout) findViewById(R.id.ll1);
        linearLayout2 = (LinearLayout) findViewById(R.id.ll2);
        linearLayout3 = (LinearLayout) findViewById(R.id.ll3);
        tvDay = (TextView) findViewById(R.id.txtTimerDay);
        tvHour = (TextView) findViewById(R.id.txtTimerHour);
        tvMinute = (TextView) findViewById(R.id.txtTimerMinute);
        tvSecond = (TextView) findViewById(R.id.txtTimerSecond);
        openbut = (Button) findViewById(R.id.openvideo_button);
    }
    public void countDownStart(final int timer){
        Intent intent = new Intent(this,Receiver.class);
        final PendingIntent operation = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);
        Receiver receiver = new Receiver();
        IntentFilter filter = new IntentFilter();
        registerReceiver(receiver, filter);
        final long diff1 = timer+System.currentTimeMillis();
        if(timer>0) {
            alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timer, operation);
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    long diff =  diff1 - System.currentTimeMillis();
                    if (diff1 > System.currentTimeMillis()) {
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        tvDay.setText("" + String.format("%02d", days));
                        tvHour.setText("" + String.format("%02d", hours));
                        tvMinute.setText("" + String.format("%02d", minutes));
                        tvSecond.setText("" + String.format("%02d", seconds));
                    } else {
                        openbut.setVisibility(View.VISIBLE);
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }
    private void open_Video() {
        final VideoView videoview = (VideoView)findViewById(R.id.VideoView);//media controller
        linearLayout2.setVisibility(View.GONE);
        linearLayout3.setVisibility(View.GONE);
        startbut.setVisibility(View.GONE);
        linearLayout1.setVisibility(View.VISIBLE);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Wait Loading ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        // Show progressbar
        pDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            String videolink = null;
            GetStringFromUrl k = new GetStringFromUrl();
            videolink = k.execute("https://musicmadness.000webhostapp.com/videolink.txt").get();
            Uri video = Uri.parse(videolink);// get link from WebHost
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });
    }

    public void starttimer(View view) {
        /* get the time from the user*/
        initUI();
        Da = (EditText) findViewById(R.id.editTimerDay);
        Ho = (EditText) findViewById(R.id.editTimerHour);
        Min = (EditText) findViewById(R.id.editTimerMinute);
        Sec = (EditText) findViewById(R.id.editTimerSecond);
        int second = Integer.parseInt(Sec.getText().toString());
        int hour = Integer.parseInt(Ho.getText().toString());
        int minute = Integer.parseInt(Min.getText().toString());
        int da = Integer.parseInt(Da.getText().toString());
        int timer_in_sec=(second*1000)+(minute*60*1000)+(hour*60*60*1000)+(da*60*60*24*1000);
        countDownStart(timer_in_sec);
    }

    public void openvideo(View view) {
        open_Video();
    }
    public class GetStringFromUrl extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            // @BadSkillz codes with same changes
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(params[0]);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity buf = new BufferedHttpEntity(entity);
                InputStream is = buf.getContent();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));
                StringBuilder total = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    total.append(line);
                }
                String result = total.toString();
                is.close();
                return result;
            } catch (Exception e) {
                Log.e("Get Url", "Error in loading: " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}

