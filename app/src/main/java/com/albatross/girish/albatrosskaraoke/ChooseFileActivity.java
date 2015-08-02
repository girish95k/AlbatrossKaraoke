package com.albatross.girish.albatrosskaraoke;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.util.ArrayList;


public class ChooseFileActivity extends ActionBarActivity {

    TextView tv;
    String root = Environment.getExternalStorageDirectory().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);

        setTitle("Please Select a File");

                Intent i = new Intent(ChooseFileActivity.this, FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                // Configure initial directory like so
                //i.putExtra(FilePickerActivity.EXTRA_START_PATH, "/storage/emulated/0/"); --------
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, root);

                startActivityForResult(i, 1);


        Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // This always works
                Intent i = new Intent(ChooseFileActivity.this, FilePickerActivity.class);
                // This works if you defined the intent filter
                // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

                // Set these depending on your use case. These are the defaults.
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                // Configure initial directory like so
                //i.putExtra(FilePickerActivity.EXTRA_START_PATH, "/storage/emulated/0/"); -----
                i.putExtra(FilePickerActivity.EXTRA_START_PATH, root);

                startActivityForResult(i, 1);
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            String a[] = {uri.getEncodedPath().replace("%20", " ")};
                            try {
                                String fileName = KFNDumper.call(a);
                                Thread.sleep(150);

                                Intent in = new Intent(ChooseFileActivity.this, PlayingActivity.class);
                                Log.e("fileName", fileName);
                                in.putExtra("fileName", fileName);
                                startActivity(in);
                                finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                                tv = (TextView)findViewById(R.id.textView);
                                tv.setText("Please choose a valid file");
                                Toast.makeText(getApplicationContext(), "Invalid file.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path: paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                            String a[] = {uri.getEncodedPath().replace("%20", " ")};
                            try {
                                String fileName = KFNDumper.call(a);
                                Thread.sleep(150);

                                Intent in = new Intent(ChooseFileActivity.this, PlayingActivity.class);
                                Log.e("fileName", fileName);
                                in.putExtra("fileName", fileName);
                                startActivity(in);
                                finish();

                            } catch (Exception e) {
                                e.printStackTrace();
                                tv = (TextView)findViewById(R.id.textView);
                                tv.setText("Please choose a valid file");
                                Toast.makeText(getApplicationContext(), "Invalid file.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI
                //tv = (TextView)findViewById(R.id.tv);
                String  textString = uri.getEncodedPath();
                Spannable spanText = Spannable.Factory.getInstance().newSpannable(textString);
                spanText.setSpan(new BackgroundColorSpan(0xFFFFFF00), 14, 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //tv.setText(spanText);
                //tv.setText(uri.getEncodedPath());
                String a[] = {uri.getEncodedPath().replace("%20", " ")};
                Log.e("encoded path", a[0]);
                try {
                    String fileName = KFNDumper.call(a);
                    Thread.sleep(150);

                    Intent in = new Intent(ChooseFileActivity.this, PlayingActivity.class);
                    Log.e("fileName", fileName);
                    in.putExtra("fileName", fileName);
                    startActivity(in);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    tv = (TextView)findViewById(R.id.textView);
                    tv.setText("Please choose a valid file");
                    Toast.makeText(getApplicationContext(), "Invalid file.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
