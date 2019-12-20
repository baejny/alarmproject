package com.example.alarmproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmMethod{
    Context context;
    SharedPreferences sharedPreferences;

    Intent alarmIntent;
    AlarmManager alarmManager;

    int alarmCount;
    int alarmPointer;

    private  AlarmListener mListener;
    public void setListener(AlarmListener listener){
        mListener = listener;
        mListener.onList(make_list());
    }

    AlarmMethod(Context context, SharedPreferences sharedPreferences){
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.alarmIntent = new Intent(context, AlarmReceiver.class);
        this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Log.d("test", "AlarmMethod Constructed");
        Log.d("test", "AlarmCount = " + String.valueOf(getAlarmCount()));
    }

    //알람 등록
    void alarm_insert(int hour, int minute){
        alarmCount = getAlarmCount();
        if (alarmCount < 5) {
            for (int i = 0; i < 5; i++) {
                if (sharedPreferences.getLong(String.valueOf(i), 0) == 0) {
                    alarmPointer = i;
                    break;
                }
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
                Toast.makeText(context,"다음날 같은 시간으로 설정합니다!", Toast.LENGTH_SHORT).show();
            }

            Date currentDateTime = calendar.getTime();
            String date_text = new SimpleDateFormat("yyyy년MM월dd일 hh시mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context, date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(String.valueOf(alarmPointer), (long)calendar.getTimeInMillis());
            editor.apply();

            Long millis = calendar.getTimeInMillis();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmPointer, alarmIntent, 0);
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millis, AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
                }
            }
        }else{
            Toast.makeText(context, "알람이 가득 찼습니다.", Toast.LENGTH_SHORT).show();
        }

        if(mListener != null){
            mListener.onList(make_list());
        }
    }

    //알람 삭제
    void alarm_delete(int SelectedItemPosition){
        alarmCount = getAlarmCount();
        if(alarmCount > 0){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, SelectedItemPosition, alarmIntent, 0);
            if (PendingIntent.getBroadcast(context, SelectedItemPosition, alarmIntent, 0) != null && alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(String.valueOf(SelectedItemPosition), 0);
                editor.apply();
            }
        }else{
            Toast.makeText(context, "저장된 알람이 없습니다.", Toast.LENGTH_SHORT).show();
        }

        if(mListener != null){
            mListener.onList(make_list());
        }
    }

    //디바이스 부팅시 알람 초기화
    void alarm_boot(){
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (sharedPreferences.getLong(String.valueOf(i), 0) != 0) {
                count++;
            }
        }
        Toast.makeText(context, "[재부팅]" + String.valueOf(count) +"개의 알람이 있습니다.", Toast.LENGTH_SHORT).show();
    }

    String make_list(){
        String msg = "";
        for (int i = 0;i < 5; i++) {
            Long timeMillis = sharedPreferences.getLong(String.valueOf(i), 0);
            if (timeMillis != 0) {
                String pattern = "MM월dd일 HH시mm분";
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                String date = (String)formatter.format(new Timestamp(timeMillis));
                msg += (i+1 + " : " + date);
            }
            if(i!=4){
                msg += ("\n");
            }
        }
        return msg;
    }

    int getAlarmCount(){
        int result = 0;
        for(int i=0; i<5; i++){
            if(sharedPreferences.getLong(String.valueOf(i),0) != 0){
                result++;
            }
        }
        return result;
    }

}
