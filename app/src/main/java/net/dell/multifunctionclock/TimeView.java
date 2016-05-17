package net.dell.multifunctionclock;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * 时钟界面
 * Created by dell on 2016/5/13.
 */
public class TimeView extends LinearLayout {

    private TextView textView;

    public TimeView(Context context) {
        super(context);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textView = (TextView) findViewById(R.id.tvTime);
//        textView.setText("HELLO");
        timeHandler.sendEmptyMessage(0);
    }

    /**
     * 处于可见刷新，不可见移除所以的消息
     *
     * @param changedView
     * @param visibility
     */
    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            timeHandler.sendEmptyMessage(0);
        } else {
            timeHandler.removeMessages(0);
        }
    }

    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshTime();
            //处于可见状态刷新
            if (getVisibility() == View.VISIBLE) {
                timeHandler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    /**
     * 刷新时间
     */
    private void refreshTime() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY); //时
        int minute = c.get(Calendar.MINUTE);  //分
        int millis = c.get(Calendar.SECOND);  //秒
        textView.setText(String.format("%d:%d:%d",hour,minute,millis));
    }
}
