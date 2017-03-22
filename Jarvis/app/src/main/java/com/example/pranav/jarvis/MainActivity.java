package com.example.pranav.jarvis;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.pranav.jarvis.R;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
private TextView speech_text;
    public String command="";
    private Socket client;
    private PrintWriter printwriter;
    protected static final int RESULT_SPEECH = 1;
    private ImageButton jarvis_button;
    private TextView error_box ;
    private boolean done_transfer=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speech_text=(TextView)findViewById(R.id.speech_textview);
        jarvis_button=(ImageButton)findViewById(R.id.jarvis_button);
        error_box=(TextView)findViewById(R.id.Error_view);
        jarvis_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-us");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    speech_text.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }


            }
        });


    }

    Thread T1=new Thread(new Runnable() {
        @Override

        public void run() {

                try {
                    if(done_transfer){
                        client = new Socket("192.168.0.102", 5050);
                        printwriter = new PrintWriter(client.getOutputStream());
                        printwriter.write(command);
                        printwriter.flush();
                        printwriter.close();
                        BufferedReader in = new  BufferedReader(new InputStreamReader(client.getInputStream()));
                        String re= in.readLine();
                        error_box.setText(re);
                        in.close();


                        client.close();

                        done_transfer=false;
                        return;
                    }


                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
    });





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    StringBuffer str=new StringBuffer("");

                    str.append(text.get(0));

                    command=str.toString();
                    error_box.setText(" ");
                    speech_text.setText(text.get(0));
                    done_transfer=true;

                    T1.start();

                     //SmsManager sms=SmsManager.getDefault();
                    //sms.sendTextMessage("09942815643", null, command, null, null);






                }
                break;
            }

        }
    }








}
