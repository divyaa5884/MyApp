package com.example.divya.myapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    Button button_signUp, button_logIn;
    EditText editText_uname, editText_pwd;
    String username, pwd;
    SharedPreferences pref;
    //SharedPreferences.Editor editor = pref.edit();
    private static final String My_URL = "http://192.168.43.14:8000";
    private String Path;
    User_Info user;
    String check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText_uname = findViewById(R.id.editText_uname);
        editText_pwd = findViewById(R.id.editText_pwd);
        button_logIn = findViewById(R.id.button_logIn);
        pref= getApplicationContext().getSharedPreferences("LogPref", 0); // 0 - for private mode

        check = pref.getString("LoggedIn","");
        if(check.equals("true")) {
            startActivity(new Intent(this, profileActivity.class));
            finish(); // this will prevent to come to this activity on pressing back Button(mobile screen)
        }
        else {
            button_logIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    username = editText_uname.getText().toString().trim();
                    pwd = editText_pwd.getText().toString().trim();
                    Path = My_URL + "/login";
                    MyAsyncTasks myAsyncTask = new MyAsyncTasks();
                    myAsyncTask.execute(Path);
                }
            });
        }
        button_signUp = findViewById(R.id.button_signUp);
        button_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_uname.setText("");
                editText_pwd.setText("");
                Intent myInt = new Intent(getApplicationContext(), secondActivity.class);
                startActivity(myInt);
            }
        });
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    public String POST(String url, User_Info user) {
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("username", user.getUsername());
            jsonObject.accumulate("pwd", user.getPwd());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            //Log.d("Json =>String",json);
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            //setting the ContentType header to application/json to give the server the
            // necessary information about the representation of the content we’re sending.
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public class MyAsyncTasks extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            user = new User_Info();
            user.setUsername(username);
            user.setPwd(pwd);

            return POST(url[0],user);
        }

        @Override
        protected void onPostExecute(String s) {
            String value=null,name = null;
            try {
                value = new JSONObject(s).getString("val");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Log.d("Value", value);
            //You need to call Toast.makeText() (and most other functions dealing with the UI) from
            //within the main thread.
            assert value != null;
            if(value.equals("gotit") ){
                Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
                try {
                    name = new JSONObject(s).getString("name");
                    Intent profileIntent = new Intent(getApplicationContext(),profileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name",name);
                    profileIntent.putExtras(bundle);
                    startActivity(profileIntent);
                    finish(); // this will prevent to come to this activity on pressing back Button
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            else if(value.equals("notpresent") ){
                Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT).show();
            }
            else if(value.equals("err") )    {
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
            }
            else if(value.equals("wpwd")){
                Toast.makeText(getApplicationContext(), "Password Mismatch !", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
            }
        }
    }

}