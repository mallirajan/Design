package com.demo.navigation;

/**
 * Created by mallirajan on 12/22/2017.
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Screen1Activity extends AppCompatActivity implements AppConstants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Screen1");
    }

    public void onAction(View v) {
        goToScreen(SCREEN2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_VALUE && resultCode == RESULT_OK){
            updateUI(data.getExtras());
        }
    }

    private void goToScreen(int screenId) {
        switch (screenId) {
            case SCREEN2:
                Intent intent = new Intent(this, Screen2Activity.class);
                EditText editText = null;
                int value = 0;
                try {
                    editText = (EditText) (findViewById(R.id.screen1editText));
                    String text = editText.getText().toString();
                    value = Integer.parseInt(text);
                    intent.putExtra(SCREEN1_VALUE,value);
                    startActivityForResult(intent, GET_VALUE);
                } catch (NumberFormatException e) {
                    editText.setError("Enter Valid Number");
                } catch (Exception e) {
                    Toast.makeText(this, "Unknow exception",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void updateUI(Bundle bundle){
        if(bundle != null) {
            int value = 0;
            if (bundle != null) {
                value = bundle.getInt(SCREEN2_VALUE);
            }
            ((EditText) findViewById(R.id.screen1editText)).setText(Integer.toString(value));
        }
    }
}
