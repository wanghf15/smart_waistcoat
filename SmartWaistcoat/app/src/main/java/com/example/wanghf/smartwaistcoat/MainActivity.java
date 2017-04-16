package com.example.wanghf.smartwaistcoat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.androidplot.xy.XYPlot;
import com.example.wanghf.myapplication.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private XYPlot impulsePlot;

    private boolean showingCurve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    private void initPlots() {
        impulsePlot = (XYPlot) findViewById(R.id.main_plot_impulse);
    }

    public void onClickShowCurve(View view) {
        if (showingCurve) {
            findViewById(R.id.linear_curve).setVisibility(View.GONE);
            findViewById(R.id.linear_table).setVisibility(View.VISIBLE);
            showingCurve = false;
        }
        else {
            findViewById(R.id.linear_table).setVisibility(View.GONE);
            findViewById(R.id.linear_curve).setVisibility(View.VISIBLE);
            showingCurve = true;
        }
    }
}
