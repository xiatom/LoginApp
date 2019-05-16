package com.ace.xiatom.loginapp;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView userView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    TextView timeout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);
        timeout = findViewById(R.id.showTimeOut);
        Button mEmailSignInButton = findViewById(R.id.submit);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                timeout.setVisibility(View.INVISIBLE);
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        userView.setError(null);
        mPasswordView.setError(null);
        String email = userView.getText().toString();
        String password = mPasswordView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // 检查密码及邮箱可用性
        if (TextUtils.isEmpty(email)) {
            userView.setError("请填入邮箱");
            focusView = userView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }


    //显示进度条
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {
        private final String mUser;
        private final String mPassword;
        UserLoginTask(String username, String password) {
            mUser = username;
            mPassword = password;
        }
        @Override
        protected Integer doInBackground(Void... params) {
            HttpURLConnection connection = null;
            DataOutputStream out;
            String string = "false";
            try {
                if(mUser.equals("ace") & mPassword.equals("1023"))
                    string = "success";
                URL url = new URL("http", "10.241.4.110", 8080, "/Android_User_Database/Login_submit");
                connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(5000);
                connection.setDoOutput(true);
                out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes("name="+mUser+"&password="+mPassword);
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                if((string=br.readLine())!=null) {
                    Log.i("loginStatus",string);
                }
                out.close();
            } catch (Exception e) {
                return 0;
            }finally {
                connection.disconnect();
            }
            if(string.equals("success"))
                return 1;
            else
                return -1;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            mAuthTask = null;
            showProgress(false);
            if (success==1) {
                Toast.makeText(LoginActivity.this,"Login successfully",Toast.LENGTH_LONG).show();
            } else if(success==-1){
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }else {
                //超时
                timeout.setVisibility(View.VISIBLE);
            }
        }
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

