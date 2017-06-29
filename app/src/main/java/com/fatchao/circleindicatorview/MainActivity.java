package com.fatchao.circleindicatorview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private CircleIndicatorView mCircleIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleIndicatorView = (CircleIndicatorView) findViewById(R.id.circle_index_view);
        mCircleIndicatorView.goToPoint(55.88f);
    }
}
