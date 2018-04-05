package com.example.divya.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class profileActivity extends AppCompatActivity {
    TextView textView_name;
    SharedPreferences pref;
    SharedPreferences.Editor editor ;
    Button button_logOut;
    String check,value;
    //private static final String My_URL = "http://192.168.43.14:8000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        button_logOut = findViewById(R.id.button_logOut);
        pref = getApplicationContext().getSharedPreferences("LogPref", 0);
        editor = pref.edit();
        check = pref.getString("LoggedIn","");
        //if loggedIn value is true => then get the name of user
        if(check.equals("true")) {
            value = pref.getString("name","");
        }
        else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                value = extras.getString("name");
                editor.putString("name", value);
                editor.putString("LoggedIn", "true");
                editor.apply();
            }
        }
        textView_name = findViewById(R.id.textView_name);
        textView_name.setText(value);
        button_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clear();
                editor.apply();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
    /*
    @Override
    public void onBackPressed()
    {

    }
*/
}
