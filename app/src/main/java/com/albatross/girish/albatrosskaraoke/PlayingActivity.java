package com.albatross.girish.albatrosskaraoke;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class PlayingActivity extends Activity implements OnCompletionListener,
        SeekBar.OnSeekBarChangeListener {

    protected PowerManager.WakeLock mWakeLock;
    String root = Environment.getExternalStorageDirectory().toString();

    public static final String EXTRA_MP3_PATH = "extra_mp3_path";
    public static final String EXTRA_SONG_TITLE = "extra_song_title";
    public static final String EXTRA_INI_PATH = "extra_ini_path";

    private ArrayList<String> mLines;
    private ArrayList<Integer> mWordCount;
    private ArrayList<Integer> mTimings;
    private int mLinesNumber;

    private ArrayList<TextView> mTextViewList;
    private static final String LINE_NUMBER_TAG = "line_number_tag";
    private static final String START_WORD_NUMBER_TAG = "start_word_number_tag";

    private RelativeLayout btnPlay2;
    private ImageButton btnPlay;
    private ImageButton btnForward;
    private ImageButton btnBackward;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;

    // Media Player
    private  MediaPlayer mp;
    private boolean mMediaPlayerReleased = false;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();;
    private SongsManager songManager;
    private Utilities utils;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        // All player buttons
        btnPlay = (ImageButton) findViewById(R.id.playPauseButton);
        btnPlay2 = (RelativeLayout)findViewById(R.id.playPauseButtonBackground);
        btnForward = (ImageButton) findViewById(R.id.nextButton);
        btnBackward = (ImageButton) findViewById(R.id.previousButton);
        btnNext = (ImageButton) findViewById(R.id.nextButton);
        btnPrevious = (ImageButton) findViewById(R.id.previousButton);
        btnPlaylist = (ImageButton) findViewById(R.id.previousButton);
        //btnRepeat = (ImageButton) findViewById(R.id.repeatButton);
        //btnShuffle = (ImageButton) findViewById(R.id.shuffleButton);
        songProgressBar = (SeekBar) findViewById(R.id.nowPlayingSeekBar);
        //songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.et);
        songTotalDurationLabel = (TextView) findViewById(R.id.tt);

        // lyrics
        //File iniFile = new File(getIntent().getStringExtra(EXTRA_INI_PATH));
        //File iniFile = new File("/storage/emulated/0/AlbatrossKaraoke/Song.ini"); --------------
        File iniFile = new File(root + "/AlbatrossKaraoke/Song.ini");
        mLines = IniParser.getLines(iniFile);
        mWordCount = IniParser.getCumulativeWordCounts(mLines);
        mTimings = IniParser.getTiming(iniFile);

        mLinesNumber = IniParser.getLinesNumber(iniFile);

        System.out.println("mTimings" + mTimings);
        System.out.println("size: " + mTimings.size());
        initLyricsTextViews();

        // Mediaplayer
        mp = new MediaPlayer();
        songManager = new SongsManager();
        utils = new Utilities();

        // Listeners
        songProgressBar.setOnSeekBarChangeListener(this); // Important
        mp.setOnCompletionListener(this); // Important

        // Getting all songs list
        // songsList = songManager.getPlayList();
        songsList = new ArrayList<>();
        HashMap<String, String> song = new HashMap<>();
        // TODO: Uncomment for production
        /* song.put(EXTRA_MP3_PATH, getIntent().getStringExtra(EXTRA_MP3_PATH));
        song.put(EXTRA_SONG_TITLE, getIntent().getStringExtra(EXTRA_SONG_TITLE)); */
        String fileName = getIntent().getStringExtra("fileName");
        //song.put(EXTRA_MP3_PATH, "/storage/emulated/0/AlbatrossKaraoke/" + fileName); ----------
        song.put(EXTRA_MP3_PATH, root + "/AlbatrossKaraoke/" + fileName);
        //song.put(EXTRA_MP3_PATH, "/storage/emulated/0/a.mp3");
        song.put(EXTRA_SONG_TITLE, fileName);

        songsList.add(song);

        // By default play first song
        if (songsList.size() != 0)
            playSong(0);

        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.play_light);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.pause_light);
                    }
                }

            }
        });
        btnPlay2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        // Changing button image to play button
                        btnPlay.setImageResource(R.drawable.play_light);
                    }
                }else{
                    // Resume song
                    if(mp!=null){
                        mp.start();
                        // Changing button image to pause button
                        btnPlay.setImageResource(R.drawable.pause_light);
                    }
                }

            }
        });

        /**
         * Forward button click event
         * Forwards song specified seconds
         * */

