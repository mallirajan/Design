package com.demo.navigation;

/**
 * Created by mallirajan on 12/22/2017.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Screen2Activity extends AppCompatActivity implements AppConstants{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2);
        Bundle bundle = getIntent().getExtras();
        int value = 0;
        if(bundle != null){
            value = bundle.getInt(SCREEN1_VALUE) * 2;
        }
        ((EditText)findViewById(R.id.screen2editText)).setText(Integer.toString(value));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Screen2");
    }

    public void onAction(View v) {
        int value = 0;
        EditText editText = ((EditText) findViewById(R.id.screen2editText));
        try {
            String text = editText.getText().toString();
            value = (Integer.parseInt(text) /2);
            Intent returnIntent = new Intent();
            returnIntent.putExtra(SCREEN2_VALUE, value);
            setResult(RESULT_OK, returnIntent);
            finish();
        } catch (NumberFormatException e) {
            editText.setError("Enter Valid Number");
            e.printStackTrace();
        }
    }
}
