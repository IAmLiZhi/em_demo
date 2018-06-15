package com.meris.em_demo.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.meris.em_demo.R;
import com.meris.em_demo.utils.ToastUtils;


public class ECLoginActivity extends AppCompatActivity {
    EditText mName;
    EditText mPwd;

    public static void show(Context context) {
        Intent intent = new Intent(context, ECLoginActivity.class);
        context.startActivity(intent);
    }

    //加载中
    ProgressDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        mName = (EditText) findViewById(R.id.name);
        mPwd = (EditText) findViewById(R.id.password);

        //注册
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        //登录
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }


    public void register() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("注册中，请稍后......");
        mDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(mName.getText().toString().trim(), mPwd.getText().toString().trim());

                    if (!ECLoginActivity.this.isFinishing()) {
                        mDialog.dismiss();
                    }
                    ToastUtils.showLong("注册成功，用户名是:" + mName.getText().toString() + "  快开始聊天吧");

                } catch (final HyphenateException e) {
                    e.printStackTrace();

                    if (!ECLoginActivity.this.isFinishing()) {
                        mDialog.dismiss();
                    }
                    /**
                     * 关于错误码可以参考官方api详细说明
                     * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                     */
                    int errorCode = e.getErrorCode();
                    String message = e.getMessage();
                    switch (errorCode) {
                        case EMError.NETWORK_ERROR:
                            ToastUtils.showLong("网络异常，请检查网络！ code: " + errorCode + "，message: " + message);
                            break;
                        case EMError.USER_ALREADY_EXIST:
                            ToastUtils.showLong("用户名已存在,请尝试登录！ code: " + errorCode + "，message: " + message);
                            break;
                        case EMError.USER_ALREADY_LOGIN:
                            ToastUtils.showLong("用户已登录！ code: " + errorCode + "，message: " + message);
                            break;
                        case EMError.USER_AUTHENTICATION_FAILED:
                            ToastUtils.showLong("用户id或密码错误！ code: " + errorCode + "，message: " + message);
                            break;
                        case EMError.SERVER_UNKNOWN_ERROR:
                            ToastUtils.showLong("服务器位置错误！ code: " + errorCode + "，message: " + message);
                            break;
                        case EMError.USER_REG_FAILED:
                            ToastUtils.showLong("注册失败！ code: " + errorCode + "，message: " + message);
                            break;
                        default:
                            ToastUtils.showLong("ml_sign_up_failed  code: " + errorCode + "，message: " + message);
                            break;

                    }


                }
            }
        }).start();


    }

    public void login() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("正在登陆请稍后......");
        mDialog.show();

        EMClient.getInstance().login(mName.getText().toString(), mPwd.getText().toString(), new EMCallBack() {
            @Override
            public void onSuccess() {
                mDialog.dismiss();

                // 加载所有群组到内存，如果使用了群组的话
//                EMClient.getInstance().groupManager().loadAllGroups();
                // 加载所有会话到内存
                EMClient.getInstance().chatManager().loadAllConversations();
                ToastUtils.showLong("登录，成功开始聊天吧");
                ECMainActivity.show(ECLoginActivity.this);
                finish();

            }

            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.dismiss();
                        ToastUtils.showLong("登录失败 code: " + i + ",message: " + s);
                        switch (i) {
                            case EMError.NETWORK_ERROR:
                                ToastUtils.showLong("网络异常，请检查网络！ code: " + i + "，message: " + s);
                                break;
                            case EMError.INVALID_USER_NAME:
                                ToastUtils.showLong("无效用户名！ code: " + i + "，message: " + s);
                                break;
                            case EMError.INVALID_PASSWORD:
                                ToastUtils.showLong("用户密码不正确！ code: " + i + "，message: " + s);
                                break;
                            case EMError.USER_AUTHENTICATION_FAILED:
                                ToastUtils.showLong("用户名或密码不正确！ code: " + i + "，message: " + s);
                                break;
                            case EMError.USER_NOT_FOUND:
                                ToastUtils.showLong("用户不存在！ code: " + i + "，message: " + s);
                                break;
                            case EMError.SERVER_NOT_REACHABLE:
                                ToastUtils.showLong("无法连接到服务器！ code: " + i + "，message: " + s);
                                break;
                            case EMError.SERVER_BUSY:
                                ToastUtils.showLong("服务器繁忙，请稍后.... code: " + i + "，message: " + s);
                                break;
                            case EMError.SERVER_TIMEOUT:
                                ToastUtils.showLong("等待服务器响应超时！ code: " + i + "，message: " + s);
                                break;
                            case EMError.SERVER_UNKNOWN_ERROR:
                                ToastUtils.showLong("未知服务器错误！ code: " + i + "，message: " + s);
                                break;
                            case EMError.USER_ALREADY_LOGIN:
                                ToastUtils.showLong("用户已登录！ code: " + i + "，message: " + s);
                                break;

                        }
                    }
                });


            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
