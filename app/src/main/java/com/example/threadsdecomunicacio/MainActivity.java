package com.example.threadsdecomunicacio;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

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
    private TextView textView0;
    private ImageView imageView0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0 = findViewById(R.id.button0);
        textView0 = findViewById(R.id.textView0);
        imageView0 = findViewById(R.id.imageView0);
        button0.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final String jsonData = getDataFromUrl("https://randomfox.ca/floof/");
                        try{
                            final String data = getDataFromUrl("https://api.myip.com/");
                            JSONObject json = new JSONObject(jsonData);
                            final String imageUrl = json.optString("image", "");
                            final Bitmap bitmap = downloadImage(imageUrl);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    textView0.setText(data);
                                    imageView0.setImageBitmap(bitmap);
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
         }});
    }
    private Bitmap downloadImage(String imageUrl) {
        try {
            InputStream in = new java.net.URL(imageUrl).openStream();
            return BitmapFactory.decodeStream(in);

        } catch (Exception e) {
            Log.e("Error", "Error al descargar la imagen: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    String error = "";
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

