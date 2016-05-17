package net.dell.multifunctionclock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends AppCompatActivity {

    private TabHost tabHost;
    private StopWatchView stopWatchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        stopWatchView = (StopWatchView) findViewById(R.id.tab_stopwatch);
    }

    private void initView() {
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();//初始化tabHost
        tabHost.addTab(tabHost.newTabSpec("tab_time").setIndicator("时钟").setContent(R.id.tab_time));
        tabHost.addTab(tabHost.newTabSpec("tab_alarm").setIndicator("闹钟").setContent(R.id.tab_alarm));
        tabHost.addTab(tabHost.newTabSpec("tab_timer").setIndicator("计时器").setContent(R.id.tab_timer));
        tabHost.addTab(tabHost.newTabSpec("tab_stopwatch").setIndicator("秒表").setContent(R.id.tab_stopwatch));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopWatchView.onWatchDestroy();
    }
}
