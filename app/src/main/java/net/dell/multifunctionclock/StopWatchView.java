package net.dell.multifunctionclock;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 秒表
 * Created by dell on 2016/5/17.
 */
public class StopWatchView extends LinearLayout implements View.OnClickListener {

    private Button btnStart, btnReset, btnRmun;
    private TextView tvHour, tvMin, tvSecond, tvMill;
    private ListView mListView;
    private ArrayAdapter<String> adapter;
    private boolean isFlag = false;
    private int tenMsec = 0;//记录毫秒
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private TimerTask showTimerTask = null;
    public static final int MSG_WATCH_SHOW_TIMER = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WATCH_SHOW_TIMER:
                    tvHour.setText(tenMsec / 100 / 60 / 60 + "");//时-分
                    tvMin.setText((tenMsec / 100 / 60) % 60 + "");//分
                    tvSecond.setText((tenMsec / 100) % 60 + "");
                    tvMill.setText(tenMsec % 100 + "");
                    break;
                default:
                    break;
            }
        }
    };

    public StopWatchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tvHour = (TextView) findViewById(R.id.tv_hour);
        tvMin = (TextView) findViewById(R.id.tv_min);
        tvSecond = (TextView) findViewById(R.id.tv_second);
        tvMill = (TextView) findViewById(R.id.tv_mill);
        btnStart = (Button) findViewById(R.id.btn_start_watch);
        btnReset = (Button) findViewById(R.id.btn_reset_watch);
        btnRmun = (Button) findViewById(R.id.btn_mun_watch);
        mListView = (ListView) findViewById(R.id.lv_watch);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        mListView.setAdapter(adapter);

        showTimerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_WATCH_SHOW_TIMER);
            }
        };
        timer.schedule(showTimerTask, 10, 10);

        btnReset.setEnabled(false);
        btnRmun.setEnabled(false);
        btnStart.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnRmun.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_watch:
                if (!isFlag) {
                    btnStart.setText("暂停");
                    isFlag = true;
                    startTimer();//开始计时
                    btnReset.setEnabled(true);
                    btnRmun.setEnabled(true);
                } else {
                    btnStart.setText("开始");
                    isFlag = false;
                    stopTimer();
                }
                break;
            case R.id.btn_reset_watch:
                stopTimer();
                tenMsec = 0;
                adapter.clear();//清空列表
                btnStart.setText("开始");
                isFlag = false;
                btnReset.setEnabled(false);
                btnRmun.setEnabled(false);
                break;
            case R.id.btn_mun_watch:
                adapter.insert(String.format("%d:%d:%d.%d", tenMsec / 100 / 60 / 60,
                        (tenMsec / 100 / 60) % 60, (tenMsec / 100) % 60, tenMsec % 100), 0);
                break;
            default:
                break;
        }
    }

    /**
     * 启动秒表
     */
    private void startTimer() {
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    tenMsec++;
                }
            };
            timer.schedule(timerTask, 10, 10);
        }
    }

    /**
     * 停止秒表
     */
    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    /**
     * 销毁
     */
    public void onWatchDestroy() {
        timer.cancel();
    }
}
