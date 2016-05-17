package net.dell.multifunctionclock.alarm;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

import net.dell.multifunctionclock.R;

/**
 * 播放闹钟界面
 * Created by dell on 2016/5/16.
 */
public class PlayAlarmActivity extends Activity {

    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_alarm);

        player = MediaPlayer.create(this, R.raw.music);
        player.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }
}
