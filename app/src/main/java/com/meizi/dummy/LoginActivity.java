package com.meizi.dummy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.meizi.dummy.bean.User;
import com.meizi.dummy.http.HttpConstants;
import com.meizi.dummy.http.HttpUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMGroupEventListener;
import com.tencent.imsdk.TIMGroupTipsElem;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMRefreshListener;
import com.tencent.imsdk.TIMSdkConfig;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.logging.Logger;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.core.codec.Base64;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final String TIM_TAG = "TIM:";
    private static final int REQUEST_SIGNUP = 0;


    /**
     * 腾讯云 SDKAppId，需要替换为您自己账号下的 SDKAppId。
     * <p>
     * 进入腾讯云云通信[控制台](https://console.cloud.tencent.com/avc ) 创建应用，即可看到 SDKAppId，
     * 它是腾讯云用于区分客户的唯一标识。
     */
    public static final int SDKAPPID = 1400295156;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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


        // Login
        _emailText.setText("1234@qq.com");
        _passwordText.setText("1234");
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // 下划线
        SpannableString spannableString = new SpannableString(_signupLink.getText().toString());
        UnderlineSpan underlineSpan = new UnderlineSpan();
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);
        spannableString.setSpan(underlineSpan, 16, _signupLink.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(foregroundColorSpan, 16, _signupLink.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        _signupLink.setText(spannableString);
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();


        User user = new User();
        user.setUserEmail(_emailText.getText().toString());
        user.setPassword(_passwordText.getText().toString());
        // TODO: Implement your own authentication logic here.
        loginRequest(user, progressDialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }


    public void loginRequest(User user, ProgressDialog progressDialog) {
        HttpUtil.sendOkHttpRequest(HttpConstants.USER_LOGIN_URL, JSON.toJSONString(user), new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        JSONObject jsonObject =
                                JSONObject.parseObject(responseText);
                        if (jsonObject.get("resultCode").equals("1")) {
                            Toast.makeText(getBaseContext(), jsonObject.get("resultMsg").toString(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            _loginButton.setEnabled(true);
                        } else {
                            // 获取userSig函数
                            String sig = jsonObject.getJSONObject("data").get("signature").toString();
                            String JWT = jsonObject.getJSONObject("data").get("token").toString();
                            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", JWT);
                            editor.putString("email", user.getUserEmail());
                            editor.putString("pwd", user.getPassword());
                            editor.putString("sig", Base64.encode(sig));
                            editor.putBoolean("rememberMe", true);
                            editor.commit();
                            progressDialog.dismiss();
                            onLoginSuccess();
                        }
                    }
                });

            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _loginButton.setEnabled(true);
                        if (e instanceof SocketTimeoutException) {
                            // 判断超时异常
                            progressDialog.dismiss();
                            Log.e("error", "连接超时，请检查后台服务. ===> " + e.toString());
                            Toast.makeText(getBaseContext(), "连接超时,请检查后台服务.", Toast.LENGTH_LONG).show();
                        }
                        if (e instanceof ConnectException) {
                            progressDialog.dismiss();
                            // 判断连接异常，我这里是报Failed to connect to 10.7.5.144
                            Toast.makeText(getBaseContext(), "连接超时,请检查后台服务.", Toast.LENGTH_LONG).show();
                            Log.e("error", "连接超时，请检查后台服务. ===> " + e.toString());
                        }
                    }
                });
            }
        });
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