/*         btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mp.getDuration()){
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });
*/
        /**
         * Backward button click event
         * Backward song to specified seconds
         * */

/*         btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });
*/
        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */

/*         btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if(currentSongIndex < (songsList.size() - 1)){
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                }else{
                    // play first song
                    playSong(0);
                    currentSongIndex = 0;
                }

            }
        });
*/
        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */

/*         btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(currentSongIndex > 0){
                    playSong(currentSongIndex - 1);
                    currentSongIndex = currentSongIndex - 1;
                }else{
                    // play last song
                    playSong(songsList.size() - 1);
                    currentSongIndex = songsList.size() - 1;
                }

            }
        });
*/
        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */

/*         btnRepeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    //btnRepeat.setImageResource(R.drawable.btn_repeat);
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isShuffle = false;
                    //btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
                    //btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }
            }
        });
*/
        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
         * */

/*         btnShuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShuffle){
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    ///btnShuffle.setImageResource(R.drawable.btn_shuffle);
                }else{
                    // make repeat to true
                    isShuffle= true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                   // btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
                    //btnRepeat.setImageResource(R.drawable.btn_repeat);
                }
            }
        });
*/
        /**
         * Button Click event for Play list click event
         * Launches list activity which displays list of songs
         * */

/*         btnPlaylist.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Intent i = new Intent(getApplicationContext(), PlayListActivity.class);
                //startActivityForResult(i, 100);
            }
        });
*/
    }

    /**
     * creates textviews and populates the list, besides adding them to the layout
     */
    private void initLyricsTextViews() {
        final int tvNumber = 15;
        LinearLayout tvLayout  = (LinearLayout) findViewById(R.id.lyrics_ll);
        mTextViewList = new ArrayList<>(tvNumber);
        for (int i = 0; i < tvNumber; i++) {
            TextView tv = new TextView(this);
            tv.setGravity(Gravity.CENTER);
            tvLayout.addView(tv);
            mTextViewList.add(tv);
        }
    }

    /**
     * Receiving song index from playlist view
     * and play the song
     * */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);
        }

    }

    /**
     * Function to play a song
     * @param songIndex - index of song
     * */
    public void  playSong(int songIndex){
        // Play song
        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get(EXTRA_MP3_PATH));
            mp.prepare();
            mp.start();
            // Displaying Song title
            String songTitle = songsList.get(songIndex).get(EXTRA_SONG_TITLE);
            //songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            btnPlay.setImageResource(R.drawable.pause_light);

            // set Progress bar values
            songProgressBar.setProgress(0);
            songProgressBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawTextViews(int time) {
        int wordNum;
        for (wordNum= 0; mTimings.get(wordNum) <= time; wordNum++) {
                if(wordNum == mTimings.size()-1)
                    break;
        }

        final int lineIndex = getLineIndex(wordNum);
        int tempLineIndex = lineIndex;
        for(Iterator<TextView> it = mTextViewList.iterator(); it.hasNext(); ) {
            TextView lineTv = it.next();
            if (tempLineIndex < mLines.size()) {
                lineTv.setText(mLines.get(tempLineIndex++));
                lineTv.setTextColor(Color.WHITE);
            } else {
                lineTv.setText("");
            }
        }

        if (wordNum > 0) {
            int wordInTv = wordNum <= mWordCount.get(0) ? wordNum : wordNum -mWordCount.get(lineIndex-1);

            highlightWord(wordInTv, mTextViewList.get(0));
        }
    }

    private void highlightWord (int wordNum, TextView tv) {
        if (wordNum == 0) {
            return;
        }
        String prefix = "<font color = \"red\">";
        String postFix = "</font> ";

        String parts[] = tv.getText().toString().split(" ");

        String text = prefix + parts[0];
        for (int i = 1; i<parts.length; i++) {
            text += (i == wordNum ? postFix : " ") + parts[i];
        }
        Log.e("PHILIP", text + "" );
        tv.setText(Html.fromHtml(text));
    }

    /**
     * returns the index of line containing wordNum'th word
     * @param wordNum
     * @return
     */
    private int getLineIndex(int wordNum) {
        int i;
        for (i = 0; mWordCount.get(i) < wordNum; i++); // fail-fast
        return i;
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mMediaPlayerReleased) {
                return;
            }
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

            drawTextViews((int) currentDuration/10);

            // Displaying Total Duration time
            songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mp.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     * */
    @Override
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
            playSong(currentSongIndex);
        } else{
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size() - 1)){
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            }else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onDestroy(){
        this.mWakeLock.release();
        super.onDestroy();
        mMediaPlayerReleased = true;
        mp.release();
    }

}