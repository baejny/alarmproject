package com.example.alarmproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity{
    TimePicker picker;
    FButton btn_insert;
    FButton btn_move_delete;
    Spinner Spinner;

    SharedPreferences sharedPreferences;
    AlarmMethod am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        am = new AlarmMethod(this, sharedPreferences);

        picker=(TimePicker)findViewById(R.id.timePicker);
        picker.setIs24HourView(true);
        Spinner = (Spinner) findViewById(R.id.spinner_media);
        btn_insert = (FButton) findViewById(R.id.button_insert);
        btn_insert.setButtonColor(getColor(R.color.fbutton_color_peter_river));
        btn_move_delete = (FButton) findViewById(R.id.button_showList);
        btn_move_delete.setButtonColor(getColor(R.color.fbutton_color_peter_river));

        // 스피너 초기화
        makeSpinnerMediaList();

        // 등록
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                am.alarm_insert(picker.getCurrentHour(), picker.getCurrentMinute(), Spinner.getSelectedItem().toString());
            }
        });
        //삭제 액티비티로 이동
        btn_move_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DeleteActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
    void makeSpinnerMediaList(){
        String[] facilityList = {"은하","시완"};
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                facilityList);
        Spinner.setAdapter(adapter);
        Spinner.setSelection(0);
    }
}