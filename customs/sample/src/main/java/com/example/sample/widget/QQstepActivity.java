package com.example.sample.widget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.sample.R;
import com.tc.qqstep.QQStepView;


public class QQstepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qqstep);

        final QQStepView qqStepView = (QQStepView) findViewById(R.id.step_view);
        qqStepView.setStepMax(4000);
        qqStepView.startPlay();
    }

    public void onBack(View view){
        finish();
    }
}
