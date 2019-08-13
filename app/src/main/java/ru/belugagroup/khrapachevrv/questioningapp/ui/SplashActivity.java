package ru.belugagroup.khrapachevrv.questioningapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.orhanobut.hawk.Hawk;

import ru.belugagroup.khrapachevrv.questioningapp.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Подключаем pref
        Hawk.init(this).build();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
