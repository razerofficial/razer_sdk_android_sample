package com.razer.sample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.razer.sdk.LoginManager;
import com.razer.sdk.Token;
import com.razer.sdk.UserAPI;
import com.razer.sdk.UserNotLoggedException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private View profileFetchProgress;

    private TextView accessToken;
    private TextView expiresIn;

    Token razerToken;
    private TextView razerId;
    private TextView email;
    private TextView openid;
    private SimpleDraweeView simpleDraweeView;
    private View profileHolder;

    static OkHttpClient client = new OkHttpClient();
    private File serializedTokenFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serializedTokenFile = new File(getCacheDir(), Constants.SERIALIZED_TOKEN_FILE_NAME);
        setContentView(R.layout.activity_main);
        razerId = findViewById(R.id.razerId);
        email = findViewById(R.id.email);
        openid = findViewById(R.id.openid);
        simpleDraweeView = findViewById(R.id.avatar);
        profileHolder = findViewById(R.id.profileHolder);

        razerToken = getIntent().getParcelableExtra("data"); //paracelable Token from the LoginActivity

        accessToken = findViewById(R.id.accessToken);
        accessToken.setText(razerToken.getAccessToken());
        expiresIn = findViewById(R.id.expiresIn);
        DateFormat sdf = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
        expiresIn.setText(sdf.format(new Date(razerToken.getExpiresIn())));
        profileFetchProgress = findViewById(R.id.profileFetchProgress);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        refresh();

        findViewById(R.id.logoutRazerSdk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serializedTokenFile.delete(); //deletes the Serialized token
                LoginManager.getInstance().logout();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

            }
        });

    }


    private void refresh() {
        if (profileFetchProgress.getVisibility() == View.VISIBLE) {
            return;
        }
        new GetUserProfileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private class GetUserProfileTask extends AsyncTask<String, Void, Object> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            profileHolder.setVisibility(View.GONE);
            profileFetchProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Object doInBackground(String... strings) {
            boolean useWrappedApi = true;


            if (useWrappedApi) {
                try {
                    String response = UserAPI.getInstance().getUser();
                    JSONObject jsonObject = new JSONObject(response);
                    return jsonObject;
                } catch (Exception e) {
                    e.printStackTrace();
                    return e;
                }
            } else {
                ///or manually add the Access token to the header
                Request request = new Request.Builder().url("https://oauth2.razerapi.com/userinfo").addHeader("Authorization", "Bearer " + razerToken.getAccessToken()).get().build();
                String responseString = "";
                try {
                    Response response = client.newCall(request).execute();
                    responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);
                    return jsonObject;

                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof JSONException) {
                        return new Exception(responseString);
                    }
                    return e;

                }
            }


        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            profileFetchProgress.setVisibility(View.GONE);
            if (o instanceof Exception) {
                Toast.makeText(MainActivity.this, ((Exception) o).getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (o instanceof JSONObject) {
                setProfileFields((JSONObject) o);
            }

        }
    }


    private void setProfileFields(JSONObject jsonObject) {

        if (jsonObject.has("razer_id")) {
            try {
                razerId.setText(jsonObject.getString("razer_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject.has("email")) {
            try {
                email.setText(jsonObject.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject.has("avatar")) {
            try {
                simpleDraweeView.setImageURI(jsonObject.getString("avatar"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject.has("open_id")) {
            try {
                openid.setText(jsonObject.getString("open_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AlphaAnimation alphaANim = new AlphaAnimation(0, 1);
        alphaANim.setDuration(1000);
        profileHolder.setVisibility(View.VISIBLE);
        profileHolder.startAnimation(alphaANim);

    }
}
