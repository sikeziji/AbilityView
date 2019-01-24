package com.example.administrator.abilityview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.administrator.abilityview.view.AibilitysView;

public class MainActivity extends AppCompatActivity {
    private AibilitysView aibilitymapview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.aibilitymapview = (AibilitysView) findViewById(R.id.aibilitymapview);
        aibilitymapview.setData(new int[]{65, 70, 80, 70, 80, 80, });
    }
}
