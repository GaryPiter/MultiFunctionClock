package net.dell.multifunctionclock.alarm;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;

import net.dell.multifunctionclock.R;

import java.util.Calendar;

/**
 * 闹钟界面
 * Created by dell on 2016/5/13.
 */
public class AlarmView extends LinearLayout {

    private Button btnAlarm;
    private ListView lvAlarm;
    private ArrayAdapter<AlarmData> adapter;
    private static final String KEY_ALARM = "alarmlist";
    private AlarmManager alarmManager;

    public AlarmView(Context context) {
        super(context);
        init();
    }

    public AlarmView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        lvAlarm = (ListView) findViewById(R.id.lv_alarm);
        adapter = new ArrayAdapter<AlarmData>(getContext(), android.R.layout.simple_list_item_1);
        lvAlarm.setAdapter(adapter);
//        adapter.add(new AlarmData(System.currentTimeMillis()));//获取系统当前时间
        readAlarm();
        btnAlarm = (Button) findViewById(R.id.btn_add_alarm);
        btnAlarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addAlarm();
            }
        });
        //长按事件
        lvAlarm.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getContext()).setTitle("操作选项").setItems(new CharSequence[]{"删除"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        delAlarm(position);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).setNegativeButton("取消", null).show();
                return true;
            }
        });
    }

    /**
     * 删除闹钟
     */
    private void delAlarm(int position) {
        AlarmData ad=adapter.getItem(position);
        adapter.remove(ad);
        //重新保存
        saveAlarm();
        alarmManager.cancel(PendingIntent.getBroadcast(getContext(), ad.getId(), new Intent(getContext(), AlarmReceiver.class), 0));
    }

    /**
     * 添加闹钟
     */
    private void addAlarm() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //设置时间
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Calendar currentTime = Calendar.getInstance();
                //如果设置时间小于当前时间，就推后一天
                if (calendar.getTimeInMillis() <= currentTime.getTimeInMillis()) {
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
                }
                AlarmData data = new AlarmData(calendar.getTimeInMillis());
                adapter.add(data);
                /**
                 * 启动闹钟
                 * @param type 后缀_WAKEUP表示在关机的情况下，也会启动
                 * @param tirggerAtMillis 当前设置的启动时间
                 * @param intervalMillis    设置间隔多久再次启动闹钟
                 * @param pendingIntent     挂起
                 */
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, data.getTime(), 5 * 60 * 1000,
                        PendingIntent.getBroadcast(getContext(), data.getId(), new Intent(getContext(), AlarmReceiver.class), 0));
                saveAlarm();
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    /**
     * 保存闹钟
     */
    private void saveAlarm() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences(AlarmData.class.getName(), Context.MODE_PRIVATE).edit();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < adapter.getCount(); i++) {
            sb.append(adapter.getItem(i).getTime()).append(",");
        }
        if (sb.length() > 1) {
            String content = sb.toString().substring(0, sb.length() - 1);
            editor.putString(KEY_ALARM, content);
            Log.e("闹钟数据====", content);
        }else {
            editor.putString(KEY_ALARM, null);
        }
        editor.commit();
    }

    /**
     * 读取闹钟
     */
    private void readAlarm() {
        SharedPreferences shpre = getContext().getSharedPreferences(AlarmData.class.getName(), Context.MODE_PRIVATE);
        String content = shpre.getString(KEY_ALARM, null);
        if (content != null) {
            String[] timeStrings = content.split(",");
            for (String string : timeStrings) {
                adapter.add(new AlarmData(Long.parseLong(string)));
            }
        }
    }

    /**
     * 实体对象
     */
    public static class AlarmData {
        private long time;
        private String timeLable;
        private Calendar date;

        public AlarmData(long time) {
            this.time = time;
            date = Calendar.getInstance();
            date.setTimeInMillis(time);
            timeLable = String.format("%d月%d日 %d:%d",
                    date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH),
                    date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));
        }

        public long getTime() {
            return time;
        }

        public String getTimeLable() {
            return timeLable;
        }

        public int getId() {
            return (int) (getTime()/1000/60);//将当前时间作为返回码
        }

        @Override
        public String toString() {
            return getTimeLable();
        }
    }
}
