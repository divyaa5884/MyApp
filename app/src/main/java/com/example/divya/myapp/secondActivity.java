package com.example.divya.myapp;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class secondActivity extends AppCompatActivity {

    Button button_create;
    EditText name1, uname1, eid1, mob1, pwd1;
    String name, username, eid, mob, pwd;
    private static final String My_URL = "http://192.168.43.14:8000";
    private String Path;
    User_Info user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        name1 = findViewById(R.id.editText_name);
        uname1 = findViewById(R.id.editText_uname);
        eid1 = findViewById(R.id.editText_eid);
        mob1 = findViewById(R.id.editText_mob);
        pwd1 = findViewById(R.id.editText_pwd);
        button_create = findViewById(R.id.button_create);
        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //After you get the value in the text/editText field you have to store it in a String
                //variable in order to access it.
                name = name1.getText().toString().trim();
                username = uname1.getText().toString().trim();
                eid = eid1.getText().toString().trim();
                mob = mob1.getText().toString().trim();
                pwd = pwd1.getText().toString().trim();

                Path = My_URL + "/register";
                Log.d("Path:", Path);
                MyAsyncTasks myAsyncTask = new MyAsyncTasks();
                myAsyncTask.execute(Path);
            }
        });
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
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
            jsonObject.accumulate("name", user.getName());
            jsonObject.accumulate("username", user.getUsername());
            jsonObject.accumulate("eid", user.getEid());
            jsonObject.accumulate("mob", user.getMob());
            jsonObject.accumulate("pwd", user.getPwd());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            Log.d("Json =>String",json);
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
    public void redirect_to(){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(myIntent);
        finish();
    }
    public class MyAsyncTasks extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            user = new User_Info();
            user.setName(name);
            user.setUsername(username);
            user.setEid(eid);
            user.setMob(mob);
            user.setPwd(pwd);
            Log.d("User_Info",user.getName()+" "+user.getEid() );

            return POST(urls[0],user);
        }

        @Override
        protected void onPostExecute(String s) {
            String value=null;
            try {
                value = new JSONObject(s).getString("val");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Value", value);
            assert value != null;
            //You need to call Toast.makeText() (and most other functions dealing with the UI) from
            //within the main thread.
            if(value.equals("Saved") ){
                Toast.makeText(getApplicationContext(), "Signed up successfully", Toast.LENGTH_SHORT).show();
                redirect_to();
            }
            else if(value.equals("eidpresent")){
                Toast.makeText(getApplicationContext(), "EmailId already registered!", Toast.LENGTH_SHORT).show();
            }
            else if(value.equals("empty")){
                Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Error, try again later!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
