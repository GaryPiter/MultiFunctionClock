package net.dell.multifunctionclock;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dell on 2016/5/16.
 */
public class TimerView extends LinearLayout implements View.OnClickListener {

    private Button btnStart, btnReset;
    private EditText etHour, etMintue, etSecond;
    private boolean isFlag = false;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private int allTimerContent = 0;//记录总时间
    public static final int MSG_TIMER_UP = 1;
    public static final int MSG_TIMER_TICK = 2;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIMER_TICK:
                    int h = allTimerContent / 60 / 60;
                    int m=(allTimerContent/60)%60;
                    int s = allTimerContent % 60;
                    etHour.setText(""+h);
                    etMintue.setText(""+m);
                    etSecond.setText(""+s);
                    break;
                case MSG_TIMER_UP:
                    new AlertDialog.Builder(getContext()).setTitle("Time is up")
                            .setMessage("Time is Up").setNegativeButton("Cancle", null).show();
                    btnStart.setText("开始");
                    isFlag = false;
                    break;
                default:
                    break;
            }
        }
    };

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        etHour = (EditText) findViewById(R.id.et_hour);
        etHour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    int value = Integer.parseInt(s.toString());
                    if (value > 59) {
                        etHour.setText("59");
                    } else if (value < 0) {
                        etHour.setText("00");
                    }
                }
                checkEnabledStart();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etMintue = (EditText) findViewById(R.id.et_mintue);
        etMintue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    int value = Integer.parseInt(s.toString());
                    if (value > 59) {
                        etMintue.setText("59");
                    } else if (value < 0) {
                        etMintue.setText("00");
                    }
                }
                checkEnabledStart();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etSecond = (EditText) findViewById(R.id.et_second);
        etSecond.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    int value = Integer.parseInt(s.toString());
                    if (value > 59) {
                        etSecond.setText("59");
                    } else if (value < 0) {
                        etSecond.setText("00");
                    }
                }
                checkEnabledStart();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        btnStart = (Button) findViewById(R.id.btn_start);
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnStart.setEnabled(false);
        btnReset.setEnabled(false);
        btnStart.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }


    /**
     * 设置按钮可点击
     */
    String hour, mintue, second;

    private void checkEnabledStart() {
        hour = etHour.getText().toString();
        mintue = etMintue.getText().toString();
        second = etSecond.getText().toString();
        Log.w("时间is?", "时"+hour+"分"+mintue+"秒"+second);
        btnStart.setEnabled(!TextUtils.isEmpty(hour) && Integer.parseInt(hour) > 0 ||
                !TextUtils.isEmpty(mintue) && Integer.parseInt(mintue) > 0 ||
                !TextUtils.isEmpty(second) && Integer.parseInt(second) > 0);
        btnReset.setEnabled(!TextUtils.isEmpty(hour) && Integer.parseInt(hour) > 0 ||
                !TextUtils.isEmpty(mintue) && Integer.parseInt(mintue) > 0 ||
                !TextUtils.isEmpty(second) && Integer.parseInt(second) > 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                if (!isFlag) {
                    btnStart.setText("暂停");
                    isFlag = true;
                    startTimer();//开始计时
                } else {
                    btnStart.setText("开始");
                    isFlag = false;
                    stopTimer(); //暂停计时
                }
                break;
            case R.id.btn_reset:
                stopTimer();
                etHour.setText("00");
                etMintue.setText("00");
                etSecond.setText("00");
                //重置按钮显示
                btnStart.setText("开始");
                isFlag = false;
                break;
            default:
                break;
        }
    }

    /**
     * 启动计时器
     */
    private void startTimer() {
        if (timerTask == null) {
            //计算总时间，/秒
            Log.w("时间", "时"+hour+"分"+mintue+"秒"+second);
            allTimerContent = Integer.parseInt(hour) * 60 * 60 + Integer.parseInt(mintue) * 60 + Integer.parseInt(second);
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    allTimerContent--;
                    handler.sendEmptyMessage(MSG_TIMER_TICK);//动态显示
                    if (allTimerContent <= 0) {
                        handler.sendEmptyMessage(MSG_TIMER_UP);
                        stopTimer();
                    }
                }
            };
            timer.schedule(timerTask, 1000, 1000);
        }
    }

    /**
     * 停止计时
     */
    private void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
