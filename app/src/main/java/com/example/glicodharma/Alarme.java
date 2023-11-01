package com.example.glicodharma;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.MessageFormat;
import java.util.Locale;

public class Alarme extends AppCompatActivity {

    Button btnNovoAlarme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarme);

        btnNovoAlarme = findViewById(R.id.btnNovoAlarme);

        btnNovoAlarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_12H)
                        .setHour(12)
                        .setMinute(0)
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .setTitleText("Selecione a Hora")
                        .build();
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        abrirTelaAlarme(timePicker.getHour(), timePicker.getMinute());
                    }
                });
                timePicker.show(getSupportFragmentManager(), "tag");
            }
        });
    }

    private void abrirTelaAlarme(int hora, int minuto) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hora);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minuto);
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, "É hora de dar uma \uD83D\uDC40 na sua glicemia!!");
        startActivity(intent);
    }
}