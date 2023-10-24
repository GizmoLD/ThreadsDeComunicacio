package com.example.threadsdecomunicacio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button button0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0 = findViewById(R.id.button0);



        ExecutorService executor = Executors.newSingleThreadExecutor();

        button0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getDataFromUrl("https://api.myip.com/");
         }});

        executor.execute(new Runnable() {
            @Override
            public void run() {

                // Tasques en background (xarxa)

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Tasques a la interfície gràfica (GUI)

                    }
                });
            }
        });

    }
    String error = ""; // string field
    private String getDataFromUrl(String demoIdUrl) {

        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(demoIdUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}