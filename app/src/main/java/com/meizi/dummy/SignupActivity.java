package com.meizi.dummy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.meizi.dummy.bean.User;
import com.meizi.dummy.http.HttpConstants;
import com.meizi.dummy.http.HttpUtil;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

//    @BindView(R.id.input_name)
//    EditText _nameText;
    //    @BindView(R.id.input_address)
//    EditText _addressText;
    @BindView(R.id.input_email)
    EditText _emailText;
    //    @BindView(R.id.input_mobile)
//    EditText _mobileText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.input_reEnterPassword)
    EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup)
    Button _signupButton;
    @BindView(R.id.link_login)
    TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);


        // Logo: FlashWord -> Dummy
        WebView webView = (WebView) findViewById(R.id.wv_webview);
        // 允许h5使用javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 允许android调用javascript
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("file:///android_asset/www/flashWord/index.html");
        // 系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 使用WebView加载显示url
                view.loadUrl(url);
                // 返回true
                return true;
            }
        });

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        // 下划线
        SpannableString spannableString = new SpannableString(_loginLink.getText().toString());
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);
        spannableString.setSpan(underlineSpan, 18, _loginLink.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(foregroundColorSpan, 18, _loginLink.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        _loginLink.setText(spannableString);

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

//        _nameText.setText("1234");
        _emailText.setText("1234@qq.com");
        _passwordText.setText("1234");
        _reEnterPasswordText.setText("1234");
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

//        String name = _nameText.getText().toString();
//        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
//        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.
        User sign_up = new User();
//        sign_up.setNickName(name);
        sign_up.setUserEmail(email);
        sign_up.setPassword(password);
        createUser(sign_up, progressDialog);
    }


    public void createUser(User user, ProgressDialog progressDialog) {
        HttpUtil.sendOkHttpRequest(HttpConstants.USER_CREATE_URL, JSONObject.toJSONString(user), new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        JSONObject jsonObject =
                                JSONObject.parseObject(responseText);
                        //返回错误信息
                        if (jsonObject.get("resultCode").equals("1")) {
                            Toast.makeText(getBaseContext(), jsonObject.get("resultMsg").toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            _signupButton.setEnabled(true);
                        } else {
                            String JWT = jsonObject.getJSONObject("data").get("token").toString();
                            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", JWT);
                            editor.putString("email", user.getUserEmail());
                            editor.putString("pwd", user.getPassword());
                            editor.putBoolean("rememberMe", true);
                            editor.commit();
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "Logged!", Toast.LENGTH_SHORT).show();
                            onSignupSuccess();
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    // 判断超时异常
                    Log.e("error", "连接超时，请检查后台服务. ===> " + e.toString());
                }
                if (e instanceof ConnectException) {
                    // 判断连接异常，我这里是报Failed to connect to 10.7.5.144
                    Log.e("error", "连接超时，请检查后台服务. ===> " + e.toString());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        _signupButton.setEnabled(true);
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public boolean validate() {
        boolean valid = true;

//        String name = _nameText.getText().toString();
//        String address = _addressText.getText().toString();
        String email = _emailText.getText().toString();
//        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

//        if (name.isEmpty() || name.length() < 3) {
//            _nameText.setError("at least 3 characters");
//            valid = false;
//        } else {
//            _nameText.setError(null);
//        }

//        if (address.isEmpty()) {
//            _addressText.setError("Enter Valid Address");
//            valid = false;
//        } else {
//            _addressText.setError(null);
//        }


        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

//        if (mobile.isEmpty() || mobile.length()!=10) {
//            _mobileText.setError("Enter Valid Mobile Number");
//            valid = false;
//        } else {
//            _mobileText.setError(null);
//        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}