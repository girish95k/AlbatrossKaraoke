package com.albatross.girish.albatrosskaraoke;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


public class SplashActivity extends ActionBarActivity {

    private final int SPLASH_DISPLAY_LENGHT = 2000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(icicle);

        setContentView(R.layout.activity_splash);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

                Intent mainIntent = new Intent(SplashActivity.this,ChooseFileActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();


            }
        }, SPLASH_DISPLAY_LENGHT);
    }
}
