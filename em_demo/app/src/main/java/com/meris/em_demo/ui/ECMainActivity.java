package com.meris.em_demo.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.meris.em_demo.R;
import com.meris.em_demo.utils.LogUtils;
import com.meris.em_demo.utils.ToastUtils;


/**
 * author:Meris
 * date:2018/5/30
 */

public class ECMainActivity extends AppCompatActivity {
    EditText mName;

    public static void show(Context context) {
        Intent intent = new Intent(context, ECMainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mName =(EditText) findViewById(R.id.name);

        //判断一下是否登录如果没登录的话跳转到登录页面
        if (!EMClient.getInstance().isLoggedInBefore()) {
            ECLoginActivity.show(ECMainActivity.this);
            finish();
        }

        //退出登录
        findViewById(R.id.login_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        //进入聊天列表
        findViewById(R.id.chat_list).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatListActivity.show(ECMainActivity.this);
            }
        });

        //开始聊天
        findViewById(R.id.chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatIn();
            }
        });



    }

    public void chatIn() {
        String name = mName.getText().toString().trim();
        String myName = EMClient.getInstance().getCurrentUser();
        if (!TextUtils.isEmpty(name)) {
            if (name.equals(myName)) {
                ToastUtils.showLong("不能和自己聊天");
                return;
            }
            Intent chat = new Intent(this, ChatActivity.class);
            chat.putExtra(EaseConstant.EXTRA_USER_ID, name);  //对方账号
            chat.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EMMessage.ChatType.Chat); //单聊模式
            startActivity(chat);

        } else {
            ToastUtils.showLong("用户名不可为空");
        }


    }

    public void logOut() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                LogUtils.d("logout success");
                finish();
            }

            @Override
            public void onError(int i, String s) {
                LogUtils.d("logout error" + i + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }


}
