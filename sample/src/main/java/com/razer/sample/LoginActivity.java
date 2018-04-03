package com.razer.sample;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.razer.sdk.Callback;
import com.razer.sdk.CallbackManager;
import com.razer.sdk.LoginManager;
import com.razer.sdk.Token;
import com.razer.sdk.TokenExpiredException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class LoginActivity extends AppCompatActivity {


    private EditText mPasswordView;
    private View signinToRazer;
    private CallbackManager callbackmanager;
    private File serializedTokenFile;
    private Token razerCachedToken;
    private EditText clientId;
    private EditText redirectUrl;
    private EditText clientSecret;
    //Razer Callback listener
    private Callback callback = new Callback() {
        @Override
        public void onError(Exception e) {
            Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onSuccess(Token token) {
            cacheToken(token);
            gotoMain(token);
        }
    };

    private void cacheToken(Token token) {
        razerCachedToken = token;
        OutputStream outStream;
        try {
            outStream = new FileOutputStream(serializedTokenFile);

            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(token);
            objectOutStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serializedTokenFile = new File(getCacheDir(), Constants.SERIALIZED_TOKEN_FILE_NAME);

        if (serializedTokenFile.exists()) {
            InputStream inStream;
            try {
                inStream = new FileInputStream(serializedTokenFile);
                ObjectInputStream objectInStream = new ObjectInputStream(inStream);
                razerCachedToken = ((Token) objectInStream.readObject());
                objectInStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (razerCachedToken != null) {
            try {
                LoginManager.getInstance().login(razerCachedToken);
                gotoMain(razerCachedToken);
            } catch (TokenExpiredException e) {
                e.printStackTrace();
            }

        }

        setContentView(R.layout.activity_login);
        callbackmanager = CallbackManager.create(); //create an instance of callback manager
        LoginManager.getInstance().registerCallback(callbackmanager, callback);
        redirectUrl = findViewById(R.id.redirectUrl);
        redirectUrl.setText(Constants.RAZER_CLIENT_REDIRECT_URL);
        clientId = findViewById(R.id.clientId);
        clientId.setText(Constants.RAZER_CLIENT_ID);
        clientSecret = findViewById(R.id.clientSecret);

        clientSecret.setText(Constants.RAZER_CLIENT_SECRET);

        signinToRazer = findViewById(R.id.signinToRazer);
        signinToRazer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startRazerAuthorization();
            }
        });

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }

    private void startRazerAuthorization() {
        LoginManager.getInstance().login(LoginActivity.this,
                clientId.getText().toString(), //your client id
                new String[]{LoginManager.SCOPE_OPENID, LoginManager.SCOPE_PROFILE, LoginManager.SCOPE_EMAIL, LoginManager.SCOPE_ADDRESS, LoginManager.SCOPE_PHONE}, //permissions you are requesting from the users
                redirectUrl.getText().toString(),
                TextUtils.isEmpty(clientSecret.getText().toString()) ? null : clientSecret.getText().toString()//optional

        );
    }

    private void gotoMain(Token token) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("data", (Parcelable) token);
        startActivity(intent);
        finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackmanager != null) {
            callbackmanager.onActivityResult(requestCode, resultCode, data);  //let the callback manager consume the result
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().unRegisterCallback(callbackmanager, callback);//unregister the callback listener
    }

    private void attemptLogin() {
        Toast.makeText(this, "nothing...", Toast.LENGTH_SHORT).show();
    }

}

