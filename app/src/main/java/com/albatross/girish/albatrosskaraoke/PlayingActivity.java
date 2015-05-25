package com.albatross.girish.albatrosskaraoke;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


public class PlayingActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        Bundle extras = getIntent().getExtras();
        String fileName = null;

        if (extras != null) {
            fileName = extras.getString("fileName");
        }
        String data;
        try {
            FileInputStream fis = new FileInputStream(new File("/storage/emulated/0/AlbatrossKaraoke/Song.ini"));
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
             data = sb.toString();
            Log.e("data", data);
            Log.e("mp3", fileName);
        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {

        } catch (IOException e) {

        }

        // Play song
        try {
            final MediaPlayer mp = new MediaPlayer();
            mp.reset();
            mp.setDataSource("/storage/emulated/0/AlbatrossKaraoke/" + fileName);
            mp.prepare();
            mp.start();


            //btnPlay.setImageResource(R.drawable.btn_pause);

            final ImageButton button2 = (ImageButton)findViewById(R.id.playPauseButton);

            RelativeLayout button = (RelativeLayout)findViewById(R.id.playPauseButtonBackground);
            button.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                if(mp.isPlaying())
                {
                    mp.pause();
                    button2.setImageResource(R.drawable.play_light);
                }
                    else
                {
                    mp.start();
                    button2.setImageResource(R.drawable.pause_light);
                }
                }
            });

            button2.setOnClickListener( new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(mp.isPlaying())
                    {
                        mp.pause();
                        button2.setImageResource(R.drawable.play_light);
                    }
                    else
                    {
                        mp.start();
                        button2.setImageResource(R.drawable.pause_light);
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_playing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
